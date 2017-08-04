package io.ph.bot.commands.general;

import java.awt.Color;
import java.util.stream.Collectors;

import io.ph.bot.Bot;
import io.ph.bot.commands.Command;
import io.ph.bot.commands.CommandCategory;
import io.ph.bot.commands.CommandData;
import io.ph.bot.model.Permission;
import io.ph.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

/**
 * Send suggestion to creator
 * @author Nick
 */
@CommandData (
        defaultSyntax = "suggestion",
        aliases = {"suggest","suggestions"},
        category = CommandCategory.UTILITY,
        permission = Permission.NONE,
        description = "Send a PM to the developer with suggestions\n"
                    + "Please be descriptive with your ideas\n"
                    + "Please be patient and wait for a reply\n"
        ,
        example = "Add love functionality, !love user to send :heart: to them"
        )
public class Suggestion extends Command {


    @Override
    public void executeCommand(Message msg) {
        EmbedBuilder em = new EmbedBuilder();
        if(msg.getContent().split(" ").length < 2) {
            MessageUtils.sendIncorrectCommandUsage(msg, this);
            return;
        }
        String suggestContents = Util.getCommandContents(Util.getCommandContents(msg));
        if(suggestionContents.length() > 500) {
            em.setTitle("Error", null)
            .setColor(Color.RED)
            .setDescription("Maximum suggestion length is 500 characters. Yours is " 
            + suggestionContents.length());
            msg.getChannel().sendMessage(em.build()).queue();
            return;
        }

        String sb = suggestContents;

        Instant now = Instant.now();
        currtime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());


        Bot.getBotDeveloperId().getUserById().openPrivateChannel().queue(success -> {
            em.setTitle("Suggestions", null)
            .setColor(Color.CYAN)
            .addField(sb, false)
            .setFooter("Message was sent ", null);
            Bot.getBotDeveloperId().getUserById().openPrivateChannel().complete()
            .sendMessage(em.build()).queue(success1 -> {
                em.clearFields();
                em.setTitle("Success", null)
                .setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.GREEN))
                .setDescription("Your suggestion has been sent!");
                msg.getChannel().sendMessage(em.build()).queue();
            });
        });
    }
}