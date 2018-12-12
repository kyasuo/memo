
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
