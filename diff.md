
## How to count

1. adjust target files(ex. java)
```
 $ cd [workdir]
 $ mkdir old new
 $ find [old source directory] -name "*.java" |xargs.exe -i cp -p {} old/
 $ find [new source directory] -name "*.java" |xargs.exe -i cp -p {} new/
```

2. count differences by [stepcounter](https://github.com/takezoe/stepcounter)
```
 $ java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.diffcount.Main -format=excel -output=diff.xls new old
```

## Generate materials infomation

```
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;

public class MaterialsInfoGenerator {

	static final File BASE_DIR = new File("");
	static final File OLD_DIR = new File(BASE_DIR, "");
	static final File NEW_DIR = new File(BASE_DIR, "");

	static final String JAVA_EXTENSION = ".java";
	static final String[] JAVA_TARGETS = { StringUtils.join(new String[] { "src", "main", "java" }, File.separatorChar),
	        StringUtils.join(new String[] { "src", "main", "generated" }, File.separatorChar),
	        StringUtils.join(new String[] { "sources" }, File.separatorChar) };

	static final String JSP_EXTENSION = ".jsp";
	static final String[] JSP_TARGETS = {
	        StringUtils.join(new String[] { "src", "main", "webapp", "WEB-INF", "views" }, File.separatorChar),
	        StringUtils.join(new String[] { "webapps" }, File.separatorChar) };

	static final String PROPERTY_EXTENSION = ".properties";
	static final String[] PROPERTY_TARGETS = {
	        StringUtils.join(new String[] { "src", "main", "resources" }, File.separatorChar),
	        StringUtils.join(new String[] { "sources" }, File.separatorChar) };
	// tld, html, css, js, xml and more...

	public static void main(String[] args) {

		// java
		for (Entry<String, Materials> e : gatherMaterials(BASE_DIR, OLD_DIR, NEW_DIR, JAVA_EXTENSION, JAVA_TARGETS)
		        .entrySet()) {
			System.out.println(e.getKey() + "\t" + StringUtils.join(e.getValue().getOldFiles(), ",") + "\t"
			        + StringUtils.join(e.getValue().getNewFiles(), ","));
		}

		// jsp
		for (Entry<String, Materials> e : gatherMaterials(BASE_DIR, OLD_DIR, NEW_DIR, JSP_EXTENSION, JSP_TARGETS)
		        .entrySet()) {
			System.out.println(e.getKey() + "\t" + StringUtils.join(e.getValue().getOldFiles(), ",") + "\t"
			        + StringUtils.join(e.getValue().getNewFiles(), ","));
		}

		// properties
		for (Entry<String, Materials> e : gatherMaterials(BASE_DIR, OLD_DIR, NEW_DIR, PROPERTY_EXTENSION,
		        PROPERTY_TARGETS).entrySet()) {
			System.out.println(e.getKey() + "\t" + StringUtils.join(e.getValue().getOldFiles(), ",") + "\t"
			        + StringUtils.join(e.getValue().getNewFiles(), ","));
		}
	}

	static Map<String, Materials> gatherMaterials(File baseDir, File oldDir, File newDir, String extension,
	        String[] targets) {
		final Map<String, Materials> materialsMap = new TreeMap<String, Materials>();
		for (File file : FileUtils.listFiles(oldDir, makeFileFileter(extension, targets),
		        FileFilterUtils.trueFileFilter())) {
			String baseName = deriveBaseName(file, targets);
			if (!materialsMap.containsKey(baseName)) {
				materialsMap.put(baseName, new Materials(baseName));
			}
			materialsMap.get(baseName)
			        .addOldFile(file.getAbsolutePath().replace(baseDir.getAbsolutePath() + File.separatorChar, ""));
		}
		for (File file : FileUtils.listFiles(newDir, makeFileFileter(extension, targets),
		        FileFilterUtils.trueFileFilter())) {
			String baseName = deriveBaseName(file, targets);
			if (!materialsMap.containsKey(baseName)) {
				materialsMap.put(baseName, new Materials(baseName));
			}
			materialsMap.get(baseName)
			        .addNewFile(file.getAbsolutePath().replace(baseDir.getAbsolutePath() + File.separatorChar, ""));
		}
		return materialsMap;
	}

	static IOFileFilter makeFileFileter(String extension, String[] targets) {
		return new IOFileFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return accept(new File(dir, name));
			}

			@Override
			public boolean accept(File file) {
				if (!file.getName().endsWith(extension)) {
					return false;
				}
				final String path = file.getAbsolutePath();
				for (String target : targets) {
					if (path.contains(target + File.separatorChar)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	static String deriveBaseName(File file, String[] targets) {
		final String path = file.getAbsolutePath();
		for (String target : targets) {
			final String[] values = StringUtils.splitByWholeSeparator(path, target + File.separatorChar);
			if (values.length == 2) {
				return values[1];
			}
		}
		throw new IllegalStateException("Unexpected file is entried. file=" + file);
	}

	static class Materials {
		private final String baseName;
		private final Set<String> oldFiles = new TreeSet<String>();
		private final Set<String> newFiles = new TreeSet<String>();

		public Materials(String baseName) {
			super();
			this.baseName = baseName;
		}

		public void addOldFile(String file) {
			this.oldFiles.add(file);
		}

		public void addNewFile(String file) {
			this.newFiles.add(file);
		}

		public Set<String> getOldFiles() {
			return oldFiles;
		}

		public Set<String> getNewFiles() {
			return newFiles;
		}

		public String getBaseName() {
			return baseName;
		}
	}

}
```
