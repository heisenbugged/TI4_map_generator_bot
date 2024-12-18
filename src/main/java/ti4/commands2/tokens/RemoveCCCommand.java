package ti4.commands2.tokens;

import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.helpers.Constants;
import ti4.helpers.Emojis;
import ti4.helpers.FoWHelper;
import ti4.helpers.Helper;
import ti4.image.Mapper;
import ti4.map.Game;
import ti4.map.Tile;
import ti4.message.MessageHelper;

public class RemoveCCCommand extends AddRemoveTokenCommand {

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, Constants.TILE_NAME, "System/Tile name")
                        .setRequired(true)
                        .setAutoComplete(true),
                new OptionData(OptionType.STRING, Constants.FACTION_COLOR, "Faction or Color")
                        .setAutoComplete(true));
    }

    @Override
    void doAction(SlashCommandInteractionEvent event, List<String> colors, Tile tile, Game game) {
        for (String color : colors) {
            String ccID = Mapper.getCCID(color);
            String ccPath = tile.getCCPath(ccID);
            if (ccPath == null) {
                MessageHelper.sendMessageToChannel(event.getChannel(), "Command Counter: " + color + " is not valid and not supported.");
            }
            if (game.isFowMode()) {
                String colorMention = Emojis.getColorEmojiWithName(color);
                FoWHelper.pingSystem(game, event, tile.getPosition(), colorMention + " has removed a token in the system");
            }

            tile.removeCC(ccID);
            Helper.isCCCountCorrect(event, game, color);
        }
    }

    @Override
    public String getDescription() {
        return "Remove CCs from tile/system";
    }

    @Override
    public String getName() {
        return Constants.REMOVE_CC;
    }
}
