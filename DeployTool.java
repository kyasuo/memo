import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DeployTool {

	private static final Charset ENCODING = Charset.forName("UTF-8");

	private static final Path CMDFILE = Paths.get("deploy_cmd.txt");

	private static final Path LOGFILE = Paths.get("deployed_cmd.log");

	private static final Path DEFAULT_REPOSITORY = Paths.get("./localRepository");

	private static final String COMMAND = "mvn deploy:deploy-file -DrepositoryId=#{repositoryId} -Durl=#{url}";

	private static final Map<String, String> TARGET_MAPPING = new LinkedHashMap<String, String>();

	static {
		TARGET_MAPPING.put(".pom", "pomFile");
		TARGET_MAPPING.put("-sources.jar", "sources");
		TARGET_MAPPING.put("-javadoc.jar", "javadoc");
		TARGET_MAPPING.put(".jar", "file");
		TARGET_MAPPING.put(".txt", "file");
	}

	public static void main(String[] args) throws Exception {

		// parse arguments
		if (args.length < 2) {
			printUsage();
			System.exit(1);
		}
		final String mvnCmd = COMMAND.replace("#{repositoryId}", args[0]).replace("#{url}", args[1]);
		Path localRepository = DEFAULT_REPOSITORY;
		if (3 <= args.length && !"".equals(args[2].trim())) {
			localRepository = Paths.get(args[2]);
		}

		// make deployedCommandList from logfile
		final List<String> deployedCommandList = readLogfile();

		// search target files and make deploy options
		final Map<String, DeployOption> deployOptionMap = new TreeMap<String, DeployOption>();
		Files.walkFileTree(localRepository, new FileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				final String fileName = path.getFileName().toString();
				String baseName;
				for (String suffix : TARGET_MAPPING.keySet()) {
					if (!fileName.endsWith(suffix)) {
						continue;
					}
					baseName = fileName.replace(suffix, "");
					if (!deployOptionMap.containsKey(baseName)) {
						deployOptionMap.put(baseName, new DeployOption());
					}
					setProperty(deployOptionMap.get(baseName), TARGET_MAPPING.get(suffix),
					        path.toAbsolutePath().toString());
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
		});

		// make list of deploy commands
		final List<String> deployCommandList = new LinkedList<String>();
		for (DeployOption deployInfo : deployOptionMap.values()) {
			String cmd = mvnCmd + " " + deployInfo.toString();
			if (deployedCommandList.contains(cmd)) {
				continue;
			}
			deployCommandList.add(cmd);
			deployedCommandList.add(cmd);
		}

		// output deployCommandList
		if (!Files.exists(CMDFILE)) {
			Files.createFile(CMDFILE);
		}
		Files.write(CMDFILE, deployCommandList, ENCODING, StandardOpenOption.CREATE,
		        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

		// output deployedCommandList to logfile
		Files.write(LOGFILE, deployedCommandList, ENCODING, StandardOpenOption.CREATE,
		        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}

	private static void printUsage() {
		System.err.println("Usage: java DeployTool [repositoryId] [url] [localRepository]");
		System.err.println("   repositoryId    : Server Id to map on the <id> under <server> section of settings.xml.");
		System.err.println("   url             : URL where the artifact will be deployed.");
		System.err.println("   localRepository : Local repository path. Default \"./localRepository\"");
	}

	private static List<String> readLogfile() throws IOException {
		final List<String> deployedCommandList = new LinkedList<String>();
		if (Files.exists(LOGFILE)) {
			deployedCommandList.addAll(Files.readAllLines(LOGFILE, ENCODING));
		}
		return deployedCommandList;
	}

	private static void setProperty(Object obj, String name, String value) {
		try {
			final Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}

	static class DeployOption implements Serializable {
		private static final long serialVersionUID = 1L;
		private String file = null;
		private String sources = null;
		private String javadoc = null;
		private String pomFile = null;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (pomFile != null) {
				sb.append(" -DpomFile=" + pomFile);
			}
			if (file != null) {
				sb.append(" -Dfile=" + file);
			} else if (pomFile != null) {
				sb.append(" -Dfile=" + pomFile);
			}
			if (sources != null) {
				sb.append(" -Dsources=" + sources);
			}
			if (javadoc != null) {
				sb.append(" -Djavadoc=" + javadoc);
			}
			return sb.toString();
		}

		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}

		public String getSources() {
			return sources;
		}

		public void setSources(String sources) {
			this.sources = sources;
		}

		public String getJavadoc() {
			return javadoc;
		}

		public void setJavadoc(String javadoc) {
			this.javadoc = javadoc;
		}

		public String getPomFile() {
			return pomFile;
		}

		public void setPomFile(String pomFile) {
			this.pomFile = pomFile;
		}

	}

}
