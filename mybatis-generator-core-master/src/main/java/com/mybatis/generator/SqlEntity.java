package com.mybatis.generator;


/**
 * @Project: mybatis-generation
 * @Description TODO
 * @author 王斌
 * @date 2017年3月10日
 * @since 1.0
 */
public class SqlEntity {
	private String elementName;
	private String elementId;
	private String resultMap;
	private String sql;

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getResultMap() {
		return resultMap;
	}

	public void setResultMap(String resultMap) {
		this.resultMap = resultMap;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		return "SqlEntity [elementName=" + elementName + ", elementId=" + elementId + ", resultMap=" + resultMap
				+ ", sql=" + sql + "]";
	}

	/**
	 * @param elementName
	 * @param elementId
	 * @param resultMap
	 * @param sql
	 */
	public SqlEntity(String elementName, String elementId, String resultMap, String sql) {
		super();
		this.elementName = elementName;
		this.elementId = elementId;
		this.resultMap = resultMap;
		this.sql = sql;
	}

	public SqlEntity() {
		super();
	}

}
