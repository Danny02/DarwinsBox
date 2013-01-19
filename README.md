Darwins Box
===========

My little toolbox which I use for developing game related stuff.
The goal is to build a wide range of utility and framework code which is needed for 3D - OpenGL Applications. I also try to use good software engineering practices.

Some stuff:
- general model writer reader framework
- little shader "scripting"/"usage" framework
- 3D Math libary
- a lot of OpenGL utility 
- JOpenCTM
- perfect :) resource managment framework
- resource compilation/build tools

stuff to come(ideas)
- webGl crosscompiler(based on GWT)


**latest release on maven-central:**

    <dependency>
        <groupId>com.github.danny02</groupId>
        <artifactId>DarwinsBox</artifactId>
        <version>2.1</version>
    </dependency>        
        
**To use the latest snapshot build, use this in your pom:**

        <dependency>
            <groupId>com.github.danny02.darwin</groupId>
            <artifactId>DarwinsBox</artifactId>
            <version>2.2-SNAPSHOT</version>
        </dependency>
        
and also this repository:
        
        <repository>
                <id>sonatype-nexus-snapshots</id>
                <name>Sonatype Nexus Snapshots</name>
                <url>https://oss.sonatype.org/content/repositories/snapshots</url>
                <snapshots>
                        <enabled>true</enabled>
                </snapshots>
        </repository>




