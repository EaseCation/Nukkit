package cn.nukkit.entity.attribute;

/**
 * Nukkit runtime IDs.
 */
public interface AttributeID {
    // mob generic attributes (see Mob::_registerMobAttributes())
    int HEALTH = 0;
    int ABSORPTION = 1;
    int KNOCKBACK_RESISTANCE = 2;
    int MOVEMENT = 3;
    int UNDERWATER_MOVEMENT = 4;
    int LAVA_MOVEMENT = 5;
    int LUCK = 6;
    int FOLLOW_RANGE = 7;
    // player attributes (see Player::_registerPlayerAttributes())
    int ATTACK_DAMAGE = 8;
    int PLAYER_HUNGER = 9;
    int PLAYER_EXHAUSTION = 10;
    int PLAYER_SATURATION = 11;
    int PLAYER_LEVEL = 12;
    int PLAYER_EXPERIENCE = 13;
    // mob special attributes
    int HORSE_JUMP_STRENGTH = 14; // see SharedAttributes::JUMP_STRENGTH
    int ZOMBIE_SPAWN_REINFORCEMENTS = 15; // see Zombie::SPAWN_REINFORCEMENTS_CHANCE

    int COUNT = 16;
}
