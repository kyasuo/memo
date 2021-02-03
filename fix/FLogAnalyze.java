import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FLogAnalyze {

	static final Path BASE = Paths.get("");
	static final Pattern TARGET = Pattern.compile("^.+log$");
	static final Pattern FLOG_PTN = Pattern.compile("^.+: (.+)$");
	static final Pattern TAG_PTN = Pattern.compile("([0-9]+?)=([^ ]+)");
	static final String KEYWORD = "XX=YY";
	static final Map<String, Path> RESULT = new TreeMap<String, Path>();

	public static void main(String[] args) {

		try {
			Files.walkFileTree(BASE, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String name = file.toFile().getName();
					if (!TARGET.matcher(name).find()) {
						return FileVisitResult.CONTINUE;
					}
					Matcher lineMch, tagMch;
					String key;
					Set<Integer> res = new TreeSet<Integer>();
					for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
						lineMch = FLOG_PTN.matcher(line);
						if (!lineMch.find() || !line.contains(KEYWORD)) {
							continue;
						}
						res.clear();
						String pfix = "";
						tagMch = TAG_PTN.matcher(lineMch.group(1));
						while (tagMch.find()) {
							key = tagMch.group(1).trim();
							if ("NN".equals(key)) {
								pfix = tagMch.group(2).trim();
							}
							res.add(Integer.parseInt(key));
						}
						pfix = pfix + "," + res.toString().replace(" ", "").replace("[", "").replace("]", "");
						if (!RESULT.containsKey(pfix)) {
							RESULT.put(pfix, file);
						}
					}
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String key : RESULT.keySet()) {
			System.out.println(key + "\t" + RESULT.get(key));
		}
	}

}
