package ti4.helpers;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Constants {
    public static final String SHOW_GAME = "show_game";
    public static final String SHOW_GAME_INFO = "show_game_info";
    public static final String GAME_NAME = "game_name";
    public static final String GAME_CUSTOM_NAME = "game_custom_name";
    public static final String COMMUNITY_MODE = "community_mode";
    public static final String ALLIANCE_MODE = "alliance_mode";
    public static final String ROLE_FOR_COMMUNITY = "roleForCommunity";
    public static final String CHANNLE_FOR_COMMUNITY = "channelForCommunity";
    public static final String DISPLAY_TYPE = "display_type";
    public static final String GAME_STATUS = "game_status";
    public static final String CONFIRM = "confirm";
    public static final String SHUTDOWN = "shutdown";
    public static final String TESTING = "testing";
    public static final String LOG_MESSAGE = "log_message";
    public static final String SET_GAME = "set_game";
    public static final String SET_STATUS = "set_status";
    public static final String REMOVE_PLAYER = "remove_player";
    public static final String PLAYER_NAME = "player_name";
    public static final String PLAYER_COUNT_FOR_MAP = "player_count_for_map";
    public static final String VP_COUNT = "vp_count";
    public static final String JOIN = "join";
    public static final String ADD = "add";
    public static final String REPLACE = "replace";
    public static final String LEAVE = "leave";
    public static final String REMOVE = "remove";

    public static final String DRAW_SPECIFIC_SO_FOR_PLAYER = "draw_specific_so_for_player";

    public static final String SAVE_GAMES = "save_games";
    public static final String RELOAD_GAME = "reload_game";
    public static final String LIST_TILES = "list_tiles";
    public static final String LIST_PLANETS = "list_planets";
    public static final String LIST_UNITS = "list_units";
    public static final String LIST_GAMES = "list_games";
    public static final String CREATE_GAME = "create_game";
    public static final String DELETE_GAME = "delete_game";
    public static final String POSITION = "position";
    public static final String TILE_NAME = "tile_name";
    public static final String TILE_NAME_TO = "tile_name_to";

    public static final String HELP = "help";
    public static final String HELP_DOCUMENTATION = "documentation";

    public static final String PLANET_NAME = "planet_name";
    public static final String TOKEN = "token";
    public static final String TILE_LIST = "tile_list";
    public static final String UNIT_NAMES = "unit_names";
    public static final String PRIORITY_NO_DAMAGE = "priority_no_damage";
    public static final String UNIT_NAMES_TO = "unit_names_to";
    public static final String COLOR = "color";
    public static final String SEARCH_WARRANT = "search_warrant";
    public static final String ADD_TILE = "add_tile";
    public static final String ADD_CC = "add_cc";
    public static final String ADD_CC_FROM_TACTICS = "add_cc_from_tactics";
    public static final String CC = "cc";
    public static final String CC_USE = "cc_use";
    public static final String ADD_CONTROL = "add_control";
    public static final String ADD_TOKEN = "add_token";
    public static final String REMOVE_TOKEN = "remove_token";
    public static final String REMOVE_CC = "remove_cc";
    public static final String REMOVE_CONTROL = "remove_control";
    public static final String REMOVE_ALL_CC = "remove_all_cc";
    public static final String REMOVE_TILE = "remove_tile";
    public static final String ADD_TILE_LIST = "add_tile_list";
    public static final String ADD_FRONTIER_TOKENS = "add_frontier_tokens";
    public static final String ADD_UNITS = "add_units";
    public static final String ADD_UNIT_DAMAGE = "add_damage_unit";
    public static final String MOVE_UNITS = "move_units";
    public static final String REMOVE_UNITS = "remove_units";
    public static final String REMOVE_UNIT_DAMAGE = "remove_damage_unit";
    public static final String REMOVE_ALL_UNITS = "remove_all_units";
    public static final String REMOVE_ALL_UNIT_DAMAGE = "remove_damage_all_units";
    public static final String SPACE = "space";
    public static final String COMMAND = "command_";
    public static final String CONTROL = "control_";
    public static final String GF = "gf";
    public static final String FF = "ff";
    public static final String BULK_GF = "_tkn_gf.png";
    public static final String BULK_FF = "_tkn_ff.png";
    public static final String COLOR_GF = "_gf.png";
    public static final String COLOR_FF = "_ff.png";
    public static final String SETUP6 = "setup6";
    public static final String MALLICE = "mallicelocked";
    public static final String MR = "mr";
    public static final int SPACE_RADIUS = 115;
    public static final int RADIUS = 45;
    public static final Point SPACE_CENTER_POSITION = new Point(172, 150);
    public static final Point MIRAGE_POSITION = new Point(55, 5);
    public static final Point MIRAGE_CENTER_POSITION = new Point(70, 60);
    public static final String MIRAGE = "mirage";
    public static final String SLEEPER = "sleeper";
    public static final String DMZ = "dmz";
    public static final String DMZ_LARGE = "dmz_large";
    public static final String WORLD_DESTROYED = "worlddestroyed";

    public static final String FRONTIER = "frontier";

    public static final String CREATION_DATE = "creation_date";
    public static final String LAST_MODIFIED_DATE = "last_modified_date";
    public static final String ROUND = "round";

    public static final String SYSTEM_INFO = "system_info";
    public static final String DIPLO_SYSTEM = "diplo_system";
    public static final String SLEEPER_TOKEN = "sleeper_token";
    public static final String STELLAR_CONVERTER = "stellar_converter";
    public static final String ION_TOKEN_FLIP = "ion_token_flip";
    public static final String TOKEN_SLEEPER_PNG = "token_sleeper.png";
    public static final String WORLD_DESTROYED_PNG = "token_worlddestroyed.png";
    public static final String TOKEN_ION_ALPHA_PNG = "token_ionalpha.png";
    public static final String TOKEN_ION_BETA_PNG = "token_ionbeta.png";
    public static final String SWAP_SYSTEMS = "swap_systems";
    public static final String ADJUST_ROUND_NUMBER = "adjust_round_number";


    public static final String MAHACT_CC = "mahact_cc";
    public static final String ADD_CC_TO_FS = "mahact_cc_to_fs";
    public static final String REMOVE_CC_FROM_FS = "mahact_cc_from_fs";
    public static final String SPECIAL = "special";

    public static final String MILTY = "milty";
    public static final String START = "start";
    public static final String SLICE_COUNT = "slice_count";
    public static final String FACTION_COUNT = "faction_count";
    public static final String ANOMALIES_CAN_TOUCH = "anomalies_can_touch";


    public static final String CUSTOM = "custom";
    public static final String SO_REMOVE_FROM_GAME = "so_remove_from_game";
    public static final String AGENDA_REMOVE_FROM_GAME = "agenda_remove_from_game";
    public static final String AC_REMOVE_FROM_GAME = "ac_remove_from_game";
    public static final String SO_ID = "so_id";
    public static final String AEGNDA_ID = "agenda_id";
    public static final String AC_ID = "ac_id";

    public static final String INACTIVE_LEADER = "hero_unplay";
    public static final String ACTIVE_LEADER = "hero_play";
    public static final String UNLOCK_LEADER = "unlock";
    public static final String LOCK_LEADER = "lock";
    public static final String EXHAUST_LEADER = "exhaust";
    public static final String REFRESH_LEADER = "refresh";
    public static final String PURGE_LEADER = "purge";
    public static final String ADD_LEADER = "add";
    public static final String LEADERS = "leaders";
    public static final String SC = "sc";

    public static final String LEADERSHIP_PRIMARY = "leadership_primary";
    public static final String DIPLOMACY_PRIMARY = "diplomacy_primary";
    public static final String POLITICS_PRIMARY = "politics_primary";
    public static final String CONSTRUCTION_PRIMARY = "construction_primary";
    public static final String TRADE_PRIMARY = "trade_primary";
    public static final String WARFARE_PRIMARY = "warfare_primary";
    public static final String TECHNOLOGY_PRIMARY = "technology_primary";
    public static final String IMPERIAL_PRIMARY = "imperial_primary";
    public static final String CUSTOM_PRIMARY = "custom_primary";

    public static final String LEADERSHIP_SECONDARY = "leadership_secondary";
    public static final String DIPLOMACY_SECONDARY = "diplomacy_secondary";
    public static final String POLITICS_SECONDARY = "politics_secondary";
    public static final String CONSTRUCTION_SECONDARY = "construction_secondary";
    public static final String TRADE_SECONDARY = "trade_secondary";
    public static final String WARFARE_SECONDARY = "warfare_secondary";
    public static final String TECHNOLOGY_SECONDARY = "technology_secondary";
    public static final String IMPERIAL_SECONDARY = "imperial_secondary";
    public static final String CUSTOM_SECONDARY = "custom_secondary";

    public static final String GAME = "game";
    public static final String INFO = "info";
    public static final String UNDO = "undo";
    public static final String SET_ORDER = "set_order";
    public static final String PASSED = "passed";
    public static final String SC_PLAYED = "sc_played";
    public static final String SC_FOLLOW = "sc_follow";
    public static final String SC_PLAY = "sc_play";
    public static final String SC_PICK = "sc_pick";
    public static final String PASS = "pass";
    public static final String TURN = "turn_end";
    public static final String SPEAKER = "speaker";

    public static final String CHANNEL1 = "channel1";
    public static final String CHANNEL2 = "channel2";
    public static final String CHANNEL3 = "channel3";
    public static final String CHANNEL4 = "channel4";
    public static final String CHANNEL5 = "channel5";
    public static final String CHANNEL6 = "channel6";
    public static final String CHANNEL7 = "channel7";
    public static final String CHANNEL8 = "channel8";
    public static final String ROLE1 = "role1";
    public static final String ROLE2 = "role2";
    public static final String ROLE3 = "role3";
    public static final String ROLE4 = "role4";
    public static final String ROLE5 = "role5";
    public static final String ROLE6 = "role6";
    public static final String ROLE7 = "role7";
    public static final String ROLE8 = "role8";
    public static final String PLAYER1 = "player1";
    public static final String PLAYER2 = "player2";
    public static final String PLAYER3 = "player3";
    public static final String PLAYER4 = "player4";
    public static final String PLAYER5 = "player5";
    public static final String PLAYER6 = "player6";
    public static final String PLAYER7 = "player7";
    public static final String PLAYER8 = "player8";
    public static final String PLAYER = "player";
    public static final String FACTION_COLOR = "faction_or_color";
    public static final String STATS = "stats";
    public static final String SEND_TG = "send_tg";
    public static final String SEND_COMMODITIES = "send_commodities";
    public static final String SETUP = "setup";
    public static final String COMMUNITY_SETUP = "community_setup";
    public static final String PLANETS = "planets";
    public static final String PLANETS_EXHAUSTED = "planets_exhausted";
    public static final String PLANETS_ABILITY_EXHAUSTED = "planets_ability_exhausted";
    public static final String PLANET = "planet";
    public static final String PLANET2 = "planet2";
    public static final String PLANET3 = "planet3";
    public static final String PLANET4 = "planet4";
    public static final String PLANET5 = "planet5";
    public static final String PLANET6 = "planet6";
    public static final String TECH = "tech";
    public static final String TECH_EXHAUSTED = "tech_exhausted";
    public static final String TECH2 = "tech2";
    public static final String TECH3 = "tech3";
    public static final String TECH4 = "tech4";
    public static final String TACTICAL = "tactical_cc";
    public static final String FLEET = "fleet_cc";
    public static final String STRATEGY = "strategy_cc";
    public static final String TG = "trade_goods";
    public static final String COMMODITIES = "commodities";
    public static final String COMMODITIES_TOTAL = "commodities_total";
    public static final String FACTION = "faction";
    public static final String AC = "action_cards";
    public static final String AC_DISCARDED = "action_cards_discarded";
    public static final String SO = "secret_objectives";
    public static final String SO_SCORED = "secret_objectives_scored";
    public static final String CRF = "cultural_relic_fragment";
    public static final String HRF = "hazardous_relic_fragment";
    public static final String IRF = "industrial_relic_fragment";
    public static final String VRF = "void_relic_fragment";
    public static final String STRATEGY_CARD = "strategy_card";

    public static final String TXT = ".txt";
    public static final String JPG = ".jpg";
    public static final String PNG = ".png";
    public static final String ADMIN = "admin";
    public static final String NAALU_PN = "gift";
    public static final String NAALU = "naalu";

    public static final String LAW = "law";
    public static final String LAW_INFO = "law_info";
    public static final String SENT_AGENDAS = "sent_agenda";
    public static final String DISCARDED_AGENDAS = "discarded_agendas";
    public static final String AGENDAS = "agendas";
    public static final String AGENDA = "agenda";
    public static final String RELIC_SHOW_REMAINING = "relic_show_remaining";
    public static final String RELIC_DRAW_SPECIFIC = "relic_draw_specific";
    public static final String DRAW = "draw";
    public static final String RELIC_DRAW = "relic_draw";
    public static final String RELIC_EXHAUST = "relic_exhaust";
    public static final String RELIC_REFRESH = "relic_refresh";
    public static final String RELIC_PURGE = "relic_purge";
    public static final String SHUFFLE_BACK = "relic_shuffle_back";
    public static final String ADD_BACK_INTO_DECK = "relic_add_back_into_deck";
    public static final String PUT_TOP = "put_top";
    public static final String PUT_BOTTOM = "put_bottom";
    public static final String SHUFFLE_AGENDAS = "shuffle_deck";
    public static final String RESET_AGENDAS = "reset_deck";
    public static final String LOOK_AT_TOP = "look_at_top";
    public static final String LOOK_AT_BOTTOM = "look_at_bottom";
    public static final String REVEAL = "reveal";
    public static final String ADD_LAW = "add_law";
    public static final String REMOVE_LAW = "remove_law";
    public static final String SHOW_DISCARDED = "show_discarded";
    public static final String AGENDA_ID = "agenda_id";
    public static final String ELECTED = "elected";


    public static final String STATUS = "status";
    public static final String CLEANUP = "cleanup";
    public static final String REVEAL_STATGE1 = "po_reveal_stage1";
    public static final String ADD_CUSTOM = "po_add_custom";
    public static final String MAKE_SO_INTO_PO = "make_secret_into_po";
    public static final String SO_TO_PO = "so_to_po";
    public static final String REMOVE_CUSTOM = "po_remove_custom";
    public static final String REVEAL_STATGE2 = "po_reveal_stage2";
    public static final String SCORE_OBJECTIVE = "po_score";
    public static final String UNSCORE_OBJECTIVE = "po_unscore";
    public static final String SHUFFLE_OBJECTIVE_BACK = "po_shuffle_back";
    public static final String PO_ID = "public_id";
    public static final String PO_NAME = "public_name";
    public static final String PO_VP_WORTH = "public_vp_worth";
    public static final String CUSTODIAN = "Custodian/Imperial";
    public static final String REVEALED_PO = "revealedPublicObjectives";
    public static final String CUSTOM_PO_VP = "customPublicVP";
    public static final String SCORED_PO = "scoredPublicObjectives";
    public static final String PO1 = "publicObjectives1";
    public static final String PO2 = "publicObjectives2";

    public static final String TURN_ORDER = "turn_order";
    public static final String VOTE_COUNT = "vote_count";
    public static final String SC_TRADE_GOODS = "sc_trade_goods";
    public static final String SC_COUNT = "sc_count";

    public static final String COUNT = "count";
    public static final String SECRET_OBJECTIVE_ID = "secret_objective_id";
    public static final String DRAW_SO = "draw";
    public static final String DEAL_SO = "deal";
    public static final String DEAL_SO_TO_ALL = "deal_to_all";
    public static final String SHOW_SO = "show";
    public static final String SHOW_ALL_SO = "show_all";
    public static final String SHOW_ALL_SO_TO_ALL = "show_all_to_all";
    public static final String SHOW_SO_TO_ALL = "show_to_all";
    public static final String CARDS = "ac";
    public static final String CARDS_SO = "so";
    public static final String PN = "pn";
    public static final String DISCARD_SO = "discard";
    public static final String SCORE_SO = "score";
    public static final String UNSCORE_SO = "unscore";
    public static final String SHORT_PN_DISPLAY = "short_pn_display";
    public static final String LONG_PN_DISPLAY = "long_pn_display";
    public static final String CUSTODIAN_VP = "token_custodianvp.png";

    public static final String ACTION_CARD_ID = "action_card_id";
    public static final String DRAW_AC = "draw";
    public static final String SHUFFLE_AC_DECK = "shuffle_deck";
    public static final String SHOW_AC = "show";
    public static final String SHOW_ALL_AC = "show_all";
    public static final String SHOW_AC_TO_ALL = "show_to_all";
    public static final String DISCARD_AC = "discard";
    public static final String DISCARD_AC_RANDOM = "discard_random";
    public static final String PLAY_AC = "play";
    public static final String PICK_AC_FROM_DISCARD = "pick_from_discard";
    public static final String SHUFFLE_AC_BACK_INTO_DECK = "shuffle_back_into_deck";
    public static final String REVEAL_AND_PUT_AC_INTO_DISCARD = "reveal_and_put_into_discard";
    public static final String SEND_AC = "send";
    public static final String SEND_AC_RANDOM = "send_random";
    public static final String SHOW_AC_DISCARD_LIST = "show_discard_list";
    public static final String SHOW_AC_REMAINING_CARD_COUNT = "show_remaining_card_count";

    public static final String PROMISSORY_NOTE_ID = "promissory_note_id";
    public static final String PROMISSORY_NOTES = "promissory_notes";
    public static final String PROMISSORY_NOTES_PLAY_AREA = "promissory_notes_play_area";
    public static final String SHOW_PN = "show";
    public static final String SHOW_ALL_PN = "show_all";
    public static final String SHOW_PN_TO_ALL = "show_to_all";
    public static final String PLAY_PN = "play";
    public static final String PLAY_PN_INTO_PLAY_AREA = "play_into_play_area";
    public static final String SEND_PN = "send";
    public static final String PURGE_PN = "purge";
    public static final String PURGED_PN = "purged_pn";

    public static final String TECH_ADD = "tech_add";
    public static final String TECH_REMOVE = "tech_remove";
    public static final String TECH_EXHAUST = "tech_exhaust";
    public static final String TECH_REFRESH = "tech_refresh";

    public static final String PLANET_ADD = "planet_add";
    public static final String PLANET_REMOVE = "planet_remove";
    public static final String PLANET_EXHAUST = "planet_exhaust";
    public static final String PLANET_REFRESH = "planet_refresh";
    public static final String PLANET_REFRESH_ALL = "planet_refresh_all";
    public static final String PLANET_EXHAUST_ALL = "planet_exhaust_all";
    public static final String PLANET_EXHAUST_ABILITY = "legendary_exhaust_ability";
    public static final String PLANET_REFRESH_ABILITY = "legendary_refresh_ability";

    public static final String EXPLORE = "explore";
    public static final String CULTURAL = "cultural";
    public static final String INDUSTRIAL = "industrial";
    public static final String HAZARDOUS = "hazardous";
    public static final String EXPLORE_CARD_ID = "explore_card_id";
    public static final String SHUFFLE_BACK_INTO_DECK = "shuffle_back_into_deck";
    public static final String DISCARD = "discard";
    public static final String DRAW_AND_DISCARD = "draw_and_discard";
    public static final String TRAIT = "trait";
    public static final String RESET = "reset";
    public static final String INSTANT = "instant";
    public static final String ATTACH = "attach";
    public static final String FRAGMENT = "fragment";
    public static final String DISCARDED_EXPLORES = "discarded_explores";
    public static final String SEND_FRAGMENT = "send_fragment";
    public static final String USE = "use";
    public static final String PURGE_FRAGMENTS = "purge_fragments";
    public static final String FRAGMENTS = "fragments";
    public static final String LIST_FRAGMENTS = "list_fragments";
    public static final String RELIC = "relic";
    public static final String RELICS = "relics";
    public static final String EXHAUSTED_RELICS = "exhausted_relics";
    public static final String ENIGMATIC_DEVICE = "enigmaticdevice";

    public static final String WARFARE = "warfare";
    public static final String PROPULSION = "propulsion";
    public static final String CYBERNETIC = "cybernetic";
    public static final String BIOTIC = "biotic";
    public static final String UNIT_UPGRADE = "unitupgrade";


    public static final String LEADER = "leader";
    public static final String AGENT = "agent";
    public static final String COMMANDER = "commander";
    public static final String HERO = "hero";

    public static final ArrayList<String> leaderList = new ArrayList<>();
    static {
        leaderList.add(AGENT);
        leaderList.add(COMMANDER);
        leaderList.add(HERO);
    }

    public static final String KELERES_HS = "keleres_hs";
    public static final String HS_TILE_POSITION = "hs_tile_position";
    public static final ArrayList<String> setup6p = new ArrayList<>();
    static {
        setup6p.add("3a");
        setup6p.add("3d");
        setup6p.add("3g");
        setup6p.add("3j");
        setup6p.add("3m");
        setup6p.add("3p");
    }

    public static final ArrayList<String> setup8p = new ArrayList<>();
    static {
        setup8p.add("4a");
        setup8p.add("4d");
        setup8p.add("4g");
        setup8p.add("4j");
        setup8p.add("4m");
        setup8p.add("4p");
        setup8p.add("4s");
        setup8p.add("4w");
    }
    public static final HashMap<String, String> KELERES_CHOICES = new HashMap<>();
    static {
        KELERES_CHOICES.put("keleresm", "Mentak");
        KELERES_CHOICES.put("keleresx", "Xxcha");
        KELERES_CHOICES.put("keleresa", "Argent");
    }

}
