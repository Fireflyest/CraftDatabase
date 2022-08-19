# CraftDatabase - Frame of Minecraft Database
##### 使用注解来分离数据持久层、服务层和应用层，自动生成数据库操作代码
在spigot插件中，使用数据库存储的操作速率远高于自带的YML文件存储的操作速率，
但是数据库的操作一般情况下都比较繁琐，且容易出现错误。固本插件提供多个sql语句构建类，
方便快速构建sql语句，同时使用注解分离各数据处理层并生成相应代码，使插件的数据处理结构更加清晰。

未完成

## 目录
* 插件内容
    * [语句构建类](#语句构建类)
    * [注解](#注解)
* 使用方法
    * [导入](#导入)
    * [数据层](#数据层)
    * [数据持久层](#数据持久层)
    * [服务层](#服务层)
    * [应用层](#应用层)
* 维护人员

## 插件内容
### 语句构建类
`SQLAlterTable`
`SQLCreateTable`
`SQLDropTable`
`SQLTruncateTable`
`SQLDelete`
`SQLInsert`
`SQLSelect`
`SQLUpdate`
### 注解
#### 数据层注解
`@Table`
`@ID`
`@Primary`
`@Column`
`@Skip`
#### 数据持久层注解
`@Dao`
`@Insert`
`@Delete`
`@Update`
`@Select`
#### 服务层注解
`@Service`
`@Auto`
## 使用方法
### 导入
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.7.0</version>
      <configuration>
        <encoding>UTF-8</encoding>
        <!--这里-->
        <annotationProcessors>
          <annotationProcessor>org.fireflyest.craftdatabase.annotation.TableProcessor</annotationProcessor>
          <annotationProcessor>org.fireflyest.craftdatabase.annotation.DaoProcessor</annotationProcessor>
        </annotationProcessors>
      </configuration>
    </plugin>
  </plugins>
</build>
```
连接数据库
```java 
SQLConnector.connect(sqlClass, url, user, password);
SQLServiceInitial.init(serviceClass, url);
```
### 数据层
### 数据持久层
### 服务层
### 应用层
## 维护人员
[Fireflyest](https://github.com/Fireflyest) QQ: 746969484