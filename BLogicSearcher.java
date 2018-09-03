public class BLogicSearcher {

	private static final File SRC_DIR = new File("");

	private static final String SRC_ENCODING = "UTF-8";

	private static final Pattern TARGET_IMPORT = Pattern.compile("import jp.terasoluna.fw.service.thin.BLogic;");
	private static final Pattern TARGET_BNAME = Pattern.compile("@Controller\\((.+)\\)");

	public static void main(String[] args) throws IOException {
		Matcher mch;
		String input;
		String blogicName;
		for (File file : FileUtils.listFiles(SRC_DIR, new String[] { "java" }, true)) {
			input = FileUtils.readFileToString(file, SRC_ENCODING);
			mch = TARGET_IMPORT.matcher(input);
			if (!mch.find()) {
				continue;
			}
			mch = TARGET_BNAME.matcher(input);
			if (!mch.find()) {
				continue;
			}
			blogicName = mch.group(1);
			// FIXME search moduleContext
		}
	}

}
