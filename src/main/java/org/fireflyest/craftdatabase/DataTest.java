package org.fireflyest.craftdatabase;


import org.fireflyest.craftdatabase.builder.*;
import org.testng.annotations.Test;

/**
 * test sql
 * todo 事务 约束 外键 检查约束 索引 触发器
 * @author Fireflyest
 * @version 1
 * @since 2022/7/28
 */
public class DataTest {

    public DataTest(){

    }

    @Test
    public void test(){

//        if(false){
//            // 数据库访问对象
//            try {
//                storage = new SqlStorage(
//                        "jdbc:mysql://localhost:3306/mc_market?useSSL=false&serverTimezone=UTC",
//                        "root",
//                        "123456");
//                data = new SqlData(storage);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }else {
//            // 本地数据库访问对象
//            String url = "jdbc:sqlite:E:/JetBrains/Project/Test/src/main/resources/storage.db";
//
//            try {
//                storage = new SqLiteStorage(url);
//                data = new SqLiteData(storage);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

        System.out.println("\nselect = \n" +
                new SQLSelect().from("user")
                        .equal("name", "aa'a")
                        .limit(10, 20)
                        .build()
        );
        System.out.println("\nupdate = \n" +
                new SQLUpdate("user")
                        .updateNumber("money", "+", 2.05)
                        .equal("name", "bbb")
                        .build()
        );
        System.out.println("\ndelete = \n" +
                new SQLDelete()
                        .from("user")
                        .in("name", "aaa", "bb")
                        .build()
        );
        System.out.println("\ninsert = \n" +
                new SQLInsert("user")
                        .columns("id", "name", "uuid", "money", "vip")
                        .values(12)
                        .values("adada", "uuidddddddddddd")
                        .values(52.21)
                        .values(false)
                        .build()
        );
        System.out.println("\ncreateTable = \n" +
                new SQLCreateTable("tsss")
                        .id("id")
                        .columns("uuid", "varchar(255)")
                        .columns("name", "varchar(255)", true)
                        .columns("aaa", "int", 2.22)
                        .columns("aaa", "varchar(255)")
                        .columns("aaa", "varchar(255)", "")
                        .columns("aaa", "varchar(255)", "NULL")
                        .build()
        );
        System.out.println("\ndropTable = \n" +
                new SQLDropTable("user").build()
        );
        System.out.println("\ntruncateTable = \n" +
                new SQLTruncateTable("user").build()
        );
        System.out.println("\nalterTable = \n" +
                new SQLAlterTable("user")
                        .drop("abb")
                        .build()
        );
    }

}
