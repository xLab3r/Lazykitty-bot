package io.ph.bot.commands.general;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        if (msg.getMentionedUsers().size() > 0) {
            target = msg.getGuild().getMember(msg.getMentionedUsers().get(0));
        } else {
            target = Util.resolveMemberFromMessage(msg.getAuthor().getName(), msg.getGuild());
        }

        EmbedBuilder em = new EmbedBuilder();
        if (Util.memberHasPermission(target, Permission.NONE)) {
            Integer c = 0;
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have NONE permissions"); 
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } else { 
            Integer c = 0;
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have NON permissions");
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } if (Util.memberHasPermission(target, Permission.KICK)) {
            Integer c = 0;
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have KICK permissions");     
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } else { 
            Integer c = 0;
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have KICK permissions");  
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } if (Util.memberHasPermission(target, Permission.BAN)) {
            Integer c = 0;
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have BAN permissions");       
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } else { 
            Integer c = 0;
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have BAN permissions");   
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } if (Util.memberHasPermission(target, Permission.MANAGE_ROLES)) {
            Integer c = 0;
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have MANAGE_ROLES permissions");
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }                            
        } else { 
            Integer c = 0;
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have MANAGE_ROLES permissions");   
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } if (Util.memberHasPermission(target, Permission.MANAGE_CHANNELS)) {
            Integer c = 0;
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have MANAGE_CHANNELS permissions");  
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } else { 
            Integer c = 0;
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have MANAGE_CHANNELS permissions");
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }                          
        } if (Util.memberHasPermission(target, Permission.MANAGE_SERVER)) {
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have MANAGE_SERVER permissions");
            Integer c = 0;    
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } else { 
            Integer c = 0;
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have MANAGE_SERVER permissions"); 
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } if (Util.memberHasPermission(target, Permission.ADMINISTRATOR)) {
            Integer c = 0;
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have ADMINISTRATOR permissions");    
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } else { 
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have ADMINISTRATOR permissions"); 
            Integer c = 0;
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } if (Util.memberHasPermission(target, Permission.BOT_OWNER)) {
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have BOT_OWNER permissions"); 
            Integer c = 0;
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } else { 
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have BOT_OWNER permissions");   
            Integer c = 0;
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } if (Util.memberHasPermission(target, Permission.BOT_DEVELOPER)) {
            em.setTitle("Allowed", null)
            .setColor(Util.resolveColor(target, Color.GREEN))
            .setDescription("You have BOT_DEVELOPER permissions");            
            Integer c = 0;
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        } else { 
            em.setTitle("Not Allowed", null)
            .setColor(Util.resolveColor(target, Color.RED))
            .setDescription("You DO NOT have BOT_DEVELOPER permissions");
            Integer c = 0;
            if (c == 0){
                msg.getChannel().sendMessage(em.build()).queue(success -> {msg.delete().queue();});                                
                c = 1;
            }
        }
    }
}