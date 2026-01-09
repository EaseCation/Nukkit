package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

/**
 * Created by Leonidius20 on 18.08.18.
 */
public class BlockObserver extends BlockSolid implements Faceable {
    public static final int DIRECTION_MASK = 0x7;
    public static final int POWERED_BIT = 0x8;

    BlockObserver() {

    }

    @Override
    public String getName() {
        return "Observer";
    }

    @Override
    public int getId() {
        return OBSERVER;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double y = player.y + player.getEyeHeight();
                if (y - this.y > 2) {
                    this.setDamage(BlockFace.DOWN.getIndex());
                } else if (this.y - y > 0) {
                    this.setDamage(BlockFace.UP.getIndex());
                } else {
                    this.setDamage(player.getHorizontalFacing().getIndex());
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getIndex());
            }
        } else {
            this.setDamage(0);
        }
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & DIRECTION_MASK);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return face == getBlockFace() && isPowered() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return getWeakPower(side);
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        level.setBlock(this, get(AIR), true);
        if (isPowered()) {
            level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!level.isRedstoneEnabled()) {
                return 0;
            }

            setDamage(getDamage() ^ POWERED_BIT);
            level.setBlock(this, this, true);

            if (isPowered()) {
                level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
                level.scheduleUpdate(this, this, 2);
            } else {
                level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
            }

            level.updateAroundRedstone(getSideVec(getBlockFace()), null);
        }
        return 0;
    }

    public boolean isPowered() {
        return (getDamage() & POWERED_BIT) != 0;
    }
}
