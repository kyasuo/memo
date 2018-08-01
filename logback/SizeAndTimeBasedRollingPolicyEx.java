package logback;

import java.lang.reflect.Field;
import java.util.Map;

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;

@SuppressWarnings("unchecked")
public class SizeAndTimeBasedRollingPolicyEx<E> extends SizeAndTimeBasedRollingPolicy<E> {

	static {
		try {
			final Field field = FileNamePattern.class.getDeclaredField("CONVERTER_MAP");
			field.setAccessible(true);
			final Map<String, String> converterMap = (Map<String, String>) field.get(null);
			converterMap.put(IntegerTokenConverter.CONVERTER_KEY, IntegerTokenConverterEx.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
