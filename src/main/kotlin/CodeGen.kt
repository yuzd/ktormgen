import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.thread


class CodeGen : AnAction() {
    private val NOTIFICATION_GROUP = NotificationGroup("OrmCodeGen", NotificationDisplayType.BALLOON, true)
    private val log: Logger = Logger.getInstance("OrmCodeGen")
    
    init {
        log.setLevel(org.apache.log4j.Level.DEBUG)
    }

    // 项目图片
    private val CODEGEN = IconLoader.getIcon("/icons/ktrom.png")
    
    @Volatile
    private var ISRUN = false
    
    override fun actionPerformed(e: AnActionEvent) {
        
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        if (virtualFile.extension != "json") return;
        val jsonFilePath = virtualFile.path

        val genFolderURL = CodeGen::class.java.classLoader.getResource("/gen/version.txt")
        if (genFolderURL == null) {
            Messages.showMessageDialog(
                "can not read version file in Resource",
                "Error",
                Messages.getErrorIcon()
            );
            return;
        }

        val version = genFolderURL.readText()
        log.info("code gen plugin version:$version")
        val ostype = OsCheck.getOperatingSystemType()
        val execFile = if (ostype == OsCheck.OSType.MacOS) "AntOrmGen$version" else "AntOrmGen$version.exe"
        //找到当前plugin所在的地方
        val pluginPath = PluginManager.getPlugin(PluginId.getId("com.yuzd.codegen.ktorm"))?.path?.absolutePath
        if (pluginPath.isNullOrEmpty()) {
            Messages.showMessageDialog(
                "can not get plugin `orm code gen` path",
                "Error",
                Messages.getErrorIcon()
            );
            return;
        }

        val exePath = Paths.get(pluginPath, execFile)
        if (!Files.exists(exePath)) {
            try {
                CodeGen::class.java.classLoader.getResourceAsStream("/gen/$execFile")
                    .use { stream ->
                        Files.copy(stream, exePath)
                    }
            } catch (e: IOException) {
                Messages.showMessageDialog(
                     e.message,
                    "Error",
                    Messages.getErrorIcon()
                );
                return;
            }
        }

        if (!Files.exists(exePath)) {
            Messages.showMessageDialog(
                "load codeGen agent in plugin `orm code gen` fail",
                "Error",
                Messages.getErrorIcon()
            );
            return;
        }
        Notifications.Bus.notify(
            Notification(
                "OrmCodeGen",
                "Codegen Start",
                "version:$version,$pluginPath",
                NotificationType.INFORMATION
            )
        )
        val project = e.dataContext.getData(PlatformDataKeys.PROJECT)
        ISRUN = true
        thread {
          
            var process: Process? = null
            var result = -1;
            var msg = "";
            try {
                if(ostype == OsCheck.OSType.MacOS){
                    val bashFile = createTempScript(pluginPath,execFile,jsonFilePath)
                    try {
                        val pb = ProcessBuilder("bash", bashFile.toString())
                        pb.inheritIO();
                        process = pb.start()//执行命令
                        result = process.waitFor() //等待codegen结果
                    }finally {
                        bashFile?.delete()
                    }
                }else{
                    val pb = ProcessBuilder(exePath.toString(), jsonFilePath)
                    pb.redirectErrorStream(true);
                    process = pb.start()//执行命令
                    result = process.waitFor() //等待codegen结果
                }
            } catch (e: Exception) {
                msg = e.message?:"err";
                result == -99;
            }finally {
                ISRUN = false;
            }
            ApplicationManager.getApplication().invokeLater {
                when {
                    result == -99 -> {
                        val notice = NOTIFICATION_GROUP.createNotification(
                            msg,
                            NotificationType.ERROR
                        )
                        notice.notify(project)
                    }
                    result != 0 -> {
                        msg = process?.inputStream?.readAll() ?: "orm codegen fail"
//                        PluginManager.getLogger().error(msg)
                        val notice = NOTIFICATION_GROUP.createNotification(
                            msg,
                            NotificationType.ERROR
                        )
                        notice.notify(project)
                    }
                    else -> {
                        
                        
                        val notice = NOTIFICATION_GROUP.createNotification("Codegen Success", NotificationType.INFORMATION)
                        notice.notify(project)
                        LocalFileSystem.getInstance().refresh(true);
                    }
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isVisible = !ISRUN && virtualFile != null && "json" == virtualFile.extension
    }

    private fun InputStream.readAll(): String {
        return try {
            val sc = Scanner(this)
            val sb = StringBuffer()
            while (sc.hasNext()) {
                sb.append(sc.nextLine())
            }
            sb.toString()
        }catch (e:Exception){
            e.message ?: "uncatch err";
        }
    }

    @Throws(IOException::class)
    fun createTempScript(folder:String,fileName:String,json:String): File? {
        val tempScript: File = File.createTempFile("genscript", null)
        val streamWriter: Writer = OutputStreamWriter(
            FileOutputStream(
                tempScript
            )
        )
        val printWriter = PrintWriter(streamWriter)
        printWriter.println("#!/bin/bash")
        printWriter.println("chmod -R 777 \"$folder\"")
        printWriter.println("cd \"$folder\"")
        printWriter.println("./$fileName \"$json\"")
        printWriter.close()
        return tempScript
    }

}