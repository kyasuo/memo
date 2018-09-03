public class BLogicIOFieldSearcher {

	private static final File SRC_DIR = new File("");

	private static final String SRC_ENCODING = "UTF-8";

	private static final Pattern TARGET_FIELD = Pattern
	        .compile("@BLogicIO\\(.+(Request|Session).+\\).+?private ([^ ]+) ([^ =;]+)", Pattern.DOTALL);

	public static void main(String[] args) throws IOException {
		Matcher mch;
		Set<String> result = new TreeSet<String>();
		for (File file : FileUtils.listFiles(SRC_DIR, new String[] { "java" }, true)) {
			mch = TARGET_FIELD.matcher(FileUtils.readFileToString(file, SRC_ENCODING));
			while (mch.find()) {
				result.add(mch.group(1) + "\t" + mch.group(2) + "\t" + mch.group(3));
			}
		}
		System.out.println(StringUtils.join(result.toArray(), "\r\n"));
	}

}
