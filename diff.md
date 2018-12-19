
## How to count

1. adjust target files(ex. java)
```
 $ cd [workdir]
 $ mkdir old new
 $ find [old source directory] -name "*.java" |xargs.exe -i cp -p {} old/
 $ find [new source directory] -name "*.java" |xargs.exe -i cp -p {} new/
```

2. count differences by [stepcounter](https://github.com/takezoe/stepcounter)
```
 $ java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.diffcount.Main -format=excel -output=diff.xls new old
```
