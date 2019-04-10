
servlet 3.1 (https://download.oracle.com/otndocs/jcp/servlet-3_1-fr-spec/index.html)
* 7.1 Session Tracking Mechanisms
* 14.4 Deployment Descriptor Diagram


tomcat
* The request we are responding to asked for a valid session
* The requested session ID was not received via a cookie
* The specified URL points back to somewhere within the web application that is responding to this request

tag
* c:url  UrlSupport#doEndTag -> call HttpServletResponse#encodeURL
* form:form  FormTag#resolveAction -> not call HttpServletResponse#encodeURL
* html:form  FormTag#renderAction -> call HttpServletResponse#encodeURL


