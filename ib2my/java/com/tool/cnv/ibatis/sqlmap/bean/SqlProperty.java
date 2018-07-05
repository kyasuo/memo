package com.tool.cnv.ibatis.sqlmap.bean;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class SqlProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	private String propertyName;
	private Class propertyClass;

	public Class getPropertyClass() {
		return propertyClass;
	}

	public void setPropertyClass(Class propertyClass) {
		this.propertyClass = propertyClass;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}
