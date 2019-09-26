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
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CopyProperiesSearcher {

	private static final Charset ENCODING = StandardCharsets.UTF_8;

	public static void main(String[] args) throws Exception {

		for (String arg : args) {
			File inputDir = new File(arg);
			for (File inputFile : FileUtils.listFiles(inputDir, FileFilterUtils.suffixFileFilter(".java"),
			        FileFilterUtils.trueFileFilter())) {
				CompilationUnit cUnit = JavaParser.parse(inputFile, ENCODING);
				Set<String> context = new HashSet<String>();
				cUnit.accept(new VoidVisitorAdapter<Set<String>>() {

					private final String TARGET_METHOD = "copyProperties";
					private final List<String> TARGET_CLASSES = Arrays
					        .asList(new String[] { "BeanUtils", "org.apache.commons.beanutils.BeanUtils" });
					private final Map<String, String> classMap = new HashMap<String, String>();

					private final Map<String, String> fieldMap = new HashMap<String, String>();

					@Override
					public void visit(ImportDeclaration n, Set<String> arg) {
						final String importStatement = n.toString().trim().replaceFirst("import ", "").replace(";", "")
						        .trim();
						final String[] secs = importStatement.split("\\.");
						classMap.put(secs[secs.length - 1], importStatement);
						super.visit(n, arg);
					}

					@Override
					public void visit(MethodCallExpr n, Set<String> arg) {
						if (TARGET_METHOD.equals(n.getNameAsString())
						        && TARGET_CLASSES.contains(n.getScope().get().toString())) {
							String clssName = fieldMap.get(n.getArgument(0).toString().trim());
							if (classMap.containsKey(clssName)) {
								arg.add(classMap.get(clssName));
							} else {
								arg.add(clssName);
							}
						}
						super.visit(n, arg);
					}

					@Override
					public void visit(VariableDeclarationExpr n, Set<String> arg) {
						fieldMap.put(n.getVariable(0).getNameAsString(),
						        n.getElementType().toString().trim().replaceAll("<.+>$", ""));
						super.visit(n, arg);
					}

					@Override
					public void visit(FieldDeclaration n, Set<String> arg) {
						fieldMap.put(n.getVariable(0).getNameAsString(),
						        n.getElementType().toString().trim().replaceAll("<.+>$", ""));
						super.visit(n, arg);
					}

				}, context);

				System.out.println(inputFile.getName() + "=" + context);
			}
		}
	}

}
