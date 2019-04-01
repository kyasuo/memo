import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * <p>
 * HttpHeadersMoniteringInterceptor
 * </p>
 * 
 * <p>
 * Configuration:
 * </p>
 * 
 * <pre>
 *   &lt;beans xmlns="http://www.springframework.org/schema/beans"
 *     <b>xmlns:cxf="http://cxf.apache.org/core"</b>
 *     xsi:schemaLocation="
 *        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
 *        <b>http://cxf.apache.org/core http://cxf.apache.org/schemas/core.xsd</b>
 *     "&gt;
 *
 *     <b>&lt;bean id="httpHeadersMoniteringInterceptor" class="HttpHeadersMoniteringInterceptor"/&gt;</b>
 *     <b>
 *     &lt;cxf:bus&gt;
 *         &lt;cxf:inInterceptors&gt;
 *             &lt;ref bean="httpHeadersMoniteringInterceptor"/&gt;
 *         &lt;/cxf:inInterceptors&gt;
 *         &lt;cxf:outInterceptors&gt;
 *             &lt;ref bean="httpHeadersMoniteringInterceptor"/&gt;
 *        &lt;/cxf:outInterceptors&gt;
 *     &lt;/cxf:bus&gt;
 *     </b>
 *   &lt;/beans&gt;
 * </pre>
 * 
 */
public class HttpHeadersMoniteringInterceptor extends AbstractPhaseInterceptor<Message> {

	public HttpHeadersMoniteringInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handleMessage(Message message) throws Fault {
		Map<String, List<String>> headers = CastUtils.cast((Map) message.get(Message.PROTOCOL_HEADERS));
		log("-- HTTP HEADERS --");
		for (Entry<String, List<String>> header : headers.entrySet()) {
			log("  " + header.getKey() + "=" + header.getValue() + "");
		}
		log("------------------");
	}

	private void log(String msg) {
		System.out.println(msg);
	}

}
