package com.glsx.connection;

import java.sql.*;
import java.util.HashMap;

/**
 * Mysql连接，可以跨机器使用数据库连接
 */

public class MysqlMutiConn {
    private static HashMap<String, Connection> mutiConnection = new HashMap<>();

    public static void addConnection(String name, String url) {
        try {
            Connection connection = DriverManager.getConnection(url);
            mutiConnection.put(name, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(String name) {
        return mutiConnection.get(name);
    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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


    public static interface QueryCallback {
        void process(ResultSet rs) throws Exception;
    }


    public static void main(String[] args) {

    }
}
