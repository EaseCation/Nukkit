package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockRedstoneTorch extends BlockTorch {

    BlockRedstoneTorch() {

    }

    @Override
    public String getName() {
        return "Redstone Torch";
    }

    @Override
    public int getId() {
        return REDSTONE_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (this.level.isRedstoneEnabled()) {
            if (!checkState()) {
                BlockFace facing = getBlockFace().getOpposite();

                for (BlockFace side : BlockFace.getValues()) {
                    if (facing == side) {
                        continue;
                    }

                    this.level.updateAroundRedstone(this.getSideVec(side), null);
                }
            }

            checkState();
        }

        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return getBlockFace() != side ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.DOWN ? this.getWeakPower(side) : 0;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        super.onBreak(item, player);

        BlockFace face = getBlockFace().getOpposite();

        if (this.level.isRedstoneEnabled()) {
            for (BlockFace side : BlockFace.getValues()) {
                if (side == face) {
                    continue;
                }

                this.level.updateAroundRedstone(this.getSideVec(side), null);
            }
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (super.onUpdate(type) == 0) {
            if (!this.level.isRedstoneEnabled()) {
                return 0;
            }

            if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
                this.level.scheduleUpdate(this, tickRate());
            } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
                RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return 0;
                }

                if (checkState()) {
                    return 1;
                }
            }
        }

        return 0;
    }

    protected boolean checkState() {
        if (isPoweredFromSide()) {
            BlockFace face = getBlockFace().getOpposite();

            this.level.setBlock(this, Block.get(BlockID.UNLIT_REDSTONE_TORCH, getDamage()), true, true);

            for (BlockFace side : BlockFace.getValues()) {
                if (side == face) {
                    continue;
                }

                this.level.updateAroundRedstone(this.getSideVec(side), null);
            }

            return true;
        }

        return false;
    }

    protected boolean isPoweredFromSide() {
        BlockFace face = getBlockFace().getOpposite();
        return this.level.isSidePowered(this.getSideVec(face), face);
    }

    @Override
    public int tickRate() {
        return 2;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }
}
