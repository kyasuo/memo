package logback;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;

public class IntegerTokenConverterEx extends IntegerTokenConverter {

	// FIXME get value from property file
	private static final int offset = 1;

	@Override
	public String convert(int i) {
		return super.convert(i + offset);
	}

}
