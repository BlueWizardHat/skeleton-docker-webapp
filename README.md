Skeleton SpringBoot webapp with dockerized devtest
========================================================


## Kotlin and Java

The project is configured so that both java and kotlin sources are compiled and lombok annotations are supported for java source. Also Kotlin source is included in the source jar.

Java files should be in the standard locations

* src/main/java
* src/test/java

And Kotlin sources should be in

* src/main/kotlin
* src/test/kotlin

Within a module Kotlin code will be able to reference Java code - including lombok generated code. However Java code will not be able to reference Kotlin code.

However between modules Kotlin and Java code may reference Java or Kotlin code from another module.

