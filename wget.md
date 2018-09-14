
1. download all library files from maven central repository

```
$ wget -r -l0 -np -nc --user-agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36" http://central.maven.org/maven2/
```

2. deploy to private repository
```
$ mvn deploy:deploy-file ^
  -DrepositoryId=[repo_id(*)] ^
  -Durl=http://[IP address:Port]/repository/[repo name]/ ^
  -Dfile=[jar file path] ^
  -Dsources=[source file path] ^
  -Djavadoc=[javadoc file path] ^
  -DpomFile=[pom file path] 
```
