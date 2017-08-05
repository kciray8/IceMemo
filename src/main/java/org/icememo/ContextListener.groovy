package org.icememo

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException

public class ServletContext implements ServletContextListener{
    ServletContext context;
    public void contextInitialized(ServletContextEvent contextEvent) {

    }
    public void contextDestroyed(ServletContextEvent contextEvent) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {

            }
        }
    }
}