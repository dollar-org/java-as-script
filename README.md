Build Status: [![Circle CI](https://circleci.com/gh/sillelien/java-as-script.png?style=badge)](https://circleci.com/gh/sillelien/java-as-script)
[ ![Download](https://api.bintray.com/packages/sillelien/maven/java-as-script/images/download.svg) ](https://bintray.com/sillelien/maven/java-as-script/_latestVersion)

Java-as-Script [![Alpha](https://img.shields.io/badge/Status-Alpha-yellowgreen.svg?style=flat)](http://github.com/sillelien/java-as-script)
==============
Java-as-Script provides a hot reloading JSR-223 implementation for Java. This version is a fork of [the original project](https://github.com/jmarranz/relproxy) specifically it has been reduced in scope to focus entirely on the JSR-223 aspect of the original project. It is also primarily been forked for use in the [Dollar project](https://github.com/sillelien/dollar).

```xml
    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>bintray-sillelien-maven</id>
            <name>bintray</name>
            <url>http://dl.bintray.com/sillelien/maven</url>
        </repository>
    </repositories>
```            

```xml
    <dependency>
        <groupId>com.sillelien</groupId>
        <artifactId>java-as-script</artifactId>
        <version>0.9.120</version>
    </dependency>
```


Dependencies: [![Dependency Status](https://www.versioneye.com/user/projects/5960064c6725bd0049735d0b/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5960064c6725bd0049735d0b)

-------

** If you use this project please consider giving us a star on [GitHub](http://github.com/sillelien/java-as-script). **

Please contact me through Gitter (chat) or through GitHub Issues.

[![GitHub Issues](https://img.shields.io/github/issues/sillelien/java-as-script.svg)](https://github.com/sillelien/java-as-script/issues) [![Join the chat at https://gitter.im/sillelien/java-as-script](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sillelien/java-as-script?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

For commercial support please <a href="mailto:hello@neilellis.me">contact me directly</a>.
-------

Java-as-Script is a JSR 223 [Java Scripting API](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html) implementation for "Java" as the target scripting language. You can embed and execute Java code as scripting into your Java program. In case of Java "scripting", there is no a new language, is pure Java code with compilation on the fly.
