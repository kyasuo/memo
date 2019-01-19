


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

<html:multibox property="xxx.yyy.zzz" styleId='<%= "xxx_yyy_zzz_" + index) %>'>
    <bean:write name="ccc" property="vvv" />
</html:multibox>
<label for='<%= "xxx_yyy_zzz_" + index %>'>
    <bean:write name="ccc" property="mmm" />
</label>

<c:set var="multibox">
    <c:out value="${ ccc.vvv }" />
</c:set>
<form:checkbox path="xxx.yyy.zzz" id='${ "xxx_yyy_zzz_".concat( index ) }' value="${ multibox }"/>
<label for='${ "xxx_yyy_zzz_".concat( index ) }'>
    <c:out value="${ ccc.mmm }" />
</label>

```
