package com.glsx.connection;

import com.glsx.conf.ConfigurationManager;

import java.sql.*;

public class MysqlConn {
    private static Connection connection = null;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private MysqlConn() {
    }

    public static Connection getConnection(String url) {
        if (connection == null) {
            synchronized (MysqlConn.class) {
                if (connection == null) {
                    try {
                        connection = DriverManager.getConnection(url);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
        return connection;
    }

    public static void executeUpdate(Connection connection, String sql) {
        PreparedStatement pstmt = null;
        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void executeQuery(Connection connection, String sql, QueryCallback callback) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            callback.process(rs);
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 流式查询
    public static void executeFlowQuery(Connection connection, String sql, QueryCallback callback) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            pstmt.setFetchSize(Integer.MIN_VALUE);
            pstmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            rs = pstmt.executeQuery();
            callback.process(rs);
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static interface QueryCallback {
        void process(ResultSet rs) throws Exception;
    }

    public static void main(String[] args) {
        Connection conn = MysqlConn.getConnection(ConfigurationManager.getProperty("testUrl"));
//        for (int i = 0; i < 100; i++) {
//            String sql = "insert into t_user(username,password) values('name"+(100+i) + "','pw"+(100+i) + "')";
//            System.out.println(sql);
//            MysqlConn.executeUpdate(conn, sql);
//        }

        final StringBuffer buffer = new StringBuffer();
        MysqlConn.executeQuery(conn, "select username,password from t_user limit 6", new QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
//                    System.out.println(rs.getString(1));
                    buffer.append(rs.getString(1));
                }
            }
        });

        System.out.println(buffer.toString());

        MysqlConn.close(conn);
    }

}
