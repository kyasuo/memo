import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class TagAnalyzer {

	static final Pattern TAG_PTN = Pattern.compile("([0-9]+?)=(.+)");

	static final File BASE = new File("");

	static final Map<String, String> ENV_MAP = new HashMap<String, String>();
	static {
		ENV_MAP.put("r_t_api_message", "t\tr");
		ENV_MAP.put("f_t_api_message", "t\tf");
		ENV_MAP.put("l_t_api_message", "t\tl");
		ENV_MAP.put("r_b_api_message", "b\tr");
		ENV_MAP.put("f_b_api_message", "b\tf");
		ENV_MAP.put("l_b_api_message", "b\tl");
	}

	public static void main(String[] args) throws IOException {

		Map<String, Map<String, Set<String>>> resultMap = new TreeMap<String, Map<String, Set<String>>>();
		String env;
		Matcher mch;
		for (File file : FileUtils.listFiles(BASE, new String[] { "log" }, true)) {
			env = null;
			for (String key : ENV_MAP.keySet()) {
				if (file.getName().contains(key)) {
					env = ENV_MAP.get(key);
					break;
				}
			}
			if (env == null) {
				continue;
			}
			if (!resultMap.containsKey(env)) {
				resultMap.put(env, new TreeMap<String, Set<String>>());
			}

			Map<String, String> lineMap = new TreeMap<String, String>();
			for (String line : FileUtils.readLines(file, "UTF-8")) {
				if (!line.contains("=")) {
					continue;
				}
				mch = TAG_PTN.matcher(line);
				while (mch.find()) {
					lineMap.put(mch.group(1).trim(), mch.group(2).trim());
				}
			}
			String msg = lineMap.get("35");
			if (!resultMap.get(env).containsKey(msg)) {
				resultMap.get(env).put(msg, new TreeSet<String>());
			}
			resultMap.get(env).get(msg).add(StringUtils.join(lineMap.keySet(), "\t"));
		}

		List<String> olines = new ArrayList<String>();
		for (Entry<String, Map<String, Set<String>>> e : resultMap.entrySet()) {
			for (Entry<String, Set<String>> s : e.getValue().entrySet()) {
				for (String v : s.getValue()) {
					olines.add(e.getKey() + "\t" + s.getKey() + "\t" + v + "\r\n");
				}
			}
		}
		System.out.println(olines);
	}

}
