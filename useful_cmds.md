
## list of file line terminator

```
 $ find . -type f |grep -v \.gitkeep |xargs file |sed -e '/with CRLF line terminators$/s/:.\+/\tCRLF/' |sed -e '/CRLF$/!s/:.\+/\tLF/'
```

## regex

```
-- new byte array
new\s+byte\s*\[[^\]]+?\]

-- Map#keySet
\.\s*keySet\s*\(\s*\)

-- Map#entrySet
\.\s*entrySet\s*\(\s*\)

-- new HashSet
new\s+HashSet

```
