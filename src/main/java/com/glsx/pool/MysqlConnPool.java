package com.glsx.pool;

import com.glsx.conf.ConfigurationManager;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class MysqlConnPool {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static MysqlConnPool instance = null;

    public static MysqlConnPool getInstance(String url) {
        if (instance == null) {
            synchronized (MysqlConnPool.class) {
                if (instance == null) {
                    instance = new MysqlConnPool(url);
                }
            }
        }
        return instance;
    }

    private LinkedList<Connection> datasource = new LinkedList<Connection>();

    public MysqlConnPool(String url) {
        int datasourceSize = ConfigurationManager.getInteger("mysql.datasource.size");
        for (int i = 0; i < datasourceSize; i++) {
            try {
                Connection connection = DriverManager.getConnection(url);
                datasource.add(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Connection getConnection() {
        while (datasource.size() == 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return datasource.poll();
    }


    public synchronized void returnConnection(Connection conn) {
        datasource.push(conn);
    }

    public int executeUpdate(String sql, Object[] params) {
        int rtn = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i+1, params[i]);
                }
            }

            rtn = pstmt.executeUpdate();
            conn.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(conn != null) {
                datasource.push(conn);
            }
        }
        return rtn;
    }

    public void executeQuery(String sql, Object[] params, QueryCallback callback) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            if(params != null && params.length > 0) {
                for(int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            rs = pstmt.executeQuery();
            callback.process(rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(conn != null) {
                datasource.push(conn);
            }
        }
    }

    public int[] executeBatch(String sql, List<Object[]> paramsList) {
        int[] rtn = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            if(paramsList != null && paramsList.size() > 0) {
                for (Object[] params : paramsList) {
                    for(int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }
                    pstmt.addBatch();
                }
            }

            rtn = pstmt.executeBatch();
            conn.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                datasource.push(conn);
            }
        }
        return rtn;
    }

    public int size() {
        return datasource.size();
    }

    public static interface QueryCallback {
        void process(ResultSet rs) throws Exception;
    }

}
