package cn.nukkit.entity.data;

public interface EntityDataID {
    int DATA_FLAGS = 0;
    int DATA_HEALTH = 1; //int structural integrity (minecart/boat)
    int DATA_VARIANT = 2; //int
    int DATA_COLOR = 3; //byte color index
    int DATA_NAMETAG = 4; //string
    int DATA_OWNER_EID = 5; //long
    int DATA_TARGET_EID = 6; //long
    int DATA_AIR = 7; //short
    int DATA_POTION_COLOR = 8; //int (ARGB!)
    int DATA_POTION_AMBIENT = 9; //byte
    int DATA_JUMP_DURATION = 10; //byte
    int DATA_HURT_TIME = 11; //int (minecart/boat/armor stand)
    int DATA_HURT_DIRECTION = 12; //int (minecart/boat/armor stand)
    int DATA_PADDLE_TIME_LEFT = 13; //float
    int DATA_PADDLE_TIME_RIGHT = 14; //float
    int DATA_EXPERIENCE_VALUE = 15; //int (xp orb)
    int DATA_MINECART_DISPLAY_BLOCK = 16; //int (block runtime ID)
    int DATA_HORSE_FLAGS = DATA_MINECART_DISPLAY_BLOCK; //int: 4 = saddle | 16 = bred | 32 = eating | 64 = standing | 128 = open mouth
    int DATA_DISPLAY_ITEM = DATA_MINECART_DISPLAY_BLOCK; //compoundtag (firework/ominous_item_spawner)
    int DATA_WITHER_SKULL_DANGEROUS = DATA_MINECART_DISPLAY_BLOCK; //byte
    int DATA_DISPLAY_OFFSET = 17; //int
    int DATA_FIREWORK_DIRECTION = DATA_DISPLAY_OFFSET; //vec3f
    int DATA_ARROW_SHOOTER_EID = DATA_DISPLAY_OFFSET; //long arrow owner
    int DATA_TICKS_BEFORE_REMOVAL = DATA_DISPLAY_OFFSET; //int (ominous_item_spawner)
    int DATA_HAS_DISPLAY = 18; //byte (must be 1 for minecart to show block inside)
    int DATA_FIREWORK_ATTACHED_EID = DATA_HAS_DISPLAY; //long
    int DATA_ARROW_AUX_VALUE = DATA_HAS_DISPLAY; //byte (tipped arrow item meta)
    int DATA_CREEPER_SWELL = 19; //int
    int DATA_HORSE_TYPE = DATA_CREEPER_SWELL; //byte: -1 = unset, 0 = default, 1 = donkey, 2 = mule, 3 = zombie, 4 = skeleton
    int DATA_CREEPER_OLD_SWELL = 20; //int
    int DATA_CREEPER_SWELL_DIR = 21; //byte
    int DATA_CHARGE_AMOUNT = 22; //byte used for ghasts and also crossbow charging
    int DATA_ENDERMAN_HELD_BLOCK = 23; //int (block runtime ID)
    int DATA_ENDERMAN_HELD_BLOCK_DAMAGE = 24; //short DEPRECATED
    int DATA_ENTITY_AGE = 25; //short
    int DATA_SLIME_CLIENT_EVENT = DATA_ENTITY_AGE; //byte: 0 = none, 1 (default) = land, 2 = jump
    int DATA_WITCH_USING_ITEM = 26; //byte /* TODO: (int) used by horse */
    int DATA_PLAYER_FLAGS = 27; //byte
    int DATA_PLAYER_INDEX = 28; //int, used for marker colors and agent nametag colors
    int DATA_PLAYER_BED_POSITION = 29; //block coords
    int DATA_FIREBALL_POWER_X = 30; //float
    int DATA_FIREBALL_POWER_Y = 31; //float
    int DATA_FIREBALL_POWER_Z = 32; //float
    int DATA_AUX_POWER = 33;
    int DATA_FISH_X = 34; //float fishing
    int DATA_FISH_Z = 35; //float fishing
    int DATA_FISH_ANGLE = 36; //float fishing
    int DATA_AUX_VALUE_DATA = 37; //short potion type
    int DATA_ZOMBIE_TYPE = DATA_AUX_VALUE_DATA; //short zombie type (0 = default, 1 = villager, 2 = husk, 3 = pig, 4 = drowned)
    int DATA_LEAD_HOLDER_EID = 38; //long
    int DATA_SCALE = 39; //float
    int DATA_INTERACTIVE_TAG = 40; //string (button text)
    int DATA_NPC_SKIN_ID = 41; //string DEPRECATED
    int DATA_URL_TAG = 42; //string related to npc component - actions (json)
    int DATA_MAX_AIR = 43; //short
    int DATA_MARK_VARIANT = 44; //int
    int DATA_CONTAINER_TYPE = 45; //byte container component
    int DATA_CONTAINER_BASE_SIZE = 46; //int container component
    int DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH = 47; //int container component (used for llamas, inventory size is baseSize + thisProp * strength)
    int DATA_BLOCK_TARGET = 48; //block coords (ender crystal)
    int DATA_WITHER_INVULNERABLE_TICKS = 49; //int
    int DATA_WITHER_TARGET_1_EID = 50; //long
    int DATA_WITHER_TARGET_2_EID = 51; //long
    int DATA_WITHER_TARGET_3_EID = 52; //long
    int DATA_AERIAL_ATTACK = 53; //short
    int DATA_BOUNDING_BOX_WIDTH = 54; //float
    int DATA_BOUNDING_BOX_HEIGHT = 55; //float
    int DATA_FUSE_LENGTH = 56; //int
    int DATA_SEAT_OFFSET = 57; //vector3f
    int DATA_SEAT_LOCK_PASSENGER_ROTATION = 58; //bool
    int DATA_SEAT_LOCK_PASSENGER_ROTATION_DEGREES = 59; //float
    int DATA_SEAT_ROTATION_OFFSET_DEGREES = 60; //float
    int DATA_AREA_EFFECT_CLOUD_RADIUS = 61; //float
    int DATA_AREA_EFFECT_CLOUD_WAITING = 62; //int
    int DATA_AREA_EFFECT_CLOUD_PARTICLE_ID = 63; //int
    int DATA_SHULKER_PEEK_ID = 64; //int
    int DATA_SHULKER_ATTACH_FACE = 65; //byte
    int DATA_SHULKER_ATTACHED = 66; //short
    int DATA_SHULKER_ATTACH_POS = 67; //block coords
    int DATA_TRADING_PLAYER_EID = 68; //long
    int DATA_TRADING_CAREER = 69; //int
    int DATA_HAS_COMMAND_BLOCK = 70; //byte
    int DATA_COMMAND_BLOCK_COMMAND = 71; //string
    int DATA_COMMAND_BLOCK_LAST_OUTPUT = 72; //string
    int DATA_COMMAND_BLOCK_TRACK_OUTPUT = 73; //byte
    int DATA_CONTROLLING_SEAT_INDEX = 74; //byte
    int DATA_STRENGTH = 75; //int
    int DATA_MAX_STRENGTH = 76; //int
    int DATA_SPELL_CASTING_COLOR = 77; //int
    int DATA_LIMITED_LIFE = 78; //int
    int DATA_ALWAYS_SHOW_NAMETAG = 80; //byte: -1 = default, 0 = only when looked at, 1 = always
    int DATA_COLOR_2 = 81; //byte color index 2
    int DATA_NAME_AUTHOR = 82; //string
    int DATA_SCORE_TAG = 83; //string
    int DATA_BALLOON_ATTACHED_EID = 84; //long
    int DATA_PUFFERFISH_SIZE = 85; //byte
    int DATA_BOAT_BUBBLE_TIME = 86; //int (time in bubble column)
    int DATA_PLAYER_AGENT_EID = 87; //long
    int DATA_SITTING_AMOUNT = 88; //float
    int DATA_SITTING_AMOUNT_PREVIOUS = 89; //float
    int DATA_EATING_COUNTER = 90; //int (used by pandas)
    int DATA_FLAGS_EXTENDED = 91; //long (data flags 2)
    int DATA_LAYING_AMOUNT = 92; //float (used by pandas)
    int DATA_LAYING_AMOUNT_PREVIOUS = 93; //float (used by pandas)
    int DATA_AREA_EFFECT_CLOUD_DURATION = 94; //int
    int DATA_AREA_EFFECT_CLOUD_SPAWN_TIME = 95; //int
    int DATA_AREA_EFFECT_CLOUD_CHANGE_RATE = 96; //float, usually negative
    int DATA_AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP = 97; //float
    int DATA_AREA_EFFECT_CLOUD_PICKUP_COUNT = 98; //int
    int DATA_HAS_NPC_COMPONENT = 99; //byte
    int DATA_TRADE_TIER = 100; //int villager (trade)
    int DATA_MAX_TRADE_TIER = 101; //int villager (trade)
    int DATA_TRADE_EXPERIENCE = 102; //int villager (trade)
    int DATA_SKIN_ID = 103; //int texture index e.g. npc
    int DATA_SPAWNING_FRAMES = 104; //int - related to wither
    int DATA_COMMAND_BLOCK_TICK_DELAY = 105; //int
    int DATA_COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK = 106; //byte
    int DATA_AMBIENT_SOUND_INTERVAL = 107; //float
    int DATA_AMBIENT_SOUND_INTERVAL_RANGE = 108; //float
    int DATA_AMBIENT_SOUND_EVENT_NAME = 109; //string
    int DATA_FALL_DAMAGE_MULTIPLIER = 110; //float
    int DATA_NAME_RAW_TEXT = 111; //string npc component
    int DATA_CAN_RIDE_TARGET = 112; //byte
    int DATA_LOW_TIER_CURED_DISCOUNT = 113; //int villager (trade)
    int DATA_HIGH_TIER_CURED_DISCOUNT = 114; //int villager (trade)
    int DATA_NEARBY_CURED_DISCOUNT = 115; //int villager (trade)
    int DATA_NEARBY_CURED_DISCOUNT_TIMESTAMP = 116; //int villager (trade)
    int DATA_HITBOX = 117; //compound
    int DATA_IS_BUOYANT = 118; //byte buoyant component e.g. boat
    int DATA_FREEZING_EFFECT_STRENGTH = 119; //float related to powder snow
    int DATA_BUOYANCY_DATA = 120; //string buoyant component (json) e.g. boat
    int DATA_GOAT_HORN_COUNT = 121; //int
    int DATA_BASE_RUNTIME_ID = 122; //string vanilla identifier
    int DATA_DEFINE_PROPERTIES = 123; // DEPRECATED
    int DATA_UPDATE_PROPERTIES = 124; // DEPRECATED
    int DATA_ARMOR_STAND_POSE_INDEX = 125; //int
    int DATA_ENDER_CRYSTAL_TIME_OFFSET = 126; //int
    int DATA_SEAT_ROTATION_OFFSET = 127; //bool
    /**
     * float.
     * @since 1.18.30
     */
    int DATA_MOVEMENT_SOUND_DISTANCE_OFFSET = 128; // related to warden
    /**
     * int.
     * @since 1.18.30
     */
    int DATA_HEARTBEAT_INTERVAL_TICKS = 129; //int related to warden
    /**
     * int.
     * @since 1.18.30
     */
    int DATA_HEARTBEAT_SOUND_EVENT = 130; //int related to warden (level sound event id)
    /**
     * block pos.
     * @since 1.19.0
     */
    int DATA_PLAYER_LAST_DEATH_POS = 131; //blockpos related to recovery compass
    /**
     * int.
     * @since 1.19.0
     */
    int DATA_PLAYER_LAST_DEATH_DIMENSION = 132; //int related to recovery compass
    /**
     * bool.
     * @since 1.19.0
     */
    int DATA_PLAYER_HAS_DIED = 133; //byte related to recovery compass
    /**
     * string.
     * @since 1.12.0
     */
    int DATA_NPC_DATA = 134; //string related to npc component (json)
    int DATA_NUKKIT_FLAGS = 135; //long CUSTOM HACK!!
    /**
     * vec3.
     * @since 1.20.10
     */
    int DATA_COLLISION_BOX = 136; //vec3
    int DATA_VISIBLE_MOB_EFFECTS = 137;

    int DATA_UNDEFINED = 138;
}
