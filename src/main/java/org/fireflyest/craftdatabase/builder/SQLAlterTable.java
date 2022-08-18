package org.fireflyest.craftdatabase.builder;

import org.jetbrains.annotations.NotNull;

/**
 * 修改表
 * @author Fireflyest
 * @since 2022/8/16
 */
public class SQLAlterTable {

    private final StringBuilder alterTableBuilder = new StringBuilder();

    private final AlterTable alterTable;
    /**
     * ALTER TABLE `{table}`
     * @param table 表名
     */
    public SQLAlterTable(@NotNull String table) {
        this.alterTable = new AlterTable();

        alterTableBuilder.append("ALTER TABLE `").append(table).append("`");
    }

    /**
     * 添加列
     * ADD COLUMN `{column}` {type}
     * @param column 列名
     * @return 修改指令
     */
    public AlterTable add(@NotNull String column, @NotNull String type){
        alterTableBuilder.append("\nADD COLUMN `")
                .append(column)
                .append("` ")
                .append(type);
        return alterTable;
    }

    /**
     * 删除列
     * DROP COLUMN `{column}`
     * @param column 列名
     * @return 修改指令
     */
    public AlterTable drop(@NotNull String column){
        alterTableBuilder.append("\nDROP COLUMN `")
                .append(column)
                .append("` ");
        return alterTable;
    }

    public class AlterTable implements SQLBuildable{

        public AlterTable() {
        }

        @Override
        public String build() {
            return alterTableBuilder + ";";
        }

    }

}
