package cn.nukkit.network.protocol;

import cn.nukkit.Player;

/**
 * @author Nukkit Project Team
 */
public class AdventureSettingsPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.ADVENTURE_SETTINGS_PACKET;

    public static final int PERMISSION_NORMAL = 0;
    public static final int PERMISSION_OPERATOR = 1;
    public static final int PERMISSION_HOST = 2;
    public static final int PERMISSION_AUTOMATION = 3;
    public static final int PERMISSION_ADMIN = 4;
    //TODO: check level 3
    /**
     * This constant is used to identify flags that should be set on the second field. In a sensible world, these
     * flags would all be set on the same packet field, but as of MCPE 1.2, the new abilities flags have for some
     * reason been assigned a separate field.
     */
    public static final int BITFLAG_SECOND_SET = 1 << 16;

    public static final int WORLD_IMMUTABLE = 1;
    public static final int NO_PVP = 1 << 1;
    public static final int NO_MVP = 1 << 2;
    public static final int UNUSED = 1 << 3;
    public static final int SHOW_NAME_TAGS = 1 << 4;
    public static final int AUTO_JUMP = 1 << 5;
    public static final int ALLOW_FLIGHT = 1 << 6;
    public static final int NO_CLIP = 1 << 7;
    public static final int WORLD_BUILDER = 1 << 8;
    public static final int FLYING = 1 << 9;
    public static final int MUTED = 1 << 10;
    public static final int BUILD_AND_MINE = 1 | BITFLAG_SECOND_SET;
    public static final int DOORS_AND_SWITCHES = (1 << 1) | BITFLAG_SECOND_SET;
    public static final int OPEN_CONTAINERS = (1 << 2) | BITFLAG_SECOND_SET;
    public static final int ATTACK_PLAYERS = (1 << 3) | BITFLAG_SECOND_SET;
    public static final int ATTACK_MOBS = (1 << 4) | BITFLAG_SECOND_SET;
    public static final int OPERATOR = (1 << 5) | BITFLAG_SECOND_SET;
    public static final int TELEPORT = (1 << 7) | BITFLAG_SECOND_SET;
    public static final int BUILD = (1 << 8) | BITFLAG_SECOND_SET;
    public static final int DEAULT_LEVEL_PERMISSIONS = (1 << 9) | BITFLAG_SECOND_SET;

    public long flags = 0;

    public long commandPermission = PERMISSION_NORMAL;

    public long flags2 = -1;

    public long playerPermission = Player.PERMISSION_MEMBER;

    public long customFlags; //...

    public long entityUniqueId; //This is a little-endian long, NOT a var-long. (WTF Mojang)

    public void decode() {
        this.flags = getUnsignedVarInt();
        this.commandPermission = getUnsignedVarInt();
        this.flags2 = getUnsignedVarInt();
        this.playerPermission = getUnsignedVarInt();
        this.customFlags = getUnsignedVarInt();
        this.entityUniqueId = getLLong();
    }

    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.flags);
        this.putUnsignedVarInt(this.commandPermission);
        this.putUnsignedVarInt(this.flags2);
        this.putUnsignedVarInt(this.playerPermission);
        this.putUnsignedVarInt(this.customFlags);
        this.putLLong(this.entityUniqueId);
    }

    public boolean getFlag(int flag) {
        if ((flag & BITFLAG_SECOND_SET) != 0) {
            return (this.flags2 & flag) != 0;
        }
        return (this.flags & flag) != 0;
    }

    public void setFlag(int flag, boolean value) {
        boolean flags = (flag & BITFLAG_SECOND_SET) != 0;

        if (value) {
            if (flags) {
                this.flags2 |= flag;
            } else {
                this.flags |= flag;
            }
        } else {
            if (flags) {
                this.flags2 &= ~flag;
            } else {
                this.flags &= ~flag;
            }
        }
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
