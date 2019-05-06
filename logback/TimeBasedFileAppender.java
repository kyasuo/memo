import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.core.FileAppender;

/**
 * TimeBasedFileAppender appends log events to a file.<br/>
 * 
 * The file path can be including date and time. [ex. %ld{yyyyMMdd}]
 * <p>【Configuration(logback.xml)】</p>
 * <pre>
 *  &lt;property name="LOG_DIR" value="/var/log/app/test" /&gt;
 * 
 *  &lt;appender name="timebasedfilelog" class="<b>TimeBasedFileAppender</b>"&gt;
 *      <b>&lt;baseTime&gt;0400&lt;/baseTime&gt;</b>
 *      &lt;file&gt;${LOG_DIR}/<b>%ld{yyyMMdd}</b>/test.log&lt;/file&gt;
 *      &lt;encoder&gt;
 *          &lt;charset&gt;UTF-8&lt;/charset&gt;
 *          &lt;pattern&gt;&lt;![CDATA[date:%d{yyyy-MM-dd HH:mm:ss}\tthread:%thread\tlevel:%-5level\tlogger:%-48logger{48}\tmessage:%msg%n]]&gt;&lt;/pattern&gt;
 *      &lt;/encoder&gt;
 *  &lt;/appender&gt;
 * </pre>
 */
public class TimeBasedFileAppender<E> extends FileAppender<E> {

	private static final DateTimeFormatter BASETIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");

	private static final Pattern DATE_PATTERN = Pattern.compile("%ld\\{([^\\{\\}]+?)\\}");

	private LocalDateTime baseDateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

	public void setBaseTime(String baseTime) {
		LocalTime base;
		if (baseTime == null) {
			base = LocalTime.of(0, 0);
		} else {
			base = LocalTime.parse(baseTime, BASETIME_FORMAT);
		}
		final LocalDateTime localDateTime = LocalDateTime.now();
		if (localDateTime.toLocalTime().isBefore(base)) {
			this.baseDateTime = localDateTime.plusDays(-1).withHour(base.getHour()).withMinute(base.getMinute())
			        .withSecond(0).withNano(0);
		}
	}

	@Override
	public void setFile(String file) {
		final Matcher matcher = DATE_PATTERN.matcher(file);
		final StringBuffer buffer = new StringBuffer(file.length());
		while (matcher.find()) {
			matcher.appendReplacement(buffer, this.baseDateTime.format(DateTimeFormatter.ofPattern(matcher.group(1))));
		}
		matcher.appendTail(buffer);
		super.setFile(buffer.toString());
	}

}
