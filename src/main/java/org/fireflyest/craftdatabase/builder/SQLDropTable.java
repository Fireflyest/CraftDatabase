package org.fireflyest.craftdatabase.builder;

import org.jetbrains.annotations.NotNull;

/**
 * 创建表语句
 * @author Fireflyest
 * @since 2022/8/14
 */
public class SQLDropTable implements SQLBuildable{

    private final StringBuilder dropTableBuilder = new StringBuilder();

    /**
     * DROP TABLE `{table}`
     * @param table 表名
     */
    public SQLDropTable(@NotNull String table) {
        dropTableBuilder.append("DROP TABLE `").append(table).append("`");
    }

    @Override
    public String build() {
        return dropTableBuilder + ";";
    }
}
