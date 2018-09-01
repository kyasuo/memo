import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class ClassSearcher {

	static final File TARGET_DIRECTORY = new File("");

	static final String[] TARGET_EXTENSIONS = { "java", "xml" };

	static final String FILE_ENCODING = "UTF-8";

	static final String[] PACKAGE_PREFIXES = { "jp", "org", "com", "java", "javax" };

	static final Pattern CLASS_PATTERN = Pattern
	        .compile("(((" + StringUtils.join(PACKAGE_PREFIXES, "|") + ")\\.([\\w]+\\.){1,})([A-Z][\\w]+))");

	public static void main(String[] args) throws Exception {

		Matcher mch;
		Set<String> classNameSet = new TreeSet<String>();
		for (File file : FileUtils.listFiles(TARGET_DIRECTORY, TARGET_EXTENSIONS, true)) {
			String input = FileUtils.readFileToString(file, FILE_ENCODING);
			mch = CLASS_PATTERN.matcher(input);
			while (mch.find()) {
				classNameSet.add(mch.group());
			}
		}
		System.out.println(StringUtils.join(classNameSet.toArray(), "\r\n"));
	}

}
