package ua.pp.dvviberbot;

import javax.naming.*;
import javax.sql.*;
import java.sql.*;

public class DbConection {
    public Connection getConnection(){
        DataSource ds = null;
        try{
            Context context = new InitialContext();
            Context envCtx = (Context) context.lookup("java:comp/env");
            ds =  (DataSource)envCtx.lookup("jdbc/dvviberbotdb");
            if (ds != null) {
                return ds.getConnection();
            }
        }
        catch (SQLException e) {
            System.out.println("Error occurred " + e);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
