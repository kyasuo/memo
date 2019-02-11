import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class TagAttributeValidator {

	static final String INPUT = "";

	// jsp, tag
	static final String TARGET_REGEX = "^.+\\.(jsp|tag)$";

	static final String ERROR_WHITESPACE = "The JSP/HTML specification requires that an attribute name is preceded by whitespace.";

	static final String ERROR_DUPLICATED = "Attribute qualified names must be unique within an element.";

	static final Charset ENCODEING = StandardCharsets.UTF_8;

	static final char OPENING = '<';

	static final char CLOSING = '>';

	static final char SOLIDUS = '/';

	static final char CUSTOM = ':';

	static final char EQUAL = '=';

	static final char ESCAPE = '\\';

	static final char SINGLE_QUOTE = '\'';

	static final char DOUBLE_QUOTE = '"';

	static final String UPPER_LOWER = "^[a-zA-Z]{1}$";

	// ASCII whitespace - space(0x20), tab(0x09), cr(0x0d), lf(0x0a), ff(0x0c)
	static final List<Character> ATTR_SEPARATORS = Arrays.asList(new Character[] { ' ', '\t', '\r', '\n', '\f' });

	static final Pattern ATTR_NAME = Pattern.compile("[a-zA-Z]+");

	public static void main(String[] args) throws IOException {
		Files.walkFileTree(Paths.get(INPUT), new JspHtmlFileVisitor());
	}

	private static class JspHtmlFileVisitor implements FileVisitor<Path> {

		class OpeningTag implements Serializable {
			private static final long serialVersionUID = 1L;
			private final String name;
			private final int start;
			private int end;
			private String tagString;

			public OpeningTag(String name, int start) {
				super();
				this.name = name;
				this.start = start;
			}

			public int getEnd() {
				return end;
			}

			public void setEnd(int end) {
				this.end = end;
			}

			public int getStart() {
				return start;
			}

			public String getTagString() {
				return tagString;
			}

			public void setTagString(String tagString) {
				this.tagString = tagString;
			}

			public String getName() {
				return name;
			}
		}

		class AttributeProperties extends Properties {

			private static final long serialVersionUID = 1L;
			private final String fileName;
			private final String tagString;

			public AttributeProperties(String fileName, String tagString) {
				super();
				this.fileName = fileName;
				this.tagString = tagString;
			}

			@Override
			public synchronized Object put(Object key, Object value) {
				if (containsKey(key)) {
					error(ERROR_DUPLICATED, fileName, tagString);
				}
				return super.put(key, value);
			}

		}

		private void error(String message, String fileName, String tagStr) {
			System.err.println(message + "\t" + fileName + "\t" + tagStr.replaceAll("(\r\n|\n)", " "));
		}

		private void validate(Path input) throws IOException {
			final Deque<OpeningTag> stack = new LinkedList<OpeningTag>();
			final List<OpeningTag> openingTags = new ArrayList<OpeningTag>();
			final String contents = new String(Files.readAllBytes(input), ENCODEING);
			char current, next, forward;
			OpeningTag openingTag;
			// parse opening tag
			for (int index = 0; index < contents.length(); index++) {
				current = contents.charAt(index);
				next = index < contents.length() - 1 ? contents.charAt(index + 1) : 0;
				if (current == OPENING && String.valueOf(next).matches(UPPER_LOWER)) {
					String name = null;
					for (int fIndex = index + 2; fIndex < contents.length(); fIndex++) {
						forward = contents.charAt(fIndex);
						if (forward == SOLIDUS || forward == CLOSING) {
							name = contents.substring(index + 1, fIndex);
							break;
						} else if (ATTR_SEPARATORS.contains(forward)) {
							name = contents.substring(index + 1, fIndex + 1);
							break;
						}
					}
					stack.push(new OpeningTag(name, index));
				} else if (0 < stack.size() && current == CLOSING) {
					openingTag = stack.pop();
					openingTag.setEnd(index);
					openingTag.setTagString(contents.substring(openingTag.getStart(), openingTag.getEnd() + 1));
					openingTags.add(openingTag);
				}
			}
			// parse tag attributes
			String attrStr;
			final StringBuilder editedAttrStr = new StringBuilder();
			for (OpeningTag tag : openingTags) {
				char prev, enclosure = 0;
				boolean enclosured = false;
				attrStr = tag.getTagString().replaceFirst("^" + OPENING + tag.getName(), "")
				        .replaceFirst(SOLIDUS + "{0,1}" + CLOSING + "$", "");
				for (int index = 0; index < attrStr.length(); index++) {
					prev = 0 < index ? attrStr.charAt(index - 1) : 0;
					current = attrStr.charAt(index);
					next = index < attrStr.length() - 1 ? attrStr.charAt(index + 1) : 0;
					if (enclosured) {
						if (prev != ESCAPE && current == enclosure) {
							if (!ATTR_SEPARATORS.contains(next) && next != 0) {
								error(ERROR_WHITESPACE, input.getFileName().toString(), tag.getTagString());
							}
							enclosure = 0;
							enclosured = false;
						}
						editedAttrStr.append(current);
					} else {
						if (prev != ESCAPE && current == SINGLE_QUOTE) {
							enclosure = SINGLE_QUOTE;
							enclosured = true;
							editedAttrStr.append(current);
						} else if (prev != ESCAPE && current == DOUBLE_QUOTE) {
							enclosure = DOUBLE_QUOTE;
							enclosured = true;
							editedAttrStr.append(current);
						} else if (ATTR_SEPARATORS.contains(current)) {
							editedAttrStr.append("\n");
						} else {
							editedAttrStr.append(current);
						}
					}
				}
				final AttributeProperties attrProperties = new AttributeProperties(input.getFileName().toString(),
				        tag.getTagString());
				attrProperties.load(new StringReader(editedAttrStr.toString()));
				editedAttrStr.delete(0, editedAttrStr.length());
			}
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (file.getFileName().toString().matches(TARGET_REGEX)) {
				validate(file);
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

	}

}
