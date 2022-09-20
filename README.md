## Spring Security - Add JDBC Database Authentication

**JDBC - Overview - Defining Database Schema**

Database Support in Spring Security 
+ Spring Security can read user account info from database 
+ By default, you have to follow Spring Security's predefined table schemas

<img src="https://user-images.githubusercontent.com/80107049/191280072-9373ad7a-a654-4e55-a85b-ce54bc2e7b05.png" width=500 />


**Customize Database Access with Spring Security**

+ Can also customize the table schemas 
+ Useful if you have custom tables specific to your project / custom
+ You will be responsible for developing the code to access the data 
  + JDBC, Hibernate etc ...

**Development Process**
1. Develop SQL Script to set up database tables 
2. Add database support to Maven POM file 
3. Create JDBC properties file 
4. Define DataSource in Spring Configuration 
5. Update Spring Security Configuration to use JDBC







_Step 1:Develop SQL Script to setup database tables_

```POSTGRESQL
CREATE TABLE users (
  username VARCHAR(50) NOT NULL,
  password VARCHAR(50) NOT NULL,
  enable SMALLINT(1) NOT NULL,
  
  PRIMARY KEY (username)
  );
 ```
 
  
 **Spring Security Password Storage**
 
 + In Spring Security 5, passwords are stored using specific formate 

          `{id}encodedPassword`
 
| ID         | Description             |
| ---------- | ----------------------- |
| **noop**   | Plain text passwords    |
| **bcrypt** | BCrypt password hashing |

```POSTGRESQL
INSERT INTO users 
VALUES 
('john', '{noop}test123' , 1),
('mary', '{noop}test123' , 1),
('susan', '{noop}test123' , 1);
```
+ `{noop}` is the encoding algorithm id and `test123` is the password
  + Let Spring Security know the password are stored as plain text (noop)


```POSTGRESQL
CREATE TABLE authorities (
  username VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  
  CONSTRAINT authorities_ibfk_1 UNIQUE  authorities_idx_1 (username , authority)
  FOREIGN KEY (username)
  REFERENCES users (username)
  );
```

```POSTGRESQL
INSERT INTO authorities
VALUES
('john', 'ROLE_EMPLOYEE'),
('mary', 'ROLE_EMPLOYEE'),
('mary', 'ROLE_MANAGER'),
('susan', 'ROLE_EMPLOYEE'),
('susan', 'ROLE_ADMIN');
```
+ Internally Spring Security uses "ROLE_" prefix


_Step 2: Add Database Support to Maven POM file_

```XML
<!--POSTGRESQL JDBC Driver-->
  
  
<!--C3PO, DB Connection Pool-->

```

_Step 3:Create JBDC Properties File_

File:src/main/resources/persistence-postgresql.properties
```properties
#
#JDBC connection properties
#
jdbc.driver=org.postgresql.jdbc.Driver
jdbc.url=jdbc:postgresql://localhost:3306/spring_security_demo
jdbc.user=springstudent
jdbc.password=springstudent

#
# Connection pool properties
#
connection.pool.initialPoolSize=5
connection.pool.minPoolSize=5
connection.pool.maxPoolSize=20
connection.pool.maxIdleTime=3000
```

_Step 4:Define DataSource in Spring Configuration_

```Java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.tilmeez.springsecurity.demo")
@PropertySource("classpath:persistence-postgresql.properties")
public class DemoAppConfig {
 ...
   
}
```

+ `@PropertySource` will read the props file src/main/resources files are automatically copied to classpath during Maven build 

```Java
...
public class DemoAppConfig {
  
  @Autowired
  private Environment env;
  
  @Bean
  public DataSource securityDataSource() {
    
    // create connection pool
    
    // set the jdbc driver
    
    // set database connection props
    
    return securityDataSource;
  }
}
```

+ `Environment env;` Will hold data read from properties files 

```JAVA
  @Autowired
  private Environment env;

  private Logger logger = Logger.getLogger(getClass().getName());
  
  @Bean
  public DataSource securityDataSource() {
    
    // create connection pool
    ComboPooledDataSource securityDataSource = new CobmoPooledDataSource();
    
    // set the jdbc driver
    try {
      securityDataSource.setDriverClass(env.getProperty("jdbc.driver"));
    }
    catch (PropertyVetoException exc) {
      throw new RuntimeException(exec);
    }
    
    // for sanity's sake, let's log url and user ... just to make sure  i am reading the data
    logger.info(">>>> jdbc.url=" + env.getProperty("jdbc.url"));
    logger.info(">>>> jdbc.user=" + env.getProperty("jdbc.user"));
    
    // set database connection props
    securityDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
    securityDataSource.setUser(env.getProperty("jdbc.user"));
    securityDataSource.setPassword(env.getProperty("jdbc.password"));
    
    // set connection pool props
    securityDataSource.setInitialPoolSize(Integer.parseInt(env.getProperty("connection.pool.initialPoolSize")));
    securityDataSource.setInitialPoolSize(Integer.parseInt(env.getProperty("connection.pool.minPoolSize")));
    securityDataSource.setInitialPoolSize(Integer.parseInt(env.getProperty("connection.pool.maxPoolSize")));
    securityDataSource.setInitialPoolSize(Integer.parseInt(env.getProperty("connection.pool.maxIdleTime")));
     
    return securityDataSource;
  }
```


_Step 5:Update Spring Security to use JDBC_

```JAVA
@Configuration
@EnableWebSecurity
public class DemoSecurityConfig {
  
  @Autowired
  private DataSource securityDataSource;
  
  @Override
  protected void config(AuthenticationManagerBuilder auth) throws Exception {
    
    auth.jdbcAuthentication().dataSource(securityDataSource);
    
  }
 ... 
}
```
+ `DataSource securityDataSource;` Inject our data source that we configured
+ `auth.jdbcAuthentication()` Tell Spring Security to use JDBC authentication with our data source


  
  
