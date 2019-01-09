


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

- html:multibox

<html:multibox property="xxx"><bean:write name="yyy" property="zzz" /></html:multibox>

<input type="checkbox" name="xxx" value="${ yyy.zzz }" <c:if test="${ xxx == yyy.zzz }">checked="checked"</c:if> />

```
