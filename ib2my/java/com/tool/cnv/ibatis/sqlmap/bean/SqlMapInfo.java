package com.tool.cnv.ibatis.sqlmap.bean;

import java.io.Serializable;
import java.util.List;

import com.tool.cnv.ibatis.sqlmap.define.SqlType;

@SuppressWarnings("rawtypes")
public class SqlMapInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private SqlType type;
	private Class parameterClass;
	private Class resultClass;
	private String statement;
	private List<SqlProperty> propertyList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SqlType getType() {
		return type;
	}

	public void setType(SqlType type) {
		this.type = type;
	}

	public Class getParameterClass() {
		return parameterClass;
	}

	public void setParameterClass(Class parameterClass) {
		this.parameterClass = parameterClass;
	}

	public Class getResultClass() {
		return resultClass;
	}

	public void setResultClass(Class resultClass) {
		this.resultClass = resultClass;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public List<SqlProperty> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<SqlProperty> propertyList) {
		this.propertyList = propertyList;
	}

}
