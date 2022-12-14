package org.fireflyest.craftdatabase.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Fireflyest
 * @since 2022/8/17
 */
public class SQLConnector {

    public static final String MYSQL = "com.mysql.cj.jdbc.Driver";
    public static final String MYSQL_OLD = "com.mysql.jdbc.Driver";
    public static final String SQLITE = "org.sqlite.JDBC";

    private static final Set<String> loadedClass = new HashSet<>();
    private static final Map<String, ConnectInfo> connectInfoMap = new HashMap<>();
    private static final Map<String, Connection> connectionMap = new HashMap<>();

    private SQLConnector() {
    }

    /**
     * 获取已有连接
     * @param url 地址
     * @return 连接
     */
    public static Connection getConnect(String url) {
        // 获取连接
        Connection connection = connectionMap.get(url);
        try {
            // 判断是否已连接
            if (connection != null && !connection.isClosed()) return connection;
            if (connectInfoMap.containsKey(url)){
                // 重新连接
                connection = connect(connectInfoMap.get(url));
            } else {
                // 尝试建立连接
                connection = connect(new ConnectInfo(url));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    /**
     * 初始化的时连接数据库
     * @param sqlClass 驱动类
     * @param url 地址
     * @param user 账户
     * @param password 密码
     * @return 连接
     * @throws SQLException 数据库连接错误
     * @throws ClassNotFoundException 驱动类无法找到
     */
    public static Connection connect(String sqlClass, String url, String user, String password) throws SQLException, ClassNotFoundException {
        if (!loadedClass.contains(sqlClass)) {
            Class.forName(sqlClass);
            loadedClass.add(sqlClass);
        }
        ConnectInfo connectInfo = new ConnectInfo(url, user, password);
        return connect(connectInfo);
    }

    /**
     * 获取连接并缓存
     * @param connectInfo 连接所需信息
     * @return 连接
     * @throws SQLException 数据库连接错误
     */
    public static Connection connect(ConnectInfo connectInfo) throws SQLException {
        // 存储连接密码，可以重连
        connectInfoMap.put(connectInfo.url, connectInfo);
        Connection connection = DriverManager.getConnection(
                connectInfo.url ,
                connectInfo.user,
                connectInfo.password);
        // 存储连接
        connectionMap.put(connectInfo.url, connection);

        return connection;
    }

    /**
     * 关闭连接
     * @param url 连接地址
     */
    public static void close(String url){
        Connection connection = connectionMap.get(url);
        if (connection == null) return;
        try {
            connection.close();
            connectionMap.remove(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ConnectInfo {

        public ConnectInfo(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }

        public ConnectInfo(String url) {
            this.url = url;
            this.user = null;
            this.password = null;
        }

        public String url;
        public String user;
        public String password;
    }

}
