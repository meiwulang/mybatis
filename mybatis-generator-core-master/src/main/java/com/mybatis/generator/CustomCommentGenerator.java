package com.mybatis.generator;


import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.util.Properties;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * @Project: hjy-middle
 * @Description mybatis generator 自定义comment生成器
 * @author 王斌
 * @date 2016年8月15日
 * @version V1.0
 */
public class CustomCommentGenerator implements CommentGenerator {

	private Properties properties;
	private Properties systemPro;
	private boolean suppressDate;
	private boolean suppressAllComments;

	public CustomCommentGenerator() {
		super();
		this.properties = new Properties();
		this.systemPro = System.getProperties();
		this.suppressDate = false;
		this.suppressAllComments = false;
	}

	public void addJavaFileComment(CompilationUnit compilationUnit) {
		return;
	}

	/**
	 * Adds a suitable comment to warn users that the element was generated, and
	 * when it was generated.
	 */
	public void addComment(XmlElement xmlElement) {
		return;
	}

	public void addRootComment(XmlElement rootElement) {
		return;
	}

	public void addConfigurationProperties(Properties properties) {
		this.properties.putAll(properties);

		suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

		suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
	}

	/**
	 * This method adds the custom javadoc tag for. You may do nothing if you do
	 * not wish to include the Javadoc tag - however, if you do not include the
	 * Javadoc tag then the Java merge capability of the eclipse plugin will
	 * break.
	 * 
	 * @param javaElement
	 *            the java element
	 */
	protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
		javaElement.addJavaDocLine(" *");
		StringBuilder sb = new StringBuilder();
		sb.append(" * ");
		sb.append(MergeConstants.NEW_ELEMENT_TAG);
		if (markAsDoNotDelete) {
			sb.append(" do_not_delete_during_merge");
		}
		javaElement.addJavaDocLine(sb.toString());
	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		innerClass.addJavaDocLine("/**");
		sb.append(" * ");
		sb.append(introspectedTable.getFullyQualifiedTable());
		sb.append(" ");
		innerClass.addJavaDocLine(sb.toString());
		innerClass.addJavaDocLine(" */");
	}

	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (introspectedColumn.getRemarks() != null && !introspectedColumn.getRemarks().equals("")) {
			field.addJavaDocLine("// " + introspectedColumn.getRemarks() + " ");
		}
	}

	public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		innerEnum.addJavaDocLine("/**");
		addJavadocTag(innerEnum, false);
		sb.append(" * ");
		sb.append(introspectedTable.getFullyQualifiedTable());
		innerEnum.addJavaDocLine(sb.toString());
		innerEnum.addJavaDocLine(" */");
	}

	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		field.addJavaDocLine("/**");
		sb.append(" * ");
		sb.append(introspectedTable.getFullyQualifiedTable());
		field.addJavaDocLine(sb.toString());
		field.addJavaDocLine(" */");
	}

	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		// method.addJavaDocLine("/**");
		// addJavadocTag(method, false);
		// method.addJavaDocLine(" */");
	}

	public void addGetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
			return;
		}

		method.addJavaDocLine("/**");

		StringBuilder sb = new StringBuilder();
		sb.append(" * ");
		sb.append(introspectedColumn.getRemarks());
		method.addJavaDocLine(sb.toString());

		sb.setLength(0);
		sb.append(" * @return ");
		sb.append(introspectedColumn.getActualColumnName());
		sb.append(" ");
		sb.append(introspectedColumn.getRemarks());
		method.addJavaDocLine(sb.toString());

		// addJavadocTag(method, false);

		method.addJavaDocLine(" */");
	}

	public void addSetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
			return;
		}

		method.addJavaDocLine("/**");
		StringBuilder sb = new StringBuilder();
		sb.append(" * ");
		sb.append(introspectedColumn.getRemarks());
		method.addJavaDocLine(sb.toString());

		Parameter parm = method.getParameters().get(0);
		sb.setLength(0);
		sb.append(" * @param ");
		sb.append(parm.getName());
		sb.append(" ");
		sb.append(introspectedColumn.getRemarks());
		method.addJavaDocLine(sb.toString());

		// addJavadocTag(method, false);

		method.addJavaDocLine(" */");
	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		innerClass.addJavaDocLine("/**");
		sb.append(" * ");
		sb.append(introspectedTable.getFullyQualifiedTable());
		innerClass.addJavaDocLine(sb.toString());

		sb.setLength(0);
		sb.append(" * @author ");
		sb.append(systemPro.getProperty("user.name"));
		sb.append(" ");

		// addJavadocTag(innerClass, markAsDoNotDelete);

		innerClass.addJavaDocLine(" */");
	}

	@Override
	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete,
			String comments) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable, String comments) {
		// TODO Auto-generated method stub

	}
}