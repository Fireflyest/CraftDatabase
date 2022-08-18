package org.fireflyest.craftdatabase.annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Fireflyest
 * @since 2022/8/17
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.fireflyest.craftdatabase.annotation.Dao")
public class DaoProcessor extends AbstractProcessor {

    public static final Pattern varPattern = Pattern.compile("\\$\\{([^}]*)}");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "Processing database dao...");

        // 当前处理器支持的所有注解种类
        for (TypeElement typeElement : annotations) {
            // 获得被该注解声明的元素
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                Dao dao = element.getAnnotation(Dao.class);

                TypeElement interfaceElement = ((TypeElement) element);
                String className = interfaceElement.getQualifiedName().toString();
                String daoName = interfaceElement.getSimpleName().toString();
                String pack = className.substring(0, className.lastIndexOf("."));

                StringBuilder javaFileBuilder = new StringBuilder()
                        .append("package ")
                        .append(pack)
                        .append(";\n\nimport java.util.*;\nimport java.sql.*;\n\npublic class ")
                        .append(daoName)
                        .append("Impl implements ")
                        .append(daoName)
                        .append(" {\n\n\tprivate final String url;\n\n\tpublic ")
                        .append(daoName)
                        .append("Impl(String url) {\n\t\tthis.url = url;\n\t}\n");

                // 遍历所有方法
                for (Element enclosedElement : interfaceElement.getEnclosedElements()) {
                    if (enclosedElement.getKind() != ElementKind.METHOD) continue;

                    javaFileBuilder.append("\n\t@Override\n\tpublic ");
                    ExecutableElement executableElement = ((ExecutableElement) enclosedElement);
                    // 返回的类型
                    String returnType = executableElement.getReturnType().toString();
                    // 对象的类型
                    String objType = returnType;
                    boolean returnArray = returnType.contains("[]");
                    if (returnArray) objType = objType.substring(0, objType.length() - 2);
                    // 对象对应的表
                    String tableName = TableProcessor.getTableName(objType);
                    javaFileBuilder.append(returnType)
                            .append(" ")
                            .append(executableElement.getSimpleName())
                            .append("(");
                    // 传递参数
                    int varNum = 0;
                    Set<String> stringParameter = new HashSet<>();
                    for (VariableElement parameter : executableElement.getParameters()) {
                        String parameterName = parameter.getSimpleName().toString();
                        String parameterType = parameter.asType().toString();
                        // 拼接
                        if (varNum++ > 0) javaFileBuilder.append(", ");
                        javaFileBuilder.append(parameterType)
                                .append(" ")
                                .append(parameterName);
                        // 字符串需要转换单引号
                        if ("java.lang.String".equals(parameterType)) stringParameter.add(parameterName);
                    }
                    // sql语句
                    javaFileBuilder.append(") {\n\t\tString sql = \"");
                    // 查询内容
                    Select select;
                    if ((select = executableElement.getAnnotation(Select.class)) != null){
                       String sql = select.value();
                        Matcher matcher = varPattern.matcher(sql);
                        while (matcher.find()){
                            String parameter = matcher.group();
                            String parameterName = parameter.substring(2, parameter.length()-1);
                            if (stringParameter.contains(parameterName)){
                                parameterName = parameterName + ".replace(\"'\", \"''\")";
                            }
                            sql = sql.replace(parameter, "\" + " + parameterName + " + \"");
                        }
                        javaFileBuilder.append(sql).append("\";");
                    }
                    // 新建返回对象列表
                    javaFileBuilder.append("\n\t\t").append(returnType).append(" returnValue = null;");
                    javaFileBuilder.append("\n\t\tList<").append(objType).append("> objList = new ArrayList<>();");
                    javaFileBuilder.append("\n\t\t");
                    javaFileBuilder.append("\n\t\tConnection connection = org.fireflyest.craftdatabase.sql.SQLConnector.getConnect(url);");
                    javaFileBuilder.append("\n\t\ttry (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)){");
                    if (returnArray){
                        javaFileBuilder.append("\n\t\t\twhile (resultSet.next()){");
                    }else {
                        javaFileBuilder.append("\n\t\t\tif (resultSet.next()){");
                    }
                    javaFileBuilder.append("\n\t\t\t\t").append(objType).append(" obj = new ").append(objType).append("();");
                    for (Map.Entry<String, TableProcessor.ColumnInfo> columnInfoEntry : TableProcessor.tableInfoMap.get(tableName).entrySet()) {
                        TableProcessor.ColumnInfo columnInfo = columnInfoEntry.getValue();
                        javaFileBuilder.append("\n\t\t\t\t")
                                .append("obj.set").append(toFirstUpCase(columnInfo.varName))
                                .append("(resultSet.get")
                                .append(toSqlDataType(columnInfo.dataType))
                                .append("(\"").append(columnInfo.columnName).append("\"));");
                    }
                    javaFileBuilder.append("\n\t\t\t\tobjList.add(obj);");
                    javaFileBuilder.append("\n\t\t\t}");
                    javaFileBuilder.append("\n\t\t} catch (SQLException e) {");
                    javaFileBuilder.append("\n\t\t\te.printStackTrace();");
                    javaFileBuilder.append("\n\t\t}");
                    javaFileBuilder.append("\n\t\t");

                    // 构建返回对象
                    if (returnArray){
                        javaFileBuilder.append("\n\t\treturnValue = objList.toArray(new ").append(objType).append("[0]);");
                    }else {
                        javaFileBuilder.append("\n\t\tif (objList.size() != 0) returnValue = objList.get(0);");
                    }

                    javaFileBuilder .append("\n\t\treturn returnValue;\n\t}\n");
                }

                javaFileBuilder.append("\n}");

                try {
                    JavaFileObject source = processingEnv.getFiler().createSourceFile(
                            pack + "." + daoName + "Impl");
                    Writer writer = source.openWriter();
                    writer.write(javaFileBuilder.toString());
                    writer.flush();
                    writer.close();
                } catch (IOException ignored) { }

                messager.printMessage(Diagnostic.Kind.NOTE, javaFileBuilder.toString());
            }
        }
        return true;
    }

    /**
     * test转换为Test
     * @param str 文本
     * @return 首字母大写
     */
    private String toFirstUpCase(String str){
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    /**
     * java.lang.String String
     * @param str 文本
     * @return 首字母大写
     */
    private String toSqlDataType(String str){
        if ("java.lang.String".equals(str)) return "String";
        return toFirstUpCase(str);
    }
}
