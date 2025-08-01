package cn.nukkit.network.protocol;

import cn.nukkit.GameVersion;

/**
 * author: MagicDroidX &amp; iNevet
 * Nukkit Project
 */
public interface ProtocolInfo {

    /**
     * Actual Minecraft: PE protocol version
     */
    int CURRENT_PROTOCOL = GameVersion.getFeatureVersion().getProtocol(); //plugins can change it

    String MINECRAFT_VERSION_NETWORK = GameVersion.getFeatureVersion().toString();
    String MINECRAFT_VERSION = "v" + MINECRAFT_VERSION_NETWORK;

    int LOGIN_PACKET = 0x01; // 1 s
    int PLAY_STATUS_PACKET = 0x02; // 2 c
    int SERVER_TO_CLIENT_HANDSHAKE_PACKET = 0x03; // 3 c
    int CLIENT_TO_SERVER_HANDSHAKE_PACKET = 0x04; // 4 s
    int DISCONNECT_PACKET = 0x05; // 5 cs
    int RESOURCE_PACKS_INFO_PACKET = 0x06; // 6 c
    int RESOURCE_PACK_STACK_PACKET = 0x07; // 7 c
    int RESOURCE_PACK_CLIENT_RESPONSE_PACKET = 0x08; // 8 s
    int TEXT_PACKET = 0x09; // 9 cs
    int SET_TIME_PACKET = 0x0a; // 10 c
    int START_GAME_PACKET = 0x0b; // 11 c
    int ADD_PLAYER_PACKET = 0x0c; // 12 c
    int ADD_ACTOR_PACKET = 0x0d; // 13 c
    int REMOVE_ACTOR_PACKET = 0x0e; // 14 c
    int ADD_ITEM_ACTOR_PACKET = 0x0f; // 15 c
    int ADD_HANGING_ACTOR_PACKET = 0x10; // 16 c D
    int SERVER_PLAYER_POST_MOVE_POSITION_PACKET = 0x10; // 16 c
    int TAKE_ITEM_ACTOR_PACKET = 0x11; // 17 c
    int MOVE_ACTOR_ABSOLUTE_PACKET = 0x12; // 18 cs
    int MOVE_PLAYER_PACKET = 0x13; // 19 cs
    int PASSENGER_JUMP_PACKET = 0x14; // 20 s
    int UPDATE_BLOCK_PACKET = 0x15; // 21 c
    int ADD_PAINTING_PACKET = 0x16; // 22 c
    int EXPLODE_PACKET = 0x17; // c D
    int TICK_SYNC_PACKET = 0x17; // 23 cs d
    int LEVEL_SOUND_EVENT_PACKET = 0x18; // 24 cs D
    int LEVEL_EVENT_PACKET = 0x19; // 25 c
    int BLOCK_EVENT_PACKET = 0x1a; // 26 c
    int ACTOR_EVENT_PACKET = 0x1b; // 27 cs
    int MOB_EFFECT_PACKET = 0x1c; // 28 c
    int UPDATE_ATTRIBUTES_PACKET = 0x1d; // 29 c
    int INVENTORY_TRANSACTION_PACKET = 0x1e; // 30 cs
    int MOB_EQUIPMENT_PACKET = 0x1f; // 31 cs
    int MOB_ARMOR_EQUIPMENT_PACKET = 0x20; // 32 cs
    int INTERACT_PACKET = 0x21; // 33 s
    int BLOCK_PICK_REQUEST_PACKET = 0x22; // 34 s
    int ACTOR_PICK_REQUEST_PACKET = 0x23; // 35 s
    int PLAYER_ACTION_PACKET = 0x24; // 36 cs
    int ACTOR_FALL_PACKET = 0x25; // 37 s D
    int HURT_ARMOR_PACKET = 0x26; // 38 c
    int SET_ACTOR_DATA_PACKET = 0x27; // 39 cs
    int SET_ACTOR_MOTION_PACKET = 0x28; // 40 cs
    int SET_ACTOR_LINK_PACKET = 0x29; // 41 cs
    int SET_HEALTH_PACKET = 0x2a; // 42 c
    int SET_SPAWN_POSITION_PACKET = 0x2b; // 43 c
    int ANIMATE_PACKET = 0x2c; // 44 cs
    int RESPAWN_PACKET = 0x2d; // 45 cs
    int CONTAINER_OPEN_PACKET = 0x2e; // 46 c
    int CONTAINER_CLOSE_PACKET = 0x2f; // 47 cs
    int PLAYER_HOTBAR_PACKET = 0x30; // 48 cs
    int INVENTORY_CONTENT_PACKET = 0x31; // 49 c
    int INVENTORY_SLOT_PACKET = 0x32; // 50 c
    int CONTAINER_SET_DATA_PACKET = 0x33; // 51 c
    int CRAFTING_DATA_PACKET = 0x34; // 52 c
    int CRAFTING_EVENT_PACKET = 0x35; // 53 s D
    int GUI_DATA_PICK_ITEM_PACKET = 0x36; // 54 c
    int ADVENTURE_SETTINGS_PACKET = 0x37; // 55 cs D
    int BLOCK_ACTOR_DATA_PACKET = 0x38; // 56 cs
    int PLAYER_INPUT_PACKET = 0x39; // 57 s
    int LEVEL_CHUNK_PACKET = 0x3a; // 58 c
    int SET_COMMANDS_ENABLED_PACKET = 0x3b; // 59 c
    int SET_DIFFICULTY_PACKET = 0x3c; // 60 cs
    int CHANGE_DIMENSION_PACKET = 0x3d; // 61 c
    int SET_PLAYER_GAME_TYPE_PACKET = 0x3e; // 62 cs
    int PLAYER_LIST_PACKET = 0x3f; // 63 c
    int SIMPLE_EVENT_PACKET = 0x40; // 64 cs
    int EVENT_PACKET = 0x41; // 65 c
    int SPAWN_EXPERIENCE_ORB_PACKET = 0x42; // 66 s
    int CLIENTBOUND_MAP_ITEM_DATA_PACKET = 0x43; // 67 c
    int MAP_INFO_REQUEST_PACKET = 0x44; // 68 s
    int REQUEST_CHUNK_RADIUS_PACKET = 0x45; // 69 s
    int CHUNK_RADIUS_UPDATED_PACKET = 0x46; // 70 c
    int ITEM_FRAME_DROP_ITEM_PACKET = 0x47; // 71 s d
    int GAME_RULES_CHANGED_PACKET = 0x48; // 72 c
    int CAMERA_PACKET = 0x49; // 73 c
    int BOSS_EVENT_PACKET = 0x4a; // 74 cs
    int SHOW_CREDITS_PACKET = 0x4b; // 75 cs
    int AVAILABLE_COMMANDS_PACKET = 0x4c; // 76 c
    int COMMAND_REQUEST_PACKET = 0x4d; // 77 s
    int COMMAND_BLOCK_UPDATE_PACKET = 0x4e; // 78 s
    int COMMAND_OUTPUT_PACKET = 0x4f; // 79 c
    int UPDATE_TRADE_PACKET = 0x50; // 80 c
    int UPDATE_EQUIPMENT_PACKET = 0x51; // 81 c
    int RESOURCE_PACK_DATA_INFO_PACKET = 0x52; // 82 c
    int RESOURCE_PACK_CHUNK_DATA_PACKET = 0x53; // 83 c
    int RESOURCE_PACK_CHUNK_REQUEST_PACKET = 0x54; // 84 s
    int TRANSFER_PACKET = 0x55; // 85 c
    int PLAY_SOUND_PACKET = 0x56; // 86 c
    int STOP_SOUND_PACKET = 0x57; // 87 c
    int SET_TITLE_PACKET = 0x58; // 88 c
    int ADD_BEHAVIOR_TREE_PACKET = 0x59; // 89 c
    int STRUCTURE_BLOCK_UPDATE_PACKET = 0x5a; // 90 s
    int SHOW_STORE_OFFER_PACKET = 0x5b; // 91 c
    int PURCHASE_RECEIPT_PACKET = 0x5c; // 92 s
    int PLAYER_SKIN_PACKET = 0x5d; // 93 cs
    int SUB_CLIENT_LOGIN_PACKET = 0x5e; // 94 s
    int AUTOMATION_CLIENT_CONNECT_PACKET = 0x5f; // 95 c
    int SET_LAST_HURT_BY_PACKET = 0x60; // 96 c
    int BOOK_EDIT_PACKET = 0x61; // 97 s
    int NPC_REQUEST_PACKET = 0x62; // 98 s
    int PHOTO_TRANSFER_PACKET = 0x63; // 99 c
    int MODAL_FORM_REQUEST_PACKET = 0x64; // 100 c
    int MODAL_FORM_RESPONSE_PACKET = 0x65; // 101 s
    int SERVER_SETTINGS_REQUEST_PACKET = 0x66; // 102 s
    int SERVER_SETTINGS_RESPONSE_PACKET = 0x67; // 103 c
    int SHOW_PROFILE_PACKET = 0x68; // 104 c
    int SET_DEFAULT_GAME_TYPE_PACKET = 0x69; // 105 cs
    int REMOVE_OBJECTIVE_PACKET = 0x6a; // 106 c
    int SET_DISPLAY_OBJECTIVE_PACKET = 0x6b; // 107 c
    int SET_SCORE_PACKET = 0x6c; // 108 c
    int LAB_TABLE_PACKET = 0x6d; // 109 cs
    int UPDATE_BLOCK_SYNCED_PACKET = 0x6e; // 110 c
    int MOVE_ACTOR_DELTA_PACKET = 0x6f; // 111 c
    int SET_SCOREBOARD_IDENTITY_PACKET = 0x70; // 112 c
    int SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET = 0x71; // 113 s
    int UPDATE_SOFT_ENUM_PACKET = 0x72; // 114 c
    int NETWORK_STACK_LATENCY_PACKET = 0x73; // 115 cs
    int BLOCK_PALETTE_PACKET = 0x74; // 116 c D
    int SCRIPT_CUSTOM_EVENT_PACKET = 0x75; // 117 cs D
    int SPAWN_PARTICLE_EFFECT_PACKET = 0x76; // 118 c
    int AVAILABLE_ACTOR_IDENTIFIERS_PACKET = 0x77; // 119 c
    int LEVEL_SOUND_EVENT_PACKET_V2 = 0x78; // 120 cs D
    int NETWORK_CHUNK_PUBLISHER_UPDATE_PACKET = 0x79; // 121 c
    int BIOME_DEFINITION_LIST_PACKET = 0x7a; // 122 c
    int LEVEL_SOUND_EVENT_PACKET_V3 = 0x7b; // 123 cs
    int LEVEL_EVENT_GENERIC_PACKET = 0x7c; // 124 c
    int LECTERN_UPDATE_PACKET = 0x7d; // 125 s
    int VIDEO_STREAM_CONNECT_PACKET = 0x7e; // 126 c D
    int ADD_ENTITY_PACKET = 0x7f; // 127 c D
    int REMOVE_ENTITY_PACKET = 0x80; // 128 c D
    int CLIENT_CACHE_STATUS_PACKET = 0x81; // 129 s
    int ON_SCREEN_TEXTURE_ANIMATION_PACKET = 0x82; // 130 c
    int MAP_CREATE_LOCKED_COPY_PACKET = 0x83; // 131 s
    int STRUCTURE_TEMPLATE_DATA_REQUEST_PACKET = 0x84; // 132 s
    int STRUCTURE_TEMPLATE_DATA_EXPORT_PACKET = 0x85; // 133 c
    int UPDATE_BLOCK_PROPERTIES_PACKET = 0x86; // 134 c D
    int CLIENT_CACHE_BLOB_STATUS_PACKET = 0x87; // 135 s
    int CLIENT_CACHE_MISS_RESPONSE_PACKET = 0x88; // 136 c
    int EDUCATION_SETTINGS_PACKET = 0x89; // 137 c
    int EMOTE_PACKET = 0x8a; // 138 cs
    int MULTIPLAYER_SETTINGS_PACKET = 0x8b; // 139 cs
    int SETTINGS_COMMAND_PACKET = 0x8c; // 140 s
    int ANVIL_DAMAGE_PACKET = 0x8d; // 141 s
    int COMPLETED_USING_ITEM_PACKET = 0x8e; // 142 c
    int NETWORK_SETTINGS_PACKET = 0x8f; // 143 c
    int PLAYER_AUTH_INPUT_PACKET = 0x90; // 144 s
    int CREATIVE_CONTENT_PACKET = 0x91; // 145 c
    int PLAYER_ENCHANT_OPTIONS_PACKET = 0x92; // 146 c
    int ITEM_STACK_REQUEST_PACKET = 0x93; // 147 s
    int ITEM_STACK_RESPONSE_PACKET = 0x94; // 148 c
    int PLAYER_ARMOR_DAMAGE_PACKET = 0x95; // 149 c
    int CODE_BUILDER_PACKET = 0x96; // 150 c
    int UPDATE_PLAYER_GAME_TYPE_PACKET = 0x97; // 151 c
    int EMOTE_LIST_PACKET = 0x98; // 152 cs
    int POSITION_TRACKING_DB_SERVER_BROADCAST_PACKET = 0x99; // 153 c
    int POSITION_TRACKING_DB_CLIENT_REQUEST_PACKET = 0x9a; // 154 s
    int DEBUG_INFO_PACKET = 0x9b; // 155 cs
    int PACKET_VIOLATION_WARNING_PACKET = 0x9c; // 156 s
    int MOTION_PREDICTION_HINTS_PACKET = 0x9d; // 157 c
    int ANIMATE_ENTITY_PACKET = 0x9e; // 158 c
    int CAMERA_SHAKE_PACKET = 0x9f; // 159 c
    int PLAYER_FOG_PACKET = 0xa0; // 160 c
    int CORRECT_PLAYER_MOVE_PREDICTION_PACKET = 0xa1; // 161 c
    int ITEM_REGISTRY_PACKET = 0xa2; // 162 c
    int FILTER_TEXT_PACKET = 0xa3; // 163 cs d
    int CLIENTBOUND_DEBUG_RENDERER_PACKET = 0xa4; // 164 c
    int SYNC_ACTOR_PROPERTY_PACKET = 0xa5; // 165 c
    int ADD_VOLUME_ENTITY_PACKET = 0xa6; // 166 c
    int REMOVE_VOLUME_ENTITY_PACKET = 0xa7; // 167 c
    int SIMULATION_TYPE_PACKET = 0xa8; // 168 c
    int NPC_DIALOGUE_PACKET = 0xa9; // 169 c
    int EDU_URI_RESOURCE_PACKET = 0xaa; // 170 c
    int CREATE_PHOTO_PACKET = 0xab; // 171 s
    int UPDATE_SUB_CHUNK_BLOCKS_PACKET = 0xac; // 172 c
    int PHOTO_INFO_REQUEST_PACKET = 0xad; // 173 s D
    int SUB_CHUNK_PACKET = 0xae; // 174 c
    int SUB_CHUNK_REQUEST_PACKET = 0xaf; // 175 s
    int PLAYER_START_ITEM_COOLDOWN_PACKET = 0xb0; // 176 c
    int SCRIPT_MESSAGE_PACKET = 0xb1; // 177 sc
    int CODE_BUILDER_SOURCE_PACKET = 0xb2; // 178 s
    int TICKING_AREAS_LOAD_STATUS_PACKET = 0xb3; // 179 c
    int DIMENSION_DATA_PACKET = 0xb4; // 180 c
    int AGENT_ACTION_EVENT_PACKET = 0xb5; // 181 c
    int CHANGE_MOB_PROPERTY_PACKET = 0xb6; // 182 s
    int LESSON_PROGRESS_PACKET = 0xb7; // 183 c
    int REQUEST_ABILITY_PACKET = 0xb8; // 184 s
    int REQUEST_PERMISSIONS_PACKET = 0xb9; // 185 s
    int TOAST_REQUEST_PACKET = 0xba; // 186 c
    int UPDATE_ABILITIES_PACKET = 0xbb; // 187 c
    int UPDATE_ADVENTURE_SETTINGS_PACKET = 0xbc; // 188 c
    int DEATH_INFO_PACKET = 0xbd; // 189 c
    int EDITOR_NETWORK_PACKET = 0xbe; // 190 cs
    int FEATURE_REGISTRY_PACKET = 0xbf; // 191 c
    int SERVER_STATS_PACKET = 0xc0; // 192 c
    int REQUEST_NETWORK_SETTINGS_PACKET = 0xc1; // 193 s
    int GAME_TEST_REQUEST_PACKET = 0xc2; // 194 s
    int GAME_TEST_RESULTS_PACKET = 0xc3; // 195 c
    int UPDATE_CLIENT_INPUT_LOCKS_PACKET = 0xc4; // 196 c
    int CLIENT_CHEAT_ABILITY_PACKET = 0xc5; // 197 s D
    int CAMERA_PRESETS_PACKET = 0xc6; // 198 c
    int UNLOCKED_RECIPES_PACKET = 0xc7; // 199 c

