package io.ph.bot.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import io.ph.bot.Bot;
import io.ph.db.ConnectionPool;
import io.ph.db.SQLUtils;
import io.ph.util.Util;
import net.dv8tion.jda.core.entities.Member;

public class ToDoObject {
    private String fallbackUsername;
    private LocalDate date;
    private Integer ToDoNum;
    private String ToDoContent;
    private int hits;
    private String userId;
    private String guildId;

    /**
     * Constructor for ToDoObject
     * @param fallbackUsername User#getName()
     * @param ToDoNum Name of ToDo
     * @param ToDoContent Contents of ToDo
     * @param hits Hits of ToDo (set to 0 when creating a new ToDo)
     * @param userId UserID of creator
     * @param guildId guildID of the guild this was created in
     */
    public ToDoObject(String fallbackUsername, Integer ToDoNum, String ToDoContent, int hits,
            String userId, String guildId) {
        this.fallbackUsername = fallbackUsername;
        this.date = LocalDate.now(ZoneId.of("America/New_York"));
        this.ToDoNum = ToDoNum;
        this.ToDoContent = ToDoContent;
        this.hits = hits;
        this.userId = userId;
        this.guildId = guildId;
    }

    public ToDoObject(String fallbackUsername, Integer ToDoNum, String ToDoContent, int hits,
            String userId, String guildId, LocalDate date) {
        this.fallbackUsername = fallbackUsername;
        this.date = date;
        this.ToDoNum = ToDoNum;
        this.ToDoContent = ToDoContent;
        this.hits = hits;
        this.userId = userId;
        this.guildId = guildId;
    }
    public static ToDoObject forNum(String num, String guildId) throws IllegalArgumentException {
        return forNum(num, guildId, false);
    }

