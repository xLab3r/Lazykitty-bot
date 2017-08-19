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

public class MutedObject {
    private String fallbackUsername;
    private LocalDate date;
    private String userId;
    private String guildId;

    /**
     * Constructor for MacroObject
     * @param fallbackUsername User#getName()
     * @param date
     * @param userId UserID of creator
     * @param guildId guildID of the guild this was created in
     */
    public MutedObject(String fallbackUsername, String userId, String guildId) {
        this.fallbackUsername = fallbackUsername;
        this.date = LocalDate.now(ZoneId.of("America/New_York"));
        this.userId = userId;
        this.guildId = guildId;
    }

    public MutedObject(String fallbackUsername, String userId, String guildId, LocalDate date) {
        this.fallbackUsername = fallbackUsername;
        this.date = date;
        this.userId = userId;
        this.guildId = guildId;
    }

    /**
     * Delete a macro that is in the database
     * @param requesterId The user that is requesting the delete
     * @return True if deleted, false if user doesn't have permissions
     * Prerequisite: Macro is in the database
     */
    public boolean delete(String userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql;
            conn = ConnectionPool.getBotMutedConnection();
            Member m = Bot.getInstance().shards.getGuildById(this.guildId).getMemberById(requesterId);
            Member mb = Bot.getInstance().shards.getGuildById(this.guildId).getMemberById(userId);
            //If user isn't a mod, need to check that they made this
            if (!Util.memberHasPermission(m, Permission.BOT_OWNER)) {
                sql = "SELECT hits FROM `bot_muted` WHERE user_nm = ? AND user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, this.fallbackUsername);
                stmt.setString(2, this.userId);
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

            sql = "DELETE FROM `bot_muted` WHERE user_nm = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, this.fallbackUsername);
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
     * Finalize this user and insert it into the database
     * @return True if successful, false if key conflict
     * @throws SQLException  Something broke - check stacktrace
     */
    public boolean create() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionPool.getBotMutedConnection();
            String sql = "INSERT INTO `bot_muted` (user_nm, user_id, guild_id, local_dt) VALUES (?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, this.fallbackUsername = fallbackUsername);        
            stmt.setString(4, this.date = date);
            stmt.setObject(2, this.userId = userId);            
            stmt.setString(3, this.guildId = guildId);
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
     * Search for macros by user and given guild
     * @param userId UserID to search for
     * @param guildId GuildID to search in
     * @return Null if no results, populated string array of macro names if results
     */
    public static String[] searchByUser(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> toReturn = new ArrayList<String>(10);
        try {
            conn = ConnectionPool.getBotMutedConnection();
            String sql = "SELECT user_nm FROM `bot_muted` WHERE user_id = ?";
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
    /**
     * Search for macros by user and given guild
     * @param userId UserID to search for
     * @param guildId GuildID to search in
     * @return Null if no results, populated string array of macro names if results
     */
    public static String[] searchByUser2(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> toReturn = new ArrayList<String>(10);
        try {
            conn = ConnectionPool.getBotMutedConnection();
            String sql = "SELECT user_nm, user_id, guild_id, local_dt FROM `bot_muted` WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();
            return new MutedObject(rs.getString(1), rs.getInt(2), rs.getString(3), LocalDate.parse(rs.getObject(4).toString()));
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
     * List all users
     * @param userId UserID to search for
     * @param guildId GuildID to search in
     * @return Null if no results, populated string array of macro names if results
     */ 
    public static MutedObject listAll() throws IllegalArgumentException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getConnection(guildId);
            String sql = "SELECT user_nm, user_id, guild_id, local_dt FROM `bot_muted`";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                throw new IllegalArgumentException("No macro found for " + name);
            }
            rs.next();
            return new MutedObject(rs.getString(1), rs.getInt(2), rs.getString(3), LocalDate.parse(rs.getObject(4).toString()));
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(conn);
        }
        return null;
    }

    public String getFallbackUsername() {
        return fallbackUsername;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    public String getGuildId() {
        return guildId;
    }
}
