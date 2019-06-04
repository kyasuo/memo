
### pluginManagement
```
                <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-assembly-plugin</artifactId>
                  <version>${maven-assembly-plugin.version}</version>
                </plugin>
```
### plugin
```
            <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-assembly-plugin</artifactId>
              <configuration>
                <descriptors>
                  <descriptor>src/main/assembly/executable.xml</descriptor>
                </descriptors>
              </configuration>
              <executions>
                <execution>
                  <phase>package</phase>
                  <goals>
                    <goal>single</goal>
                  </goals>
                </execution>
              </executions>
            </plugin>
```
### descriptor
```
<assembly xmlns="http://maven.apache.org/ASSEMBLY/2.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/ASSEMBLY/2.0.0 http://maven.apache.org/xsd/assembly-2.0.0.xsd">
  <id>release</id>
  <formats>
    <format>tar.gz</format>
  </formats>
<!--   <includeBaseDirectory>false</includeBaseDirectory>  -->
  <fileSets>
    <fileSet>
        <directory>src/main/resources</directory>
        <outputDirectory>classes</outputDirectory>
        <includes>
            <include>*.properties</include>
        </includes>
    </fileSet>
    <fileSet>
        <directory>src/main/sh</directory>
        <outputDirectory>bin</outputDirectory>
        <includes>
            <include>*.sh</include>
            <include>*.conf</include>
        </includes>
    </fileSet>
  </fileSets>
  <dependencySets>
    <dependencySet>
      <unpack>false</unpack>
      <scope>runtime</scope>
      <outputDirectory>lib</outputDirectory>
    </dependencySet>
  </dependencySets>
</assembly>
```
### snapshots
```

  <activeProfiles>
    <activeProfile>nexus</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>nexus</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>releases</id>
          <url>http://[NEXUS]:8081/repository/xxx-releases/</url>
          <layout>default</layout>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
        <repository>
          <id>snapshots</id>
          <url>http://[NEXUS]:8081/repository/xxx-snapshots/</url>
          <layout>default</layout>
            <releases>
              <enabled>false</enabled>
            </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
```

