
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class AFSearcher {

	private static final Charset ENCODING = StandardCharsets.UTF_8;

	private static final File BASE_DIR = new File("C:/tmp/base");

	private static final File INPUT = new File("C:/tmp/input.txt");

	private static final File RESULT = new File("C:/tmp/output.txt");

	private static final Map<String, File> CLASS_MAP = new HashMap<String, File>();

	public static void main(String[] args) throws Exception {

		for (File file : FileUtils.listFiles(BASE_DIR, FileFilterUtils.suffixFileFilter(".java"),
		        FileFilterUtils.trueFileFilter())) {
			CompilationUnit cUnit = JavaParser.parse(file, ENCODING);
			cUnit.accept(new VoidVisitorAdapter<Object>() {
				@Override
				public void visit(PackageDeclaration n, Object arg) {
					CLASS_MAP.put(n.getNameAsString().trim() + "." + file.getName().replace(".java", ""), file);
					super.visit(n, arg);
				}
			}, null);
			if (!CLASS_MAP.containsValue(file)) {
				CLASS_MAP.put(file.getName().replace(".java", ""), file);
			}
		}

		final Set<String> lines = new HashSet<String>();
		for (String input : FileUtils.readLines(INPUT, ENCODING)) {
			if (!CLASS_MAP.containsKey(input)) {
				System.err.println("[" + input + "] couldn't find resource file.");
				continue;
			}
			CompilationUnit cUnit = JavaParser.parse(CLASS_MAP.get(input), ENCODING);
			cUnit.accept(new VoidVisitorAdapter<Object>() {
				private final List<String> wrapperClass = Arrays.asList(
				        new String[] { "Boolean", "Byte", "Character", "Double", "Float", "Integer", "Long", "Short",
				                "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Double",
				                "java.lang.Float", "java.lang.Integer", "java.lang.Long", "java.lang.Short" });

				@Override
				public void visit(FieldDeclaration n, Object arg) {
					Type elm = n.getElementType();
					if (!n.isStatic() && !n.isFinal()) {
						String type = elm.toString().trim();
						if (wrapperClass.contains(type)) {
							lines.add(input + "\tW\t" + n);
						}
						type = elm.getParentNode().get().toString().trim();
						if (type.contains("[") && type.endsWith("]")) {
							lines.add(input + "\tA\t" + n);
						}
					}
					super.visit(n, arg);
				}

			}, null);

		}
		FileUtils.writeLines(RESULT, lines);
	}

}
