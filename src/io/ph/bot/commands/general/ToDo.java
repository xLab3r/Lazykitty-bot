package io.ph.bot.commands.general;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import io.ph.bot.commands.Command;
import io.ph.bot.commands.CommandCategory;
import io.ph.bot.commands.CommandData;
import io.ph.bot.model.Permission;
import io.ph.db.ConnectionPool;
import io.ph.db.SQLUtils;
import io.ph.util.MessageUtils;
import io.ph.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import org.apache.commons.lang3.StringUtils;

import io.ph.bot.model.GuildObject;
import io.ph.bot.model.ToDoObject;
import net.dv8tion.jda.core.entities.Member;


/**
 * Set a ToDo list, seeable by all
 * @author Nick
 *
 */

@CommandData (
        defaultSyntax = "todo",
        aliases = {"list"},
        category = CommandCategory.UTILITY,
        permission = Permission.BOT_DEVELOPER,
        description = "Create a ToDo list for bot.\n",
        example = "add contents *This add contents*\n"
                + "delete num *This deletes ToDo num*\n"
                + "edit num *This edits num's contents*\n"
                + "list *This gives information on the list*\n"
                + "info num *This gives information on the list item*\n"
                + "check num *This completes the ToDo*"
        )
public class ToDo extends Command {
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
        if(param.equalsIgnoreCase("add")) {
            createToDo();
        } else if(param.equalsIgnoreCase("delete")) {
            deleteToDo();
        } else if(param.equalsIgnoreCase("edit")) {
            editToDo();
        } else if(param.equalsIgnoreCase("list")) {
            listToDo();
        } else if(param.equalsIgnoreCase("info")) {
            infoToDo();
        } else if(param.equalsIgnoreCase("check")) {
            ToDoCheck();           
        } else {
            try {
                ToDoObject m = ToDoObject.forNum(contents, msg.getGuild().getId(), true);
                msg.getChannel().sendMessage(m.getToDoContent()).queue(success -> {msg.delete().queue();});
                return;
            } catch (IllegalArgumentException e) {
                em.setTitle("Error", null)
                .setColor(Color.RED)
                .setDescription(e.getMessage());
            }
        }
        msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});
    }

    /**
     * Create a ToDo
     */
    private void createToDo() {
        contents = Util.getCommandContents(contents);
        if(contents.equals("") || contents.split(" ").length < 1) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .addField(GuildObject.guildMap.get(msg.getGuild().getId()).getConfig().getCommandPrefix() 
                    + "ToDo create *contents*",
                    "You have designated to create a ToDo, but your"
                            + " command does not meet all requirements\n"
                            + "*contents* - Contents of the ToDo", true);
            return;
        }
        String[] resolved = resolveToDoNameAndContents(contents);
        Integer iter = 1;xd
        ToDoObject m = new ToDoObject(msg.getAuthor().getName(), resolved[0], iter,
                0, msg.getAuthor().getId(), msg.getGuild().getId());
        try {
            if(m.create()) {
                em.setTitle("Success", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("ToDo **" + iter + "** created");
            } else {
                while (! m.create()){
                    iter += 1;
                    ToDoObject m = new ToDoObject(msg.getAuthor().getName(), resolved[0], iter,
                            0, msg.getAuthor().getId(), msg.getGuild().getId());                  
                }
                if(m.create()) {
                    em.setTitle("Success", null)
                    .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                    .setDescription("Macro **" + iter + "** created");
                } else {
                    em.setTitle("Error", null)
                    .setColor(Color.RED)
                    .setDescription("Macro **" + iter + "** failed");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a ToDo
     */
    private void deleteToDo() {
        contents = Util.getCommandContents(contents);
        if(contents.equals("")) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .addField(GuildObject.guildMap.get(msg.getGuild().getId()).getConfig().getCommandPrefix() 
                    + "ToDo delete name",
                    "You have designated to delete a ToDo, "
                            + "but your command does not meet all requirements"
                            + "*name* - Name of the ToDo. No quotation "
                            + "marks for multi-worded ToDo", true);
            return;
        }
        try {
            ToDoObject m = ToDoObject.forNum(contents, msg.getGuild().getId());
            if(m.delete(msg.getAuthor().getId())) {
                em.setTitle("Success", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("ToDo **" + contents + "** deleted");
            } else {
                em.setTitle("Error", null)
                .setColor(Color.RED)
                .setDescription("You cannot delete ToDo **" + contents + "**")
            }
        } catch (IllegalArgumentException e) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .setDescription(e.getMessage());
        }
    }

    /**
     * Edit a ToDo
     */
    private void editToDo() {
        contents = Util.getCommandContents(contents);
        if(contents.equals("")) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .addField(GuildObject.guildMap.get(msg.getGuild().getId()).getConfig().getCommandPrefix() 
                    + "ToDo edit num content",
                    "You have designated to edit a ToDo, but your "
                            + "command does not meet all requirements"
                            + "*num* - Number of the ToDo."
                            + "*content* - Content of the ToDo", true);
            return;

        }
        String[] resolved = resolveToDoNameAndContents(contents);
        try {
            ToDoObject m = ToDoObject.forNum(resolved[0], msg.getGuild().getId());
            if(m.edit(msg.getAuthor().getId(), resolved[1])) {
                em.setTitle("Success", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("ToDo **" + resolved[0] + "** edited");
            } else {
                em.setTitle("Error", null)
                .setColor(Color.RED)
                .setDescription("You cannot edit ToDo **" + contents + "**")
            }
        } catch (IllegalArgumentException e) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .setDescription("ToDo **" + resolved[0] + "** does not exist");
        }
    }
  
    /**
     * List all ToDo made by a user
     */
    private void listToDo() {
        Member m;
        String possibleUser = Util.getCommandContents(this.contents);
        if (possibleUser.isEmpty()) {
            m = msg.getMember();
        } else {
            m = Util.resolveMemberFromMessage(possibleUser, msg.getGuild());
        }
        String[] results = ToDoObject.searchByUser(m.getUser().getId(), msg.getGuild().getId());
        if (results == null) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .setDescription("No ToDo found for user " + m.getEffectiveName());
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : results) {
            sb.append(s + ", ");
        }
        
        em.setTitle("ToDo created by " + m.getEffectiveName(), null)
        .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
        .setDescription(sb.substring(0, sb.length() - 2));
    }

    /**
     * Send information on a ToDo
     */
    private void infoToDo() {
        contents = Util.getCommandContents(contents);
        if(contents.equals("")) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .addField(GuildObject.guildMap.get(msg.getGuild().getId()).getConfig().getCommandPrefix() 
                    + "ToDo info name",
                    "You have designated to search for a ToDo, "
                            + "but your command does not meet all requirements"
                            + "*num* - Number of the ToDo to display info for. "
                            + "No quotation marks needed for multi-word ToDo", true);
            return;
        }
        try {
            ToDoObject m = ToDoObject.forNum(contents, msg.getGuild().getId());
            Member mem = msg.getGuild().getMemberById(m.getUserId());
            em.setTitle("Information on " + contents, null)
            .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
            .addField("Creator", mem == null ? m.getFallbackUsername() : mem.getEffectiveName(), true)
            .addField("Date created", m.getDate().toString(), true);
        } catch (IllegalArgumentException e) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .setDescription(e.getMessage());
        }
    }
    /**
     * Complete a ToDo
     */
    private void editToDo() {
        contents = Util.getCommandContents(contents);
        if(contents.equals("")) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .addField(GuildObject.guildMap.get(msg.getGuild().getId()).getConfig().getCommandPrefix() 
                    + "ToDo check num ",
                    "You have designated to check a ToDo, but your "
                            + "command does not meet all requirements"
                            + "*num* - Number of the ToDo.", true);
            return;

        }
        String[] resolved = resolveToDoNameAndContents(contents);
        try {
            ToDoObject m = ToDoObject.forNum(resolved[0], msg.getGuild().getId());
            if(m.edit(msg.getAuthor().getId(), "COMPLETE *******" + resolved[0) + "******* COMPLETE") {
                em.setTitle("Success", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("ToDo **" + resolved[0] + "** edited");
            } else {
                em.setTitle("Error", null)
                .setColor(Color.RED)
                .setDescription("You cannot check ToDo **" + resolved[0]+ "**")
            }
        } catch (IllegalArgumentException e) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .setDescription("ToDo **" + resolved[0] + "** does not exist");
        }
    }
  
    /**
     * Resolve ToDo name and contents from a create statement
     * This works to involve quotations around a spaced ToDo name
     * @param s The parameters of a create statement - The contents past the $ToDo create bit
     * @return Two index array: [0] is the ToDo name, [1] is the contents
     * Prerequisite: s.split() must have length of >= 2
     */
    private static String[] resolveToDoNameAndContents(String s) {
        String[] toReturn = new String[2];
        if(s.contains("\"") && StringUtils.countMatches(s, "\"") > 1) {
            int secondIndexOfQuotes = s.indexOf("\"", s.indexOf("\"") + 1);
            toReturn[0] = s.substring(s.indexOf("\"") + 1, secondIndexOfQuotes);
            toReturn[1] = s.substring(secondIndexOfQuotes + 2);
        } else {
            toReturn[0] = s.split(" ")[0];
            toReturn[1] = Util.getCommandContents(s);
        }
        return toReturn;
    }
}
