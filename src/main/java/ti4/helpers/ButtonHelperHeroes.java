package ti4.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ti4.buttons.Buttons;
import ti4.commands.cardsac.ACInfo;
import ti4.commands.cardspn.PNInfo;
import ti4.commands.combat.StartCombat;
import ti4.commands.franken.LeaderAdd;
import ti4.commands.leaders.HeroPlay;
import ti4.commands.leaders.UnlockLeader;
import ti4.commands.planet.PlanetAdd;
import ti4.commands.planet.PlanetRefresh;
import ti4.commands.player.ClearDebt;
import ti4.commands.player.SCPlay;
import ti4.commands.units.AddUnits;
import ti4.commands.units.MoveUnits;
import ti4.commands.units.RemoveUnits;
import ti4.generator.Mapper;
import ti4.helpers.Units.UnitKey;
import ti4.helpers.Units.UnitType;
import ti4.map.Game;
import ti4.map.Leader;
import ti4.map.Planet;
import ti4.map.Player;
import ti4.map.Tile;
import ti4.map.UnitHolder;
import ti4.message.MessageHelper;
import ti4.model.ExploreModel;
import ti4.model.PlanetModel;
import ti4.model.PublicObjectiveModel;
import ti4.model.TechnologyModel;
import ti4.model.TechnologyModel.TechnologyType;
import ti4.model.UnitModel;

public class ButtonHelperHeroes {

