# Financial_Statement_Analyzer_BackEnd



## Goal

Open Dart API를 이용해 재무제표 데이터를 받아와서 데이터를 데이터베이스에 저장한 후, 요청이 있을 경우 데이터를 전송하는 프로젝트입니다.

[Financial Statement Analyzer FrontEnd](https://github.com/nicesick/Financial_Statement_Analyzer_FrontEnd) 에서 해당 데이터를 요청하여 시각화 할 수 있습니다.



## Environment

* [IntelliJ IDEA Community](https://www.jetbrains.com/ko-kr/idea/)
* [Spring-boot ](https://start.spring.io/) 2.4.2
* [MySql](https://dev.mysql.com/downloads/installer/) 8.0.23
* Spring-boot JPA
* JDOM
* [Open Dart API](https://opendart.fss.or.kr/)

```xml
<!-- pom.xml 에서 위의 라이브러리들을 다운받을 수 있습니다. -->

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.jdom/jdom -->
    <dependency>
        <groupId>org.jdom</groupId>
        <artifactId>jdom</artifactId>
        <version>2.0.2</version>
    </dependency>
</dependencies>
```



## How to Execute

* 구동 전 application.properties 파일에 입력해야 할 사항들이 있습니다.
  * Open Dart API 를 이용하기 위한 API Key
  * DB DataSource 생성을 위한 정보 (driver-class-name, url, username, password)

```properties
# resource/application.properties

dart.key            = { Your Open Dart API Key }

dart.corpCode.uri   = https://opendart.fss.or.kr/api/corpCode.xml
dart.corpInfo.uri   = https://opendart.fss.or.kr/api/company.json
dart.corpDetail.uri = https://opendart.fss.or.kr/api/fnlttMultiAcnt.json

# dart.document.uri   = https://opendart.fss.or.kr/api/document.xml
# dart.xbrl.uri       = https://opendart.fss.or.kr/api/fnlttXbrl.xml

spring.datasource.driver-class-name     = { Your driver class name for DB }
spring.datasource.jdbcUrl               = { Your jdbcUrl for DB }
# spring.datasource.url                   = jdbc:mysql://localhost:3306/annotationStudy?serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username              = { Your username for DB }
spring.datasource.password              = { Your password for DB }

spring.jpa.hibernate.ddl-auto           = update
spring.jpa.show-sql                     = false

logging.level.com.jihun.study.openDartApi = INFO
```

* 해당 정보 입력 후에는 구동하시면 됩니다.

```powershell
> ./mvnw spring-boot:run
```

