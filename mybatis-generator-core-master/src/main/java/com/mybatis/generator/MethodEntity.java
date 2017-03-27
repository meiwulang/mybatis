package com.mybatis.generator;


import java.util.HashMap;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;

/**
 * @Project: mybatis-generation
 * @Description 方法实体
 * @author 王斌
 * @date 2017年3月10日
 * @since 1.0
 */
public class MethodEntity {
	private String methodName;
	private HashMap<String, FullyQualifiedJavaType> params;
	private JavaVisibility visibility;
	private FullyQualifiedJavaType returnType;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public HashMap<String, FullyQualifiedJavaType> getParams() {
		return params;
	}

	public void setParams(HashMap<String, FullyQualifiedJavaType> params) {
		this.params = params;
	}

	public JavaVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(JavaVisibility visibility) {
		this.visibility = visibility;
	}

	public FullyQualifiedJavaType getReturnType() {
		return returnType;
	}

	public void setReturnType(FullyQualifiedJavaType returnType) {
		this.returnType = returnType;
	}

	@Override
	public String toString() {
		return "MethodEntity [methodName=" + methodName + ", params=" + params + ", visibility=" + visibility
				+ ", returnType=" + returnType + "]";
	}

	/**
	 * @param methodName
	 * @param params
	 * @param visibility
	 * @param returnType
	 */
	public MethodEntity(String methodName, HashMap<String, FullyQualifiedJavaType> params, JavaVisibility visibility,
			FullyQualifiedJavaType returnType) {
		super();
		this.methodName = methodName;
		this.params = params;
		this.visibility = visibility;
		this.returnType = returnType;
	}

	public MethodEntity() {
		super();
	}

}
