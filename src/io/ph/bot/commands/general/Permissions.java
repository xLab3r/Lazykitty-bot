package io.ph.bot.commands.general;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.lang.StringBuilder;

import io.ph.bot.commands.Command;
import io.ph.bot.commands.CommandCategory;
import io.ph.bot.commands.CommandData;
import io.ph.bot.commands.CommandHandler;
import io.ph.bot.model.GuildObject;
import io.ph.bot.model.Permission;
import io.ph.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Member;
/**
 * Get help with commands
 * @author Nick
 */
/*
 *   NONE("No permissions", net.dv8tion.jda.core.Permission.MESSAGE_READ),
 *   KICK("Kick", net.dv8tion.jda.core.Permission.KICK_MEMBERS),
 *   BAN("Ban", net.dv8tion.jda.core.Permission.BAN_MEMBERS),
 *   MANAGE_ROLES("Manage roles", net.dv8tion.jda.core.Permission.MANAGE_ROLES),
 *   MANAGE_CHANNELS("Manage channels", net.dv8tion.jda.core.Permission.MANAGE_CHANNEL),
 *   MANAGE_SERVER("Manage server", net.dv8tion.jda.core.Permission.MANAGE_SERVER),
 *   ADMINISTRATOR("Administrator", net.dv8tion.jda.core.Permission.ADMINISTRATOR),
 *   BOT_OWNER("Bot owner", null);
 *   BOT_DEVELOPER("Bot developer", null);
*/

@CommandData (
        defaultSyntax = "permissions",
        aliases = {"permission","power"},
        category = CommandCategory.UTILITY,
        permission = Permission.NONE,
        description = "Tells users their server power level",
        example = ""
        )
public class Permissions extends Command {

    @Override
    public void executeCommand(Message msg) {
        String t = Util.getCommandContents(msg);
        Member target = null;
        String authorname = msg.getAuthor().getName();
        String suser = null;
    if (Util.memberHasPermission(msg.getGuild().getMember(msg.getAuthor()), Permission.BOT_OWNER)){
            if (msg.getMentionedUsers().size() > 0) {
                target = msg.getGuild().getMember(msg.getMentionedUsers().get(0));
                suser = msg.getGuild().getMember(msg.getMentionedUsers().get(0)).getEffectiveName();
            } else if (Util.resolveMemberFromMessage(t, msg.getGuild()) != null) {
                target = Util.resolveMemberFromMessage(t, msg.getGuild());
                suser = Util.resolveMemberFromMessage(t, msg.getGuild()).getEffectiveName();
            } else {
                target = msg.getGuild().getMember(msg.getAuthor());
                suser = authorname;
            }
        } else {
            target = msg.getGuild().getMember(msg.getAuthor());
            suser = authorname;
        }

        EmbedBuilder em = new EmbedBuilder();
        Integer c = 0;
        StringBuilder stringBuilder = new StringBuilder();
        
        if (Util.memberHasPermission(target, Permission.NONE)) {
            stringBuilder.append("ALLOWED NONE\n");
        } else
            stringBuilder.append("NOT ALLOWED NONE\n");

    
        if (Util.memberHasPermission(target, Permission.KICK)) {
            stringBuilder.append("ALLOWED KICK\n");
        } else
            stringBuilder.append("NOT ALLOWED KICK\n");

    
        if (Util.memberHasPermission(target, Permission.BAN)) {
            stringBuilder.append("ALLOWED BAN\n");
        } else
            stringBuilder.append("NOT ALLOWED BAN\n");

    
        if (Util.memberHasPermission(target, Permission.MANAGE_ROLES)) {
            stringBuilder.append("ALLOWED MANAGE_ROLES\n");
        } else
            stringBuilder.append("NOT ALLOWED MANAGE_ROLES\n");

    
        if (Util.memberHasPermission(target, Permission.MANAGE_SERVER)) {
            stringBuilder.append("ALLOWED MANAGE_SERVER\n");
        } else
            stringBuilder.append("NOT ALLOWED MANAGE_SERVER\n");

    
        if (Util.memberHasPermission(target, Permission.MANAGE_CHANNELS)) {
            stringBuilder.append("ALLOWED MANAGE_CHANNELS\n");
        } else
            stringBuilder.append("NOT ALLOWED MANAGE_CHANNELS\n");

    
        if (Util.memberHasPermission(target, Permission.BOT_OWNER)) {
            stringBuilder.append("ALLOWED BOT_OWNER\n");
        } else
            stringBuilder.append("NOT ALLOWED BOT_OWNER\n");


        if (Util.memberHasPermission(target, Permission.BOT_DEVELOPER)) {
            stringBuilder.append("ALLOWED BOT_DEVELOPER\n");
        } else
            stringBuilder.append("NOT ALLOWED BOT_DEVELOPER\n");


        String finalString = stringBuilder.toString();
        StringBuilder stringDisplay = new StringBuilder();

        stringDisplay.append("These are the permissions for guild: ");
        stringDisplay.append(msg.getGuild().getName()+"");
        stringDisplay.append("\n");
        stringDisplay.append("For the user: ");
        stringDisplay.append(suser+"");
        String finalDisplay = stringDisplay.toString();


        if (c == 0){
            msg.getAuthor().openPrivateChannel().queue(success -> {
                em.setTitle("Permissions", null)
                .setColor(Color.CYAN)
                .setDescription(finalDisplay)
                .addField("",finalString,true);
            msg.getAuthor().openPrivateChannel().complete()
            .sendMessage(em.build()).queue(success1 -> {
                em.clearFields();
                em.setTitle("Success", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("Check your PMs!");
                msg.getChannel().sendMessage(em.build()).queue();
                });
            msg.delete().queue();
            });                          
            c = 1;
        }
    }
}