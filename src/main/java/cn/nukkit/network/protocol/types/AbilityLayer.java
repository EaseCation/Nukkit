package cn.nukkit.network.protocol.types;

import lombok.ToString;

import java.util.EnumSet;
import java.util.Set;

@ToString
public class AbilityLayer {
    public static final int TYPE_CACHE = 0;
    public static final int TYPE_BASE = 1;
    public static final int TYPE_SPECTATOR = 2;
    public static final int TYPE_COMMANDS = 3;
    /**
     * @since 1.19.40
     */
    public static final int TYPE_EDITOR = 4;
    /**
     * @since 1.21.20
     */
    public static final int TYPE_LOADING_SCREEN = 5;

    public int type = TYPE_BASE;
    public Set<PlayerAbility> abilitiesSet = EnumSet.allOf(PlayerAbility.class);
    public Set<PlayerAbility> abilityValues = EnumSet.noneOf(PlayerAbility.class);
    public float flySpeed = 0.05f;
    /**
     * @since 1.21.60
     */
    public float verticalFlySpeed = 1;
    public float walkSpeed = 0.1f;

    public AbilityLayer() {
    }

    public AbilityLayer(int type, Set<PlayerAbility> abilitiesSet, Set<PlayerAbility> abilityValues, float flySpeed, float verticalFlySpeed, float walkSpeed) {
        this.type = type;
        this.abilitiesSet = abilitiesSet;
        this.abilityValues = abilityValues;
        this.flySpeed = flySpeed;
        this.verticalFlySpeed = verticalFlySpeed;
        this.walkSpeed = walkSpeed;
    }
}
