package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * Created by CreeperFace on 10.4.2017.
 */
public class BlockRedstoneRepeaterPowered extends BlockRedstoneDiode {

    public BlockRedstoneRepeaterPowered() {
        this(0);
    }

    public BlockRedstoneRepeaterPowered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POWERED_REPEATER;
    }

    @Override
    public String getName() {
        return "Powered Repeater";
    }

    @Override
    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(getDamage());
    }

    @Override
    protected boolean isAlternateInput(Block block) {
        return isDiode(block);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.REPEATER);
    }

    @Override
    protected int getDelay() {
        return (1 + (getDamage() >> 2)) * 2;
    }

    @Override
    protected Block getPowered() {
        return this;
    }

    @Override
    protected Block getUnpowered() {
        return Block.get(BlockID.UNPOWERED_REPEATER, this.getDamage());
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(this.getDamage() + 4);
        if (this.getDamage() > 0xf) this.setDamage(this.getDamage() % 4);

        this.level.setBlock(this, this, true, false);
        return true;
    }

    @Override
    public boolean isLocked() {
        return this.getPowerOnSides() > 0;
    }

    @Override
    public boolean isPoweredBlock() {
        return true;
    }
}
