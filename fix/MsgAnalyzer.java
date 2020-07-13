public class MsgAnalyzer {

	static final SimpleDateFormat LTM = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
	static final SimpleDateFormat UTM = new SimpleDateFormat("yyyyMMdd-hh:mm:ss.SSS");

	static final Pattern LOG_PTN = Pattern.compile(
			"^\\[([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})\\].+[^0-9]*132=([0-9\\.]+).+[^0-9]*133=([0-9\\\\.]+).+[^0-9]*60=(([0-9]{8}-[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}))");

	static final Pattern CURRENCY_PTN = Pattern.compile("15=([A-Z]{3})");

	public static void main(String[] args) {
		for (String arg : args) {
			File file = new File(arg);
			if (!file.exists()) {
				System.out.println(file + " is not found.");
				continue;
			}
			parse(file);
		}
	}

	static void parse(File input) {
		Map<String, List<String>> result = new TreeMap<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(input));
			String line, currency;
			Matcher mch;
			while ((line = br.readLine()) != null) {
				if (line.indexOf("35=S") <= 0) {
					continue;
				}
				mch = CURRENCY_PTN.matcher(line);
				if (!mch.find()) {
					continue;
				}
				currency = mch.group(1);
				mch = LOG_PTN.matcher(line);
				if (!mch.find()) {
					continue;
				}
				if (!result.containsKey(currency)) {
					result.put(currency, new ArrayList<>());
				}
				result.get(currency)
						.add(UTM.parse(mch.group(4)).getTime() + "\t" + mch.group(4) + "\t"
								+ LTM.parse(mch.group(1)).getTime() + "\t" + mch.group(1) + "\t" + mch.group(2) + "\t"
								+ mch.group(3));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		BufferedWriter bw = null;
		for (String key : result.keySet()) {
			File output = new File(input.getParent(), input.getName() + "_" + key + ".tsv");
			try {
				bw = new BufferedWriter(new FileWriter(output));
				for (String line : result.get(key)) {
					bw.write(line);
					bw.write("\r\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}
