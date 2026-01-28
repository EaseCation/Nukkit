package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.CommandBlockMode;
import lombok.ToString;

@ToString
public class CommandBlockUpdatePacket extends DataPacket {

    public boolean isBlock;
    public int x;
    public int y;
    public int z;
    public CommandBlockMode commandBlockMode;
    public boolean isRedstoneMode;
    public boolean isConditional;
    public long minecartEid;
    public String command;
    public String lastOutput;
    public String name;
    /**
     * @since 1.21.60
     */
    public String filteredName = "";
    public boolean shouldTrackOutput;
    /**
     * @since 1.12.0
     */
    public int tickDelay;
    /**
     * @since 1.12.0
     */
    public boolean executingOnFirstTick;

    @Override
    public int pid() {
        return ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET;
    }

    @Override
    public void decode() {
        this.isBlock = this.getBoolean();
        if (this.isBlock) {
            BlockVector3 v = this.getBlockVector3();
            this.x = v.x;
            this.y = v.y;
            this.z = v.z;
            this.commandBlockMode = CommandBlockMode.getValues()[(int) this.getUnsignedVarInt()];
            this.isRedstoneMode = this.getBoolean();
            this.isConditional = this.getBoolean();
        } else {
            this.minecartEid = this.getEntityRuntimeId();
        }
        this.command = this.getString();
        this.lastOutput = this.getString();
        this.name = this.getString();
        this.shouldTrackOutput = this.getBoolean();
    }

    @Override
    public void encode() {
    }
}
