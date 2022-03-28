package ti4.commands.agenda;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.helpers.Constants;
import ti4.map.Map;
import ti4.message.MessageHelper;

public class PutAgendaBottom extends AgendaSubcommandData {
    public PutAgendaBottom() {
        super(Constants.PUT_BOTTOM, "Put Agenda bottom");
        addOptions(new OptionData(OptionType.INTEGER, Constants.AGENDA_ID, "Agenda ID that is sent between ()").setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption(Constants.AGENDA_ID);
        if (option == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No Agenda ID defined");
            return;
        }
        Map activeMap = getActiveMap();
        boolean success = activeMap.putAgendaBottom(option.getAsInt());
        if (success) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Agenda put at bottom");
        } else {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No Agenda ID found");
        }
    }
}
