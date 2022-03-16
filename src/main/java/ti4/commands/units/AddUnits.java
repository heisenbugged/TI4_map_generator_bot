package ti4.commands.units;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ti4.helpers.Constants;
import ti4.map.Tile;

public class AddUnits extends AddRemoveUnits {
    @Override
    protected void unitAction(SlashCommandInteractionEvent event, Tile tile, int count, String planetName, String unitID) {

        tile.addUnit(planetName, unitID, count);
    }

    @Override
    public String getActionID() {
        return Constants.ADD_UNITS;
    }

    @Override
    protected String getActionDescription() {
        return "Add units to map";
    }


}
