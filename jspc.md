
#### preparation

``` mvn dependency:copy-dependencies -DoutputDirectory=src/main/webapp/WEB-INF/lib```


#### pom 
reveiew tldJarNamePatterns

```
  <build>
    <plugins>
      <plugin>
        <groupId>org.eclipse.jetty</groupId>
        <artifactId>jetty-jspc-maven-plugin</artifactId>
        <version>9.4.11.v20180605</version>
        <executions>
          <execution>
            <id>jspc</id>
            <goals>
              <goal>jspc</goal>
            </goals>
            <configuration>
              <sourceVersion>1.8</sourceVersion>
              <targetVersion>1.8</targetVersion>
              <keepSources>true</keepSources>
              <tldJarNamePatterns>spring-security-taglibs-*.jar|tiles-jsp-*.jar|terasoluna-gfw-web-jsp-*.jar|.*taglibs[^/]*\.jar|.*jstl-impl[^/]*\.jar$</tldJarNamePatterns>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
  ```
 
#### note

* Apache Tomcat Versions(http://tomcat.apache.org/whichversion.html)

* JSP specification 2.3(https://download.oracle.com/otndocs/jcp/jsp-2_3-mrel2-spec/)
  tld JSP.7.3.2
