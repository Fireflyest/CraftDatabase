package org.fireflyest.craftdatabase.sql;

import org.fireflyest.craftdatabase.annotation.Auto;
import org.fireflyest.craftdatabase.annotation.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Fireflyest
 * @since 2022/8/19
 */
public class SQLServiceInitial {

    private static final Pattern jdbcPattern = Pattern.compile("jdbc:([^:]*)");
    private static final Pattern varPattern = Pattern.compile("\\$\\{([^{]*)}");

    private SQLServiceInitial() {
    }

    public static void  init(Class<?> serviceClass, String url) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Service service = serviceClass.getAnnotation(Service.class);
        if (service == null) return;
        // 是否自动建表
        boolean createTable = service.createTable();
        // 遍历成员变量
        for (Field declaredField : serviceClass.getDeclaredFields()) {
            Class<?> daoClass, daoImplClass;
            // 判断是否自动实现
            if (declaredField.getAnnotation(Auto.class) == null) continue;
            declaredField.setAccessible(true);
            // 获取实例类
            daoClass = declaredField.getType();
            String implClass = daoClass.getPackageName() + "." + daoClass.getSimpleName() + "Impl";
            daoImplClass = Class.forName(implClass);
            // 实例化对象并赋值
            Constructor<?> declaredConstructor = daoImplClass.getDeclaredConstructor(String.class);
            declaredField.set(null, declaredConstructor.newInstance(url));
            // 建表
            if (!createTable) continue;
            // 获取sql
            Method method = daoImplClass.getMethod("getCreateTableSQL");
            String sql = (String) method.invoke(declaredField.get(null));
            if ("".equals(sql)) continue;
            // 替换数据类型
            Matcher matcher = jdbcPattern.matcher(url);
            if (matcher.find()){
                String type = matcher.group().substring(5);
                boolean sqliteType = "sqlite".equals(type);
                if (sqliteType) {
                    sql = sql.replace("AUTOINCREMENT", "AUTO_INCREMENT");
                }
                Matcher varMatcher = varPattern.matcher(sql);
                while (varMatcher.find()){
                    String parameter = varMatcher.group();
                    String parameterName = parameter.substring(2, parameter.length()-1);
                    if (sqliteType){
                        parameterName = javaType2SqliteType(parameterName);
                    }else {
                        parameterName = javaType2MysqlType(parameterName);
                    }
                    sql = sql.replace(parameter, parameterName);
                }
                // 建表
                Connection connection = SQLConnector.getConnect(url);
                try (Statement statement = connection.createStatement()){
                    statement.execute(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将java数据类型转化为sql数据类型
     * @param type java数据类型
     * @return sql数据类型
     */
    private static String javaType2MysqlType(String type){
        switch (type){
            case "int":
                return "int";
            case "long":
            case "java.lang.Long":
                return "bigint";
            case "boolean":
            case "java.lang.Boolean":
                return "bit";
            case "short":
            case "java.lang.Short":
                return "tinyint";
            case "java.lang.String":
                return "varchar(511)";
            case "java.lang.Double":
            case "java.lang.Float":
            case "double":
            case "float":
                return "decimal(10,2)";
            case "java.lang.Integer":
                return "integer";
            default:
        }
        return "varchar(63)";
    }

    /**
     * 将java数据类型转化为sql数据类型
     * @param type java数据类型
     * @return sql数据类型
     */
    private static String javaType2SqliteType(String type){
        switch (type){
            case "int":
            case "java.lang.Integer":
            case "long":
            case "java.lang.Long":
            case "boolean":
            case "java.lang.Boolean":
            case "short":
            case "java.lang.Short":
                return "integer";
            case "java.lang.String":
                return "varchar(511)";
            case "java.lang.Double":
            case "java.lang.Float":
            case "float":
            case "double":
                return "real";
            default:
        }
        return "varchar(63)";
    }

}
