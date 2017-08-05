package io.ph.bot.commands.general;

import java.awt.Color;

import io.ph.bot.Bot;
import io.ph.bot.commands.Command;
import io.ph.bot.commands.CommandCategory;
import io.ph.bot.commands.CommandData;
import io.ph.bot.model.Permission;
import io.ph.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * Information & intro
 * @author Nick
 *
 */
@CommandData (
		defaultSyntax = "info",
		aliases = {"information"},
		category = CommandCategory.UTILITY,
		permission = Permission.NONE,
		description = "Information on the bot",
		example = "(no parameters)"
		)
public class Info extends Command {

	@Override
	public void executeCommand(Message msg) {
		EmbedBuilder em = new EmbedBuilder();
		em.setTitle("Hi, I'm " + msg.getGuild().getMember(msg.getJDA().getSelfUser()).getEffectiveName(), null)
		.setColor(Util.resolveColor(Util.memberFromMessage(msg), Color.MAGENTA))
		.addField("Repository", "<https://github.com/nickalaskreynolds/Momobutt.git>", true)
		.addField("Invite Server link", Bot.getInstance().getConfig().getBotInviteLink(), true)
		.addField("Invite Bot link", Bot.getInstance().getConfig().getBotInviteBotLink(), true)
		.addField("Command list", "run !commands", true)
		.setDescription("I can do a lot of things! Too many to list here, though. Feel free to take a look "
				+ "through the links below, though, to get a quick rundown of my features")
		.setThumbnail(msg.getJDA().getSelfUser().getAvatarUrl())
		.setFooter(String.format("Version %s | Made with <3 by %s", 
				Bot.BOT_VERSION,
				"Kagumi and contributions from Nick"), null);
		msg.getChannel().sendMessage(em.build()).queue();
	}

}
