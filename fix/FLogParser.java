
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FLogParser {

	static final Pattern FLOG_PTN = Pattern.compile("^\\[(.+?)\\].+: (.+)$");

	static final Pattern TAG_PTN = Pattern.compile("([0-9]+?)=([^ ]+)");

	static final String RESULT_COL_SEP = "\t";

	static final String RESULT_ROW_SEP = "\r\n";

	static final String RESULT_FILE_SUFFIX = "_result.tsv";

	public static void main(String[] args) throws Exception {
		for (String arg : args) {
			parseFile(Paths.get(arg));
		}
	}

	private static void parseFile(Path path) throws IOException {
		outputLog("parse file=" + path);
		Matcher lineMch, tagMch;
		FLogInfo info;
		Set<Integer> keySet = new TreeSet<Integer>();
		List<FLogInfo> infoList = new ArrayList<FLogInfo>();
		int num = 0;
		for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
			num++;
			lineMch = FLOG_PTN.matcher(line);
			if (!lineMch.find()) {
				outputLog("!! WARNNING !! line is an illegal format. num=" + num);
				continue;
			}
			info = new FLogInfo(num, lineMch.group(1), line);
			tagMch = TAG_PTN.matcher(lineMch.group(2));
			while (tagMch.find()) {
				info.addTag(Integer.parseInt(tagMch.group(1)), tagMch.group(2));
			}
			infoList.add(info);
			keySet.addAll(info.getTags().keySet());
		}

		StringBuilder s = new StringBuilder();
		// line number
		for (FLogInfo f : infoList) {
			s.append(RESULT_COL_SEP);
			s.append(f.getNum());
		}
		s.append(RESULT_ROW_SEP);
		// prefix
		for (FLogInfo f : infoList) {
			s.append(RESULT_COL_SEP);
			s.append(f.getPrefix());
		}
		s.append(RESULT_ROW_SEP);
		// tag
		for (int key : keySet) {
			s.append(key);
			for (FLogInfo f : infoList) {
				s.append(RESULT_COL_SEP);
				s.append(defaultString(f.getTags().get(key), ""));
			}
			s.append(RESULT_ROW_SEP);
		}
		Files.writeString(Paths.get(path.toString() + RESULT_FILE_SUFFIX), s, StandardCharsets.UTF_8);
		outputLog("result file=" + Paths.get(path.toString() + RESULT_FILE_SUFFIX));
	}

	private static String defaultString(String v, String d) {
		if (v == null || "".equals(v.trim())) {
			return d;
		} else {
			return v;
		}
	}

	static class FLogInfo {

		int num = -1;
		String prefix = null;
		String log = null;
		Map<Integer, String> tags = new TreeMap<Integer, String>();

		public FLogInfo(int num, String prefix, String log) {
			super();
			this.prefix = prefix;
			this.num = num;
			this.log = log;
		}

		public void addTag(int key, String value) {
			tags.put(key, value);
		}

		public int getNum() {
			return num;
		}

		public String getPrefix() {
			return prefix;
		}

		public String getLog() {
			return log;
		}

		public Map<Integer, String> getTags() {
			return tags;
		}

	}

	static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	private static void outputLog(String msg) {
		System.out.println(SDF.format(new Date()) + "\t" + msg);
	}

}
