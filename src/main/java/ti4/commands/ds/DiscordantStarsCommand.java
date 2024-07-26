package ti4.commands.ds;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import ti4.commands.Command;
import ti4.helpers.Constants;
import ti4.map.Game;
import ti4.map.GameManager;
import ti4.map.GameSaveLoadManager;
import ti4.message.MessageHelper;

public class DiscordantStarsCommand implements Command {

    private final Collection<DiscordantStarsSubcommandData> subcommandData = getSubcommands();

    @Override
    public String getActionID() {
        return Constants.DS_COMMAND;
    }

    @Override
    public boolean accept(SlashCommandInteractionEvent event) {
        if (event.getName().equals(getActionID())) {
            String userID = event.getUser().getId();
            GameManager gameManager = GameManager.getInstance();
            if (!gameManager.isUserWithActiveGame(userID)) {
                MessageHelper.replyToMessage(event, "Set your active game using: `/set_game gameName`.");
                return false;
            }
            Game userActiveGame = gameManager.getUserActiveGame(userID);
            if (!userActiveGame.getPlayerIDs().contains(userID) && !userActiveGame.isCommunityMode()) {
                MessageHelper.replyToMessage(event, "You're not a player of the game, please call function `/join gameName`.");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String subcommandName = event.getInteraction().getSubcommandName();
        DiscordantStarsSubcommandData executedCommand = null;
        for (DiscordantStarsSubcommandData subcommand : subcommandData) {
            if (Objects.equals(subcommand.getName(), subcommandName)) {
                subcommand.preExecute(event);
                subcommand.execute(event);
                executedCommand = subcommand;
                break;
            }
        }
        if (executedCommand == null) {
            reply(event);
        } else {
            executedCommand.reply(event);
        }
    }

    public static void reply(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        Game game = GameManager.getInstance().getUserActiveGame(userID);
        GameSaveLoadManager.saveMap(game, event);

        //  FileUpload file = new GenerateMap().saveImage(activeMap, event);
        //  MessageHelper.replyToMessage(event, file);
    }

    protected String getActionDescription() {
        return "Discordant Stars Commands";
    }

    private Collection<DiscordantStarsSubcommandData> getSubcommands() {
        Collection<DiscordantStarsSubcommandData> subcommands = new HashSet<>();
        subcommands.add(new ZelianHero());
        subcommands.add(new TrapToken());
        subcommands.add(new TrapReveal());
        subcommands.add(new TrapSwap());
        subcommands.add(new FlipGrace());
        subcommands.add(new SetPolicy());
        subcommands.add(new DrawBlueBackTile());
        subcommands.add(new DrawRedBackTile());
        subcommands.add(new AddOmenDie());
        subcommands.add(new KyroHero());
        subcommands.add(new ATS());

        return subcommands;
    }

    @Override
    public void registerCommands(CommandListUpdateAction commands) {
        commands.addCommands(
            Commands.slash(getActionID(), getActionDescription())
                .addSubcommands(getSubcommands()));
    }
}
