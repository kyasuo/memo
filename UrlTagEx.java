import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.Param;
import org.springframework.web.servlet.tags.UrlTag;

public class UrlTagEx extends UrlTag {

	private static final long serialVersionUID = 1L;

	public static final String RANDOM_ID_KEY = "r";

	private final java.util.Random RANDOM = new java.util.Random();

	private String generateRandomID() {
		long r = RANDOM.nextLong();
		long t = System.currentTimeMillis();
		return String.valueOf(Math.abs(r + t));
	}

	@Override
	public int doStartTagInternal() throws JspException {
		int result = super.doStartTagInternal();

		// add random id into parameters
		Param param = new Param();
		param.setName(RANDOM_ID_KEY);
		param.setValue(generateRandomID());
		super.addParam(param);

		return result;
	}

}
