package explorer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class BLogicIOExplorer {

	private static final File SRC_DIR = new File("xxxx");
	// private static final Charset SRC_ENCODING = StandardCharsets.UTF_8;
	private static final Charset SRC_ENCODING = Charset.forName("MS932");

	private static final String SEPARATOR = "\t";

	private static final Pattern BLC_PATTERN = Pattern.compile("implements BLogic<(.+?)>", Pattern.DOTALL);
	private static final Pattern RSL_PATTERN = Pattern.compile("\\.setResultObject\\(([^\\)]+?)\\)", Pattern.DOTALL);

	public static void main(String[] args) throws IOException {
		Matcher blcMatcher;
		Matcher rslMatcher;
		Matcher outMatcher;
		final Set<String> output = new HashSet<String>();
		log(new Object[] { "blogic", "input", "count of setResultObject", "output" });
		for (final File file : FileUtils.listFiles(SRC_DIR, new String[] { "java" }, true)) {
			final String src = FileUtils.readFileToString(file, SRC_ENCODING).replaceAll("(\r\n|\n)", "")
					.replaceAll(" {2,}", " ");
			blcMatcher = BLC_PATTERN.matcher(src);
			if (!blcMatcher.find()) {
				continue;
			}
			final String input = blcMatcher.group(1);
			int count = 0;
			rslMatcher = RSL_PATTERN.matcher(src);
			while (rslMatcher.find()) {
				count++;
				outMatcher = getOutputPattern(rslMatcher.group(1)).matcher(src);
				if (outMatcher.find()) {
					output.add(outMatcher.group(1));
				}
			}
			log(new Object[] { getFullyQualifiedName(file.getAbsolutePath()), input, count, output });
			output.clear();
		}
	}

	private static void log(Object[] params) {
		System.out.println(StringUtils.join(params, SEPARATOR));
	}

	private static Pattern getOutputPattern(String propName) {
		return Pattern.compile("([^ ]+?) " + propName, Pattern.DOTALL);
	}

	private static String getFullyQualifiedName(String filePath) {
		return filePath.replace(SRC_DIR.getAbsolutePath(), "").replace(File.separator, ".").replaceFirst("^\\.", "")
				.replaceAll(".java", "");
	}

}
