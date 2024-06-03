package org.example.servlet;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author 23126
 */
public class MyDataSource {
    private static DataSource ds;
    static {
        try {
            Properties pro = new Properties();
            pro.load(MyDataSource.class.getResourceAsStream("/druid.properties"));
            ds = DruidDataSourceFactory.createDataSource(pro);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
