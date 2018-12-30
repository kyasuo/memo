
## ibatis
* com.ibatis.sqlmap.engine.accessplan.PropertyAccessPlan#setProperties

## jdbc

* sqlMapConfig.xml
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE sqlMapConfig PUBLIC "-//ibatis.apache.org//DTD SQL Map Config 2.0//EN"
 "http://ibatis.apache.org/dtd/sql-map-config-2.dtd">
<sqlMapConfig>
  <transactionManager type="JDBC">
    <dataSource type="SIMPLE">
      <property name="JDBC.Driver" value="org.postgresql.Driver" />
      <property name="JDBC.ConnectionURL" value="jdbc:postgresql://127.0.0.1:5432/xxxx" />
      <property name="JDBC.Username" value="xxxx" />
      <property name="JDBC.Password" value="xxxx" />
    </dataSource>
  </transactionManager>

  <sqlMap resource="sqlMap.xml" />
</sqlMapConfig>
```

* sqlMap.xml
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE sqlMap PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN"
 "http://ibatis.apache.org/dtd/sql-map-2.dtd">
    
<sqlMap>

<select id="xxxx" parameterClass="xxxx" resultMap="xxxx">
    SELECT
        xxx
    FROM xxxx
</select>

</sqlMap>
```

* java
```
	private static SqlMapClient sqlMap;
	static {
		try {
			Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SqlMapClient getSqlMapInstance() {
		return sqlMap;
	}

	public static void main(String[] args) throws SQLException {
		SqlMapClient sqlMap = getSqlMapInstance();
		sqlMap.queryForList("xxxx", input);
	}
```


## reflection
```
		for(Field field : FieldUtils.getAllFields(input.getClass())) {
			if (!Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				paramMap.put(field.getName(), field.get(input));
			}
		}
```

## binding 

|TYPE | SQL | JAVA | RESULT |
|-----|-----|------|--------|
|I/O | l | l | OK |
|I/O | l | u | OK |
|I/O | u | l | __NG__ |
|I/O | u | u | OK |
|ATTR | l | l | OK |
|ATTR | l | u | __NG__ |
|ATTR | u | l | __NG__ |
|ATTR | u | u | OK |