    public static void argentHeroStep1(Game game, Player player, GenericInteractionCreateEvent event) {
        List<Button> buttons = new ArrayList<>();
        for (Tile tile : ButtonHelper.getTilesWithYourCC(player, game, event)) {
            buttons.add(Button.success("argentHeroStep2_" + tile.getPosition(),
                tile.getRepresentationForButtons(game, player)));
        }
        buttons.add(Button.danger("deleteButtons", "Done Resolving"));
        String msg = player.getRepresentation() + " choose the tile you wish to move stuff to using Mirik Aun Sissiri, the Argent hero.";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg, buttons);
    }

    public static void argentHeroStep2(Game game, Player player, GenericInteractionCreateEvent event, String buttonID) {
        List<Button> buttons = new ArrayList<>();
        String pos1 = buttonID.split("_")[1];
        Tile destination = game.getTileByPosition(pos1);
        for (Tile tile : ButtonHelper.getTilesWithShipsInTheSystem(player, game)) {
            buttons.add(Button.success("argentHeroStep3_" + pos1 + "_" + tile.getPosition(),
                tile.getRepresentationForButtons(game, player)));
        }
        String msg = player.getRepresentation()
            + " choose the tile you wish to move stuff from. These will move stuff to "
            + destination.getRepresentationForButtons(game, player) + ".";
        buttons.add(Button.danger("deleteButtons",
            "Done Moving To " + destination.getRepresentationForButtons(game, player)));
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg, buttons);
    }

    public static List<Button> getArgentHeroStep3Buttons(Game game, Player player, GenericInteractionCreateEvent event,
        String buttonID) {
        List<Button> buttons = new ArrayList<>();
        String pos1 = buttonID.split("_")[1];
        Tile destination = game.getTileByPosition(pos1);
        String pos2 = buttonID.split("_")[2];
        Tile origin = game.getTileByPosition(pos2);
        for (UnitHolder unitHolder : origin.getUnitHolders().values()) {
            Map<UnitKey, Integer> units = new HashMap<>(unitHolder.getUnits());
            for (Map.Entry<UnitKey, Integer> unitEntry : units.entrySet()) {
                if (!player.unitBelongsToPlayer(unitEntry.getKey()))
                    continue;
                UnitModel unitModel = player.getUnitFromUnitKey(unitEntry.getKey());
                if (unitModel == null)
                    continue;
                UnitKey unitKey = unitEntry.getKey();
                String unitName = ButtonHelper.getUnitName(unitKey.asyncID());
                int totalUndamagedUnits = unitEntry.getValue();
                int damagedUnits = 0;

                if (unitHolder.getUnitDamage() != null && unitHolder.getUnitDamage().get(unitKey) != null) {
                    damagedUnits = unitHolder.getUnitDamage().get(unitKey);
                }
                String end = "";
                if (!"space".equalsIgnoreCase(unitHolder.getName())) {
                    end = " from " + Helper.getPlanetRepresentation(unitHolder.getName(), game);
                }
                totalUndamagedUnits = totalUndamagedUnits - damagedUnits;
                if (totalUndamagedUnits > 0) {
                    buttons.add(Button.success("argentHeroStep4_" + pos1 + "_" + origin.getPosition() + "_"
                        + unitHolder.getName() + "_" + unitName, "1 " + unitName + end));
                }
                if (damagedUnits > 0) {
                    buttons.add(Button.success("argentHeroStep4_" + pos1 + "_" + origin.getPosition() + "_"
                        + unitHolder.getName() + "_" + unitName + "damaged", "1 damaged " + unitName + end));
                }

            }
        }
        buttons.add(Button.danger("deleteButtons",
            "Done Moving To " + destination.getPosition() + " From " + origin.getPosition()));
        return buttons;
    }

    public static void argentHeroStep3(Game game, Player player, GenericInteractionCreateEvent event, String buttonID) {
        List<Button> buttons = getArgentHeroStep3Buttons(game, player, event, buttonID);
        String pos1 = buttonID.split("_")[1];
        Tile destination = game.getTileByPosition(pos1);
        // String pos2 = buttonID.split("_")[2];
        // Tile origin = game.getTileByPosition(pos2);
        String msg = player.getRepresentation() + " choose the units you wish to move. These will move stuff to " + destination.getRepresentationForButtons(game, player) + ".";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg, buttons);
    }

    public static void argentHeroStep4(Game game, Player player, ButtonInteractionEvent event, String buttonID) {
        List<Button> buttons = getArgentHeroStep3Buttons(game, player, event, buttonID);
        String pos1 = buttonID.split("_")[1];
        Tile destination = game.getTileByPosition(pos1);
        String pos2 = buttonID.split("_")[2];
        Tile origin = game.getTileByPosition(pos2);
        String unitHolderName = buttonID.split("_")[3];
        String unitName = buttonID.split("_")[4];
        boolean damaged = false;
        if (unitName.contains("damaged")) {
            damaged = true;
            unitName = unitName.replace("damaged", "");
        }
        destination = MoveUnits.flipMallice(event, destination, game);
        new RemoveUnits().unitParsing(event, player.getColor(), origin, unitName + " " + unitHolderName, game);
        new AddUnits().unitParsing(event, player.getColor(), destination, unitName, game);
        String msg2 = player.getFactionEmoji() + " moved 1 " + unitName + " from "
            + origin.getRepresentationForButtons(game, player) + " to "
            + destination.getRepresentationForButtons(game, player) + ".";
        if (damaged) {
            origin.getUnitHolders().get(unitHolderName)
                .removeUnitDamage(Mapper.getUnitKey(AliasHandler.resolveUnit(unitName), player.getColorID()), 1);
            destination.getUnitHolders().get("space")
                .addUnitDamage(Mapper.getUnitKey(AliasHandler.resolveUnit(unitName), player.getColorID()), 1);
            msg2 = player.getFactionEmoji() + " moved 1 damaged " + unitName + " from "
                + origin.getRepresentationForButtons(game, player) + " to "
                + destination.getRepresentationForButtons(game, player) + ".";
        }

        MessageHelper.sendMessageToChannel(event.getMessageChannel(), msg2);
        String msg = player.getRepresentation() + " choose the units you wish to move. These will move stuff to "
            + destination.getRepresentationForButtons(game, player) + ".";
        event.getMessage().editMessage(msg)
            .setComponents(ButtonHelper.turnButtonListIntoActionRowList(buttons)).queue();
    }

    public static List<String> getAttachmentsForFlorzenHero(Game game, Player player) {
        List<String> legendaries = new ArrayList<>();
        for (Tile tile : game.getTileMap().values()) {
            if (!FoWHelper.playerHasShipsInSystem(player, tile)) {
                continue;
            }
            for (UnitHolder uh : tile.getPlanetUnitHolders()) {
                for (String token : uh.getTokenList()) {
                    if (!token.contains("attachment")) {
                        continue;
                    }
                    token = token.replace(".png", "").replace("attachment_", "");

                    String s = uh.getName() + "_" + token;
                    if (!token.contains("sleeper") && !token.contains("control") && !legendaries.contains(s)) {
                        legendaries.add(s);
                    }
                }
            }
        }
        return legendaries;
    }

    public static void resolveGledgeHero(Player player, Game game) {
        List<Button> buttons = getAttachmentSearchButtons(game, player);
        String msg = player.getRepresentation() + " use these buttons to find attachments. Explore #";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg + "1.", buttons);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg + "2.", buttons);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg + "3.", buttons);

    }

    public static void resolveKhraskHero(Player player, Game game) {
        List<Button> buttons = new ArrayList<>();
        for (Player p2 : game.getRealPlayers()) {
            if (game.isFowMode()) {
                buttons.add(Button.secondary("khraskHeroStep2_" + p2.getFaction(), p2.getColor()));
            } else {
                Button button = Button.secondary("khraskHeroStep2_" + p2.getFaction(), " ");
                String factionEmojiString = p2.getFactionEmoji();
                button = button.withEmoji(Emoji.fromFormatted(factionEmojiString));
                buttons.add(button);
            }
        }
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " tell the bot who's planet you wish to exhaust or ready.",
            buttons);
    }

    public static void resolveKhraskHeroStep2(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        List<Button> buttons = new ArrayList<>();
        String faction = buttonID.split("_")[1];
        buttons.add(Button.success("khraskHeroStep3Ready_" + faction, "Ready a Planet"));
        buttons.add(Button.danger("khraskHeroStep3Exhaust_" + faction, "Exhaust a Planet"));
        ButtonHelper.deleteMessage(event);
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " tell the bot if you wish to exhaust or ready a planet.",
            buttons);
    }

    public static void resolveKhraskHeroStep3Exhaust(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        Player p2 = game.getPlayerFromColorOrFaction(buttonID.split("_")[1]);
        if (p2.getReadiedPlanets().isEmpty()) {
            MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
                "Chosen player had no readied planets. Nothing has been done.");
            ButtonHelper.deleteMessage(event);
            return;
        }
        List<Button> buttons = new ArrayList<>();
        for (String planet : p2.getReadiedPlanets()) {
            buttons.add(Button.secondary("khraskHeroStep4Exhaust_" + p2.getFaction() + "_" + planet,
                Helper.getPlanetRepresentation(planet, game)));
        }
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " select the planet you wish to exhaust.", buttons);
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveKhraskHeroStep4Exhaust(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        Player p2 = game.getPlayerFromColorOrFaction(buttonID.split("_")[1]);
        String planet = buttonID.split("_")[2];
        String planetRep = Helper.getPlanetRepresentation(planet, game);
        p2.exhaustPlanet(planet);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), player.getRepresentation(true, true) + " you exhausted " + planetRep + ".");
        MessageHelper.sendMessageToChannel(p2.getCorrectChannel(), p2.getRepresentation(true, true) + " your planet " + planetRep + " was exhausted.");
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveAxisHeroStep1(Player player, Game game) {
        List<Button> buttons = new ArrayList<>();
        String message = player.getRepresentation() + " Click the Axis Order you would like to send.";
        for (String shipOrder : ButtonHelper.getPlayersShipOrders(player)) {
            Button transact = Button.success("axisHeroStep2_" + shipOrder, "" + Mapper.getRelic(shipOrder).getName());
            buttons.add(transact);
        }
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(), message, buttons);
    }

    public static List<String> getAllRevealedRelics(Game game) {
        List<String> relicsTotal = new ArrayList<>();
        for (Player player : game.getRealPlayers()) {
            for (String relic : player.getRelics()) {
                if (relic.contains("axisorder") || relic.contains("enigmatic") || relic.contains("shiporder") || relic.contains("starchart")) {
                    continue;
                }
                relicsTotal.add(player.getFaction() + ";" + relic);
            }
        }

        String key = "lanefirRelicReveal";
        for (int x = 1; x < 4; x++) {
            if (!game.getStoredValue(key + x).isEmpty()) {
                relicsTotal.add(key + ";" + game.getStoredValue(key + x));
            }
        }
        return relicsTotal;
    }

    public static void cheiranHeroResolution(Player player, Game game, GenericInteractionCreateEvent event) {
        List<Button> buttons = new ArrayList<>();
        // "dn,cv,dd,2 ff,mech a,2 inf g,sd a"
        buttons.addAll(Helper.getTileForCheiranHeroPlaceUnitButtons(player, game, "dreadnought",
            "placeOneNDone_skipbuild"));
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 dreadnought.", buttons);
        buttons = new ArrayList<>();
        buttons.addAll(
            Helper.getTileWithTrapsPlaceUnitButtons(player, game, "destroyer", "placeOneNDone_skipbuild"));
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 destroyer.", buttons);
        buttons = new ArrayList<>();
        buttons.addAll(
            Helper.getTileWithTrapsPlaceUnitButtons(player, game, "carrier", "placeOneNDone_skipbuild"));
        buttons = new ArrayList<>();
        buttons.addAll(Helper.getTileWithTrapsPlaceUnitButtons(player, game, "ff", "placeOneNDone_skipbuild"));
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 fighter.", buttons);
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 fighter.", buttons);

        buttons = new ArrayList<>();
        buttons.addAll(Helper.getTileWithTrapsPlaceUnitButtons(player, game, "gf", "placeOneNDone_skipbuild"));
        buttons.addAll(Helper.getPlanetPlaceUnitButtons(player, game, "gf", "placeOneNDone_skipbuild"));
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 infantry.", buttons);
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 infantry.", buttons);

        buttons = new ArrayList<>();
        buttons.addAll(Helper.getTileWithTrapsPlaceUnitButtons(player, game, "sd", "placeOneNDone_skipbuild"));
        buttons.addAll(Helper.getPlanetPlaceUnitButtons(player, game, "sd", "placeOneNDone_skipbuild"));
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 space dock.", buttons);

        buttons = new ArrayList<>();
        buttons.addAll(Helper.getTileWithTrapsPlaceUnitButtons(player, game, "mech", "placeOneNDone_skipbuild"));
        buttons.addAll(Helper.getPlanetPlaceUnitButtons(player, game, "mech", "placeOneNDone_skipbuild"));
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 mech.", buttons);

    }

    public static void lizhoHeroFighterDistribution(Player player, Game game, ButtonInteractionEvent event) {
        ButtonHelper.deleteMessage(event);
        List<Button> buttons = new ArrayList<>();
        buttons.addAll(Helper.getTileWithTrapsPlaceUnitButtons(player, game, "2ff", "placeOneNDone_skipbuild"));
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 2 fighters.", buttons);
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 2 fighters.", buttons);
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 2 fighters.", buttons);
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 2 fighters.", buttons);
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 2 fighters.", buttons);
        buttons = new ArrayList<>();
        buttons.addAll(Helper.getTileWithTrapsPlaceUnitButtons(player, game, "ff", "placeOneNDone_skipbuild"));
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 fighter.", buttons);
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Place 1 fighter.", buttons);
    }

    public static void resolveLanefirHeroStep1(Player player, Game game) {
        List<String> revealedRelics = getAllRevealedRelics(game);
        String key = "lanefirRelicReveal";
        String revealMsg = player.getRepresentation() + " you revealed the following 3 relics:\n";
        for (int x = 1; x < 4; x++) {
            String relic = game.drawRelic();
            game.setStoredValue(key + x, relic);
            revealMsg = revealMsg + x + ". " + Mapper.getRelic(relic).getName() + "\n";
        }
        int size = revealedRelics.size();
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
            player.getFactionEmoji() + " may gain " + size + " command token" + (size == 1 ? "" : "s") + ".");
        List<Button> buttons = ButtonHelper.getGainCCButtons(player);
        String trueIdentity = player.getRepresentation(true, true);
        String message2 = trueIdentity + "! Your current command tokens are " + player.getCCRepresentation()
            + ". Use buttons to gain command tokens.";
        game.setStoredValue("originalCCsFor" + player.getFaction(), player.getCCRepresentation());
        MessageHelper.sendMessageToChannelWithButtons(
            player.getCorrectChannel(), message2, buttons);
        revealedRelics = getAllRevealedRelics(game);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), revealMsg);

        List<Button> relicButtons = new ArrayList<Button>();
        for (String fanctionNRelic : revealedRelics) {
            String relic = fanctionNRelic.split(";")[1];
            relicButtons.add(Button.success("relicSwapStep1_" + fanctionNRelic, Mapper.getRelic(relic).getName()));
        }
        String revealMsg2 = player.getRepresentation()
            + " use buttons to swap any revealed relic or relic in play area with another relic. No Automated effects of a relic gain or loss will be applied. All relics may only move places once.\n";
        MessageHelper.sendMessageToChannelWithButtons(
            player.getCorrectChannel(), revealMsg2, relicButtons);
    }

    public static void resolveRelicSwapStep1(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        String fanctionNRelic = buttonID.replace("relicSwapStep1_", "");
        ButtonHelper.deleteTheOneButton(event);
        String relic = fanctionNRelic.split(";")[1];
        List<String> revealedRelics = getAllRevealedRelics(game);
        String revealMsg = player.getRepresentation() + " you chose to swap the relic "
            + Mapper.getRelic(relic).getName() + ". Choose another relic to swap it with.";
        List<Button> relicButtons = new ArrayList<Button>();
        for (String fanctionNRelic2 : revealedRelics) {
            if (fanctionNRelic.equalsIgnoreCase(fanctionNRelic2)) {
                continue;
            }
            String relic2 = fanctionNRelic2.split(";")[1];
            relicButtons.add(Button.success("relicSwapStep2;" + fanctionNRelic + ";" + fanctionNRelic2,
                Mapper.getRelic(relic2).getName()));
        }
        MessageHelper.sendMessageToChannelWithButtons(
            player.getCorrectChannel(), revealMsg, relicButtons);
    }

    public static void resolveRelicSwapStep2(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        ButtonHelper.deleteMessage(event);
        String faction = buttonID.split(";")[1];
        String relic = buttonID.split(";")[2];
        String faction2 = buttonID.split(";")[3];
        String relic2 = buttonID.split(";")[4];
        String revealMsg = player.getRepresentation() + " you chose to swap the relic "
            + Mapper.getRelic(relic).getName() + " with the relic " + Mapper.getRelic(relic2).getName() + ".";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
            revealMsg);
        if (faction.contains("lanefirRelicReveal")) {
            game.removeStoredValue(faction);
        } else {
            Player p2 = game.getPlayerFromColorOrFaction(faction);
            String msg = p2.getRepresentation() + " your relic " + Mapper.getRelic(relic).getName()
                + " was swapped via The Venerable, the Lanefir hero, with the relic " + Mapper.getRelic(relic2).getName()
                + ". Please resolve any necessary effects of this transition.";
            p2.removeRelic(relic);
            p2.addRelic(relic2);
            MessageHelper.sendMessageToChannel(p2.getCorrectChannel(), msg);
        }
        if (faction2.contains("lanefirRelicReveal")) {
            game.removeStoredValue(faction2);
        } else {
            Player p2 = game.getPlayerFromColorOrFaction(faction2);
            String msg = p2.getRepresentation() + " your relic " + Mapper.getRelic(relic2).getName()
                + " was swapped via The Venerable, the Lanefir hero, with the relic " + Mapper.getRelic(relic).getName()
                + ". Please resolve any necessary effects of this transition.";
            p2.removeRelic(relic2);
            p2.addRelic(relic);
            MessageHelper.sendMessageToChannel(p2.getCorrectChannel(), msg);
        }

    }

    public static void resolveAxisHeroStep2(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        List<Button> buttons = new ArrayList<>();
        String shipOrder = buttonID.split("_")[1];
        String message = player.getRepresentation()
            + " Click the player you would like to give the order to, and force them to give you a promissory note.";
        for (Player p2 : game.getRealPlayers()) {
            if (p2 == player) {
                continue;
            }
            if (game.isFowMode()) {
                buttons.add(Button.secondary("axisHeroStep3_" + shipOrder + "_" + p2.getFaction(), p2.getColor()));
            } else {
                Button button = Button.secondary("axisHeroStep3_" + shipOrder + "_" + p2.getFaction(), " ");
                String factionEmojiString = p2.getFactionEmoji();
                button = button.withEmoji(Emoji.fromFormatted(factionEmojiString));
                buttons.add(button);
            }
        }
        ButtonHelper.deleteTheOneButton(event);
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(), message,
            buttons);
    }

    public static void resolveAxisHeroStep3(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        String shipOrder = buttonID.split("_")[1];
        String faction = buttonID.split("_")[2];
        Player p2 = game.getPlayerFromColorOrFaction(faction);
        String message = player.getRepresentation() + " sent " + Mapper.getRelic(shipOrder).getName() + " to "
            + ButtonHelper.getIdentOrColor(p2, game)
            + " who now owes a promissory note. Buttons have been sent to the player's `#Cards Info` thread to resolve";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), message);
        List<Button> stuffToTransButtons = ButtonHelper.getForcedPNSendButtons(game, player, p2);
        String message2 = p2.getRepresentation(true, true)
            + " You have been given an Axis Order by Demi-Queen Mdcksssk, the Axis hero, and now must send a promissory note in return. Please select the promissory note you would like to send.";
        MessageHelper.sendMessageToChannelWithButtons(p2.getCardsInfoThread(), message2, stuffToTransButtons);
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveKhraskHeroStep3Ready(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        Player p2 = game.getPlayerFromColorOrFaction(buttonID.split("_")[1]);
        if (p2.getExhaustedPlanets().isEmpty()) {
            MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
                "Chosen player had no exhausted planets. Nothing has been done.");
            ButtonHelper.deleteMessage(event);
            return;
        }
        List<Button> buttons = new ArrayList<>();
        for (String planet : p2.getExhaustedPlanets()) {
            buttons.add(Button.secondary("khraskHeroStep4Ready_" + p2.getFaction() + "_" + planet,
                Helper.getPlanetRepresentation(planet, game)));
        }
        ButtonHelper.deleteMessage(event);
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " select the planet you wish to ready.", buttons);
    }

    public static void resolveKhraskHeroStep4Ready(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        Player p2 = game.getPlayerFromColorOrFaction(buttonID.split("_")[1]);
        String planet = buttonID.split("_")[2];
        String planetRep = Helper.getPlanetRepresentation(planet, game);
        ButtonHelper.deleteMessage(event);
        p2.refreshPlanet(planet);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " you readied " + planetRep);
        if (p2 != player) {
            MessageHelper.sendMessageToChannel(p2.getCorrectChannel(),
                p2.getRepresentation(true, true) + " your planet " + planetRep + " was readied.");
            if (player.hasLeader("xxchaagent")) {
                UnitHolder uH = ButtonHelper.getUnitHolderFromPlanetName(planet, game);
                if (uH != null && uH.getUnitCount(UnitType.Infantry, p2) > 0) {
                    Tile tile = game.getTileFromPlanet(planet);
                    for (String tilePos : FoWHelper.getAdjacentTiles(game, tile.getPosition(), player, false, true)) {
                        Tile tile2 = game.getTileByPosition(tilePos);
                        if (FoWHelper.playerHasShipsInSystem(player, tile2)) {
                            List<Button> buttons = new ArrayList<>();
                            buttons.add(Button.danger("xxchaAgentRemoveInfantry_" + p2.getFaction() + "_" + planet, "Remove Infantry"));
                            buttons.add(Button.secondary("deleteButtons", "Decline"));
                            MessageHelper.sendMessageToChannel(player.getCorrectChannel(), player.getRepresentation() + " you have the opportunity to remove 1 of the infantry on the readied planet if you wish.", buttons);

                            return;
                        }
                    }

                }
            }
        }

    }

    public static List<Button> getAttachmentSearchButtons(Game game, Player player) {
        List<Button> buttons = new ArrayList<>();
        Set<String> types = ButtonHelper.getTypesOfPlanetPlayerHas(game, player);
        for (String type : types) {
            if ("industrial".equals(type) && doesExploreDeckHaveAnAttachmentLeft(type, game)) {
                buttons.add(Button.success("findAttachmentInDeck_industrial", "Explore Industrial"));
            }
            if ("cultural".equals(type) && doesExploreDeckHaveAnAttachmentLeft(type, game)) {
                buttons.add(Button.primary("findAttachmentInDeck_cultural", "Explore Cultural"));
            }
            if ("hazardous".equals(type) && doesExploreDeckHaveAnAttachmentLeft(type, game)) {
                buttons.add(Button.danger("findAttachmentInDeck_hazardous", "Explore Hazardous"));
            }
        }
        return buttons;
    }

    public static void resolveAttachAttachment(Player player, Game game, String buttonID,
        ButtonInteractionEvent event) {
        ButtonHelper.deleteMessage(event);
        String planet = buttonID.split("_")[1];
        String attachment = buttonID.replace("attachAttachment_" + planet + "_", "");
        String planetID = planet;
        Tile tile = game.getTileFromPlanet(planet);
        PlanetModel planetInfo = Mapper.getPlanet(planetID);
        if (Optional.ofNullable(planetInfo).isPresent()) {
            if (Optional.ofNullable(planetInfo.getTechSpecialties()).orElse(new ArrayList<>()).size() > 0
                || ButtonHelper.doesPlanetHaveAttachmentTechSkip(tile, planetID)) {
                if ((attachment.equals(Constants.WARFARE) ||
                    attachment.equals(Constants.PROPULSION) ||
                    attachment.equals(Constants.CYBERNETIC) ||
                    attachment.equals(Constants.BIOTIC) ||
                    attachment.equals(Constants.WEAPON))) {
                    attachment = attachment + "stat";
                }
            }
        }
        UnitHolder uH = ButtonHelper.getUnitHolderFromPlanetName(planet, game);
        uH.addToken("attachment_" + attachment + ".png");
        String msg = player.getRepresentation(true, true) + " put " + attachment + " on "
            + Helper.getPlanetRepresentation(planet, game);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg);
    }

    public static List<Button> getAttachmentAttach(Game game, Player player, String type, String attachment) {
        List<Button> buttons = new ArrayList<>();
        for (String planet : player.getPlanetsAllianceMode()) {
            if (planet.toLowerCase().contains("custodia") || planet.contains("ghoti")) {
                continue;
            }
            Planet p = (Planet) ButtonHelper.getUnitHolderFromPlanetName(planet, game);
            if (p != null && (type.equalsIgnoreCase(p.getOriginalPlanetType())
                || p.getTokenList().contains("attachment_titanspn.png"))) {
                buttons.add(Button.success("attachAttachment_" + planet + "_" + attachment,
                    Helper.getPlanetRepresentation(planet, game)));
            }
        }
        return buttons;
    }

    public static void findAttachmentInDeck(Player player, Game game, String buttonID,
        ButtonInteractionEvent event) {
        String type = buttonID.split("_")[1];
        int counter = 0;
        boolean foundOne = false;
        StringBuilder sb = new StringBuilder();
        MessageChannel channel = player.getCorrectChannel();
        if (!doesExploreDeckHaveAnAttachmentLeft(type, game)) {
            MessageHelper.sendMessageToChannel(channel, "The " + type + " deck doesn't have any attachments left in it.");
            return;
        } else {
            ButtonHelper.deleteMessage(event);
        }
        while (!foundOne && counter < 40) {
            counter++;
            String cardID = game.drawExplore(type);
            ExploreModel explore = Mapper.getExplore(cardID);
            sb.append(explore.textRepresentation()).append(System.lineSeparator());
            if ("attach".equalsIgnoreCase(explore.getResolution())) {
                foundOne = true;
                sb.append(player.getRepresentation()).append(" Found attachment " + explore.getName() + ".");
                game.purgeExplore(cardID);
                MessageHelper.sendMessageToChannel(channel, sb.toString());
                String msg = player.getRepresentation() + " tell the bot what planet this should be attached too.";
                MessageHelper.sendMessageToChannel(channel, msg,
                    getAttachmentAttach(game, player, type, explore.getAttachmentId().get()));
                return;
            }
        }

    }

    public static boolean doesExploreDeckHaveAnAttachmentLeft(String type, Game game) {

        List<String> deck = game.getExploreDeck(type);
        deck.addAll(game.getExploreDiscard(type));
        for (String cardID : deck) {
            ExploreModel explore = Mapper.getExplore(cardID);
            if ("attach".equalsIgnoreCase(explore.getResolution())) {
                return true;
            }
        }

        return false;
    }

    public static void resolveFlorzenHeroStep1(Player player, Game game) {
        List<Button> buttons = new ArrayList<>();
        for (String attachment : getAttachmentsForFlorzenHero(game, player)) {
            String planet = attachment.split("_")[0];
            String attach = attachment.split("_")[1];
            buttons.add(Button.success("florzenHeroStep2_" + attachment,
                attach + " on " + Helper.getPlanetRepresentation(planet, game)));
        }
        String msg = player.getRepresentation(true, true) + " choose the attachment you wish to steal.";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg, buttons);
    }

    public static void resolveFlorzenHeroStep2(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        String planet = buttonID.split("_")[1];
        String attachment = buttonID.split("_")[2];
        List<Button> buttons = new ArrayList<>();
        Tile hs = player.getHomeSystemTile();
        for (UnitHolder uh : hs.getPlanetUnitHolders()) {
            String planet2 = uh.getName();
            buttons.add(Button.success("florzenAgentStep3_" + planet + "_" + planet2 + "_" + attachment,
                Helper.getPlanetRepresentation(planet2, game)));
        }

        String msg = player.getRepresentation(true, true) + " choose the home system planet you wish to put the attachment on.";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg, buttons);
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveTnelisHeroAttach(Player tnelis, Game game, String soID,
        ButtonInteractionEvent event) {
        Map<String, Integer> customPOs = new HashMap<>(game.getRevealedPublicObjectives());
        for (String customPO : customPOs.keySet()) {
            if (customPO.contains("Tnelis Hero")) {
                game.removeCustomPO(customPOs.get(customPO));
                String sb = "Removed Turra Sveyar, the Tnelis hero, from a secret objective.";
                MessageHelper.sendMessageToChannel(tnelis.getCorrectChannel(), sb);
            }
        }
        Integer poIndex = game
            .addCustomPO("Tnelis Hero (" + Mapper.getSecretObjectivesJustNames().get(soID) + ")", 1);
        String sb = tnelis.getRepresentation() + " Attached Turra Sveyar, the Tnelis hero, to a secret objective ("
            + Mapper.getSecretObjectivesJustNames().get(soID)
            + "). This is represented in the bot as a custom public objective (" + poIndex
            + ") and should only be scored by them. This public objective will be removed/changed automatically if the hero is attached to another secret objective via button.";
        MessageHelper.sendMessageToChannel(tnelis.getCorrectChannel(), sb);
        ButtonHelper.deleteMessage(event);
    }

    public static List<Button> getTilesToGhotiHeroIn(Player player, Game game,
        GenericInteractionCreateEvent event) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> buttons = new ArrayList<>();
        for (Map.Entry<String, Tile> tileEntry : new HashMap<>(game.getTileMap()).entrySet()) {
            if (FoWHelper.playerHasShipsInSystem(player, tileEntry.getValue())) {
                Tile tile = tileEntry.getValue();
                Button validTile = Button.success(finChecker + "ghotiHeroIn_" + tileEntry.getKey(),
                    tile.getRepresentationForButtons(game, player));
                buttons.add(validTile);
            }
        }
        Button validTile2 = Button.danger(finChecker + "deleteButtons", "Done");
        buttons.add(validTile2);
        return buttons;
    }

    public static List<Button> getUnitsToGlimmersHero(Player player, Game game,
        GenericInteractionCreateEvent event, Tile tile) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        Set<UnitType> allowedUnits = Set.of(UnitType.Destroyer, UnitType.Cruiser, UnitType.Carrier,
            UnitType.Dreadnought, UnitType.Flagship, UnitType.Warsun, UnitType.Fighter);

        List<Button> buttons = new ArrayList<>();
        for (Map.Entry<String, UnitHolder> entry : tile.getUnitHolders().entrySet()) {
            UnitHolder unitHolder = entry.getValue();
            Map<UnitKey, Integer> units = unitHolder.getUnits();
            if (unitHolder instanceof Planet)
                continue;

            Map<UnitKey, Integer> tileUnits = new HashMap<>(units);
            for (Map.Entry<UnitKey, Integer> unitEntry : tileUnits.entrySet()) {
                UnitKey unitKey = unitEntry.getKey();
                if (!player.unitBelongsToPlayer(unitKey))
                    continue;
                if (!allowedUnits.contains(unitKey.getUnitType())) {
                    continue;
                }
                EmojiUnion emoji = Emoji.fromFormatted(unitKey.unitEmoji());
                UnitModel unitModel = player.getUnitFromUnitKey(unitKey);
                String prettyName = unitModel == null ? unitKey.getUnitType().humanReadableName() : unitModel.getName();
                String unitName = unitKey.unitName();
                Button validTile2 = Button.danger(finChecker + "glimmersHeroOn_" + tile.getPosition() + "_" + unitName,
                    "Duplicate " + prettyName);
                validTile2 = validTile2.withEmoji(emoji);
                buttons.add(validTile2);

            }
        }
        Button validTile2 = Button.danger(finChecker + "deleteButtons", "Decline");
        buttons.add(validTile2);
        return buttons;
    }

    public static List<Button> getTilesToGlimmersHeroIn(Player player, Game game,
        GenericInteractionCreateEvent event) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> buttons = new ArrayList<>();
        for (Map.Entry<String, Tile> tileEntry : new HashMap<>(game.getTileMap()).entrySet()) {
            if (FoWHelper.playerHasShipsInSystem(player, tileEntry.getValue())) {
                Tile tile = tileEntry.getValue();
                Button validTile = Button.success(finChecker + "glimmersHeroIn_" + tileEntry.getKey(),
                    tile.getRepresentationForButtons(game, player));
                buttons.add(validTile);
            }
        }
        Button validTile2 = Button.danger(finChecker + "deleteButtons", "Done");
        buttons.add(validTile2);
        return buttons;
    }

    public static void offerFreeSystemsButtons(Player player, Game game, GenericInteractionCreateEvent event) {
        List<Button> buttons = new ArrayList<>();
        for (String planet : player.getPlanets()) {
            Planet unitHolder = game.getPlanetsInfo().get(planet);
            Planet planetReal = unitHolder;
            boolean oneOfThree = planetReal != null && planetReal.getOriginalPlanetType() != null
                && ("industrial".equalsIgnoreCase(planetReal.getOriginalPlanetType())
                    || "cultural".equalsIgnoreCase(planetReal.getOriginalPlanetType())
                    || "hazardous".equalsIgnoreCase(planetReal.getOriginalPlanetType()));
            if (oneOfThree || planet.contains("custodiavigilia") || planet.contains("ghoti")) {
                buttons.add(Button.success("freeSystemsHeroPlanet_" + planet,
                    Helper.getPlanetRepresentation(planet, game)));
            }
        }
        String message = "Use buttons to select one which planet to use Count Otto P’may, the Free Systems hero.";
        MessageHelper.sendMessageToChannelWithButtons(event.getMessageChannel(), message, buttons);
    }

    public static void freeSystemsHeroPlanet(String buttonID, ButtonInteractionEvent event, Game game,
        Player player) {
        String planet = buttonID.split("_")[1];
        Planet unitHolder = game.getPlanetsInfo().get(planet);
        Planet planetReal = unitHolder;
        planetReal.addToken("token_dmz.png");
        unitHolder.removeAllUnits(player.getColor());
        if (player.getExhaustedPlanets().contains(planet)) {
            PlanetRefresh.doAction(player, planet, game);
        }
        MessageHelper.sendMessageToChannel(event.getChannel(),
            "Attached Count Otto P'may, the Free Systems hero, to " + Helper.getPlanetRepresentation(planet, game) + ".");
        ButtonHelper.deleteMessage(event);
    }

    public static List<Button> getButtonsForGheminaLadyHero(Player player, Game game) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> buttons = new ArrayList<>();
        List<Tile> tilesWithBombard = ButtonHelper.getTilesOfPlayersSpecificUnits(game, player, UnitType.Lady,
            UnitType.Flagship);
        Set<String> adjacentTiles = FoWHelper.getAdjacentTilesAndNotThisTile(game,
            tilesWithBombard.get(0).getPosition(), player, false);
        for (Tile tile : tilesWithBombard) {
            adjacentTiles
                .addAll(FoWHelper.getAdjacentTilesAndNotThisTile(game, tile.getPosition(), player, false));
        }
        for (String pos : adjacentTiles) {
            Tile tile = game.getTileByPosition(pos);
            for (UnitHolder unitHolder : tile.getUnitHolders().values()) {
                if (unitHolder instanceof Planet planet) {
                    if (!player.getPlanetsAllianceMode().contains(planet.getName())
                        && !tile.isHomeSystem()
                        && !planet.getName().toLowerCase().contains("rex")) {
                        buttons.add(Button.success(finChecker + "gheminaLadyHero_" + planet.getName(),
                            Helper.getPlanetRepresentation(planet.getName(), game)));
                    }
                }
            }
        }
        return buttons;
    }

    public static List<Button> getButtonsForGheminaLordHero(Player player, Game game) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> buttons = new ArrayList<>();
        List<Tile> tilesWithBombard = ButtonHelper.getTilesOfPlayersSpecificUnits(game, player, UnitType.Lady,
            UnitType.Flagship);
        Set<String> adjacentTiles = FoWHelper.getAdjacentTilesAndNotThisTile(game,
            tilesWithBombard.get(0).getPosition(), player, false);
        for (Tile tile : tilesWithBombard) {
            adjacentTiles
                .addAll(FoWHelper.getAdjacentTilesAndNotThisTile(game, tile.getPosition(), player, false));
        }
        for (String pos : adjacentTiles) {
            Tile tile = game.getTileByPosition(pos);
            for (UnitHolder unitHolder : tile.getUnitHolders().values()) {
                if (unitHolder instanceof Planet planet) {
                    if (!player.getPlanetsAllianceMode().contains(planet.getName())
                        && !tile.isHomeSystem()
                        && !planet.getName().toLowerCase().contains("rex")
                        && (unitHolder.getUnits() == null || unitHolder.getUnits().isEmpty())) {
                        buttons.add(Button.success(finChecker + "gheminaLordHero_" + planet.getName(),
                            Helper.getPlanetRepresentation(planet.getName(), game)));
                    }
                }
            }
        }
        return buttons;
    }

    public static void resolveGheminaLadyHero(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        String planetName = buttonID.split("_")[1];
        UnitHolder unitHolder = ButtonHelper.getUnitHolderFromPlanetName(planetName, game);
        for (Player p2 : game.getRealPlayers()) {
            unitHolder.removeAllUnits(p2.getColor());
        }
        String planetRepresentation2 = Helper.getPlanetRepresentation(planetName, game);
        String msg = player.getFactionEmoji() + " destroyed all units on the planet " + planetRepresentation2
            + " using the The Lady, a Ghemina hero.";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg);
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveGheminaLordHero(String buttonID, String ident, Player player, Game game,
        ButtonInteractionEvent event) {
        String planet = buttonID.split("_")[1];
        if ("lockedmallice".equalsIgnoreCase(planet)) {
            planet = "mallice";
            Tile tile = game.getTileFromPlanet("lockedmallice");
            tile = MoveUnits.flipMallice(event, tile, game);
        }
        PlanetAdd.doAction(player, planet, game, event, false);
        PlanetRefresh.doAction(player, planet, game);
        String planetRepresentation2 = Helper.getPlanetRepresentation(planet, game);
        String msg = ident + " claimed the planet " + planetRepresentation2 + " using The Lord, a Ghemina hero.";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg);
        ButtonHelper.deleteMessage(event);
    }

    public static List<Button> getPossibleTechForVeldyrToGainFromPlayer(Player veldyr, Player victim, Game game) {
        List<Button> techToGain = new ArrayList<>();
        List<String> techs = new ArrayList<>();
        for (String tech : victim.getTechs()) {
            TechnologyModel techM = Mapper.getTech(tech);
            if (techM.isUnitUpgrade()) {
                if (techM.getFaction().orElse("").length() > 0) {
                    techM = Mapper.getTech(techM.getBaseUpgrade().orElse(tech));
                }
                for (String fTech : veldyr.getFactionTechs()) {
                    TechnologyModel techF = Mapper.getTech(fTech);
                    if (techF.isUnitUpgrade()) {
                        if (techF.getBaseUpgrade().orElse("").equalsIgnoreCase(techM.getAlias())) {
                            techM = techF;
                        }
                    }
                }
                if (!veldyr.getTechs().contains(techM.getAlias()) && !techs.contains(techM.getAlias())) {
                    techToGain.add(Button.success("getTech_" + techM.getAlias() + "__noPay",
                        techM.getName()));
                    techs.add(techM.getAlias());
                }
            }

        }
        return techToGain;
    }

    public static List<Button> getArboHeroButtons(Game game, Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Tile> tiles = new ArrayList<>();
        tiles.addAll(ButtonHelper.getTilesOfPlayersSpecificUnits(game, player, UnitType.Infantry));
        tiles.addAll(ButtonHelper.getTilesOfPlayersSpecificUnits(game, player, UnitType.Mech));
        List<String> poses = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!poses.contains(tile.getPosition())) {
                buttons.add(Button.success("arboHeroBuild_" + tile.getPosition(),
                    tile.getRepresentationForButtons(game, player)));
                poses.add(tile.getPosition());
            }
        }
        buttons.add(Button.danger("deleteButtons", "Done"));
        return buttons;
    }

    public static List<Button> getSaarHeroButtons(Game game, Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Tile> tilesUsed = new ArrayList<>();
        for (Tile tile1 : ButtonHelper.getTilesOfPlayersSpecificUnits(game, player, UnitType.Spacedock)) {
            for (String tile2Pos : FoWHelper.getAdjacentTilesAndNotThisTile(game, tile1.getPosition(), player,
                false)) {
                Tile tile2 = game.getTileByPosition(tile2Pos);
                if (!tilesUsed.contains(tile2)) {
                    tilesUsed.add(tile2);
                    buttons.add(Button.success("saarHeroResolution_" + tile2.getPosition(),
                        tile2.getRepresentationForButtons(game, player)));
                }
            }

        }
        buttons.add(Button.danger("deleteButtons", "Done"));
        return buttons;
    }

    public static void resolveSaarHero(Game game, Player player, ButtonInteractionEvent event, String buttonID) {
        String pos = buttonID.split("_")[1];
        Tile tile = game.getTileByPosition(pos);
        for (UnitHolder unitHolder : tile.getUnitHolders().values()) {
            for (Player p2 : game.getRealPlayers()) {
                if (p2 == player) {
                    continue;
                }
                String name = unitHolder.getName().replace("space", "");
                if (tile.containsPlayersUnits(p2)) {
                    int amountInf = unitHolder.getUnitCount(UnitType.Infantry, p2.getColor());
                    if (p2.hasInf2Tech()) {
                        ButtonHelper.resolveInfantryDeath(game, p2, amountInf);
                    }
                    if (amountInf > 0) {
                        new RemoveUnits().unitParsing(event, p2.getColor(), tile, amountInf + " inf " + name, game);
                    }
                    int amountFF = unitHolder.getUnitCount(UnitType.Fighter, p2.getColor());
                    if (amountFF > 0) {
                        new RemoveUnits().unitParsing(event, p2.getColor(), tile, amountFF + " ff", game);
                    }
                    if (amountFF + amountInf > 0) {
                        MessageHelper.sendMessageToChannel(p2.getCardsInfoThread(),
                            p2.getRepresentation()
                                + " heads up, a tile with your units in it got Armageddon'd by Gurno Aggero, the Saar hero, removing all fighters and infantry.");
                    }
                }

            }
        }
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), player.getFactionEmoji() + " removed all opposing infantry and fighters in "
            + tile.getRepresentationForButtons(game, player) + " using Gurno Aggero, the Saar hero.");
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveArboHeroBuild(Game game, Player player, ButtonInteractionEvent event,
        String buttonID) {
        String pos = buttonID.split("_")[1];
        List<Button> buttons;
        buttons = Helper.getPlaceUnitButtons(event, player, game, game.getTileByPosition(pos),
            "arboHeroBuild", "place");
        String message = player.getRepresentation() + " Use the buttons to produce units. ";
        MessageHelper.sendMessageToChannelWithButtons(event.getChannel(), message, buttons);
        ButtonHelper.deleteTheOneButton(event);
    }

    public static List<Button> getNekroHeroButtons(Player player, Game game) {
        List<Button> techPlanets = new ArrayList<>();
        for (Tile tile : game.getTileMap().values()) {
            if (tile.containsPlayersUnits(player)) {
                for (UnitHolder unitHolder : tile.getUnitHolders().values()) {
                    if (unitHolder instanceof Planet planetHolder) {
                        String planet = planetHolder.getName();
                        if (ButtonHelper.checkForTechSkips(game, planet)) {
                            techPlanets.add(
                                Button.secondary("nekroHeroStep2_" + planet, Mapper.getPlanet(planet).getName()));
                        }
                    }
                }
            }
        }
        return techPlanets;
    }

    public static void purgeCeldauriHero(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        Leader playerLeader = player.unsafeGetLeader("celdaurihero");
        if (playerLeader == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(),
                player.getFactionEmoji() + "You don't have Titus Flavius, the Celdauri hero.");
        }
        StringBuilder message = new StringBuilder(player.getRepresentation()).append(" played ")
            .append(Helper.getLeaderFullRepresentation(playerLeader));
        boolean purged = player.removeLeader(playerLeader);
        if (purged) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(),
                message + "\n(Titus Flavius, the Celdauri hero, has been purged.)");
        } else {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(),
                "Titus Flavius, the Celdauri hero, was not purged - something went wrong.");
        }
        player.setTg(player.getCommodities() + player.getTg());
        Tile tile = game.getTileByPosition(buttonID.split("_")[1]);
        List<Button> buttons = new ArrayList<>();
        Button tgButton = Button.danger("deleteButtons", "Delete Buttons");
        for (UnitHolder uH : tile.getPlanetUnitHolders()) {
            if (player.getPlanets().contains(uH.getName())) {
                String planet = uH.getName();
                Button sdButton = Button.success("winnuStructure_sd_" + planet,
                    "Place 1 space dock on " + Helper.getPlanetRepresentation(planet, game));
                buttons.add(sdButton);
            }
        }
        buttons.add(tgButton);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " Use buttons to place 1 space dock on a planet you control", buttons);
        List<Button> buttons2 = Helper.getPlaceUnitButtons(event, player, game, tile, "celdauriHero", "place");
        String message2 = player.getRepresentation() + " Use the buttons to produce units. ";
        String message3 = "You have " + Helper.getProductionValue(player, game, tile, false)
            + " PRODUCTION value in this system\n";
        if (Helper.getProductionValue(player, game, tile, false) > 0
            && game.playerHasLeaderUnlockedOrAlliance(player, "cabalcommander")) {
            message3 = message3
                + ". You also have the That Which Molds Flesh, the Vuil'raith commander, which allows you to produce 2 fighters/infantry that don't count towards production limit.\n";
        }
        MessageHelper.sendMessageToChannel(event.getChannel(),
            message.toString());
        MessageHelper.sendMessageToChannel(event.getChannel(),
            message3 + ButtonHelper.getListOfStuffAvailableToSpend(player, game));
        MessageHelper.sendMessageToChannelWithButtons(event.getChannel(), message2, buttons2);
        ButtonHelper.deleteMessage(event);
    }

    public static void purgeMentakHero(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        Leader playerLeader = player.unsafeGetLeader("mentakhero");
        if (playerLeader == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(),
                player.getFactionEmoji() + "You don't have this Ipswitch, Loose Cannon, the Mentak hero.");
        }
        StringBuilder message = new StringBuilder(player.getRepresentation()).append(" played ")
            .append(Helper.getLeaderFullRepresentation(playerLeader));
        boolean purged = player.removeLeader(playerLeader);
        if (purged) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(),
                message + "\n(Ipswitch, Loose Cannon, the Mentak hero, has been purged.)");
        } else {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(),
                "Ipswitch, Loose Cannon, the Mentak hero, was not purged - something went wrong.");
        }

        // Tile tile = game.getTileByPosition(buttonID.split("_")[1]);

        MessageHelper.sendMessageToChannel(event.getChannel(), message.toString());
        game.setStoredValue("mentakHero", player.getFaction());
        ButtonHelper.deleteTheOneButton(event);
    }

    public static void purgeTech(Player player, Game game, ButtonInteractionEvent event, String buttonID) {
        String techID = buttonID.replace("purgeTech_", "");
        player.purgeTech(techID);
        String msg = player.getRepresentation(true, true) + " purged " + Mapper.getTech(techID).getName();
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg);
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveNekroHeroStep2(Player player, Game game, ButtonInteractionEvent event, String buttonID) {
        String planet = buttonID.split("_")[1];
        Planet unitHolder = game.getPlanetsInfo().get(planet);
        Set<String> techTypes = unitHolder.getTechSpecialities();
        if (techTypes.isEmpty()) {
            MessageHelper.sendMessageToChannel(player.getCorrectChannel(), "No technology specialties found.");
            return;
        }
        for (Player p2 : game.getRealPlayers()) {
            if (p2 == player) {
                continue;
            }
            String color = p2.getColor();
            unitHolder.removeAllUnits(color);
            unitHolder.removeAllUnitDamage(color);
        }
        Planet planetHolder = (Planet) unitHolder;
        int oldTg = player.getTg();
        int count = planetHolder.getResources() + planetHolder.getInfluence();
        player.setTg(oldTg + count);
        MessageHelper.sendMessageToChannel(event.getChannel(),
            player.getFactionEmoji() + " gained " + count + "trade good" + (count == 1 ? "" : "s") + " (" + oldTg + "->" + player.getTg() + ") from selecting the planet "
                + Helper.getPlanetRepresentationPlusEmojiPlusResourceInfluence(planetHolder.getName(), game) + ".");
        ButtonHelperAbilities.pillageCheck(player, game);
        ButtonHelperAgents.resolveArtunoCheck(player, game, count);
        game.setComponentAction(true);
        List<TechnologyModel> techs = new ArrayList<>();
        for (String type : techTypes)
            techs.addAll(Helper.getAllTechOfAType(game, type, player));
        List<Button> buttons = Helper.getTechButtons(techs, player, "nekro");
        String message = player.getRepresentation() + " Use the buttons to gain the technology you want.";
        MessageHelper.sendMessageToChannelWithButtons(event.getChannel(), message, buttons);
        ButtonHelper.deleteMessage(event);
    }

    public static List<Button> getCabalHeroButtons(Player player, Game game) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> empties = new ArrayList<>();

        List<Tile> tiles = new ArrayList<>();
        for (Player p : game.getRealPlayers()) {
            if (p.hasTech("dt2") || p.getUnitsOwned().contains("cabal_spacedock")
                || p.getUnitsOwned().contains("cabal_spacedock2")) {
                tiles.addAll(ButtonHelper.getTilesOfPlayersSpecificUnits(game, p, UnitType.CabalSpacedock,
                    UnitType.Spacedock));
            }
        }

        List<Tile> adjTiles = new ArrayList<>();
        for (Tile tile : tiles) {
            for (String pos : FoWHelper.getAdjacentTiles(game, tile.getPosition(), player, false)) {
                Tile tileToAdd = game.getTileByPosition(pos);
                if (!adjTiles.contains(tileToAdd) && !tile.getPosition().equalsIgnoreCase(pos)) {
                    adjTiles.add(tileToAdd);
                }
            }
        }

        for (Tile tile : adjTiles) {
            empties.add(Button.primary(finChecker + "cabalHeroTile_" + tile.getPosition(),
                "Roll For Units In " + tile.getRepresentationForButtons(game, player)));
        }
        return empties;
    }

    public static void executeCabalHero(String buttonID, Player player, Game game, ButtonInteractionEvent event) {
        String pos = buttonID.replace("cabalHeroTile_", "");
        Tile tile = game.getTileByPosition(pos);
        for (Player p2 : game.getRealPlayers()) {
            if (p2 == player) {
                continue;
            }
            if (FoWHelper.playerHasShipsInSystem(p2, tile)
                && !ButtonHelperFactionSpecific.isCabalBlockadedByPlayer(p2, game, player)) {
                ButtonHelper.riftAllUnitsInASystem(pos, event, game, p2, p2.getFactionEmoji(), player);
            }
            if (FoWHelper.playerHasShipsInSystem(p2, tile)
                && ButtonHelperFactionSpecific.isCabalBlockadedByPlayer(p2, game, player)) {
                String msg = player.getRepresentation(true, true) + " has failed to eat units owned by "
                    + p2.getRepresentation()
                    + " because they were blockaded. Womp Womp.";
                MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg);
            }
        }
        ButtonHelper.deleteTheOneButton(event);
    }

    public static List<Button> getEmpyHeroButtons(Player player, Game game) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> empties = new ArrayList<>();
        for (Tile tile : game.getTileMap().values()) {
            if (tile.getUnitHolders().values().size() > 1 || !FoWHelper.playerHasShipsInSystem(player, tile)) {
                continue;
            }
            empties.add(Button.primary(finChecker + "exploreFront_" + tile.getPosition(),
                "Explore " + tile.getRepresentationForButtons(game, player)));
        }
        return empties;
    }

    public static void resolveNaaluHeroSend(Player p1, Game game, String buttonID, ButtonInteractionEvent event) {
        buttonID = buttonID.replace("naaluHeroSend_", "");
        String factionToTrans = buttonID.substring(0, buttonID.indexOf("_"));
        String amountToTrans = buttonID.substring(buttonID.indexOf("_") + 1);
        Player p2 = game.getPlayerFromColorOrFaction(factionToTrans);
        if (p2 == null) {
            MessageHelper.sendMessageToChannel(p1.getCorrectChannel(), "Could not resolve second player, please resolve manually.");
            return;
        }
        String message2;
        // String ident = p1.getRepresentation();
        String ident2 = p2.getRepresentation();
        String id = null;
        int pnIndex;
        pnIndex = Integer.parseInt(amountToTrans);
        for (Map.Entry<String, Integer> pn : p1.getPromissoryNotes().entrySet()) {
            if (pn.getValue().equals(pnIndex)) {
                id = pn.getKey();
            }
        }
        if (id == null) {
            MessageHelper.sendMessageToChannel(p1.getCorrectChannel(), "Could not resolve promissory note; promissory note not sent.");
            return;
        }
        p1.removePromissoryNote(id);
        p2.setPromissoryNote(id);
        if (id.contains("dspnveld")) {
            ButtonHelper.resolvePNPlay(id, p2, game, event);
        }
        boolean sendSftT = false;
        boolean sendAlliance = false;
        String promissoryNoteOwner = Mapper.getPromissoryNote(id).getOwner();
        if ((id.endsWith("_sftt") || id.endsWith("_an")) && !promissoryNoteOwner.equals(p2.getFaction())
            && !promissoryNoteOwner.equals(p2.getColor())
            && !p2.isPlayerMemberOfAlliance(game.getPlayerFromColorOrFaction(promissoryNoteOwner))) {
            p2.setPromissoryNotesInPlayArea(id);
            if (id.endsWith("_sftt")) {
                sendSftT = true;
            } else {
                sendAlliance = true;
                if (game.getPNOwner(id).hasLeaderUnlocked("bentorcommander")) {
                    p2.setCommoditiesTotal(p2.getCommodities() + 1);
                }
            }
        }
        PNInfo.sendPromissoryNoteInfo(game, p1, false);
        PNInfo.sendPromissoryNoteInfo(game, p2, false);
        String text = sendSftT ? "**Support for the Throne** " : (sendAlliance ? "**Alliance** " : "");
        message2 = p1.getRepresentation() + " sent " + Emojis.PN + text + "promissory note to " + ident2 + ".";
        MessageHelper.sendMessageToChannel(p2.getCorrectChannel(), message2);
        if (game.isFowMode()) {
            MessageHelper.sendMessageToChannel(p1.getCorrectChannel(), message2);
        }
        ButtonHelper.deleteMessage(event);
        Helper.checkEndGame(game, p2);

    }

    public static void resolveNivynHeroSustainEverything(Game game, Player nivyn) {
        for (Tile tile : game.getTileMap().values()) {
            for (UnitHolder unitHolder : tile.getUnitHolders().values()) {
                Map<UnitKey, Integer> units = unitHolder.getUnits();
                for (Player player : game.getRealPlayers()) {
                    for (Map.Entry<UnitKey, Integer> unitEntry : units.entrySet()) {
                        if (!player.unitBelongsToPlayer(unitEntry.getKey()))
                            continue;
                        UnitModel unitModel = player.getUnitFromUnitKey(unitEntry.getKey());
                        if (unitModel == null)
                            continue;
                        UnitKey unitKey = unitEntry.getKey();
                        int damagedUnits = 0;
                        if (unitHolder.getUnitDamage() != null && unitHolder.getUnitDamage().get(unitKey) != null) {
                            damagedUnits = unitHolder.getUnitDamage().get(unitKey);
                        }
                        int totalUnits = unitEntry.getValue() - damagedUnits;
                        if (totalUnits > 0 && unitModel.getSustainDamage()
                            && (player != nivyn || !"mech".equalsIgnoreCase(unitModel.getBaseType()))) {
                            tile.addUnitDamage(unitHolder.getName(), unitKey, totalUnits);
                        }
                    }
                }
            }
        }
    }

    public static void offerStealRelicButtons(Game game, Player player, String buttonID,
        ButtonInteractionEvent event) {
        ButtonHelper.deleteTheOneButton(event);
        String faction = buttonID.split("_")[1];
        Player victim = game.getPlayerFromColorOrFaction(faction);
        List<Button> buttons = new ArrayList<>();
        for (String relic : victim.getRelics()) {
            buttons.add(Button.success("stealRelic_" + victim.getFaction() + "_" + relic,
                "Steal " + Mapper.getRelic(relic).getName()));
        }
        String msg = player.getRepresentation(true, true) + " choose the relic you wish to steal.";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg, buttons);

    }

    public static void stealRelic(Game game, Player player, String buttonID, ButtonInteractionEvent event) {
        ButtonHelper.deleteMessage(event);
        String faction = buttonID.split("_")[1];
        Player p2 = game.getPlayerFromColorOrFaction(faction);
        String relic = buttonID.split("_")[2];
        String msg = ButtonHelper.getIdentOrColor(player, game) + " stole " + Mapper.getRelic(relic).getName()
            + " from " + ButtonHelper.getIdentOrColor(p2, game) + ".";
        if (game.isFowMode()) {
            MessageHelper.sendMessageToChannel(p2.getCorrectChannel(), msg);
        }
        boolean exhausted = p2.getExhaustedRelics().contains(relic);
        p2.removeRelic(relic);
        player.addRelic(relic);
        if (exhausted) {
            p2.removeExhaustedRelic(relic);
            player.addExhaustedRelic(relic);
        }

        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg);
    }

    public static void resolvDihmohnHero(Game game) {
        Tile tile = game.getTileByPosition(game.getActiveSystem());
        UnitHolder unitHolder = tile.getUnitHolders().get("space");
        Map<UnitKey, Integer> units = unitHolder.getUnits();
        for (Player player : game.getRealPlayers()) {
            for (Map.Entry<UnitKey, Integer> unitEntry : units.entrySet()) {
                if (!player.unitBelongsToPlayer(unitEntry.getKey()))
                    continue;
                UnitModel unitModel = player.getUnitFromUnitKey(unitEntry.getKey());
                if (unitModel == null)
                    continue;
                UnitKey unitKey = unitEntry.getKey();
                int damagedUnits = 0;
                if (unitHolder.getUnitDamage() != null && unitHolder.getUnitDamage().get(unitKey) != null) {
                    damagedUnits = unitHolder.getUnitDamage().get(unitKey);
                }
                int totalUnits = unitEntry.getValue() - damagedUnits;
                if (totalUnits > 0 && unitModel.getIsShip()) {
                    tile.addUnitDamage(unitHolder.getName(), unitKey, totalUnits);
                }
            }
        }

    }

    public static void augersHeroSwap(Player player, Game game, String buttonID, ButtonInteractionEvent event) {
        buttonID = buttonID.replace("augerHeroSwap.", "");
        String id = StringUtils.substringAfter(buttonID, ".");
        String num = StringUtils.substringBefore(buttonID, ".");
        if ("1".equalsIgnoreCase(num)) {
            game.swapObjectiveOut(1, 0, id);
        } else {
            game.swapObjectiveOut(2, 0, id);
        }
        MessageHelper.sendMessageToChannel(player.getCardsInfoThread(),
            player.getRepresentation(true, true) + " put " + Mapper.getPublicObjective(id).getName()
                + " as next up. Feel free to peek at it to confirm it worked.");
        // GameSaveLoadManager.saveMap(game, event);
        ButtonHelper.deleteMessage(event);
    }

    public static void augersHeroResolution(Player player, Game game, String buttonID) {
        List<Button> buttons = new ArrayList<>();
        if ("1".equalsIgnoreCase(buttonID.split("_")[1])) {
            for (int x = 0; x < 3; x++) {
                String obj = game.getTopObjective(1);
                PublicObjectiveModel po = Mapper.getPublicObjective(obj);
                buttons.add(Button.success("augerHeroSwap.1." + obj, "Put " + po.getName() + " As The Next Objective"));
            }
        } else {
            for (int x = 0; x < 3; x++) {
                String obj = game.getTopObjective(2);
                PublicObjectiveModel po = Mapper.getPublicObjective(obj);
                buttons.add(Button.success("augerHeroSwap.2." + obj, "Put " + po.getName() + " As The Next Objective"));
            }
        }
        buttons.add(Button.danger("deleteButtons", "Decline to change the next objective"));
        MessageHelper.sendMessageToChannelWithButtons(player.getCardsInfoThread(),
            player.getRepresentation(true, true) + " use buttons to resolve.", buttons);
    }

    public static void resolveNaaluHeroInitiation(Player player, Game game, ButtonInteractionEvent event) {
        Leader playerLeader = player.unsafeGetLeader("naaluhero");
        StringBuilder message2 = new StringBuilder(player.getRepresentation()).append(" played ")
            .append(Helper.getLeaderFullRepresentation(playerLeader));
        boolean purged = player.removeLeader(playerLeader);
        if (purged) {
            MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
                message2 + "\n(The Oracle, the Naalu hero, has been purged.)\n\n Sent buttons to resolve to everyone's channels.");
        } else {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(),
                "The Oracle, the Naalu hero, was not purged - something went wrong.");
        }
        for (Player p1 : game.getRealPlayers()) {
            if (p1 == player) {
                continue;
            }
            List<Button> stuffToTransButtons = ButtonHelper.getForcedPNSendButtons(game, player, p1);
            String message = p1.getRepresentation(true, true)
                + " The Oracle, the Naalu hero, has been played and you must send a promissory note. Please select the promissory note you would like to send.";
            MessageHelper.sendMessageToChannelWithButtons(p1.getCardsInfoThread(), message, stuffToTransButtons);
        }
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveKyroHeroInitiation(Player player, Game game, ButtonInteractionEvent event) {
        Leader playerLeader = player.unsafeGetLeader("kyrohero");
        StringBuilder message2 = new StringBuilder(player.getRepresentation()).append(" played ")
            .append(Helper.getLeaderFullRepresentation(playerLeader));
        boolean purged = player.removeLeader(playerLeader);
        if (purged) {
            MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
                message2 + ".\n(Speygh, the Kyro hero, has been purged.)");
        } else {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(),
                "Speygh, the Kyro hero, was not purged - something went wrong.");
        }
        int dieResult = player.getLowestSC();
        game.setStoredValue("kyroHeroSC", dieResult + "");
        game.setStoredValue("kyroHeroPlayer", player.getFaction());
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
            Helper.getSCName(dieResult, game) + ", has been marked with Speygh, the Kyro hero, and the faction that played the hero as " + player.getFaction() + ".");
        ButtonHelper.deleteMessage(event);
    }

    public static void offerOlradinHeroFlips(Game game, Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.success("olradinHeroFlip_people", "People Policy"));
        buttons.add(Button.success("olradinHeroFlip_environment", "Environment Policy"));
        buttons.add(Button.success("olradinHeroFlip_economy", "Economy Policy"));
        buttons.add(Button.danger("deleteButtons", "Decline"));
        String msg = player.getRepresentation() + " you may flip one policy.";
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg, buttons);
    }

    public static void olradinHeroFlipPolicy(String buttonID, ButtonInteractionEvent event, Game game,
        Player player) {
        int negativePolicies = 0;
        int positivePolicies = 0;
        String policy = buttonID.split("_")[1];
        // go through each option and set the policy accordingly
        String msg = player.getRepresentation() + " ";
        if ("people".equalsIgnoreCase(policy)) {
            if (player.hasAbility("policy_the_people_connect")) {
                player.removeAbility("policy_the_people_connect");
                msg = msg + "removed Policy - The People: Connect (+) and added Policy - The People: Control (-).";
                player.addAbility("policy_the_people_control");
            } else if (player.hasAbility("policy_the_people_control")) {
                player.removeAbility("policy_the_people_control");
                msg = msg + "removed Policy - The People: Control (-) and added Policy - The People: Connect (+).";
                player.addAbility("policy_the_people_connect");
            }
        }
        if ("environment".equalsIgnoreCase(policy)) {
            if (player.hasAbility("policy_the_environment_preserve")) {
                player.removeAbility("policy_the_environment_preserve");
                msg = msg
                    + "removed Policy - The Environment: Preserve (+) and added Policy - The Environment: Plunder (-).";
                player.addAbility("policy_the_environment_plunder");
            }
            if (player.hasAbility("policy_the_environment_plunder")) {
                player.removeAbility("policy_the_environment_plunder");
                msg = msg
                    + "removed Policy - The Environment: Plunder (-) and added Policy - The Environment: Preserve (+).";
                player.addAbility("policy_the_environment_preserve");
            }
        }
        if ("economy".equalsIgnoreCase(policy)) {
            if (player.hasAbility("policy_the_economy_empower")) {
                player.removeAbility("policy_the_economy_empower");
                msg = msg + "removed Policy - The Economy: Empower (+)";
                player.addAbility("policy_the_economy_exploit");
                player.setCommoditiesTotal(player.getCommoditiesTotal() - 1);
                msg = msg
                    + " and added Policy - The Economy: Exploit (-). Decreased Commodities total by 1 - double check the value is correct!";
            } else if (player.hasAbility("policy_the_economy_exploit")) {
                player.removeAbility("policy_the_economy_exploit");
                player.setCommoditiesTotal(player.getCommoditiesTotal() + 1);
                msg = msg + "removed Policy - The Economy: Exploit (-)";
                player.addAbility("policy_the_economy_empower");
                msg = msg + " and added Policy - The Economy: Empower (+).";
            }
        }
        player.removeOwnedUnitByID("olradin_mech");
        player.removeOwnedUnitByID("olradin_mech_positive");
        player.removeOwnedUnitByID("olradin_mech_negative");
        String unitModelID;
        if (player.hasAbility("policy_the_economy_exploit")) {
            negativePolicies++;
        } else {
            positivePolicies++;
        }
        if (player.hasAbility("policy_the_environment_plunder")) {
            negativePolicies++;
        } else {
            positivePolicies++;
        }
        if (player.hasAbility("policy_the_people_connect")) {
            positivePolicies++;
        } else {
            negativePolicies++;
        }
        if (positivePolicies >= 2) {
            unitModelID = "olradin_mech_positive";
        } else if (negativePolicies >= 2) {
            unitModelID = "olradin_mech_negative";
        } else {
            unitModelID = "olradin_mech";
        }
        player.addOwnedUnitByID(unitModelID);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg);
        DiscordantStarsHelper.checkOlradinMech(game);
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveCymiaeHeroStart(String buttonID, ButtonInteractionEvent event, Game game,
        Player player) {
        String num = buttonID.split("_")[1];
        int n = Integer.parseInt(num);
        List<Button> buttons = new ArrayList<>();
        for (int x = 0; x < n; x++) {
            String acID = game.drawActionCardAndDiscard();
            String sb = Mapper.getActionCard(acID).getRepresentation() + "\n";
            MessageHelper.sendMessageToChannel(player.getCorrectChannel(), sb);
            buttons.add(Button.success("cymiaeHeroStep2_" + acID, Mapper.getActionCard(acID).getName()));
        }
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
            player.getRepresentation() + " use the buttons to give out action cards to players.", buttons);
        ButtonHelper.deleteMessage(event);
    }

    public static void resolveCymiaeHeroStep2(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        String acID = buttonID.replace("cymiaeHeroStep2_", "");
        List<Button> buttons = new ArrayList<>();
        for (Player p2 : game.getRealPlayers()) {
            if (game.isFowMode()) {
                buttons.add(Button.secondary("cymiaeHeroStep3_" + p2.getFaction() + "_" + acID, p2.getColor()));
            } else {
                Button button = Button.secondary("cymiaeHeroStep3_" + p2.getFaction() + "_" + acID, " ");
                String factionEmojiString = p2.getFactionEmoji();
                button = button.withEmoji(Emoji.fromFormatted(factionEmojiString));
                buttons.add(button);
            }
        }
        ButtonHelper.deleteTheOneButton(event);
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " tell the bot who you wish to give "
                + Mapper.getActionCard(acID).getName() + ".",
            buttons);
    }

    public static void resolveCymiaeHeroStep3(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        Player p2 = game.getPlayerFromColorOrFaction(buttonID.split("_")[1]);
        String acID = buttonID.replace("cymiaeHeroStep3_" + p2.getFaction() + "_", "");
        boolean picked = game.pickActionCard(p2.getUserID(), game.getDiscardActionCards().get(acID));
        if (!picked) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No such action card ID found, please retry.");
            return;
        }
        ACInfo.sendActionCardInfo(game, p2, event);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
            player.getRepresentation() + " has given " + Mapper.getActionCard(acID).getName() + " to "
                + p2.getRepresentation());
        ButtonHelper.deleteMessage(event);
        if (p2 != player) {
            MessageHelper.sendMessageToChannel(p2.getCardsInfoThread(),
                "The Voice United, the Cymiae hero, has given " + Mapper.getActionCard(acID).getName()
                    + " to you and you now have to discard 1 action card.");
            String msg = p2.getRepresentation(true, true) + " use buttons to discard.";
            List<Button> buttons = ACInfo.getDiscardActionCardButtons(game, p2, false);
            MessageHelper.sendMessageToChannelWithButtons(p2.getCardsInfoThread(), msg, buttons);
        }
    }

    public static void lastStepOfYinHero(String buttonID, ButtonInteractionEvent event, Game game, Player player,
        String trueIdentity) {
        String planetNInf = buttonID.replace("yinHeroInfantry_", "");
        String planet = planetNInf.split("_")[0];
        String amount = planetNInf.split("_")[1];
        Tile tile = game.getTile(AliasHandler.resolveTile(planet));

        new AddUnits().unitParsing(event, player.getColor(),
            game.getTile(AliasHandler.resolveTile(planet)), amount + " inf " + planet,
            game);
        MessageHelper.sendMessageToChannel(event.getChannel(), trueIdentity + " Chose to land " + amount
            + " infantry on " + Helper.getPlanetRepresentation(planet, game) + ".");
        UnitHolder unitHolder = tile.getUnitHolders().get(planet);
        List<Player> players = ButtonHelper.getPlayersWithUnitsOnAPlanet(game, tile, unitHolder.getName());
        if (players.size() > 1) {
            MessageHelper.sendMessageToChannel(player.getCorrectChannel(), player.getRepresentation()
                + " Reminder that Dannel of the Tenth, the Yin hero, skips space cannon fire.");
            StartCombat.startGroundCombat(players.get(0), players.get(1), game, event, unitHolder, tile);
        }

        ButtonHelper.deleteMessage(event);
    }

    public static List<Button> getGhostHeroTilesStep1(Game game, Player player) {
        List<Button> buttons = new ArrayList<>();
        for (Tile tile : game.getTileMap().values()) {
            if (tile.getPosition().contains("t") || tile.getPosition().contains("b")) {
                continue;
            }
            if (FoWHelper.doesTileHaveWHs(game, tile.getPosition())
                || FoWHelper.playerHasUnitsInSystem(player, tile)) {
                buttons.add(Button.secondary("creussHeroStep1_" + tile.getPosition(),
                    tile.getRepresentationForButtons(game, player)));
            }

        }
        return buttons;
    }

    public static List<Button> getBenediction2ndTileOptions(Player player, Game game, String pos1) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> buttons = new ArrayList<>();
        Player origPlayer = player;
        Tile tile1 = game.getTileByPosition(pos1);
        List<Player> players2 = ButtonHelper.getOtherPlayersWithShipsInTheSystem(player, game, tile1);
        if (players2.size() != 0) {
            player = players2.get(0);
        }
        for (String pos2 : FoWHelper.getAdjacentTiles(game, pos1, player, false)) {
            if (pos1.equalsIgnoreCase(pos2)) {
                continue;
            }
            Tile tile2 = game.getTileByPosition(pos2);
            if (FoWHelper.otherPlayersHaveShipsInSystem(player, tile2, game)) {
                buttons.add(Button.secondary(finChecker + "mahactBenedictionFrom_" + pos1 + "_" + pos2,
                    tile2.getRepresentationForButtons(game, origPlayer)));
            }
        }
        return buttons;
    }

    public static List<Button> getBenediction1stTileOptions(Player player, Game game) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> buttons = new ArrayList<>();
        for (Tile tile1 : game.getTileMap().values()) {
            String pos1 = tile1.getPosition();
            for (Player p2 : game.getRealPlayers()) {
                if (FoWHelper.playerHasShipsInSystem(p2, tile1)) {
                    boolean adjacentPeeps = false;
                    for (String pos2 : FoWHelper.getAdjacentTiles(game, pos1, p2, false)) {
                        if (pos1.equalsIgnoreCase(pos2)) {
                            continue;
                        }
                        Tile tile2 = game.getTileByPosition(pos2);
                        if (FoWHelper.otherPlayersHaveShipsInSystem(p2, tile2, game)) {
                            adjacentPeeps = true;
                        }
                    }
                    if (adjacentPeeps) {
                        buttons.add(Button.secondary(finChecker + "benedictionStep1_" + pos1,
                            tile1.getRepresentationForButtons(game, player)));
                    }
                    break;
                }

            }

        }

        return buttons;
    }

    public static List<Button> getJolNarHeroSwapOutOptions(Player player) {
        String finChecker = "FFCC_" + player.getFaction() + "_";
        List<Button> buttons = new ArrayList<>();
        for (String tech : player.getTechs()) {
            TechnologyModel techM = Mapper.getTech(tech);
            if (!techM.isUnitUpgrade()) {
                buttons.add(Button.secondary(finChecker + "jnHeroSwapOut_" + tech, techM.getName()));
            }
        }
        buttons.add(Button.danger("deleteButtons", "Done Resolving"));
        return buttons;
    }

    public static List<Button> getJolNarHeroSwapInOptions(Player player, Game game, String buttonID) {
        String tech = buttonID.replace("jnHeroSwapOut_", "");
        TechnologyModel techM = Mapper.getTech(tech);
        List<TechnologyModel> techs = new ArrayList<>();
        for (TechnologyType type : techM.getTypes())
            techs.addAll(Helper.getAllTechOfAType(game, type.toString(), player));
        return Helper.getTechButtons(techs, player, tech);
    }

    public static void resolveAJolNarSwapStep1(Player player, Game game, String buttonID, ButtonInteractionEvent event) {
        List<Button> buttons = getJolNarHeroSwapInOptions(player, game, buttonID);
        String message = player.getRepresentation(true, true) + " select the technology you would like to acquire.";
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(), message, buttons);
        ButtonHelper.deleteTheOneButton(event);
    }

    public static void resolveAJolNarSwapStep2(Player player, Game game, String buttonID, ButtonInteractionEvent event) {
        String techOut = buttonID.split("__")[1];
        String techIn = buttonID.split("__")[2];
        TechnologyModel techM1 = Mapper.getTech(techOut);
        TechnologyModel techM2 = Mapper.getTech(techIn);
        player.addTech(techIn);
        player.removeTech(techOut);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), player.getFactionEmoji() + " swapped the technology "
            + techM1.getName() + " for the technology " + techM2.getName() + ".");
        ButtonHelper.deleteMessage(event);
    }

    public static void mahactBenediction(String buttonID, ButtonInteractionEvent event, Game game,
        Player player) {
        String pos1 = buttonID.split("_")[1];
        String pos2 = buttonID.split("_")[2];
        Tile tile1 = game.getTileByPosition(pos1);
        Tile tile2 = game.getTileByPosition(pos2);
        List<Player> players2 = ButtonHelper.getOtherPlayersWithShipsInTheSystem(player, game, tile1);
        if (players2.size() != 0) {
            player = players2.get(0);
        }

        game.setStoredValue("mahactHeroTarget", player.getFaction());

        UnitHolder unitHolder = tile1.getUnitHolders().get("space");
        Map<UnitKey, Integer> units = new HashMap<>(unitHolder.getUnits());
        for (Map.Entry<UnitKey, Integer> unitEntry : units.entrySet()) {
            if (!player.unitBelongsToPlayer(unitEntry.getKey()))
                continue;
            UnitModel unitModel = player.getUnitFromUnitKey(unitEntry.getKey());
            if (unitModel == null)
                continue;
            if (unitModel.getCapacityValue() < 1) {
                continue;
            }
            UnitKey unitKey = unitEntry.getKey();
            String unitName = ButtonHelper.getUnitName(unitKey.asyncID());
            int totalUnits = unitEntry.getValue();
            int damagedUnits = 0;

            if (unitHolder.getUnitDamage() != null && unitHolder.getUnitDamage().get(unitKey) != null) {
                damagedUnits = unitHolder.getUnitDamage().get(unitKey);
            }

            new RemoveUnits().removeStuff(event, tile1, totalUnits, "space", unitKey, player.getColor(), false,
                game);
            new AddUnits().unitParsing(event, player.getColor(), tile2, totalUnits + " " + unitName, game);
            if (damagedUnits > 0) {
                game.getTileByPosition(pos2).addUnitDamage("space", unitKey, damagedUnits);
            }
        }
        // this will catch all the capacity units left behind in the previous iteration
        for (Map.Entry<UnitKey, Integer> unitEntry : units.entrySet()) {
            if (!player.unitBelongsToPlayer(unitEntry.getKey()))
                continue;
            UnitModel unitModel = player.getUnitFromUnitKey(unitEntry.getKey());
            if (unitModel == null)
                continue;
            if (unitModel.getCapacityValue() > 0) {
                continue;
            }
            UnitKey unitKey = unitEntry.getKey();
            String unitName = ButtonHelper.getUnitName(unitKey.asyncID());
            int totalUnits = unitEntry.getValue();
            int damagedUnits = 0;

            if (unitHolder.getUnitDamage() != null && unitHolder.getUnitDamage().get(unitKey) != null) {
                damagedUnits = unitHolder.getUnitDamage().get(unitKey);
            }

            new RemoveUnits().removeStuff(event, tile1, totalUnits, "space", unitKey, player.getColor(), false,
                game);
            new AddUnits().unitParsing(event, player.getColor(), tile2, totalUnits + " " + unitName, game);
            if (damagedUnits > 0) {
                game.getTileByPosition(pos2).addUnitDamage("space", unitKey, damagedUnits);
            }
        }

        List<Player> players = ButtonHelper.getOtherPlayersWithShipsInTheSystem(player, game, tile2);
        Player player2 = player;
        for (Player p2 : players) {
            if (p2 != player && !player.getAllianceMembers().contains(p2.getFaction())) {
                player2 = p2;
                break;
            }
        }
        if (player != player2) {

            StartCombat.startSpaceCombat(game, player, player2, tile2, event, "-benediction");
        }

    }

    public static void getGhostHeroTilesStep2(Game game, Player player, ButtonInteractionEvent event,
        String buttonID) {
        String pos1 = buttonID.split("_")[1];
        List<Button> buttons = new ArrayList<>();
        Tile tile1 = game.getTileByPosition(pos1);
        for (Tile tile : game.getTileMap().values()) {
            if (tile.getPosition().contains("t") || tile.getPosition().contains("b") || tile == tile1) {
                continue;
            }
            if (FoWHelper.doesTileHaveWHs(game, tile.getPosition())
                || FoWHelper.playerHasUnitsInSystem(player, tile)) {
                buttons.add(Button.secondary("creussHeroStep2_" + pos1 + "_" + tile.getPosition(),
                    tile.getRepresentationForButtons(game, player)));
            }
        }
        MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " Chose the tile you wish to swap places with "
                + tile1.getRepresentationForButtons(game, player) + ".",
            buttons);
        ButtonHelper.deleteMessage(event);
    }

    public static void startVadenHero(Game game, Player vaden) {
        List<Button> buttons = new ArrayList<>();

        for (Player target : game.getRealPlayers()) {
            if (vaden.getDebtTokenCount(target.getColor()) > 0) {
                Button button;
                String prefix = "vadenHeroClearDebt";
                Player player = target;
                String faction = player.getFaction();
                if (!game.isFowMode() && !faction.contains("franken")) {
                    button = Button.secondary(prefix + "_" + faction, " ");
                    button = button.withEmoji(Emoji.fromFormatted(player.getFactionEmoji()));
                } else {
                    button = Button.secondary(prefix + "_" + player.getColor(), player.getColor());
                }
                buttons.add(button);
            }
        }
        MessageHelper.sendMessageToChannel(vaden.getCorrectChannel(), "Use buttons to resolve.", buttons);
    }

    public static void vadenHeroClearDebt(Game game, Player vaden, ButtonInteractionEvent event, String buttonID) {
        Player target = game.getPlayerFromColorOrFaction(buttonID.split("_")[1]);
        List<Button> buttons = new ArrayList<>();
        if (target.getTg() > 0) {
            buttons.add(Button.success("sendVadenHeroSomething_" + vaden.getFaction() + "_tg", "Send 1 Trade Good"));
        }
        if (target.getCommodities() > 1) {
            buttons.add(Button.secondary("sendVadenHeroSomething_" + vaden.getFaction() + "_comms", "Send 2 Commodities"));
        }
        buttons.add(Button.danger("sendVadenHeroSomething_" + vaden.getFaction() + "_pn", "Send 1 Promissory Note"));
        ClearDebt.clearDebt(vaden, target, 1);
        MessageHelper.sendMessageToChannel(vaden.getCorrectChannel(),
            vaden.getRepresentation() + " returned 1 debt tokens owned by " + target.getRepresentation(false, true)
                + " using Putriv Sirvonsk, the Vaden hero. Buttons have been sent to their `#Cards Info` thread to resolve.");
        MessageHelper.sendMessageToChannelWithButtons(target.getCardsInfoThread(), target.getRepresentation(true, true)
            + " please select something to give due to Putriv Sirvonsk, the Vaden hero, returning one of your tokens.", buttons);
        if (vaden.getDebtTokenCount(target.getColor()) == 0) {
            ButtonHelper.deleteTheOneButton(event);
        }
    }

    public static void sendVadenHeroSomething(Player player, Game game, String buttonID,
        ButtonInteractionEvent event) {
        String tgOrComm = buttonID.split("_")[2];
        Player vaden = game.getPlayerFromColorOrFaction(buttonID.split("_")[1]);
        ButtonHelper.deleteMessage(event);

        String msg = player.getRepresentation(false, true) + " sent ";
        if ("tg".equalsIgnoreCase(tgOrComm)) {
            msg = msg + " 1 trade good to " + vaden.getRepresentation(false, true) + " as a result of playing Putriv Sirvonsk, the Vaden Hero.";
            if (player.getTg() > 0) {
                vaden.setTg(vaden.getTg() + 1);
                player.setTg(player.getTg() - 1);
            } else {
                MessageHelper.sendMessageToChannel(event.getMessageChannel(),
                    player.getRepresentation(true, true) + " you had no trade goods to send, so no trade goods have been sent.");
                return;
            }
        } else {
            if ("comms".equalsIgnoreCase(tgOrComm)) {
                msg = msg + " 2 commodities to " + vaden.getRepresentation(false, true) + " as a result of playing Putriv Sirvonsk, the Vaden Hero.";
                if (player.getCommodities() > 1) {
                    vaden.setTg(vaden.getTg() + 2);
                    player.setCommodities(player.getCommodities() - 2);
                } else {
                    MessageHelper.sendMessageToChannel(event.getMessageChannel(),
                        player.getRepresentation(true, true) + " you didn't have 2 commodities to send, so no commodities were sent.");
                    return;
                }
            } else {
                msg = player.getRepresentation(false, true) + " will send 1 promissory note as a result of playing Putriv Sirvonsk, the Vaden Hero.";
                List<Button> stuffToTransButtons = ButtonHelper.getForcedPNSendButtons(game, vaden, player);
                String message = player.getRepresentation(true, true)
                    + " Please select the promissory note you would like to send.";
                MessageHelper.sendMessageToChannelWithButtons(player.getCardsInfoThread(), message,
                    stuffToTransButtons);
            }
        }
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), msg);
        if (game.isFowMode()) {
            MessageHelper.sendMessageToChannel(vaden.getCorrectChannel(), msg);
        }

    }

    public static void killShipsSardakkHero(Player player, Game game, ButtonInteractionEvent event) {
        String pos1 = game.getActiveSystem();
        Tile tile1 = game.getTileByPosition(pos1);
        for (Map.Entry<String, UnitHolder> entry : tile1.getUnitHolders().entrySet()) {
            UnitHolder unitHolder = entry.getValue();
            Map<UnitKey, Integer> units = new HashMap<>(unitHolder.getUnits());
            if (unitHolder instanceof Planet)
                continue;
            for (Map.Entry<UnitKey, Integer> unitEntry : units.entrySet()) {
                if (!player.unitBelongsToPlayer(unitEntry.getKey()))
                    continue;

                UnitKey unitKey = unitEntry.getKey();
                int totalUnits = unitEntry.getValue();
                if (unitKey.getUnitType() != UnitType.Infantry && unitKey.getUnitType() != UnitType.Mech) {
                    new RemoveUnits().removeStuff(event, tile1, totalUnits, "space", unitKey, player.getColor(), false, game);
                }
            }
        }
    }

    public static void resolveWinnuHeroSC(Player player, Game game, ButtonInteractionEvent event,
        String buttonID) {
        Integer sc = Integer.parseInt(buttonID.split("_")[1]);
        SCPlay.playSC(event, sc, game, game.getMainGameChannel(), player, true);
        MessageHelper.sendMessageToChannel(game.getMainGameChannel(), game.getPing()
            + " reminder that the Winnu player has to allow you to follow this, and that when you do follow, you must spend a commend token from your strategy pool like normal"
            + " (unless you're following Leadership, if you're following Construction, the command token is placed in the system).");
        ButtonHelper.deleteMessage(event);
    }

    public static void checkForMykoHero(Game game, String hero, Player player) {

        if (player.hasLeaderUnlocked("mykomentorihero")) {
            List<Leader> leaders = new ArrayList<>(player.getLeaders());
            for (Leader leader : leaders) {
                if (leader.getId().contains("hero")) {
                    player.removeLeader(leader);
                }
            }
        }
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.success("mykoheroSteal_" + hero, "Copy " + hero));
        buttons.add(Button.danger("deleteButtons", "Decline"));
        for (Player p2 : game.getRealPlayers()) {
            if (p2.hasLeaderUnlocked("mykomentorihero")) {
                String msg = p2.getRepresentation(true, true)
                    + " you have the opportunity to use Coprinus Comatus, the Myko-Mentori hero, to grab the ability of the hero " + hero
                    + ". Use buttons to resolve.";
                MessageHelper.sendMessageToChannelWithButtons(p2.getCardsInfoThread(), msg, buttons);
            }
        }
    }

    public static void resolveMykoHero(Game game, Player player, ButtonInteractionEvent event, String buttonID) {
        String hero = buttonID.split("_")[1];
        HeroPlay.playHero(event, game, player, player.unsafeGetLeader("mykomentorihero"));
        player.addLeader(hero);
        UnlockLeader.unlockLeader(hero, game, player);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), LeaderAdd.getAddLeaderText(player, hero));
        ButtonHelper.deleteMessage(event);

    }

    public static void resolveBentorHero(Game game, Player player) {
        for (String planet : player.getPlanetsAllianceMode()) {
            UnitHolder unitHolder = ButtonHelper.getUnitHolderFromPlanetName(planet, game);
            if (unitHolder == null) {
                continue;
            }
            Planet planetReal = (Planet) unitHolder;
            List<Button> buttons = ButtonHelper.getPlanetExplorationButtons(game, planetReal, player);
            if (buttons != null && !buttons.isEmpty()) {
                String message = "Click button to explore " + Helper.getPlanetRepresentation(planet, game) + ".";
                MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(),
                    message, buttons);
            }
        }
    }

    public static List<Button> getWinnuHeroSCButtons(Game game) {
        List<Button> scButtons = new ArrayList<>();
        for (Integer sc : game.getSCList()) {
            if (sc <= 0)
                continue; // some older games have a 0 in the list of SCs
            Emoji scEmoji = Emoji.fromFormatted(Emojis.getSCBackEmojiFromInteger(sc));
            Button button;
            String label = " ";
            if (scEmoji.getName().contains("SC") && scEmoji.getName().contains("Back")
                && !game.isHomebrewSCMode()) {
                button = Button.secondary("winnuHero_" + sc, label).withEmoji(scEmoji);
            } else {
                button = Button.secondary("winnuHero_" + sc, "" + sc + label);
            }
            scButtons.add(button);
        }
        return scButtons;
    }

    public static List<Button> getNRAHeroButtons(Game game) {
        List<Button> scButtons = new ArrayList<>();
        if (game.getScPlayed().get(1) == null || !game.getScPlayed().get(1)) {
            scButtons.add(Button.success("leadershipGenerateCCButtons", "Spend And Gain Command Tokens"));
            //scButtons.add(Button.danger("leadershipExhaust", "Exhaust Planets"));
        }
        if (game.getScPlayed().get(2) == null || !game.getScPlayed().get(2)) {
            scButtons.add(Button.success("diploRefresh2", "Ready 2 Planets"));
        }
        if (game.getScPlayed().get(3) == null || !game.getScPlayed().get(3)) {
            scButtons.add(Button.secondary("draw2 AC", "Draw 2 Action Cards")
                .withEmoji(Emoji.fromFormatted(Emojis.ActionCard)));
        }
        if (game.getScPlayed().get(4) == null || !game.getScPlayed().get(4)) {
            scButtons.add(
                Button.success("construction_spacedock", "Place 1 space dock").withEmoji(Emoji.fromFormatted(Emojis.spacedock)));
            scButtons.add(Button.success("construction_pds", "Place 1 PDS").withEmoji(Emoji.fromFormatted(Emojis.pds)));
        }
        if (game.getScPlayed().get(5) == null || !game.getScPlayed().get(5)) {
            scButtons.add(Button.secondary("sc_refresh", "Replenish Commodities")
                .withEmoji(Emoji.fromFormatted(Emojis.comm)));
        }
        if (game.getScPlayed().get(6) == null || !game.getScPlayed().get(6)) {
            scButtons.add(Button.success("warfareBuild", "Build At Home"));
        }
        if (game.getScPlayed().get(7) == null || !game.getScPlayed().get(7)) {
            scButtons.add(Buttons.GET_A_TECH);
        }
        if (game.getScPlayed().get(8) == null || !game.getScPlayed().get(8)) {
            scButtons.add(Button.secondary("non_sc_draw_so", "Draw Secret Objective")
                .withEmoji(Emoji.fromFormatted(Emojis.SecretObjective)));
        }
        scButtons.add(Button.danger("deleteButtons", "Done Resolving"));

        return scButtons;
    }

    public static void resolveGhostHeroStep2(Game game, Player player, ButtonInteractionEvent event,
        String buttonID) {
        String position = buttonID.split("_")[1];
        String position2 = buttonID.split("_")[2];
        Tile tile = game.getTileByPosition(position);
        Tile tile2 = game.getTileByPosition(position2);
        tile.setPosition(position2);
        tile2.setPosition(position);
        game.setTile(tile);
        game.setTile(tile2);
        game.rebuildTilePositionAutoCompleteList();
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(),
            player.getRepresentation(true, true) + " Chose to swap "
                + tile2.getRepresentationForButtons(game, player) + " with "
                + tile.getRepresentationForButtons(game, player) + ".");
        ButtonHelper.deleteMessage(event);
    }
}
