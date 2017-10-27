package com.pri.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class Transaction {

    private DataSource dSource;
    protected Connection conn;


    public Transaction(DataSource source) {
        dSource = source;
    }

    public Connection getConnection() throws SQLException {
        if (conn != null) {
            return conn;
        }

        conn = dSource.getConnection();
        conn.setAutoCommit(false);

        return conn;
    }

    public void rollback() throws SQLException {
        if (conn == null) {
            return;
        }

        conn.rollback();
    }

    public void commit() throws SQLException {
        if (conn == null) {
            return;
        }

        conn.commit();
    }

    public void rollbackNClose() throws SQLException {
        if (conn == null) {
            return;
        }

        conn.rollback();
        conn.close();
    }

    public void commitNClose() throws SQLException {
        if (conn == null) {
            return;
        }

        conn.commit();
        conn.setAutoCommit(true);
        conn.close();
    }
}
