# [DRAFT] Tanzu Observability - Springboot Tracing

TO - Tanzu Observability

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

    a. Create project on [Spring Initializer][initializer] ([pre-filled][initializer-catalog])
    b. Update application config
    c. Update code

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
[initializer-catalog]: https://start.spring.io
[initializer-store]: https://start.spring.io
