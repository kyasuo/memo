import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UrlPathHelper;

@Controller
public class CommonController {

	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

	private static final String DISPATCH_KEY = CommonController.class.getName() + ".DISPATCH_KEY";

	private static final String EVENT_PARAMETER_NAME = "event";

	private static final String FORWARD_PREFIX = "forward_";

	private final UrlPathHelper urlPathHelper = new UrlPathHelper();

	@RequestMapping(value = "/{module:(?!^resources$)^.+$}/**")
	public String dispatch(HttpServletRequest request) throws Exception {

		final String lookupPath = urlPathHelper.getLookupPathForRequest(request);

		if (request.getAttribute(DISPATCH_KEY) != null) {
			request.removeAttribute(DISPATCH_KEY);
			throw new InvalidServletRequestException("RequestMapping is duplicately dispatched.");
		}
		request.setAttribute(DISPATCH_KEY, true);

		final String event = request.getParameter(EVENT_PARAMETER_NAME);
		if (event != null && event.startsWith(FORWARD_PREFIX)) {
			final String forward = "forward:" + lookupPath + "?" + event;
			logger.info("The event is selected as forward key. [forward:" + forward + "]");
			return forward;
		}

		for (String parameterName : request.getParameterMap().keySet()) {
			if (parameterName.startsWith(FORWARD_PREFIX)) {
				final String forward = "forward:" + lookupPath + "?" + EVENT_PARAMETER_NAME + "=" + parameterName;
				logger.info("The forward parameter is selected as forward key. [forward:" + forward + "]");
				return forward;
			}
		}

		throw new InvalidServletRequestException("The forward key cannot be detected within request parameters.");
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	private class InvalidServletRequestException extends ServletRequestBindingException {

		private static final long serialVersionUID = 4315383556680467907L;

		public InvalidServletRequestException(String msg) {
			super(msg);
		}

	}

}
