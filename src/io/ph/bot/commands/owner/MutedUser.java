package io.ph.bot.commands.general;

import java.awt.Color;
import java.sql.SQLException;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

import io.ph.bot.Bot;
import io.ph.bot.commands.Command;
import io.ph.bot.commands.CommandCategory;
import io.ph.bot.commands.CommandData;
import io.ph.bot.model.GuildObject;
import io.ph.bot.model.MutedObject;
import io.ph.bot.model.Permission;
import io.ph.util.MessageUtils;
import io.ph.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Guild;

/**
 * Create, delete, search, call information, and call macros
 * A macro is a way of mapping a word or phrase to an output
 * This can be used for actions such as reaction images, or saving something for later use
 * @author Nick
 *
 */
@CommandData (
        defaultSyntax = "botmuteduser",
        aliases = {"botmute"},
        category = CommandCategory.UTILITY,
        permission = Permission.BOT_OWNER,
        description = "Create, delete, search,on a muted user\n",
        example = "create \"userid\" *This creates a muted user named `test macro`*\n"
                + "delete userid *This deletes the muted user*\n"
                + "search @user *This deletes the muted user*\n"
                + "list userid *This deletes the muted user*\n"
        )
public class MutedUser extends Command {
    private EmbedBuilder em;
    private Message msg;
    private String contents;

    @Override
    public void executeCommand(Message msg) {
        this.msg = msg;
        this.em = new EmbedBuilder();
        this.contents = Util.getCommandContents(msg);
        if(contents.equals("")) {
            MessageUtils.sendIncorrectCommandUsage(msg, this);
            return;
        }

        String param = Util.getParam(msg);
        if(param.equalsIgnoreCase("create")) {
            createMuted();
        } else if(param.equalsIgnoreCase("delete")) {
            deleteMuted();
        } else if(param.equalsIgnoreCase("search")) {
            searchForMuted();
        } else if (param.equalsIgnoreCase("list")) {
            listMuted();
        } else {
            return;
        }
        msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});
    }

    /**
     * Create a macro
     */
    private void createMuted() {
        contents = Util.getCommandContents(this.contents);
        if(contents.equals("") || contents.split(" ").length < 1) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .addField(GuildObject.guildMap.get(this.msg.getGuild().getId()).getConfig().getCommandPrefix() 
                    + "botmuted create userid",
                    "You have designated to create a muteduser, but your"
                            + " command does not meet all requirements\n"
                            + "*userid* - user id to be muted from @user", true);
            return;
        }
        String[] resolved = MutedObject.searchByUser(contents,GuildObject.guildMap.get(msg.getGuild().getId()));
        MutedObject m = new MutedObject(Bot.getInstance().getBots().get(0).getUserById(contents).getName(), contents, GuildObject.guildMap.get(msg.getGuild().getId()));
        try {
            if(m.create()) {
                em.setTitle("Success", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(this.msg), Color.GREEN))
                .setDescription("Muted **" + resolved[0] + "** created");
            } else {
                em.setTitle("Error", null)
                .setColor(Color.RED)
                .setDescription("Muted **" + resolved[0] + "** already exists");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a macro
     */
    private void deleteMuted() {
        contents = Util.getCommandContents(contents);
        if(contents.equals("")) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .addField(GuildObject.guildMap.get(msg.getGuild().getId()).getConfig().getCommandPrefix() 
                    + "botmuted delete userid",
                    "You have designated to delete a muted user, "
                            + "but your command does not meet all requirements"
                            + "*userid* - userid of user. No quotation ", true);
            return;
        }
        try {
            MutedObject m = MutedObject.searchByUser2(contents.split(" ")[1],msg.getGuild().getId());
            if(m.delete(contents)) {
                em.setTitle("Success", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("Macro **" + contents + "** deleted");
            } else {
                em.setTitle("Error", null)
                .setColor(Color.RED)
                .setDescription("You cannot delete macro **" + contents + "**")
                .setFooter("Users can only delete their own macros", null);
            }
        } catch (IllegalArgumentException e) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .setDescription(e.getMessage());
        }
    }

    /**
     * Fuzzy search for a macro
     */
    private void searchForMuted() {
        contents = Util.getCommandContents(contents);
        if(contents.equals("")) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .addField(GuildObject.guildMap.get(msg.getGuild().getId()).getConfig().getCommandPrefix() 
                    + "botmute search userid",
                    "You have designated to search for a muted user, but "
                            + "your command does not meet all requirements"
                            + "*user* - An @ mention of a user to search for", true);
            return;
        }
        String[] result;
        StringBuilder sb = new StringBuilder();
        // Search mentions a user
        if(msg.getMentionedUsers().size() == 1) {
            if((result = MutedObject.searchByUser(msg.getMentionedUsers().get(0).getId(), msg.getGuild().getId())) != null) {
                em.setTitle("Search results for user " + msg.getGuild().getMember(msg.getMentionedUsers().get(0)).getEffectiveName(), null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN));
                sb.setLength(sb.length() - 2);
                em.setDescription(sb.toString());
            } else {
                em.setTitle("No user found", null)
                .setColor(Color.RED)
                .setDescription("No results for user **");
            }
        } else {
            if((result = MutedObject.searchByUser(contents, msg.getGuild().getId())) != null) {
                em.setTitle("Search results for " + contents, null)
                .setColor(Color.GREEN);
                sb.setLength(sb.length() - 2);
                em.setDescription(sb.toString());
            } else {
                em.setTitle("No muted users found", null)
                .setColor(Color.RED)
                .setDescription("No results for **" + contents + "**");
            }
        }
    }
    
    /**
     * List all muted
     */
    private void listMuted() {
        Member m;
        String[] results = MutedObject.listAll();
        if (results == null) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .setDescription("No muted users found");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : results) {
            sb.append(s + ", ");
        }
        
        em.setTitle("All the users that are bot muted ", null)
        .setColor(Color.GREEN)
        .setDescription(sb.substring(0, sb.length() - 2));
    }
}
