package com.tool.cnv.ibatis.sqlmap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.common.xml.NodeletException;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapParser;
import com.ibatis.sqlmap.engine.builder.xml.XmlParserState;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMapping;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.mapping.statement.StatementType;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;
import com.tool.cnv.ibatis.sqlmap.bean.SqlMapInfo;
import com.tool.cnv.ibatis.sqlmap.bean.SqlProperty;
import com.tool.cnv.ibatis.sqlmap.define.SqlType;

@SuppressWarnings("rawtypes")
public class SqlMapAnalyzerImpl implements SqlMapAnalyzer {

	static final Logger logger = LoggerFactory.getLogger(SqlMapAnalyzerImpl.class);
	static final Map<StatementType, SqlType> TYPEMAP = new HashMap<StatementType, SqlType>();
	static {
		TYPEMAP.put(StatementType.SELECT, SqlType.SELECT);
		TYPEMAP.put(StatementType.INSERT, SqlType.INSERT);
		TYPEMAP.put(StatementType.DELETE, SqlType.DELETE);
		TYPEMAP.put(StatementType.UPDATE, SqlType.UPDATE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SqlMapInfo> analyze(File xml) {
		final XmlParserState xmlParserState = createXmlParserState(xml);
		final Iterator<String> statementNames = xmlParserState.getConfig().getDelegate().getMappedStatementNames();
		final List<SqlMapInfo> sqlMapInfoList = new ArrayList<SqlMapInfo>();
		MappedStatement statement;
		while (statementNames.hasNext()) {
			statement = xmlParserState.getConfig().getDelegate().getMappedStatement(statementNames.next());
			final SqlMapInfo sqlMapInfo = new SqlMapInfo();
			sqlMapInfo.setId(statement.getId());
			sqlMapInfo.setType(TYPEMAP.get(statement.getStatementType()));
			sqlMapInfo.setParameterClass(statement.getParameterClass());
			sqlMapInfo.setResultClass(
					(statement.getResultMap() != null ? statement.getResultMap().getResultClass() : null));
			sqlMapInfo.setStatement(statement.getSql().getSql(new StatementScope(new SessionScope()), null).trim());
			if (0 < statement.getParameterMap().getParameterCount()) {
				final List<SqlProperty> propertyList = new ArrayList<SqlProperty>();
				for (ParameterMapping mapping : statement.getParameterMap().getParameterMappings()) {
					final SqlProperty sqlProperty = new SqlProperty();
					sqlProperty.setPropertyName(mapping.getPropertyName());
					if (sqlMapInfo.getParameterClass() != null) {
						sqlProperty.setPropertyClass(
								findPropertyType(sqlMapInfo.getParameterClass(), sqlProperty.getPropertyName()));
					}
					propertyList.add(sqlProperty);
				}
				sqlMapInfo.setPropertyList(propertyList);
			}
			sqlMapInfoList.add(sqlMapInfo);
		}
		return sqlMapInfoList;
	}

	private XmlParserState createXmlParserState(File xml) {
		final XmlParserState xmlParserState = new XmlParserState();
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(xml));
			(new SqlMapParser(xmlParserState)).parse(stream);
		} catch (NodeletException | FileNotFoundException e) {
			throw new RuntimeException(e);
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

	private Class findPropertyType(Class targetClass, String propertyName) {
		Class resultClass = targetClass;
		try {
			for (String name : StringUtils.split(propertyName, ".")) {
				if (resultClass == null) {
					break;
				}
				resultClass = PropertyUtils.getPropertyType(resultClass.newInstance(), name);
			}
			return resultClass;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

}