    /**
     * Returns top ToDo and hits
     * @param guildId
     * @return Object array with index 0: hits 1: ToDo name 2: userid 3: fallback username
     * @throws NoToDoFoundException
     */
    public static Object[] topToDo(String guildId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getConnection(guildId);
            stmt = conn.prepareStatement("SELECT hits, ToDo, user_id, user_created FROM `discord_ToDo` ORDER BY hits DESC LIMIT 1");
            rs = stmt.executeQuery();
            if(!rs.isBeforeFirst())
                return null;
            rs.next();
            return new Object[] {rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)};
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(conn);
        }
        return null;
    }


    /**
     * Get and return a ToDo for given ToDoNum
     * If found, increment the hits
     * @param num ToDo num to search for
     * @param guildId Guild ID to search in
     * @return ToDo if found
     * @throws NoToDoFoundException if no ToDo is found
     */
    public static ToDoObject forNuma(Integer num, String guildId, boolean hit) throws IllegalArgumentException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getConnection(guildId);
            String sql = "SELECT user_created, date_created, content, hits, user_id FROM `discord_ToDo` WHERE ToDo = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name.toLowerCase());
            rs = stmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                throw new IllegalArgumentException("No ToDo found for " + num);
            }
            rs.next();
            if(hit) {
                sql = "UPDATE `discord_ToDo` SET hits = hits+1 WHERE ToDo = ?";
                PreparedStatement stmt2 = conn.prepareStatement(sql);
                stmt2.setString(1, num);
                stmt2.execute();
                SQLUtils.closeQuietly(stmt2);
            }
            return new ToDoObject(rs.getString(1), num, rs.getString(3), rs.getInt(4), rs.getString(5),
                    guildId, LocalDate.parse(rs.getObject(2).toString()));
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(conn);
        }
        return null;
    }

    /**
     * Delete a ToDo that is in the database
     * @param requesterId The user that is requesting the delete
     * @return True if deleted, false if user doesn't have permissions
     * Prerequisite: ToDo is in the database
     */
    public boolean delete(String requesterId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql;
            conn = ConnectionPool.getConnection(this.guildId);
            Member m = Bot.getInstance().shards.getGuildById(this.guildId).getMemberById(requesterId);
            //If user isn't a mod, need to check that they made this
            if (!Util.memberHasPermission(m, Permission.KICK)) {
                sql = "SELECT hits FROM `discord_ToDo` WHERE ToDo = ? AND user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, this.ToDoNum);
                stmt.setString(2, requesterId);
                try {
                    rs = stmt.executeQuery();
                    if(!rs.isBeforeFirst())
                        return false;
                } catch(SQLException e) {
                    e.printStackTrace();
                } finally {
                    SQLUtils.closeQuietly(rs);
                    SQLUtils.closeQuietly(stmt);
                }
            }

            sql = "DELETE FROM `discord_ToDo` WHERE ToDo = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, this.ToDoNum);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(conn);
        }
    }

    /**
     * Edit a ToDo that is in the database
     * @param requesterId The user that is requesting the edit
     * @return True if deleted, false if user doesn't have permissions
     * Prerequisite: ToDo is in the database
     */
    public boolean edit(String requesterId, String newContent) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql;
            conn = ConnectionPool.getConnection(this.guildId);
            Member m = Bot.getInstance().shards.getGuildById(this.guildId).getMemberById(requesterId);
            //If user isn't a mod, need to check that they made this
            if (!Util.memberHasPermission(m, Permission.KICK)) {
                sql = "SELECT hits FROM `discord_ToDo` WHERE ToDo = ? AND user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, this.ToDoNum);
                stmt.setString(2, requesterId);
                try {
                    rs = stmt.executeQuery();
                    if(!rs.isBeforeFirst())
                        return false;
                } catch(SQLException e) {
                    e.printStackTrace();
                } finally {
                    SQLUtils.closeQuietly(rs);
                    SQLUtils.closeQuietly(stmt);
                }
            }

            sql = "UPDATE `discord_ToDo` SET content = ? WHERE ToDo = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newContent);
            stmt.setString(2, this.ToDoNum);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(conn);
        }
    }

    /**
     * Finalize this ToDo and insert it into the database
     * @return True if successful, false if key conflict
     * @throws SQLException  Something broke - check stacktrace
     */
    public boolean create() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionPool.getConnection(this.guildId);
            String sql = "INSERT INTO `discord_ToDo` (ToDo, user_created, date_created, content, hits, user_id) VALUES (?,?,?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, this.ToDoNum);
            stmt.setString(2, this.fallbackUsername);
            stmt.setObject(3, this.date);
            stmt.setString(4, this.ToDoContent);
            stmt.setInt(5, 0);
            stmt.setString(6, this.userId);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            if(e.getErrorCode() == 19) {
                return false;
            }
            throw e;
        } finally {
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(conn);
        }
    }

    /**
     * Search for a ToDo by name given the guild ID
     * @param num num to wildcard
     * @param guildId GuildID to search in
     * @return Null if no results, populated string array of ToDo names if results
     */
    public static String[] searchForNum(Integer num, String guildId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> toReturn = new ArrayList<String>(10);
        try {
            conn = ConnectionPool.getConnection(guildId);
            String sql = "SELECT ToDo FROM `discord_ToDo` WHERE ToDo LIKE ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + num + "%");
            rs = stmt.executeQuery();
            if(!rs.isBeforeFirst())
                return null;
            while(rs.next()) {
                toReturn.add(rs.getString(1));
            }
            return toReturn.toArray(new String[0]);
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(conn);
        }

    }

    /**
     * Search for ToDos by user and given guild
     * @param userId UserID to search for
     * @param guildId GuildID to search in
     * @return Null if no results, populated string array of ToDo names if results
     */
    public static String[] searchByUser(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> toReturn = new ArrayList<String>(10);
        try {
            conn = ConnectionPool.getConnection(guildId);
            String sql = "SELECT ToDo FROM `discord_ToDo` WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();
            if(!rs.isBeforeFirst())
                return null;
            while(rs.next()) {
                toReturn.add(rs.getString(1));
            }
            return toReturn.toArray(new String[0]);
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(conn);
        }
    }

    public String getFallbackUsername() {
        return fallbackUsername;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getToDoNum() {
        return ToDoNum;
    }

    public String getToDoContent() {
        return ToDoContent;
    }

    public int getHits() {
        return hits;
    }

    public String getUserId() {
        return userId;
    }

    public String getGuildId() {
        return guildId;
    }
}