    //NetEase Common Mod
    int PACKET_PY_RPC = 0xc8; // 200 cs
    int PACKET_CHANGE_MODEL = 0xc9; // 201
    int PACKET_STORE_BUY_SUCC = 0xca; // 202 s
    int PACKET_NETEASE_JSON = 0xcb; // 203 cs
    int PACKET_CHANGE_MODEL_TEXTURE = 0xcc; // 204
    int PACKET_CHANGE_MODEL_OFFSET = 0xcd; // 205
    int PACKET_CHANGE_MODEL_BIND = 0xce; // 206
    int PACKET_HUNGER_ATTR = 0xcf; // 207
    int PACKET_SET_DIMENSION_LOCAL_TIME = 0xd0; // 208
    int PACKET_WITHDRAW_FURNACE_XP = 0xd1; // 209
    int PACKET_SET_DIMENSION_LOCAL_WEATHER = 0xd2; // 210

    //int PACKET_CUSTOM = 0xdf; // 223
    int PACKET_COMBINE = 0xe0; // 224
    int PACKET_V_CONNECTION = 0xe1; // 225
    int PACKET_TRANSPORT = 0xe2; // 226
    //int PACKET_CUSTOM = 0xe3; // 227
    int PACKET_CONFIRM_SKIN = 0xe4; // 228 c
    int PACKET_TRANSPORT_NO_COMPRESS = 0xe5; // 229
    int PACKET_MOD_EFFECT = 0xe6; // 230
    int PACKET_MOD_BLOCK_ACTOR_CHANGED = 0xe7; // 231
    int PACKET_CHANGE_ACTOR_MOTION = 0xe8; // 232
    int PACKET_ANIMATE_EMOTE_ENTITY = 0xe9; // 233
    int PACKET_CHANGE_BIOME = 0xea; // 234
    int PACKET_UPDATE_BIOME = 0xeb; // 235
    int PACKET_SYNC_SKIN = 0xec; // 236

