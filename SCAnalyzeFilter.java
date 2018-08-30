package com.tool.migration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ModuleConfig;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * 1. add below configuration to web.xml<br>
 * 
 * <pre>
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;SCAnalyzeFilter&lt;/filter-name&gt;
 *     &lt;filter-class&gt;com.tool.migration.SCAnalyzeFilter&lt;/filter-class&gt;
 *   &lt;/filter&gt;
 *   &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;SCAnalyzeFilter&lt;/filter-name&gt;
 *     &lt;url-pattern&gt;/SCA&lt;/url-pattern&gt;
 *   &lt;/filter-mapping&gt;
 * </pre>
 * 
 * 2. deploy web application to server<br>
 * 3. run server<br>
 * 4. browse /SCA<br>
 *
 */
public class SCAnalyzeFilter implements Filter {

	public void destroy() {
	}

	public void init(FilterConfig config) throws ServletException {
	}

	@SuppressWarnings("rawtypes")
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
	        throws IOException, ServletException {
		ServletContext sc = ((HttpServletRequest) req).getSession().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(sc);

		// extract action configs
		List<ActionConfig> actionConfigs = new ArrayList<ActionConfig>();
		Enumeration em = sc.getAttributeNames();
		while (em.hasMoreElements()) {
			String name = em.nextElement().toString();
			if (name.startsWith(Globals.MODULE_KEY)) {
				actionConfigs.addAll(Arrays.asList(((ModuleConfig) sc.getAttribute(name)).findActionConfigs()));
			}
		}

		// analyze action
		for (ActionConfig actionConfig : actionConfigs) {
			String path = actionConfig.getPath();
			Object bean = wac.getBean(path);
			try {
				// analyze bean object
				if (bean instanceof jp.terasoluna.fw.web.struts.actions.BLogicAction) {
					Object businessLogic = PropertyUtils.getProperty(bean, "businessLogic");
					// TODO detail conditions
				}
			} catch (Exception e) {
			}
			// TODO output result
			System.out.println(path + "," + bean.getClass().getName());
		}

		((HttpServletResponse) res).setStatus(200);
	}
}
