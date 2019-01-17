import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestParameterManipulating extends HttpServletRequestWrapper {

	private static final String PARAMETER_KEY = "xxx";

	private final String parameter;

	public RequestParameterManipulating(HttpServletRequest request) {
		super(request);
		if (request.getParameterMap().containsKey(PARAMETER_KEY)) {
			this.parameter = request.getParameter(PARAMETER_KEY);
		} else {
			this.parameter = null;
		}
	}

	@Override
	public String getParameter(String name) {
		final String[] values = this.getParameterValues(name);
		if (values != null) {
			if (values.length == 0)
				return "";
			return values[0];
		} else {
			return null;
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		final Map<String, String[]> parameterMap = super.getParameterMap();
		if (parameter != null) {
			final Map<String, String[]> manipulatingMap = new HashMap<String, String[]>(parameterMap);
			manipulatingMap.put(parameter, new String[] { parameter });
			return manipulatingMap;
		}
		return parameterMap;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(this.getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return this.getParameterMap().get(name);
	}

}
