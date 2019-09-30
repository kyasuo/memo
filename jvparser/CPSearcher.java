
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
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CPSearcher {

	private static final Charset ENCODING = StandardCharsets.UTF_8;

	private static final File RESULT = new File("C:/tmp/result.txt");

	public static class Context {
		private final Set<String> typeSet = new HashSet<String>();
		private final File inputFile;

		public Context(File inputFile) {
			super();
			this.inputFile = inputFile;
		}

		public void addType(String type) {
			this.typeSet.add(type);
		}

		public Set<String> getTypeSet() {
			return typeSet;
		}

		public File getInputFile() {
			return inputFile;
		}

	}

	public static void main(String[] args) throws Exception {
		final Set<String> lines = new HashSet<String>();
		for (String arg : args) {
			File inputDir = new File(arg);
			for (File inputFile : FileUtils.listFiles(inputDir, FileFilterUtils.suffixFileFilter(".java"),
			        FileFilterUtils.trueFileFilter())) {
				CompilationUnit cUnit = JavaParser.parse(inputFile, ENCODING);
				Context context = new Context(inputFile);
				cUnit.accept(new VoidVisitorAdapter<Context>() {

					private final String TARGET_METHOD = "copyProperties";
					private final List<String> TARGET_CLASSES = Arrays
					        .asList(new String[] { "BeanUtils", "org.apache.commons.beanutils.BeanUtils" });

					private final List<String> JAVA_LANG_CLASSES = Arrays.asList(new String[] { "Boolean", "Byte",
					        "Character", "Class", "ClassLoader", "ClassValue", "Compiler", "Double", "Enum", "Float",
					        "InheritableThreadLocal", "Integer", "Long", "Math", "Number", "Object", "Package",
					        "Process", "ProcessBuilder", "ProcessBuilder", "Runtime", "RuntimePermission",
					        "SecurityManager", "Short", "StackTraceElement", "StrictMath", "String", "StringBuffer",
					        "StringBuilder", "System", "Thread", "ThreadGroup", "ThreadLocal", "Throwable", "Void" });

					private final Map<String, String> classMap = new HashMap<String, String>();

					private final Map<String, String> fieldMap = new HashMap<String, String>();

					private String packageName;

					@Override
					public void visit(PackageDeclaration n, Context arg) {
						this.packageName = n.getNameAsString().trim();
						super.visit(n, arg);
					}

					@Override
					public void visit(ImportDeclaration n, Context arg) {
						final String importStatement = n.toString().trim().replaceFirst("import ", "").replace(";", "")
						        .trim();
						final String[] secs = importStatement.split("\\.");
						classMap.put(secs[secs.length - 1], importStatement);
						super.visit(n, arg);
					}

					@Override
					public void visit(MethodCallExpr n, Context arg) {
						if (TARGET_METHOD.equals(n.getNameAsString())
						        && TARGET_CLASSES.contains(n.getScope().get().toString())) {
							Expression e = n.getArgument(0);
							if (e.isNameExpr()) {
								String fieldName = e.toString().trim();
								if (fieldMap.containsKey(fieldName)) {
									String clssName = fieldMap.get(fieldName);
									if (classMap.containsKey(clssName)) {
										arg.addType(classMap.get(clssName));
									} else {
										if (JAVA_LANG_CLASSES.contains(clssName)) {
											arg.addType("java.lang." + clssName);
										} else if (packageName != null && !"".equals(packageName)) {
											arg.addType(packageName + "." + clssName);
										} else {
											arg.addType(clssName);
										}
									}
								} else {
									System.err.println("[" + arg.getInputFile()
									        + "] couldn't determine type of argument because it is not included in fieldMap. argument = "
									        + e);
								}
							} else {
								System.err.println("[" + arg.getInputFile()
								        + "] couldn't determine type of argument because it is not name expression. argument = "
								        + e);
							}
						}
						super.visit(n, arg);
					}

					@Override
					public void visit(VariableDeclarationExpr n, Context arg) {
						fieldMap.put(n.getVariable(0).getNameAsString(),
						        n.getElementType().toString().trim().replaceAll("<.+>$", ""));
						super.visit(n, arg);
					}

					@Override
					public void visit(FieldDeclaration n, Context arg) {
						fieldMap.put(n.getVariable(0).getNameAsString(),
						        n.getElementType().toString().trim().replaceAll("<.+>$", ""));
						super.visit(n, arg);
					}

					@Override
					public void visit(ForEachStmt n, Context arg) {
						VariableDeclarationExpr v = n.getVariable();
						fieldMap.put(v.getVariable(0).getNameAsString(),
						        v.getElementType().toString().trim().replaceAll("<.+>$", ""));
						super.visit(n, arg);
					}

					@Override
					public void visit(MethodDeclaration n, Context arg) {
						for (Parameter p : n.getParameters()) {
							fieldMap.put(p.getNameAsString(), p.getTypeAsString().trim().replaceAll("<.+>$", ""));
						}
						super.visit(n, arg);
					}

				}, context);

				for (String line : context.getTypeSet()) {
					lines.add(inputFile.getAbsolutePath() + "," + inputFile.getName() + "," + line);
				}
			}
		}
		FileUtils.writeLines(RESULT, lines);
	}

}