```
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.ibatis.sqlmap.engine.builder.xml.SqlMapParser;
import com.ibatis.sqlmap.engine.builder.xml.XmlParserState;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMapping;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMapping;
import com.ibatis.sqlmap.engine.mapping.sql.dynamic.DynamicSql;
import com.ibatis.sqlmap.engine.mapping.sql.stat.StaticSql;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.mapping.statement.StatementType;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FieldBindingChecker {

	static final Map<StatementType, String> TYPEMAP = new HashMap<StatementType, String>();
	static {
		TYPEMAP.put(StatementType.UNKNOWN, "UNKNOWN");
		TYPEMAP.put(StatementType.INSERT, "INSERT");
		TYPEMAP.put(StatementType.UPDATE, "UPDATE");
		TYPEMAP.put(StatementType.DELETE, "DELETE");
		TYPEMAP.put(StatementType.SELECT, "SELECT");
		TYPEMAP.put(StatementType.PROCEDURE, "PROCEDURE");
	}

	public static void main(String[] args) throws Exception {

		final List<String> lines = new ArrayList<String>();
		lines.add(format("FILE", "SQLID", "TYPE", "BINDING", "PROPERTY", "DTO"));
		final Enumeration<URL> enumration = FieldBindingChecker.class.getClassLoader().getResources(".");
		File dir;
		while (enumration.hasMoreElements()) {
			dir = new File(enumration.nextElement().getFile());
			if (!dir.exists() || !dir.isDirectory()) {
				continue;
			}
			for (File file : FileUtils.listFiles(dir, FileFilterUtils.and(FileFilterUtils.prefixFileFilter(""),
			        FileFilterUtils.suffixFileFilter("_sqlMap.xml")), FileFilterUtils.trueFileFilter())) {
				check(parse(file), lines, file.getName());
			}
		}

		System.out.println(StringUtils.join(lines, "\r\n"));
	}

	private static void check(List<SqlInfo> infoList, List<String> lines, String fileName) {
		String prop;
		for (SqlInfo info : infoList) {
			for (String input : info.getInputSet()) {
				prop = info.getParameterMap().get(input.toUpperCase());
				if (!StringUtils.equals(input, prop)) {
					lines.add(format(fileName, info.getId(), info.getSqlType(), input, prop, info.getParameterClass()));
				}
			}
			for (String output : info.getOutputSet()) {
				prop = info.getResultMap().get(output.toUpperCase());
				if (!StringUtils.equals(output, prop)) {
					lines.add(format(fileName, info.getId(), info.getSqlType(), output, prop, info.getResultClass()));
				}
			}
		}
	}

	private static List<SqlInfo> parse(File xml) throws Exception {
		final List<SqlInfo> result = new ArrayList<SqlInfo>();
		final XmlParserState xmlParserState = createXmlParserState(xml);
		final Iterator<String> statementNames = xmlParserState.getConfig().getDelegate().getMappedStatementNames();
		MappedStatement statement;
		while (statementNames.hasNext()) {
			statement = xmlParserState.getConfig().getDelegate().getMappedStatement(statementNames.next());
			final SqlInfo info = new SqlInfo(statement.getId(), TYPEMAP.get(statement.getStatementType()),
			        statement.getParameterClass(),
			        (statement.getResultMap() != null ? statement.getResultMap().getResultClass() : null));
			result.add(info);
			final StatementScope scope = new StatementScope(new SessionScope());
			scope.setStatement(statement);

			// parse parameterMap
			if (statement.getParameterMap() != null) {
				mapper(statement.getParameterMap().getParameterMappings(), info);
			}

			// parse resultMap
			if (statement.getResultMap() != null) {
				ResultMap resultMap = statement.getResultMap();
				mapper(resultMap.getResultMappings(), info);
				if (resultMap.getNestedResultMappings() != null) {
					mapper((ResultMapping[]) resultMap.getNestedResultMappings().toArray(new ResultMapping[] {}), info);
				}
			}
			for (ResultMap resultMap : statement.getAdditionalResultMaps()) {
				mapper(resultMap.getResultMappings(), info);
				if (resultMap.getNestedResultMappings() != null) {
					mapper((ResultMapping[]) resultMap.getNestedResultMappings().toArray(new ResultMapping[] {}), info);
				}
			}

			// case of dynamic sql
			if (statement.getSql() instanceof DynamicSql) {
				final DynamicSql dynaSql = (DynamicSql) statement.getSql();

				// parse parameterMap
				mapper(dynaSql.getParameterMap(scope, newInstance(info.getParameterClass())).getParameterMappings(),
				        info);

				// parse resultMap
				final ResultMap resultMap = dynaSql.getResultMap(scope, newInstance(info.getResultClass()));
				if (resultMap != null) {
					mapper(resultMap.getResultMappings(), info);
					if (resultMap.getNestedResultMappings() != null) {
						mapper((ResultMapping[]) resultMap.getNestedResultMappings().toArray(new ResultMapping[] {}),
						        info);
					}
				}
			} else if (!(statement.getSql() instanceof StaticSql)) {
				throw new RuntimeException(statement.getSql().getClass() + " is not supported.");
			}
		}
		return result;
	}

	private static class SqlInfo {
		private final String id;
		private final String sqlType;
		private final Class parameterClass;
		private final Class resultClass;
		private final Map<String, String> parameterMap = new TreeMap<String, String>();
		private final Map<String, String> resultMap = new TreeMap<String, String>();
		private final Set<String> inputSet = new TreeSet<String>();
		private final Set<String> outputSet = new TreeSet<String>();

		public SqlInfo(String id, String sqlType, Class parameterClass, Class resultClass) {
			super();
			this.id = id;
			this.sqlType = sqlType;
			this.parameterClass = parameterClass;
			this.resultClass = resultClass;
			if (parameterClass != null) {
				for (Class clz : ClassUtils.hierarchy(parameterClass)) {
					for (Field field : FieldUtils.getAllFieldsList(clz)) {
						if (Modifier.isStatic(field.getModifiers())) {
							continue;
						}
						parameterMap.put(field.getName().toUpperCase(), field.getName());
					}
				}
			}
			if (resultClass != null) {
				for (Class clz : ClassUtils.hierarchy(resultClass)) {
					for (Field field : FieldUtils.getAllFieldsList(clz)) {
						if (Modifier.isStatic(field.getModifiers())) {
							continue;
						}
						resultMap.put(field.getName().toUpperCase(), field.getName());
					}
				}
			}
		}

		public void addInput(String input) {
			this.inputSet.add(input);
		}

		public void addOutput(String output) {
			this.outputSet.add(output);
		}

		public String getId() {
			return id;
		}

		public String getSqlType() {
			return sqlType;
		}

		public Set<String> getInputSet() {
			return inputSet;
		}

		public Set<String> getOutputSet() {
			return outputSet;
		}

		public Class getParameterClass() {
			return parameterClass;
		}

		public Class getResultClass() {
			return resultClass;
		}

		public Map<String, String> getParameterMap() {
			return parameterMap;
		}

		public Map<String, String> getResultMap() {
			return resultMap;
		}

	}

	private static void mapper(ParameterMapping[] mappings, SqlInfo info) {
		if (mappings == null) {
			return;
		}
		for (ParameterMapping pm : mappings) {
			info.addInput(pm.getPropertyName());
		}
	}

	private static void mapper(ResultMapping[] mappings, SqlInfo info) {
		if (mappings == null) {
			return;
		}
		for (ResultMapping pm : mappings) {
			info.addOutput(pm.getPropertyName());
		}
	}

	private static Object newInstance(Class clz) throws Exception {
		return clz != null ? clz.newInstance() : null;
	}

	private static XmlParserState createXmlParserState(File xml) throws Exception {
		final XmlParserState xmlParserState = new XmlParserState();
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(xml));
			(new SqlMapParser(xmlParserState)).parse(stream);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return xmlParserState;
	}

	private static String format(String fileName, String sqlId, String type, String binding, String property,
	        Object dto) {
		return String.format("%s,%s,%s,%s,%s,%s", fileName, sqlId, type, binding, property, dto);
	}

}
```
