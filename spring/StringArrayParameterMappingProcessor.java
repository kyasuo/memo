import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/**
 * StringArrayParameterMappingProcessor
 * 
 * <p>configuration:</p>
 * <pre>
 *   &lt;mvc:annotation-driven&gt;
 *     &lt;mvc:argument-resolvers&gt;
 *       &lt;bean class="StringArrayParameterMappingProcessor"&gt;
 *         &lt;constructor-arg name="annotationNotRequired" value="true"/&gt;
 *       &lt;/bean&gt;
 *     &lt;/mvc:argument-resolvers&gt;
 *   &lt;/mvc:annotation-driven&gt;
 * </pre>
 *
 */
public class StringArrayParameterMappingProcessor extends ServletModelAttributeMethodProcessor {

	public StringArrayParameterMappingProcessor(boolean annotationNotRequired) {
		super(annotationNotRequired);
	}

	@Override
	protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {

		super.bindRequestParameters(binder, request);

		final Map<String, String[]> parameterMap = request.getParameterMap();
		if (binder.getTarget() == null || parameterMap.isEmpty()) {
			return;
		}

		final MutablePropertyValues propertyValues = new MutablePropertyValues();
		String name;
		String[] values;
		for (Entry<String, String[]> param : parameterMap.entrySet()) {
			name = param.getKey();
			values = param.getValue();
			if (values == null || values.length <= 1) {
				continue;
			}
			// TODO verify target field type (do nothing if it is string array)

			propertyValues.add(name, values[0]);
		}
		binder.bind(propertyValues);
	}

}
