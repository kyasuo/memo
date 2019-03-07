import java.io.IOException;
import java.io.Serializable;
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
import java.util.regex.Pattern;

public class TagValidator {

	static final String INPUT = "";

	// jsp, tag, html
	static final String TARGET_REGEX = "^.+\\.(jsp|tag|html)$";

	static final Charset ENCODEING = StandardCharsets.UTF_8;

	static final char OPENING = '<';

	static final char CLOSING = '>';

	static final char SOLIDUS = '/';

	static final char CUSTOM = ':';

	static final char ESCAPE = '\\';

	static final char SINGLE_QUOTE = '\'';

	static final char DOUBLE_QUOTE = '"';

	static final String UPPER_LOWER = "^[a-zA-Z]{1}$";

	// ASCII whitespace - space(0x20), tab(0x09), cr(0x0d), lf(0x0a), ff(0x0c)
	static final List<Character> ATTR_SEPARATORS = Arrays.asList(new Character[] { ' ', '\t', '\r', '\n', '\f' });

	static final Pattern ATTR_NAME = Pattern.compile("[a-zA-Z]+");

	public static void main(String[] args) throws IOException {
		JspHtmlFileVisitor visitor = new JspHtmlFileVisitor();
		Files.walkFileTree(Paths.get(INPUT), visitor);
		for (String line : visitor.getLogs()) {
			System.out.println(line);
		}
	}

	private static class JspHtmlFileVisitor implements FileVisitor<Path> {

		private final List<String> logs = new ArrayList<String>();

		public List<String> getLogs() {
			return logs;
		}

		enum TAGTYPE {
			BEGIN, END;
		}

		class Tag implements Serializable {

			private static final long serialVersionUID = 1L;
			private final String name;
			private final int start;
			private final TAGTYPE tagType;
			private int end;
			private String tagString;

			public Tag(String name, int start, TAGTYPE tagType) {
				super();
				this.name = name;
				this.start = start;
				this.tagType = tagType;
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

			public TAGTYPE getTagType() {
				return tagType;
			}
		}

		private void validate(Path input) throws IOException {
			final Deque<Tag> stack = new LinkedList<Tag>();
			final String contents = new String(Files.readAllBytes(input), ENCODEING);
			char current, next, forward;
			Tag tag;
			// parse opening tag
			for (int index = 0; index < contents.length(); index++) {
				current = contents.charAt(index);
				next = index < contents.length() - 1 ? contents.charAt(index + 1) : 0;
				forward = index < contents.length() - 2 ? contents.charAt(index + 2) : 0;
				if (current == OPENING) {
					int offset;
					TAGTYPE tagType;
					if (String.valueOf(next).matches(UPPER_LOWER)) {
						offset = 2;
						tagType = TAGTYPE.BEGIN;
					} else if (next == SOLIDUS && String.valueOf(forward).matches(UPPER_LOWER)) {
						offset = 3;
						tagType = TAGTYPE.END;
					} else {
						continue;
					}
					String name = null;
					for (int fIndex = index + offset; fIndex < contents.length(); fIndex++) {
						forward = contents.charAt(fIndex);
						if (forward == SOLIDUS || forward == CLOSING) {
							name = contents.substring(index + 1, fIndex);
							break;
						} else if (ATTR_SEPARATORS.contains(forward)) {
							name = contents.substring(index + 1, fIndex + 1);
							break;
						}
					}
					if (name != null) {
						stack.push(new Tag(name, index, tagType));
					}
				} else if (0 < stack.size() && current == CLOSING) {
					tag = stack.pop();
					tag.setEnd(index);
					tag.setTagString(contents.substring(tag.getStart(), tag.getEnd() + 1));
					// TODO check tag
					if (tag.getTagType() == TAGTYPE.BEGIN && tag.getTagString().endsWith("/>")) {
						logs.add(input.getFileName().toString() + "\t" + tag.getName() + "\t" + tag.getTagString());
					}
				}
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
