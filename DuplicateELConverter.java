import java.io.File;

import org.apache.commons.io.FileUtils;

public class DuplicateELConverter {

	static final File INPUT = new File("");

	static final File OUTPUT = new File("");

	static final String ENCODEING = "UTF-8";

	static final char ESCAPE = '\\';

	static final char[] OPENING = { '$', '{' };

	static final char CLOSING = '}';

	public static void main(String[] args) throws Exception {
		for (File input : FileUtils.listFiles(INPUT, new String[] { "jsp" }, true)) {
			File output = new File(OUTPUT, input.getAbsolutePath().replace(INPUT.getAbsolutePath(), ""));
			convert(input, output);
		}
	}

	private static void convert(File input, File output) throws Exception {
		final StringBuilder result = new StringBuilder();
		final String contents = FileUtils.readFileToString(input, ENCODEING);
		int enclosureCount = 0;
		char prev, current, next;
		for (int index = 0; index < contents.length(); index++) {
			prev = 0 < index ? contents.charAt(index - 1) : 0;
			current = contents.charAt(index);
			next = index < contents.length() - 1 ? contents.charAt(index + 1) : 0;
			if (prev != ESCAPE && current == CLOSING) {
				if (enclosureCount <= 1) {
					result.append(current);
					enclosureCount = 0;
				} else {
					enclosureCount--;
				}
			} else if (prev != ESCAPE && current == OPENING[0] && next == OPENING[1]) {
				if (enclosureCount == 0) {
					result.append(current);
					result.append(next);
				}
				enclosureCount++;
				index++;
			} else {
				result.append(current);
			}
		}
		FileUtils.writeStringToFile(output, result.toString(), ENCODEING);
	}

}
