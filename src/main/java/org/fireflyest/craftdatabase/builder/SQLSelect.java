package org.fireflyest.craftdatabase.builder;

/**
 * todo 函数 分组 过滤 子查询
 * @author Fireflyest
 * @since 2022/8/14
 */
public class SQLSelect {

    private final StringBuilder selectBuilder = new StringBuilder();
    private final Select select;

    /**
     * SELECT *
     */
    public SQLSelect() {
        this.select = new Select(selectBuilder);

        selectBuilder.append("SELECT *");
    }

    /**
     * SELECT {column},{column}...
     * @param columns 查找的所有键
     */
    public SQLSelect(String... columns) {
        this.select = new Select(selectBuilder);

        int i = 0;
        selectBuilder.append("SELECT ");
        for (String column : columns) {
            if (i++ > 0) selectBuilder.append(",");
            selectBuilder.append(column);
        }
    }

    /**
     * FROM `{table}`,`{table}`...
     * @param tables 查找的所有表
     * @return 查找语句
     */
    public Select from(String... tables){
        int i = 0;
        selectBuilder.append(" FROM ");
        for (String table : tables) {
            if (i++ > 0) selectBuilder.append(",");
            selectBuilder.append("`").append(table).append("`");
        }
        return select;
    }

    public class Select extends SQLWhere implements SQLBuildable {

        public Select(StringBuilder outsetBuilder) {
            super(outsetBuilder);
        }

        @Override
        public String build() {
            return selectBuilder + ";";
        }

    }

}
