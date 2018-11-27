


#### memo
```


${prefix:function('<xxx xx="xx" yy="yy" />')}
${prefix:function('<xxx xx="xx" yy="yy" onclick=\'alert("message")\' />')}

<c:set var="param" value="${prefix:function(xxxx)}" />
${prefix:function('<xxx xx="xx" yy="yy" zz="${param}" />')}

- version
tomcat-jsp-api
tomcat-embed-el

- Not use
@Email
@Size
