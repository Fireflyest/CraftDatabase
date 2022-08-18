package org.fireflyest.craftdatabase.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 创建表语句
 * @author Fireflyest
 * @since 2022/8/14
 */
public class SQLCreateTable implements SQLBuildable {

    private final StringBuilder createTableBuilder = new StringBuilder();
    private boolean firstColumns = true;

    public SQLCreateTable(@NotNull String table) {
        createTableBuilder.append("CREATE TABLE IF NOT EXISTS `").append(table).append("`(\n");
    }

    /**
     * 添加一列
     * @param column 列名
     * @param type 数据类型
     * @param noNull 是否可以为NULL
     * @param defaultValue 默认值
     * @return 建表语句
     */
    public SQLCreateTable columns(@NotNull String column, @NotNull String type, boolean noNull, @Nullable String defaultValue){
        if (firstColumns){
            firstColumns = false;
        } else {
            createTableBuilder.append(",\n");
        }
        createTableBuilder.append("  `").append(column).append("`");
        createTableBuilder.append(" ").append(type);
        if (noNull) createTableBuilder.append(" NOT NULL");
        if (defaultValue == null) return this;
        if ("NULL".equals(defaultValue)){
            createTableBuilder.append(" DEFAULT NULL");
        }else {
            createTableBuilder.append(" DEFAULT '")
                    .append(defaultValue.replace("'", "''"))
                    .append("'");
        }
        return this;
    }

    public SQLCreateTable columns(@NotNull String column, @NotNull String type, boolean noNull){
        return columns(column, type, noNull, null);
    }

    public SQLCreateTable columns(@NotNull String column, @NotNull String type, @Nullable String defaultValue){
        return columns(column, type, false, defaultValue);
    }

    public SQLCreateTable columns(@NotNull String column, @NotNull String type){
        return columns(column, type, false, null);
    }

    /**
     * 添加一列数字类型或者布尔类型
     * @param column 列名
     * @param type 数据类型
     * @return 建表语句
     */
    public SQLCreateTable columns(@NotNull String column, @NotNull String type, @NotNull Number defaultValue){
        if (firstColumns){
            firstColumns = false;
        } else {
            createTableBuilder.append(",\n");
        }
        createTableBuilder.append("  `").append(column).append("`");
        createTableBuilder.append(" ").append(type);
        createTableBuilder.append(" DEFAULT ").append(defaultValue);
        return this;
    }

    /**
     * 添加自增的主键
     * @param column 列名
     * @param autoIncrement 自增词不同数据库不同
     * @return 建表指令
     */
    public SQLCreateTable id(@NotNull String column, @NotNull String autoIncrement){
        if (firstColumns){
            firstColumns = false;
        } else {
            createTableBuilder.append(",\n");
        }
        createTableBuilder
                .append("  `")
                .append(column)
                .append("` integer NOT NULL PRIMARY KEY ")
                .append(autoIncrement);
        return this;
    }

    public SQLCreateTable id(@NotNull String column){
        return id(column, "AUTOINCREMENT");
    }

    /**
     * 添加一个主键
     * @param column 列名
     * @param type 数据类型
     * @return 建表指令
     */
    public SQLCreateTable primary(@NotNull String column, @NotNull String type){
        if (firstColumns){
            firstColumns = false;
        } else {
            createTableBuilder.append(",\n");
        }
        createTableBuilder
                .append("  `")
                .append(column)
                .append("` ")
                .append(type)
                .append(" NOT NULL PRIMARY KEY");
        return SQLCreateTable.this;
    }

    @Override
    public String build() {
        return createTableBuilder + "\n);";
    }
}
