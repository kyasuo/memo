
## list of file line terminator

```
 $ find . -type f |grep -v \.gitkeep |xargs file |sed -e '/with CRLF line terminators$/s/:.\+/\tCRLF/' |sed -e '/with CRLF line terminators$/!s/:.\+/\tLF/'
```

