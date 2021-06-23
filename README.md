# [DRAFT] Tanzu Observability - Springboot Tracing

TO - Tanzu Observability

## Pre-requisites

1. Java 11
1. IDE/Codee Editor
1. Maven 3.x

## Just Run

1. Clone this repo

```bash
git clone git@github.com:yogendra/tanzu-observability-springboot-tracing.git springboot-tracing
cd springboot-tracing
```

### V1 - Initial

1. Checkout `initial`

    ```bash
    git checkout initial
    ```

1. Run application
  
    ```bash
    ./mvnw springboot:run
    ```

    **Output**

    ```bash
    ..snip..
    Connect to your Wavefront dashboard using this one-time use link:
    https://wavefront.surf/us/XXXXXXXXX
    ..snip..
    ```

1. Checkout TO Dashboard

### V2 - Fixed Code

1. Update code to fix an error

1. Run application

1. Check TO Dashboard

## Starting from Scratch

1. Create parent project
  
    1. Create a project directory

        ```bash
        mkdir springboot-tracing
        ```

    1. Go to project directory

        ```bash
        cd springboot-tracing
        ```

    1. Initialize Maven wrapper

        ```bash
        mvn -N io.takari:maven:wrapper
        ```

    1. Create a `pom.xml` with following content

        ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
            <modelVersion>4.0.0</modelVersion>
            <parent>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-parent</artifactId>
              <version>2.5.0</version>
              <relativePath/> <!-- lookup parent from repository -->
            </parent>
            <groupId>me.yogendra.samples.springboot</groupId>
            <artifactId>tracing</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <packaging>pom</packaging>

            <modules>
              <module>catalog</module>
              <module>store</module>
            </modules>
        </project>
        ```

1. Create a catalog service

    1. Create project on [Spring Initializer][initializer] ([pre-filled][initializer-catalog])
    1. Download zip file - `catalog.zip`
    1. Extract zip file    
    1. Update application config - `catalog/src/main/resources/application.properties`

        ```ini
        spring.application.name=catalog
        server.port=8083
        wavefront.application.name=console-availability
        management.metrics.export.wavefront.source=dev-workstation
        ```

    1. Update `catalog/src/main/java/com/example/catalog/CatalogApplication.java`

        1. Add `@Slf4j` annotation to class

            ```java
            ...
            import lombok.extern.slf4j.Slf4j;
            ...

            @Slf4j
            @SpringBootApplication
            public class CatalogApplication {
            ...
            ```

        1. Add log message to `main` method

            ```java
            ...
            public static void main(String[] args) {
                log.info("Starting Catalog Service");
            ...
            ```

        1. Add `AvailabilityController` class

            ```java
            ...
            import java.util.Map;
            import java.util.Set;
            import org.springframework.util.Assert;
            import org.springframework.util.StringUtils;
            import org.springframework.web.bind.annotation.GetMapping;
            import org.springframework.web.bind.annotation.PathVariable;
            import org.springframework.web.bind.annotation.RestController;            
            
            ...
            
            @RestController
            class AvailabilityController {

                    private boolean validate(String console) {
                            return StringUtils.hasText(console) &&
                                    Set.of("ps5", "ps4", "switch", "xbox").contains(console);
                    }

                    @GetMapping("/availability/{console}")
                    Map<String, Object> getAvailability(@PathVariable String console) {
                            return Map.of("console", console,
                                            "available", checkAvailability(console));
                    }

                    private boolean checkAvailability(String console) {
                            Assert.state(validate(console), () -> "the console specified, " + console + ", is not valid.");
                            if("ps5".equals(console)){
                                throw new RuntimeException("Service exception");
                            }else if( "xbox".equals(console)){
                                return true;
                            }else{
                                return false;
                            }
                    }
            }

            ```

        1. Run application

            ```bash
            ./mvnw -pl catalog -DskipTests  spring-boot:run
            ```

1. Create a store service

    a. Create project on [Spring Initializer][initializer] ([pre-filled][initializer-store])
    b. Update application config
    c. Update code

1. Run project

    a. On the parent project directory (`springboot-tracing`), run `springboot:run` target

      ```bash
      ./mvnw springboot:run
      ```

1. See Dashboard

## Credits

This is an adaptation from:

- [Spring Tutorial - Metrics and Tracing][spring-tutorial-metrics] ([github][spring-tutorial-metrics-github])
- [Spring Blog - Metrics and Tracing][spring-metric-and-tracing]
- [Spring Guide - Tanzu Observability][spring-guide-to]
- [Tanzu Developer Center - Wavefront for Springboot][tanzu-wf-for-springboot]
- [SpringBoot Tips - The Wavefront Observability Platform][spring-tips-wf]
- [Tanzu Observability - Springboot][wf-springboot]

[spring-tutorial-metrics]: https://spring.io/guides/tutorials/metrics-and-tracing/
[spring-tutorial-metrics-github]: https://github.com/spring-guides/tut-metrics-and-tracing
[spring-guide-to]: https://spring.io/guides/gs/tanzu-observability/
[spring-metric-and-tracing]: https://spring.io/blog/2021/02/09/metrics-and-tracing-better-together
[spring-tips-wf]: https://spring.io/blog/2020/04/29/spring-tips-the-wavefront-observability-platform
[tanzu-wf-for-springboot]: https://tanzu.vmware.com/developer/guides/spring/spring-wavefront-gs/
[wf-springboot]: https://docs.wavefront.com/wavefront_springboot.html
[initializer]: https://start.spring.io
[initializer-catalog]: https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.5.1.RELEASE&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=catalog&name=Catalog&description=Spring%20Boot%20Metrics%20and%20Tracing%20%2F%2F%20Catalog%20Service&packageName=com.example.catalog&dependencies=webflux,actuator,lombok,cloud-starter-sleuth,wavefront,devtools
[initializer-store]: https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.5.1.RELEASE&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=store&name=store&description=Spring%20Boot%20Metrics%20and%20Tracing%20%2F%2F%20Store&packageName=com.example.store&dependencies=webflux,actuator,lombok,cloud-starter-sleuth,wavefront,devtools
