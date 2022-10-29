package cn.nukkit.network.protocol.types;

public enum PlayerAbility {
    BUILD,
    MINE,
    DOORS_AND_SWITCHES, // disabling this also disables dropping items (???)
    OPEN_CONTAINERS,
    ATTACK_PLAYERS,
    ATTACK_MOBS,
    OPERATOR_COMMANDS,
    TELEPORT,
    INVULNERABLE,
    FLYING,
    MAY_FLY,
    INSTABUILD,
    LIGHTNING,
    FLY_SPEED,
    WALK_SPEED,
    MUTED,
    WORLD_BUILDER,
    NO_CLIP;

    private static final PlayerAbility[] VALUES = values();

    public static PlayerAbility[] getValues() {
        return VALUES;
    }

    public static PlayerAbility byId(int id) {
        return VALUES[id];
    }
}
