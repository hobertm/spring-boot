

# SRING BOOT  

- springboot构建  

添加依赖包  

spring-boot-starter-web: MVC,AOP的依赖包....  

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.4.1.RELEASE</version>
    </parent>

    <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <!-- 指定一下jdk的版本 ，这里我们使用jdk 1.8 ,默认是1.6 -->
    <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!-- 
            spring-boot-starter-web: MVC,AOP的依赖包....
         -->
        <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
                <!-- 
                    <version></version>
                    由于我们在上面指定了 parent(spring boot)
                 -->
        </dependency>   
    </dependencies> 
```


App.java

```java
//在这里我们使用@SpringBootApplication指定这是一个 spring boot的应用程序
@SpringBootApplication
public class App{
    public static void main(String[] args) {
        /*
         * 在main方法进行启动我们的应用程序.
         */
        SpringApplication.run(App.class, args);
    }
}
```


- spring-boot-devtools   

使用方法  
配置到pom.xml，分别配置依赖包和插件  

```xml
        <!-- spring boot devtools 依赖包. -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
           <scope>true</scope>
        </dependency>
        <!-- 这是spring boot devtool plugin -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <!--fork :  如果没有该项配置，肯呢个devtools不会起作用，即应用不会restart -->
                <fork>true</fork>
            </configuration>
        </plugin>
```

- 集成mybatis  

```xml
        <!-- mysql 数据库驱动. -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>   
        
        <!--    
            spring-boot mybatis依赖：
            请不要使用1.0.0版本，因为还不支持拦截器插件，
         -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.1.1</version>
        </dependency>
        
        
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper</artifactId>
            <version>4.1.0</version>
        </dependency>   

```

分页使用PageHelper  
使用@Configuration配置，进行分页配置  

```java
@Configuration
public class MyBatisConfiguration {
    
    @Bean
    public PageHelper pageHelper() {
        System.out.println("MyBatisConfiguration.pageHelper()");
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        pageHelper.setProperties(p);
        return pageHelper;
    }
}
```

编写启动类  
启动类配置扫描包  @MapperScan("com.kfit.*")  

```java
@SpringBootApplication
@MapperScan("com.kfit.*")//扫描：该包下相应的class,主要是MyBatis的持久化类.
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

application.properties  
springboot自动加载的配置文件  

```
########################################################
###datasource -- mysql的数据库配置.
########################################################
spring.datasource.url = jdbc:mysql://localhost:3306/test
spring.datasource.username = root
spring.datasource.password = root
spring.datasource.driverClassName = com.mysql.jdbc.Driver
spring.datasource.max-active=20
spring.datasource.max-idle=8
spring.datasource.min-idle=8
spring.datasource.initial-size=10
```


DemoMappper  
@Options配置主键信息，配置主键自增  

```java
public interface DemoMappper {
    
    //#{name}:参数占位符
    @Select("select *from Demo where name=#{name}")
    public List<Demo> likeName(String name);

    @Select("select *from Demo where id = #{id}")
    public Demo getById(long id);
    
    @Select("select name from Demo where id = #{id}")
    public String getNameById(long id);
    /**
     * 保存数据.
     */
    @Insert("insert into Demo(name) values(#{name})")
    @Options(useGeneratedKeys=true,keyProperty="id",keyColumn="id")
    public void save(Demo demo);
    
}
```

DemoService  

```java
@Service
public class DemoService {

    @Autowired
    private DemoMappper demoMappper;
    
    public List<Demo> likeName(String name){
        return demoMappper.likeName(name);
    }
    
    @Transactional//添加事务.
    public void save(Demo demo){
        demoMappper.save(demo);
    }
    
}
```

DemoController  

```java
    @RestController
public class DemoController {
    
    @Autowired
    private DemoService demoService;
    
    @RequestMapping("/likeName")
    public List<Demo> likeName(String name){
        /*
         * 第一个参数：第几页;
         * 第二个参数：每页获取的条数.
         */
        PageHelper.startPage(1, 2);
        return demoService.likeName(name);
    }
    
    @RequestMapping("/save")
    public Demo save(){
        Demo demo = new Demo();
        demo.setName("张三");
        demoService.save(demo);
        return demo;
    }
    
}
```

- euraka  

server端  

```java
package com.itmuch.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {
  public static void main(String[] args) {
    SpringApplication.run(EurekaApplication.class, args);
  }
}

```

server端配置  

eureka.client.registerWithEureka: 值为false意味着自身仅作为服务器，不作为客户端
eureka.client.fetchRegistry: 值为false意味着无需注册自身
eureka.client.serviceUrl.defaultZone: 指明了应用的URL

