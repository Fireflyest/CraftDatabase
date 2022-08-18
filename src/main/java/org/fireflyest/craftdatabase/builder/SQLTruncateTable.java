package org.fireflyest.craftdatabase.builder;

import org.jetbrains.annotations.NotNull;

/**
 * 清空所有行
 * @author Fireflyest
 * @since 2022/8/16
 */
public class SQLTruncateTable implements SQLBuildable{
    private final StringBuilder truncateTableBuilder = new StringBuilder();

    /**
     * TRUNCATE TABLE `{table}`
     * @param table 表名
     */
    public SQLTruncateTable(@NotNull String table) {
        truncateTableBuilder.append("TRUNCATE TABLE `").append(table).append("`");
    }

    @Override
    public String build() {
        return truncateTableBuilder + ";";
    }
}
