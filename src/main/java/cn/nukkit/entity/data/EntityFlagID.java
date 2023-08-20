package cn.nukkit.entity.data;

public interface EntityFlagID {
    int DATA_FLAG_ONFIRE = 0;
    int DATA_FLAG_SNEAKING = 1;
    int DATA_FLAG_RIDING = 2;
    int DATA_FLAG_SPRINTING = 3;
    int DATA_FLAG_ACTION = 4;
    int DATA_FLAG_INVISIBLE = 5;
    int DATA_FLAG_TEMPTED = 6;
    int DATA_FLAG_INLOVE = 7;
    int DATA_FLAG_SADDLED = 8;
    int DATA_FLAG_POWERED = 9;
    int DATA_FLAG_IGNITED = 10;
    int DATA_FLAG_BABY = 11; //disable head scaling
    int DATA_FLAG_CONVERTING = 12;
    int DATA_FLAG_CRITICAL = 13;
    int DATA_FLAG_CAN_SHOW_NAMETAG = 14;
    int DATA_FLAG_ALWAYS_SHOW_NAMETAG = 15;
    int DATA_FLAG_IMMOBILE = 16, DATA_FLAG_NO_AI = 16;
    int DATA_FLAG_SILENT = 17;
    int DATA_FLAG_WALLCLIMBING = 18;
    int DATA_FLAG_CAN_CLIMB = 19;
    int DATA_FLAG_SWIMMER = 20;
    int DATA_FLAG_CAN_FLY = 21;
    int DATA_FLAG_CAN_WALK = 22;
    int DATA_FLAG_RESTING = 23;
    int DATA_FLAG_SITTING = 24;
    int DATA_FLAG_ANGRY = 25;
    int DATA_FLAG_INTERESTED = 26;
    int DATA_FLAG_CHARGED = 27;
    int DATA_FLAG_TAMED = 28;
    int DATA_FLAG_ORPHANED = 29;
    int DATA_FLAG_LEASHED = 30;
    int DATA_FLAG_SHEARED = 31;
    int DATA_FLAG_GLIDING = 32;
    int DATA_FLAG_ELDER = 33;
    int DATA_FLAG_MOVING = 34;
    int DATA_FLAG_BREATHING = 35;
    int DATA_FLAG_CHESTED = 36;
    int DATA_FLAG_STACKABLE = 37;
    int DATA_FLAG_SHOWBASE = 38;
    int DATA_FLAG_REARING = 39;
    int DATA_FLAG_VIBRATING = 40;
    int DATA_FLAG_IDLING = 41;
    int DATA_FLAG_EVOKER_SPELL = 42;
    int DATA_FLAG_CHARGE_ATTACK = 43;
    int DATA_FLAG_WASD_CONTROLLED = 44;
    int DATA_FLAG_CAN_POWER_JUMP = 45;
    int DATA_FLAG_LINGER = 46;
    int DATA_FLAG_HAS_COLLISION = 47;
    int DATA_FLAG_GRAVITY = 48;
    int DATA_FLAG_FIRE_IMMUNE = 49;
    int DATA_FLAG_DANCING = 50;
    int DATA_FLAG_ENCHANTED = 51;
    int DATA_FLAG_RETURN_TRIDENT = 52;
    int DATA_FLAG_CONTAINER_IS_PRIVATE = 53;
    int DATA_FLAG_TRANSFORMING = 54;
    int DATA_FLAG_SPIN_ATTACK = 55;
    int DATA_FLAG_SWIMMING = 56;
    int DATA_FLAG_BRIBED = 57;
    int DATA_FLAG_PREGNANT = 58;
    int DATA_FLAG_LAYING_EGG = 59;
    int DATA_FLAG_RIDER_CAN_PICK = 60;
    int DATA_FLAG_TRANSITION_SETTING = 61;
    int DATA_FLAG_EATING = 62;
    int DATA_FLAG_LAYING_DOWN = 63;
    int DATA_FLAG_SNEEZING = 64;
    int DATA_FLAG_TRUSTING = 65;
    int DATA_FLAG_ROLLING = 66;
    int DATA_FLAG_SCARED = 67;
    int DATA_FLAG_IN_SCAFFOLDING = 68;
    int DATA_FLAG_OVER_SCAFFOLDING = 69;
    int DATA_FLAG_FALL_THROUGH_SCAFFOLDING = 70;
    int DATA_FLAG_BLOCKING = 71;
    int DATA_FLAG_TRANSITION_BLOCKING = 72;
    int DATA_FLAG_BLOCKED_USING_SHIELD = 73;
    int DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD = 74;
    int DATA_FLAG_SLEEPING = 75;
    int DATA_FLAG_ENTITY_GROW_UP = 76;
    int DATA_FLAG_TRADE_INTEREST = 77;
    int DATA_FLAG_DOOR_BREAKER = 78;
    int DATA_FLAG_BREAKING_OBSTRUCTION = 79;
    int DATA_FLAG_DOOR_OPENER = 80;
    int DATA_FLAG_IS_ILLAGER_CAPTAIN = 81;
    int DATA_FLAG_STUNNED = 82;
    int DATA_FLAG_ROARING = 83;
    int DATA_FLAG_DELAYED_ATTACK = 84;
    int DATA_FLAG_IS_AVOIDING_MOBS = 85;
    int DATA_FLAG_IS_AVOIDING_BLOCKS = 86;
    int DATA_FLAG_FACING_TARGET_TO_RANGE_ATTACK = 87;
    int DATA_FLAG_HIDDEN_WHEN_INVISIBLE = 88;
    int DATA_FLAG_IS_IN_UI = 89;
    int DATA_FLAG_STALKING = 90;
    int DATA_FLAG_EMOTING = 91;
    int DATA_FLAG_CELEBRATING = 92;
    int DATA_FLAG_ADMIRING = 93;
    int DATA_FLAG_CELEBRATING_SPECIAL = 94;
    int DATA_FLAG_OUT_OF_CONTROL = 95;
    int DATA_FLAG_RAM_ATTACK = 96;
    int DATA_FLAG_PLAYING_DEAD = 97;
    int DATA_FLAG_IN_ASCENDABLE_BLOCK = 98;
    int DATA_FLAG_OVER_DESCENDABLE_BLOCK = 99;
    int DATA_FLAG_CROAKING = 100;
    int DATA_FLAG_EAT_MOB = 101;
    int DATA_FLAG_JUMP_GOAL_JUMP = 102;
    int DATA_FLAG_EMERGING = 103;
    int DATA_FLAG_SNIFFING = 104;
    int DATA_FLAG_DIGGING = 105;
    int DATA_FLAG_SONIC_BOOM = 106;
    /**
     * @since 1.19.50
     */
    int DATA_FLAG_CAN_DASH = 107;
    /**
     * @since 1.19.50
     */
    int DATA_FLAG_HAS_DASH_COOLDOWN = 108;
    /**
     * @since 1.19.50
     */
    int DATA_FLAG_PUSH_TOWARDS_CLOSEST_SPACE = 109;
    /**
     * @since 1.19.70
     */
    int DATA_FLAG_SCENTING = 110;
    /**
     * @since 1.19.70
     */
    int DATA_FLAG_RISING = 111;
    /**
     * @since 1.19.70
     */
    int DATA_FLAG_HAPPY = 112;
    /**
     * @since 1.19.70
     */
    int DATA_FLAG_SEARCHING = 113;
    /**
     * @since 1.20.10
     */
    int DATA_FLAG_CRAWLING = 114;

    int DATA_FLAG_UNDEFINED = 115;


    long NUKKIT_FLAG_VARIANT_BLOCK = 1L << 1;
}