```yaml
security:
  basic:
    enabled: true
  user:
    name: user
    password: password123
server:
  port: 8761
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://user:password123@localhost:8761/eureka
```

microservice-consumer-movie-ribbon  
com/itmuch/cloud/controller/MovieController.java  

```java
package com.itmuch.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.itmuch.cloud.entity.User;

@RestController
public class MovieController {
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private LoadBalancerClient loadBalancerClient;

  @GetMapping("/movie/{id}")
  public User findById(@PathVariable Long id) {
    // http://localhost:7900/simple/
    // VIP virtual IP
    // HAProxy Heartbeat
    return this.restTemplate.getForObject("http://microservice-provider-user/simple/" + id, User.class);
  }

  @GetMapping("/test")
  public String test() {
    ServiceInstance serviceInstance = this.loadBalancerClient.choose("microservice-provider-user");
    System.out.println("111" + ":" + serviceInstance.getServiceId() + ":" + serviceInstance.getHost() + ":" + serviceInstance.getPort());

    ServiceInstance serviceInstance2 = this.loadBalancerClient.choose("microservice-provider-user2");
    System.out.println("222" + ":" + serviceInstance2.getServiceId() + ":" + serviceInstance2.getHost() + ":" + serviceInstance2.getPort());

    return "1";
  }
}


```
  
ConsumerMovieRibbonApplication  
启动类设置不扫描的注解ExcludeFromComponentScan.class  
@RibbonClient,设置microservice-provider-user这个服务使用随机访问配置  


```java
package com.itmuch.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "microservice-provider-user", configuration = TestConfiguration.class)
@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeFromComponentScan.class) })
public class ConsumerMovieRibbonApplication {

  @Bean
  @LoadBalanced
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public static void main(String[] args) {
    SpringApplication.run(ConsumerMovieRibbonApplication.class, args);
  }
}

```

TestConfiguration  
@ExcludeFromComponentScan防止启动时默认配置被覆盖  

```java
package com.itmuch.cloud;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;

@Configuration
@ExcludeFromComponentScan
public class TestConfiguration {
  //  @Autowired
  //  IClientConfig config;

  @Bean
  public IRule ribbonRule() {
    return new RandomRule();
  }
}

```




- config server  

microservice-config-client-refresh  ConfigClientController  
@RefreshScope支持刷新配置  
@Value("${profile}")获取配置  

```java
package com.itmuch.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigClientController {

  @Value("${profile}")
  private String profile;

  @GetMapping("/profile")
  public String getProfile() {
    return this.profile;
  }
}


```

microservice-config-client-refresh  bootstrap.yml

```yaml
spring:
  cloud:
    config:
      uri: http://localhost:8080
      profile: dev
      label: master   # 当configserver的后端存储是Git时，默认就是master 
  application:
    name: foobar
```


- turbine hystrix  

microservice-hystrix-turbine  

```java
package com.itmuch.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableTurbine
@SpringBootApplication
public class TurbineApplication {
  public static void main(String[] args) {
    SpringApplication.run(TurbineApplication.class, args);
  }
}

```

appConfig配置监控的项目  

```yaml
server:
  port: 8031
spring:
  application:
    name: microservice-hystrix-turbine
eureka:
  client:
    serviceUrl:
      defaultZone: http://user:password123@localhost:8761/eureka
  instance:
    prefer-ip-address: true
turbine:
  aggregator:
    clusterConfig: default
  appConfig: microservice-consumer-movie-ribbon-with-hystrix,microservice-consumer-movie-feign-with-hystrix
  clusterNameExpression: "'default'"
```


microservice-consumer-movie-ribbon-with-hystrix  
@HystrixCommand(fallbackMethod = "findByIdFallback")

```java

package com.itmuch.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.itmuch.cloud.entity.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class MovieController {
  @Autowired
  private RestTemplate restTemplate;

  @GetMapping("/movie/{id}")
  @HystrixCommand(fallbackMethod = "findByIdFallback")
  public User findById(@PathVariable Long id) {
    return this.restTemplate.getForObject("http://microservice-provider-user/simple/" + id, User.class);
  }

  public User findByIdFallback(Long id) {
    User user = new User();
    user.setId(0L);
    return user;
  }
}

```


@EnableCircuitBreaker配置短路器  

```java
package com.itmuch.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class ConsumerMovieRibbonApplication {

  @Bean
  @LoadBalanced
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public static void main(String[] args) {
    SpringApplication.run(ConsumerMovieRibbonApplication.class, args);
  }
}

```