    int BATCH_PACKET = 0xfe; // 254

    int CAMERA_INSTRUCTION_PACKET = 0x12c; // 300 c
    int COMPRESSED_BIOME_DEFINITION_LIST_PACKET = 0x12d; // 301 c
    int TRIM_DATA_PACKET = 0x12e; // 302 c
    int OPEN_SIGN_PACKET = 0x12f; // 303 c
    int AGENT_ANIMATION_PACKET = 0x130; // 304 c
    int REFRESH_ENTITLEMENTS_PACKET = 0x131; // 305 c
    int PLAYER_TOGGLE_CRAFTER_SLOT_REQUEST_PACKET = 0x132; // 306 s
    int SET_PLAYER_INVENTORY_OPTIONS_PACKET = 0x133; // 307 cs
    int SET_HUD_PACKET = 0x134; // 308 c
    int AWARD_ACHIEVEMENT_PACKET = 0x135; // 309 c
    int CLIENTBOUND_CLOSE_FORM_PACKET = 0x136; // 310 c
    int CLIENTBOUND_LOADING_SCREEN_PACKET = 0x137; // 311 c D
    int SERVERBOUND_LOADING_SCREEN_PACKET = 0x138; // 312 s
    int JIGSAW_STRUCTURE_DATA_PACKET = 0x139; // 313 c
    int CURRENT_STRUCTURE_FEATURE_PACKET = 0x13a; // 314 c
    int SERVERBOUND_DIAGNOSTICS_PACKET = 0x13b; // 315 s
    int CAMERA_AIM_ASSIST_PACKET = 0x13c; // 316 c
    int CONTAINER_REGISTRY_CLEANUP_PACKET = 0x13d; // 317 c
    int MOVEMENT_EFFECT_PACKET = 0x13e; // 318 c
    int SET_MOVEMENT_AUTHORITY_PACKET = 0x13f; // 319 c D
    int CAMERA_AIM_ASSIST_PRESETS_PACKET = 0x140; // 320 c
    int CLIENT_CAMERA_AIM_ASSIST_PACKET = 0x141; // 321 s
    int CLIENT_MOVEMENT_PREDICTION_SYNC_PACKET = 0x142; // 322 s
    int UPDATE_CLIENT_OPTIONS_PACKET = 0x143; // 323 s
    int PLAYER_VIDEO_CAPTURE_PACKET = 0x144; // 324 c
    int PLAYER_UPDATE_ENTITY_OVERRIDES_PACKET = 0x145; // 325 c
    int PLAYER_LOCATION_PACKET = 0x146; // 326 c
    int CLIENTBOUND_CONTROL_SCHEME_SET_PACKET = 0x147; // 327 c
    int SERVER_SCRIPT_DEBUG_DRAWER_PACKET = 0x148; // 328 c


    int COUNT = 1024;
}
