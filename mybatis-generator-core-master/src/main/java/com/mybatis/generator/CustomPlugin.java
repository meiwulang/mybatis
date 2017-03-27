package com.mybatis.generator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellRunner;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

public class CustomPlugin extends PluginAdapter {
	private static String baseDaoPath;// 基类完整路径
	private static ArrayList<MethodEntity> methodEntities = new ArrayList<>();// 基类完整路径
	private static ArrayList<SqlEntity> sqlEntities = new ArrayList<>();// 基类完整路径

	/**
	 * 生成dao
	 */
	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		String baseRecordType = introspectedTable.getBaseRecordType();
		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
				baseDaoPath.substring(baseDaoPath.lastIndexOf('.') + 1) + "<" + baseRecordType + ",String >");
		HashSet<FullyQualifiedJavaType> set = new HashSet<>();
		FullyQualifiedJavaType imp = new FullyQualifiedJavaType(baseDaoPath);
		interfaze.addSuperInterface(fqjt);
		interfaze.getMethods().clear();
		set.add(imp);
		set.add(new FullyQualifiedJavaType(baseRecordType));
		initMethodEntities(introspectedTable);
		// 添加新方法
		for (MethodEntity t : methodEntities) {
			for (FullyQualifiedJavaType type : t.getParams().values()) {
				set.add(type);
			}

			interfaze.addMethod(generateExtraMethod(t));
		}
		interfaze.addImportedTypes(set);
		return true;
	}

	/**
	 * @date 2017年3月10日
	 * @Description: 添加自定义方法
	 * @author：王斌
	 * @param methodName方法名
	 * @param params方法参数
	 * @param visibility方法可见度
	 * @param returnType返回类型
	 * @return Method
	 */
	private Method generateExtraMethod(MethodEntity mEntity) {
		Method method = new Method();
		method.setName(mEntity.getMethodName());
		method.setVisibility(mEntity.getVisibility());
		method.setReturnType(mEntity.getReturnType());
		for (HashMap.Entry<String, FullyQualifiedJavaType> entry : mEntity.getParams().entrySet()) {
			method.addParameter(new Parameter(entry.getValue(), entry.getKey()));
		}
		return method;
	}

	/**
	 * 生成实体中每个属性
	 */
	@Override
	public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		return true;
	}

	/**
	 * 生成实体
	 */
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// addSerialVersionUID(topLevelClass, introspectedTable);
		return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
	}

	/**
	 * 生成mapping
	 */
	@Override
	public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
		return super.sqlMapGenerated(sqlMap, introspectedTable);
	}

	/**
	 * 生成mapping 添加自定义sql
	 */
	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		// String tableName =
		// introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();//
		// 数据库表名
		// List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
		XmlElement parentElement = document.getRootElement();
		// 添加sql——where
		XmlElement sql = new XmlElement("sql");
		sql.addAttribute(new Attribute("id", "fullWhere"));
		XmlElement sql1 = new XmlElement("sql");
		sql1.addAttribute(new Attribute("id", "likeWhere"));
		XmlElement where = new XmlElement("where");
		XmlElement where1 = new XmlElement("where");
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		// for (IntrospectedColumn introspectedColumn :
		// introspectedTable.getNonPrimaryKeyColumns()) {
		for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
			XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			sb.setLength(0);
			String javaProperty = introspectedColumn.getJavaProperty();
			sb.append(javaProperty);
			sb.append(" != null"); //$NON-NLS-1$
			isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
			where.addElement(isNotNullElement);

			sb.setLength(0);
			sb.append(" and ");
			String escapedColumnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
			sb.append(escapedColumnName);
			sb.append(" = "); //$NON-NLS-1$
			sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
			isNotNullElement.addElement(new TextElement(sb.toString()));
			// ------------likeWhere------
			XmlElement isNotNullElement1 = new XmlElement("if"); //$NON-NLS-1$
			sb1.setLength(0);
			if (escapedColumnName.equals("init_date")) {
				sb1.append("startTime != null");
				isNotNullElement1.addAttribute(new Attribute("test", sb1.toString()));
				where1.addElement(isNotNullElement1);
				sb1.setLength(0);

				sb1.append(" and ");
				sb1.append(escapedColumnName);
				sb1.append(" &gt;=${startTime}"); //$NON-NLS-1$
				isNotNullElement1.addElement(new TextElement(sb1.toString()));
				XmlElement isNotNullElement2 = new XmlElement("if");
				sb1.setLength(0);
				sb1.append("endTime != null");
				isNotNullElement2.addAttribute(new Attribute("test", sb1.toString()));
				where1.addElement(isNotNullElement2);
				sb1.setLength(0);

				sb1.append(" and ");
				sb1.append(escapedColumnName);
				sb1.append(" &lt;=${endTime}"); //$NON-NLS-1$
				isNotNullElement2.addElement(new TextElement(sb1.toString()));
				// <if test="startTime != null" >
				// and concat(init_date,init_time) &gt;=${startTime}
				// </if>
				// <if test="endTime != null" >
				// and concat(init_date,init_time) &lt;=${startTime}
				// </if>
			} else {
				if (escapedColumnName.equals("init_time")) {
					continue;
				}
				sb1.append(javaProperty);
				sb1.append(" != null"); //$NON-NLS-1$
				isNotNullElement1.addAttribute(new Attribute("test", sb1.toString())); //$NON-NLS-1$
				where1.addElement(isNotNullElement1);

				sb1.setLength(0);

				sb1.append(" and ");
				sb1.append(escapedColumnName);
				sb1.append(" like '%"); //$NON-NLS-1$
				sb1.append(MyBatis3FormattingUtilities.getParameterClause1(introspectedColumn, null));
				sb1.append("%'"); //$NON-NLS-1$
				isNotNullElement1.addElement(new TextElement(sb1.toString()));
			}

		}
		sql.addElement(where);
		parentElement.addElement(sql);
		sql1.addElement(where1);
		parentElement.addElement(sql1);
		XmlElement tableName = new XmlElement("sql");
		tableName.addAttribute(new Attribute("id", "tableName"));
		tableName.addElement(new TextElement(introspectedTable.getFullyQualifiedTable().toString()));
		parentElement.addElement(0, tableName);
		// XmlElement include = new XmlElement("include");
		// include.addAttribute(new Attribute("refid", "sql_where"));
		// XmlElement selectTest = new XmlElement("select");
		// selectTest.addAttribute(new Attribute("id", "countSelect"));
		// selectTest.addAttribute(new Attribute("resultType", "int"));
		// selectTest.addAttribute(new Attribute("parameterType",
		// introspectedTable.getBaseRecordType()));
		// selectTest.addElement(
		// new TextElement(" select * from " +
		// introspectedTable.getFullyQualifiedTableNameAtRuntime()));
		// selectTest.addElement(include);
		// parentElement.addElement(selectTest);
		// 添加额外方法
		initSqlEntities(introspectedTable);
		for (SqlEntity sqlEntity : sqlEntities) {
			parentElement.addElement(generateExtraSql(sqlEntity));
		}

		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	private XmlElement generateExtraSql(SqlEntity sqlEntity) {
		XmlElement sqlElement = new XmlElement(sqlEntity.getElementName());
		sqlElement.addAttribute(new Attribute("id", sqlEntity.getElementId()));
		if (!sqlEntity.getElementName().equals("insert") && sqlEntity.getResultMap() != null) {
			sqlElement.addAttribute(new Attribute("resultType", sqlEntity.getResultMap()));
		} else if (sqlEntity.getElementName().equals("insert")) {
			sqlElement.addAttribute(new Attribute("useGeneratedKeys", "true"));

		}
		sqlElement.addElement(new TextElement(sqlEntity.getSql()));
		return sqlElement;
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		// LIMIT5,10; // 检索记录行 6-15
		// XmlElement isNotNullElement = new XmlElement("if");//$NON-NLS-1$
		// isNotNullElement.addAttribute(new Attribute("test", "limitStart !=
		// null and limitStart >=0"));//$NON-NLS-1$ //$NON-NLS-2$
		// isNotNullElement.addElement(new
		// TextElement("limit ${limitStart} , ${limitEnd}"));
		// element.addElement(isNotNullElement);
		// LIMIT 5;//检索前 5个记录行
		return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
	}

	/**
	 * mapping中添加方法
	 */
	// @Override
	public boolean sqlMapDocumentGenerated2(Document document, IntrospectedTable introspectedTable) {
		String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();// 数据库表名
		// List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
		// 添加sql
		// XmlElement sql = new XmlElement("select");

		XmlElement parentElement = document.getRootElement();
		XmlElement deleteLogicByIdsElement = new XmlElement("update");
		deleteLogicByIdsElement.addAttribute(new Attribute("id", "deleteLogicByIds"));
		deleteLogicByIdsElement.addElement(new TextElement("update " + tableName
				+ " set deleteFlag = #{deleteFlag,jdbcType=INTEGER} where id in "
				+ " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach> "));

		parentElement.addElement(deleteLogicByIdsElement);
		XmlElement queryPage = new XmlElement("select");
		queryPage.addAttribute(new Attribute("id", "queryPage"));
		queryPage.addAttribute(new Attribute("resultType", "BaseResultMap"));
		queryPage.addElement(new TextElement("select "));

		XmlElement include = new XmlElement("include");
		include.addAttribute(new Attribute("refid", "fullColumns"));

		queryPage.addElement(include);
		queryPage.addElement(new TextElement(" from " + tableName + " ${sql}"));
		parentElement.addElement(queryPage);
		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	/**
	 * @date 2017年3月10日
	 * @Description: 可以参考这种实体生成新字段的方式
	 * @author：王斌
	 * @param topLevelClass
	 * @param introspectedTable
	 *            void
	 */
	@SuppressWarnings("unused")
	private void addSerialVersionUID(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		CommentGenerator commentGenerator = context.getCommentGenerator();
		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(new FullyQualifiedJavaType("long"));
		field.setStatic(true);
		field.setFinal(true);
		field.setName("serialVersionUID");
		field.setInitializationString("1L");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
	}

	/*
	 * Dao中添加方法
	 */
	@SuppressWarnings("unused")
	private Method generateDeleteLogicByIds(Method method, IntrospectedTable introspectedTable) {
		Method m = new Method("deleteLogicByIds");
		m.setVisibility(method.getVisibility());
		m.setReturnType(FullyQualifiedJavaType.getIntInstance());
		m.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "deleteFlag", "@Param(\"deleteFlag\")"));
		m.addParameter(new Parameter(new FullyQualifiedJavaType("Integer[]"), "ids", "@Param(\"ids\")"));
		context.getCommentGenerator().addGeneralMethodComment(m, introspectedTable);
		return m;
	}

	/*
	 * 实体中添加属性
	 */
	@SuppressWarnings("unused")
	private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
		CommentGenerator commentGenerator = context.getCommentGenerator();
		Field field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getIntInstance());
		field.setName(name);
		field.setInitializationString("-1");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		char c = name.charAt(0);
		String camel = Character.toUpperCase(c) + name.substring(1);
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("set" + camel);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), name));
		method.addBodyLine("this." + name + "=" + name + ";");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		method.setName("get" + camel);
		method.addBodyLine("return " + name + ";");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
	}

	public boolean validate(List<String> arg0) {
		return true;
	}

	public static void generate() {
		String config = CustomPlugin.class.getClassLoader().getResource("custom/generatorConfig.xml").getFile();
		String[] arg = { "-configfile", config, "-overwrite" };
		ShellRunner.main(arg);
	}

	/**
	 * @date 2017年3月11日
	 * @Description: 自定义sql
	 * @author：王斌 void
	 */
	private static void initSqlEntities(IntrospectedTable introspectedTable) {
		sqlEntities.add(new SqlEntity("select", "query", introspectedTable.getBaseRecordType(),
				"select <include refid=\"fullColumns\"/> from \n\t<include refid=\"tableName\"/> \n\t<include refid=\"fullWhere\"/> \n\t<include refid=\"sortAndPage\"></include>"));
		sqlEntities.add(new SqlEntity("sql", "sortAndPage", null,
				"<if test=\"null != sortMarkers\"> order by \n\t\t"
						+ "<foreach collection=\"sortMarkers\" item=\"sortMarker\" separator=\",\"> \n\t\t\t"
						+ "${sortMarker.field} ${sortMarker.direction} \n\t\t</foreach> \n\t</if> \n\t<if test=\"null != page\"> \n\t\t"
						+ "limit #{page.offset}, #{page.page_size} \n\t</if>"));
		sqlEntities.add(new SqlEntity("select", "countByLike", "int",
				"select count(1) from\n\t<include refid=\"tableName\"/>\n\t<include refid=\"likeWhere\"></include>"));
		sqlEntities.add(new SqlEntity("select", "count", "int",
				"select count(1) from\n\t<include refid=\"tableName\"/>\n\t<include refid=\"fullWhere\"></include>"));
		sqlEntities.add(new SqlEntity("select", "queryByLike", "map",
				"select <include refid=\"fullColumns\" /> from\n\t<include refid=\"tableName\"/>\n\t<include refid=\"likeWhere\"></include>\n\t"
						+ "<include refid=\"sortAndPage\"></include>"));
		sqlEntities.add(new SqlEntity("insert", "save", null, "insert into <include refid=\"tableName\"/>\n\t"
				+ "( <include refid=\"fullColumns\"></include> )\n\tvalues( <include refid=\"fullColumnsValues\"></include> )"));
	}

	/**
	 * @date 2017年3月11日
	 * @Description: 自定义dao方法
	 * @author：王斌 void
	 */
	private static void initMethodEntities(IntrospectedTable introspectedTable) {
		FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("java.util.Map<String,Object>");
		HashMap<String, FullyQualifiedJavaType> params = new HashMap<>();
		params.put("example", paramType);
		methodEntities.add(new MethodEntity("queryByLike", params, JavaVisibility.DEFAULT,
				new FullyQualifiedJavaType("java.util.ArrayList<java.util.Map<String,Object>>")));
		methodEntities.add(
				new MethodEntity("countByLike", params, JavaVisibility.DEFAULT, new FullyQualifiedJavaType("int")));
	}

	public static void main(String[] args) throws IOException {
		// 设置基类完整路径
		InputStream inStream = CustomPlugin.class.getClassLoader()
				.getResourceAsStream("custom/mybatis-config.properties");
		Properties prop = new Properties();
		prop.load(inStream);
		baseDaoPath = prop.getProperty("baseDaoPath");

		generate();
	}

}