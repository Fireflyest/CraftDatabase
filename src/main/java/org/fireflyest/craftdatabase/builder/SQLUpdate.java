package org.fireflyest.craftdatabase.builder;

import org.jetbrains.annotations.NotNull;

/**
 * @author Fireflyest
 * @since 2022/8/14
 */
public class SQLUpdate {

    private final StringBuilder updateBuilder = new StringBuilder();
    private final Update update;

    /**
     * UPDATE `{table}` SET
     * @param table 表名
     */
    public SQLUpdate(@NotNull String table) {
        this.update = new Update(updateBuilder);

        updateBuilder.append("UPDATE `").append(table).append("`\nSET");
    }

    /**
     * UPDATE `{table}` SET `{column}`=`{column}`{symbol}{number}
     * @param symbol 运算符号
     * @param number 运算数字
     * @return 更新语句
     */
    public Update updateNumber(@NotNull String column, @NotNull String symbol, @NotNull Number number){
        updateBuilder.append(" `")
                .append(column)
                .append("`=`")
                .append(column)
                .append("`")
                .append(symbol)
                .append(number);
        return update;
    }

    /**
     *  UPDATE `{table}` SET `{column}`={number}
     * @param column 键
     * @param number 值
     * @return 更新语句
     */
    public Update updateNumber(@NotNull String column, @NotNull Number number){
        updateBuilder.append("`")
                .append(column)
                .append("`=")
                .append(number);
        return update;
    }

    /**
     *  UPDATE `{table}` SET `{column}`='{str}'
     * @param column 键
     * @param value 值
     * @return 更新语句
     */
    public Update updateString(@NotNull String column, @NotNull String value){
        updateBuilder.append("`")
                .append(column)
                .append("`='")
                .append(value.replace("'", "''"))
                .append("'");
        return update;
    }

    public Update update(@NotNull String column, boolean bool){
        updateBuilder.append("`")
                .append(column)
                .append("`=")
                .append(bool ? 1 : 0);
        return update;
    }

    public class Update extends SQLWhere implements SQLBuildable {

        public Update(StringBuilder outsetBuilder) {
            super(outsetBuilder);
        }

        @Override
        public String build() {
            return updateBuilder + ";";
        }

    }
}
