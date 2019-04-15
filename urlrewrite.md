
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

firewall

1. change filter-name in web.xml    
ex) springSecurityFilterChain -> springSecurityFilterChainEx

        <filter>
            <filter-name>springSecurityFilterChainEx</filter-name>
            <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>springSecurityFilterChainEx</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>

1. add extended FilterChainProxy and StrictHttpFirewall    

        import java.io.IOException;
        
        import javax.servlet.FilterChain;
        import javax.servlet.ServletException;
        import javax.servlet.ServletRequest;
        import javax.servlet.ServletResponse;
        
        import org.springframework.security.web.FilterChainProxy;
        import org.springframework.security.web.firewall.HttpFirewall;
        import org.springframework.web.filter.GenericFilterBean;
        
        public class FilterChainProxyEx extends GenericFilterBean {
        
            private final FilterChainProxy filterChainProxy;
        
            public FilterChainProxyEx(FilterChainProxy filterChainProxy, HttpFirewall firewall) {
                super();
                this.filterChainProxy = filterChainProxy;
                this.filterChainProxy.setFirewall(firewall);
            }
        
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                this.filterChainProxy.doFilter(request, response, chain);
            }
        
        }
    `
    
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletRequestWrapper;
        
        import org.springframework.security.web.firewall.FirewalledRequest;
        import org.springframework.security.web.firewall.RequestRejectedException;
        import org.springframework.security.web.firewall.StrictHttpFirewall;
        
        public class StrictHttpFirewallEx extends StrictHttpFirewall {
        
            static final String REMOVE_REQUESTED_SESSIONID = StrictHttpFirewallEx.class.getName().concat(".APPLIED");
        
            private String sessionIdPrefix = ";jsessionid=";
        
            public void setSessionIdPrefix(String sessionIdPrefix) {
                this.sessionIdPrefix = sessionIdPrefix;
            }
        
            @Override
            public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {
                request.setAttribute(REMOVE_REQUESTED_SESSIONID, true);
                FirewalledRequest req = super.getFirewalledRequest(new RequestWarpper(request));
                request.removeAttribute(REMOVE_REQUESTED_SESSIONID);
                return req;
            }
        
            class RequestWarpper extends HttpServletRequestWrapper {
        
                public RequestWarpper(HttpServletRequest request) {
                    super(request);
                }
        
                @Override
                public String getRequestURI() {
                    String uri = super.getRequestURI();
                    String requestedSessionId = super.getRequestedSessionId();
                    if (super.getAttribute(REMOVE_REQUESTED_SESSIONID) != null && requestedSessionId != null) {
                        return uri.replace(sessionIdPrefix + requestedSessionId, "");
                    } else {
                        return uri;
                    }
                }
        
            }
        
        }


1. configure these beans definition

        <bean id="springSecurityFilterChainEx" class="FilterChainProxyEx">
            <constructor-arg ref="springSecurityFilterChain" />
            <constructor-arg><bean class="StrictHttpFirewallEx"/></constructor-arg>
        </bean>


