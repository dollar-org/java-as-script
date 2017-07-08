${HEADER}


Java-as-Script ${STATE_ALPHA}
==============
Java-as-Script provides a hot reloading JSR-223 implementation for Java. This version is a fork of [the original project](https://github.com/jmarranz/relproxy) (see the Historical Section below) specifically it has been reduced in scope to focus entirely on the JSR-223 aspect of the original project. It is also primarily been forked for use in the [Dollar project](https://github.com/sillelien/dollar).


```xml
<dependency>
    <groupId>com.sillelien</groupId>
    <artifactId>java-as-script</artifactId>
    <version>${RELEASE}</version>
    <type>jar</type>
</dependency>
```


Dependencies: [![Dependency Status](https://www.versioneye.com/user/projects/5960064c6725bd0049735d0b/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5960064c6725bd0049735d0b)

${BLURB}

Java-as-Script is a JSR 223 [Java Scripting API](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html) implementation for "Java" as the target scripting language. You can embed and execute Java code as scripting into your Java program. In case of Java "scripting", there is no a new language, is pure Java code with compilation on the fly.
