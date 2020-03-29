
### [ktorm框架](https://github.com/vincentlauvlwj/Ktorm)代码生成器

代码生成器支持的数据库有 
- Mysql
- Sqlserver

### 官方下载地址：https://plugins.jetbrains.com/plugin/14022

### 如果插件在jetbrains下载失败可以试试从百度网盘 插件下载地址：
链接：https://pan.baidu.com/s/1XQ8MMuglz1gU_x4Hjnn0wg 
提取码：r861


### 以下是手动下载插件后手动安装插件教程

#####  打开idea->File->Settings...

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
  "NamespaceName": "DbModel",
  "ConnectionString": "Server=localhost;Port=53306;Database=antmgr;Uid=root;Pwd=123456;charset=utf8;SslMode=none",
  "TableFilter": []
}
```

## Ktorm生成的Json字段说明
字段 | 说明
---|---
Type | 支持 mysql 和 sqlserver (sqlserver的话支持在后面指定版本号：sqlserver2000, sqlserver2005,sqlserver2008,sqlserver2012,sqlserver2017，如果不指定的话 sqlserver = sqlserver2008)
OutPutFolder| 生成的代码保存在本机的哪个文件夹(可以使用绝对路径和相对路径) 相对路径是相对于你选择的json文件 例如(../java)
IsKotlin| 需要设置为true
NamespaceName| 指定 package 名称
ConnectionString| db连接字符串
TableFilter| 表名称的string数组，如果指定了只会生成特定的表的代码


### 测试演示

![image](https://images4.c-ctrip.com/target/zb0w1e000001fy5pm2190.gif)

