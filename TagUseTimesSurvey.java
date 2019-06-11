import java.io.IOException;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

public class TagUseTimesSurvey {

	static final String INPUT = "/xxx/xxx/xxx";
	static final String OUTPUT = INPUT + "\\result.csv";
	static final List<String> TARGET_TAGNAMES = Arrays.asList(new String[] { "xxx:xxx", "yyy:yyy" });

	static final String TARGET_REGEX = "^.+\\.(jsp|tag)$";
	static final Charset ENCODEING = StandardCharsets.UTF_8;

	public static void main(String[] args) throws IOException {
		JspFileVisitor visitor = new JspFileVisitor(TARGET_TAGNAMES);
		Files.walkFileTree(Paths.get(INPUT), visitor);

		List<String> lines = new ArrayList<String>();
		lines.add(String.format("%s,%s,%s,%s", new Object[] { "tagname", "use times", "file count", "files" }));
		for (TagUseTimesSurvey.JspFileVisitor.UseTimes value : visitor.getResult().values()) {
			lines.add(String.format("%s,%d,%d,%s", new Object[] { value.getTagName(), value.getHit(),
			        value.getFiles().size(), StringUtils.collectionToDelimitedString(value.getFiles(), "|") }));
		}
		Files.write(Paths.get(OUTPUT), lines, ENCODEING);
	}

	private static class JspFileVisitor implements FileVisitor<Path> {

		private final Map<Pattern, UseTimes> result = new LinkedHashMap<Pattern, UseTimes>();

		public JspFileVisitor(List<String> tags) {
			for (String tag : tags) {
				this.result.put(Pattern.compile("<" + tag), new UseTimes(tag));
			}
		}

		private void parseJsp(Path file) throws IOException {
			final String fileContents = FileUtils.readFileToString(file.toFile(), ENCODEING);
			Matcher mch;
			for (Pattern tag : this.result.keySet()) {
				int count = 0;
				mch = tag.matcher(fileContents);
				while (mch.find()) {
					count++;
				}
				if (0 < count) {
					this.result.get(tag).addHit(count);
					this.result.get(tag).addFile(file.getFileName().toString());
				}
			}
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (file.getFileName().toString().matches(TARGET_REGEX)) {
				parseJsp(file);
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

		public class UseTimes {
			private final String tagName;
			private long hit = 0;
			private final List<String> files = new ArrayList<String>();

			public UseTimes(String tagName) {
				this.tagName = tagName;
			}

			public long getHit() {
				return hit;
			}

			public void addHit(int hit) {
				this.hit += hit;
			}

			public String getTagName() {
				return tagName;
			}

			public List<String> getFiles() {
				return files;
			}

			public void addFile(String file) {
				this.files.add(file);
			}

		}

		public Map<Pattern, UseTimes> getResult() {
			return result;
		}
	}

}
