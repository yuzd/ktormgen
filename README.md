### [ktorm框架](https://github.com/vincentlauvlwj/Ktorm)代码生成器

代码生成器支持的数据库有

- Mysql
- Sqlserver

### 官方下载地址：https://plugins.jetbrains.com/plugin/14033

### 如果插件在jetbrains下载失败可以试试从百度网盘 插件下载地址：

链接：https://pan.baidu.com/s/1XQ8MMuglz1gU_x4Hjnn0wg
提取码：r861

### 以下是手动下载插件后手动安装插件教程

##### 打开idea->File->Settings...

![image](https://images4.c-ctrip.com/target/zb0n1e000001fr6eq9640.png)

##### 选择 plugins 本地选择下载的zip文件

![image](https://images4.c-ctrip.com/target/zb0p1e000001fr92h36F3.png)

![image](https://images4.c-ctrip.com/target/zb021e000001fz753AD11.png)

![image](https://images4.c-ctrip.com/target/zb0b1e000001fuiaiE9BF.png)

##### 重启idea即可

### 重要

### 如何使用插件生成ktorm的dbmodes代码

##### 打开项目 在项目的 resources下面新建一个 json格式的文件，名称自己随便命名，但是一定得是.json后缀的文件

如下图

![image](https://images4.c-ctrip.com/target/zb091e000001fuiuv5945.png)

## Ktorm的Json内容模板：

```json
{
  "Type": "Mysql",
  "OutPutFolder": "../java/dal",
  "IsKotlin": true,
  "CamelCase": true,
  "KtormVersionNew": true,
  "NamespaceName": "DbModel",
  "ConnectionString": "Server=localhost;Port=53306;Database=antmgr;Uid=root;Pwd=123456;charset=utf8;SslMode=none",
  "TableFilter": [],
  "SetFkList": [
    "system_users,RoleTid,system_role,Tid"
  ]
}
```

## Ktorm生成的Json字段说明

字段 | 说明
---|---
Type | 支持 mysql 和 sqlserver (sqlserver的话支持在后面指定版本号：sqlserver2000, sqlserver2005,sqlserver2008,sqlserver2012,sqlserver2017，如果不指定的话 sqlserver = sqlserver2008)
OutPutFolder| 生成的代码保存在本机的哪个文件夹(可以使用绝对路径和相对路径) 相对路径是相对于你选择的json文件 例如(../java)
IsKotlin| 需要设置为true
KtormVersionNew| 如果用的是新版本的ktorm的话需要设置为true(因为ktorm更改了namespace)
CamelCase| 生成Dao和Model的时候可以把字段名称下划线转驼峰
NamespaceName| 指定 package 名称
ConnectionString| db连接字符串
TableFilter| 表名称的string数组，如果指定了只会生成特定的表的代码 ， system_% -》代表只生成system_开头的表
SetFkList| 外键关系设置(可以是非物理的外键)有4个参数逗号隔开分别是，A表的哪个字段关联B表的哪个字段，关联关系是1:1

### 测试演示

![image](https://images4.c-ctrip.com/target/zb0w1e000001fy5pm2190.gif)

### 生成的代码结构

![image](https://images4.c-ctrip.com/target/0zb4f120008c5nmub791E.png)

#### 1.dbmodels

是db里面的表，一张表对应这个目录下的一个kotlin文件

#### 2.dao是表的字段类型映射定义

一个model对应一个dao

#### 3.Tables

dataBase的扩展方法，只需要拿到database 就可以拿到表对象进行db操作。 可以参考下面的代码。

```kotlin
   val database = Database.connect(
    "jdbc:mysql://localhost:3306/antmgr?user=root&password=123456&useSSL=false",
    logger = ConsoleLogger(threshold = LogLevel.DEBUG)
)

//默认自动join是关闭的
val user = database.systemUsers.filter { it.Tid eq 2 }.firstOrNull()
println(user?.SystemRole)
//按照下面的方式开启自动join查询
val user2 = database.systemUsers.joinReferencesAndSelect().filter { it.Tid eq 2 }.firstOrNull()
println(user2?.SystemRole)

//筛选
val systemMenu = database.systemMenus.filter { (it.IsActive) and (it.Name eq "yuzd") }.firstOrNull()
println(systemMenu)

//新增
database.systemMenus.insert {
    set(it.Name, "test")
    set(it.IsActive, false)
}

//修改
database.systemMenus.update {
    set(it.IsActive, true)
    where { it.Name eq "test" }
}

//删除
database.systemMenus.delete {
    it.Name eq "test"
}

```

更多详细可以参考ktorm框架的文档
https://www.ktorm.org/en/entities-and-column-binding.html

![image](https://images4.c-ctrip.com/target/0zb5d120008od7nor3A7E.png)

### 2021-11-27
优化遇到错误的信息展示

![image](https://dimg04.c-ctrip.com/images/0v54712000958okfs31A2.png)

支持sqlserver：
由于在mac上的sqlserver依赖包大，所以支持sqlserver的版本请在百度网盘下载：

链接：https://pan.baidu.com/s/16oSpkU7OoMW92qaxZedsYg 
提取码：yuzd 
