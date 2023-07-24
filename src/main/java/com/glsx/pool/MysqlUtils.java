package com.glsx.pool;

import com.glsx.conf.ConfigurationManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlUtils {




    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            String sql = "";
            conn = DriverManager.getConnection(ConfigurationManager.getProperty("testUrl"));
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }
}
