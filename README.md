# 二郎神用户管理平台
用户的统一管理平台。部署完成后，可通过RESTful接口或SDK访问项目，实现用户的全流程管理，无需再自己实现用户模块，数据权限，数据安全等一应俱全。

erlangshen java后台服务\
erlangshen_web 前端控制台\
erlangshen-java-sdk java版sdk。

**包含以下功能：**
1. 可同时管理多个系统（应用），按系统管理用户，安全设置。
2. 用户相关操作；注册流程、信息维护、邮件短信平台设置、实名认证流程。
3. 用户权限的维护；分配角色，菜单及菜单下的功能；灵活配置。
4. 生成AK/SK，使用AK/SK调用接口更安全。
5. 用户登录日志。
6. 用户安全设置。异地登录提醒，不同客户端登录限制等。（即将竣工）


**docker：**
```
docker pull registry.cn-beijing.aliyuncs.com/shuli495/erlangshen:master
```

## erlangshen_web
使用Flux框架，webpack+npm打包。

**启动步骤：**
1. 下载依赖包：进入erlangshen_web根目录，执行npm install
2. 设置初始化参数：src\js\common\setting.js\
    token：数据库中管理员token\
    serverUrl：后端url（例如http://127.0.0.1:8080/erlangshen）
3. 设置运行ip和端口：package.json\
    scripts:dev --a IP --port 端口\
    scripts:deploy --host IP --port 端口
3. 本地环境运行：npm run dev
3. 正式环境运行：\
    npm run deploy\
    或\
    直接拷贝build和src文件夹以静态文件形式运行


## erlangshen
使用springMVC框架。

**初始化**
1. 初始化sql：src\main\resources\init\
    table.sql：表结构\
    admin.sql：管理员用户和token,默认账号admin@erlangshen.com/123456（建议不使用管理员账号维护信息，新建账号）
2. 设置配置：src\main\resources\setting.properties
    jdbc：数据库\
    admin.token：数据库初始化token\
    aes.secret：加密密码的密钥\
    bdmap.\*：百度地图API（根据ip查询城市），用于检测异地登录
    bdyun.\*：百度云API，用于识别身份证图片

**开发参考：**
https://github.com/shuli495/fastFlux

## erlangshen-java-sdk
**使用方法一：**
使用maven
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.shuli495</groupId>
        <artifactId>erlangshen.erlangshenJavaSDK</artifactId>
    </dependency>
</dependencies>
```
**使用方法二：**
下载源码，运行一下命令，生成target/jar包，加入项目。

```
mvn clean install
```



**开发参考：**
https://github.com/shuli495/fastJava


## 界面
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/0.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/1.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/2.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/3.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/4.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/5.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/6.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/7.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/8.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/9.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/10.png)
![image](http://p8d55ld0z.bkt.clouddn.com/erlangshen/11.png)
