package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public abstract class BlockButton extends BlockTransparentMeta implements Faceable {

    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int BUTTON_PRESSED_BIT = 0b1000;

    public BlockButton() {
        this(0);
    }

    public BlockButton(int meta) {
        super(meta);
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!SupportType.hasCenterSupport(target, face)) {
            return false;
        }

        this.setDamage(face.getIndex());
        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (this.isActivated()) {
            return false;
        }

        this.level.scheduleUpdate(this, 30);

        this.setDamage(this.getDamage() ^ BUTTON_PRESSED_BIT);
        this.level.setBlock(this, this, true, false);

        if (this.level.isRedstoneEnabled()) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));

            level.updateAroundRedstone(this, null);
            level.updateAroundRedstone(this.getSideVec(getBlockFace().getOpposite()), null);
        }

        this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_POWER_ON, (this.getId() << BLOCK_META_BITS) | this.getDamage());
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = getBlockFace();
            if (!SupportType.hasCenterSupport(getSide(face.getOpposite()), face)) {
                this.level.useBreakOn(this, Item.get(Item.WOODEN_PICKAXE));
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (this.isActivated()) {
                this.setDamage(this.getDamage() ^ BUTTON_PRESSED_BIT);
                this.level.setBlock(this, this, true, false);
                this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_POWER_OFF, (this.getId() << BLOCK_META_BITS) | this.getDamage());

                if (this.level.isRedstoneEnabled()) {
                    this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                    level.updateAroundRedstone(this, null);
                    level.updateAroundRedstone(this.getSideVec(getBlockFace().getOpposite()), null);
                }
            }

            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    public boolean isActivated() {
        return ((this.getDamage() & BUTTON_PRESSED_BIT) == BUTTON_PRESSED_BIT);
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public int getWeakPower(BlockFace side) {
        return isActivated() ? 15 : 0;
    }

    public int getStrongPower(BlockFace side) {
        return !isActivated() ? 0 : (getBlockFace() == side ? 15 : 0);
    }

    @Override
    public boolean onBreak(Item item) {
        if (isActivated()) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
        }

        return super.onBreak(item);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & FACING_DIRECTION_MASK);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isButton() {
        return true;
    }
}
