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
        String command = Util.getCommandContents(msg).toLowerCase();
        EmbedBuilder em = new EmbedBuilder();
        if (command.length() == 0) {
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.NONE)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have NONE permissions");                              
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have NON permissions");   
            }
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.KICK)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have KICK permissions");                             
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have KICK permissions");   
            }
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.BAN)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have BAN permissions");                             
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have BAN permissions");   
            }
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.MANAGE_ROLES)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have MANAGE_ROLES permissions");                             
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have MANAGE_ROLES permissions");   
            }
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.MANAGE_CHANNELS)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have MANAGE_CHANNELS permissions");     
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have MANAGE_CHANNELS permissions");   
            }                       
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.MANAGE_SERVER)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have MANAGE_SERVER permissions");                             
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have MANAGE_SERVER permissions");   
            }
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.ADMINISTRATOR)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have ADMINISTRATOR permissions");                             
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have ADMINISTRATOR permissions");   
            }
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.BOT_OWNER)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have BOT_OWNER permissions");                             
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have BOT_OWNER permissions");   
            }
            if (Util.memberHasPermission(Util.resolveMemberFromMessage(command), Permission.BOT_DEVELOPER)) {
                em.setTitle("Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("You have BOT_DEVELOPER permissions");                             
            } else {
                em.setTitle("Not Allowed", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.RED))
                .setDescription("You DO NOT have BOT_DEVELOPER permissions");   
            }

        }
