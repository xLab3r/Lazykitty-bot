package io.ph.bot.commands.fun;

import io.ph.bot.commands.Command;
import io.ph.bot.commands.CommandCategory;
import io.ph.bot.commands.CommandData;
import io.ph.bot.model.Permission;
import io.ph.util.Util;
import net.dv8tion.jda.core.entities.Message;

@CommandData (
                defaultSyntax = "love",
                aliases = {"hug", "glomp"},
                category = CommandCategory.FUN,
                permission = Permission.NONE,
                description = "Gives love to another user.",
                example = "Jiminya"
                )

public class Love extends Command {
    @Override
    public void executeCommand(Message msg) {
                String lover = msg.getAuthor().getName();
                String loved = msg.getMentionedUsers().size() > 0 ? msg.getMentionedUsers().get(0).getName(): Util.getCommandContents(msg);
                msg.getChannel().sendMessage(lover + " gives love to " + loved).queue();
    }
}