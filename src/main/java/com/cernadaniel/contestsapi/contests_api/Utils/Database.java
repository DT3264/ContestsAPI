package com.cernadaniel.contestsapi.contests_api.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cernadaniel.contestsapi.contests_api.Models.Contest;

public class Database{

    String url = System.getenv("DB_CONNECTION");
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASS");

    Connection conn;

    public Database(){
        
    }
    
    void openDB(){
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
    }

    void closeDB() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.print(e.getMessage());
            }
        }
    }

    public long getLastTimeUpdate(){
        long lastTime = 3600;
        try {
            openDB();
            CallableStatement cs = conn.prepareCall("{call getTimeSinceLastUpdate}");
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
               lastTime = rs.getLong("secondsDiff");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeDB();
        }
        return lastTime;
    }

    public void updateContests(List<Contest> contestsList) {
        try {
            openDB();
            Statement st = conn.createStatement();
            String sql = "";
            for (Contest c : contestsList) {
                sql = String.format("call insertContest('%s',%d,%d,'%s','%s');", c.name, c.start, c.end, c.url,
                        c.platform);
                st.executeQuery(sql);
            }
            sql = "call resetAfterUpdated();";
            st.executeQuery(sql);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeDB();
        }
    }

    public List<Contest> getContests() {
        List<Contest> contests = new ArrayList<Contest>();
        try {
            openDB();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select * from contests");
            while (rs.next()) {
                String name = rs.getString("contestName");
                long start = rs.getLong("contestStart");
                long end = rs.getLong("contestEnd");
                String url = rs.getString("contestUrl");
                String platform = rs.getString("contestPlatform");
                Contest c = new Contest(name, start, end, url, platform);
                contests.add(c);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeDB();
        }
        return contests;
    }

    public TreeMap<String, Integer> getNewContests(){
        TreeMap<String, Integer> newContests = new TreeMap<String, Integer>();
        try {
            openDB();
            CallableStatement cs = conn.prepareCall("{call getNewContests}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                String platform = rs.getString("contestPlatform");
                int contests = rs.getInt("newContests");
                newContests.put(platform, contests);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeDB();
        }
        return newContests;
    }
}
