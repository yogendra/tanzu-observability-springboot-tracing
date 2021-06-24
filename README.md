# Tanzu Observability - Springboot Tracing

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


1. Run catalog service
  
    ```bash
    ./mvnw -pl catalog clean spring-boot:run
    ```

    **Output**

    ```bash
    ..snip..
    Connect to your Wavefront dashboard using this one-time use link:
    https://wavefront.surf/us/XXXXXXXXX
    ..snip..
    ```

    Get the dashboard link from output and open in browser.

1. Open another terminal and run Store Service

    ```bash
    ./mvnw -pl store clean spring-boot:run
    ```

1. On TO dashboard observe the application

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

    1. Create project on [Spring Initializer][initializer] ([pre-filled][initializer-catalog]) and download the zip (`catalog.zip`). Extract this archive in the project folder

    1. Update application config - `catalog/src/main/resources/application.properties`

        ```ini
        spring.application.name=catalog
        server.port=8083
        wavefront.application.name=console-availability
        management.metrics.export.wavefront.source=dev-workstation
        ```

    1. Update `catalog/src/main/java/com/example/catalog/CatalogApplication.java`

        1. Add logging annotation to class

            ```java
            ...
            import lombok.extern.slf4j.Slf4j;            

            @Slf4j
            @SpringBootApplication
            public class CatalogApplication {
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

    1. Create project on [Spring Initializer][initializer] ([pre-filled][initializer-store])
    1. Update application config - `store/src/main/resources/application.properties`
    1. Update `store/src/main/java/com/example/store/StoreApplication.java`

        1. Add logging to `StoreApplication` class

            ```java
            ...
            import lombok.extern.slf4j.Slf4j;
            ...

            
            @Slf4j
            @SpringBootApplication
            public class StoreApplication {
                public static void main(String[] args) {
                log.info("Starting Store Service");
            ...
            ```

        1. Add `WebClient` bean creation logic

            ```java
            import org.springframework.context.annotation.Bean;
            import org.springframework.web.reactive.function.client.WebClient;
            ...
            @Bean
            WebClient webClient(WebClient.Builder builder) {
                return builder.build();
            }
            ```

        1. Add `Availability` and `AvailabilityClient` class

            ```java
            import reactor.core.publisher.Flux;
            import reactor.core.publisher.Mono;
            import lombok.Data;
            import lombok.AllArgsConstructor;
            import lombok.NoArgsConstructor;
            import lombok.RequiredArgsConstructor;
            import org.springframework.stereotype.Component;

            ...
            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            class Availability {
                private boolean available;
                private String console;
            }
            

            @Component
            @RequiredArgsConstructor
            class AvailabilityClient {

                private final WebClient webClient;
                private static final String URI = "http://localhost:8083/availability/{console}";

                Mono<Availability> checkAvailability(String console) {
                    return this.webClient
                            .get()
                            .uri(URI, console)
                            .retrieve()
                            .bodyToMono(Availability.class)
                            .onErrorReturn(new Availability(false, console));
                }

            }
            ```

        1. Add a post start handler to run console availability check. 

            ```java
            import java.time.Duration;
            import org.springframework.boot.context.event.ApplicationReadyEvent;
            import org.springframework.context.ApplicationListener;
            import reactor.core.publisher.Flux;
            ...
            @Bean
            ApplicationListener<ApplicationReadyEvent> ready(AvailabilityClient client) {
                return applicationReadyEvent -> {
                    for (var console : "ps5,xbox,ps4,switch".split(",")) {
                        Flux.range(0, Integer.MAX_VALUE).delayElements(Duration.ofMillis(100)).subscribe(i ->
                                client
                                        .checkAvailability(console)
                                        .subscribe(availability ->
                                                log.info("console: {}, availability: {} ", console, availability.isAvailable())));
                    }
                };
            }
            ```

    1. On a new terminal, run application

        ```bash
        ./mvnw -pl catalog -DskipTests  spring-boot:run
        ```

        **Output**

        ```bash
        ....
        Your existing Wavefront account information has been restored from disk.

        To share this account, make sure the following is added to your configuration:

                management.metrics.export.wavefront.api-token=xxxxxxxx-xxxx-xxxxx-xxxx-xxxxxxxxxxxx
                management.metrics.export.wavefront.uri=https://wavefront.surf

        Connect to your Wavefront dashboard using this one-time use link:
        https://wavefront.surf/us/XXXXXXXXXXX
        ....
        ```

1. Open dashboard using the link from earlier step.


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
