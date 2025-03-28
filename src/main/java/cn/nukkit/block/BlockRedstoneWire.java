package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.EnumSet;
import java.util.Set;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockRedstoneWire extends BlockFlowable {

    private boolean canProvidePower = true;

    public BlockRedstoneWire() {
        this(0);
    }

    public BlockRedstoneWire(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Redstone Wire";
    }

    @Override
    public int getId() {
        return REDSTONE_WIRE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canBePlacedOn(block.down())) {
            return false;
        }

        if (this.level.isRedstoneEnabled()) {
            this.getLevel().setBlock(block, this, true, false);

            this.updateSurroundingRedstone(true);

            for (BlockFace blockFace : Plane.VERTICAL) {
                this.level.updateAroundRedstone(this.getSideVec(blockFace), blockFace.getOpposite());
            }

            for (BlockFace blockFace : Plane.VERTICAL) {
                this.updateAround(this.getSideVec(blockFace), blockFace.getOpposite());
            }

            for (BlockFace blockFace : Plane.HORIZONTAL) {
                Vector3 v = this.getSideVec(blockFace);

                if (this.level.getBlock(v).isNormalBlock()) {
                    this.updateAround(v.up(), BlockFace.DOWN);
                } else {
                    this.updateAround(v.down(), BlockFace.UP);
                }
            }
        } else {
            this.getLevel().setBlock(block, this, true, true);
        }
        return true;
    }

    private void updateAround(Vector3 pos, BlockFace face) {
        if (this.level.getBlock(pos).getId() == Block.REDSTONE_WIRE) {
            this.level.updateAroundRedstone(pos, face);

            for (BlockFace side : BlockFace.getValues()) {
                this.level.updateAroundRedstone(pos.getSideVec(side), side.getOpposite());
            }
        }
    }

    private void updateSurroundingRedstone(boolean force) {
        this.calculateCurrentChanges(force);
    }

    private void calculateCurrentChanges(boolean force) {
        int meta = this.getDamage();
        int maxStrength = meta;
        this.canProvidePower = false;
        int power = this.getIndirectPower();

        this.canProvidePower = true;

        if (power > 0 && power > maxStrength - 1) {
            maxStrength = power;
        }

        int strength = 0;

        for (BlockFace face : Plane.HORIZONTAL) {
            Vector3 v = this.getSideVec(face);

            if (v.getX() == this.getX() && v.getZ() == this.getZ()) {
                continue;
            }

            strength = this.getMaxCurrentStrength(v, strength);

            boolean vNormal = this.level.getBlock(v).isNormalBlock();

            if (vNormal && !this.up().isNormalBlock()) {
                strength = this.getMaxCurrentStrength(v.upVec(), strength);
            } else if (!vNormal) {
                strength = this.getMaxCurrentStrength(v.downVec(), strength);
            }
        }

        if (strength > maxStrength) {
            maxStrength = strength - 1;
        } else if (maxStrength > 0) {
            --maxStrength;
        } else {
            maxStrength = 0;
        }

        if (power > maxStrength - 1) {
            maxStrength = power;
        } else if (power < maxStrength && strength <= maxStrength) {
            maxStrength = Math.max(power, strength - 1);
        }

        if (meta != maxStrength) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, meta, maxStrength));

            this.setDamage(maxStrength);
            this.level.setBlock(this, this, true, false);

            this.level.updateAroundRedstone(this, null);
            for (BlockFace face : BlockFace.getValues()) {
                this.level.updateAroundRedstone(this.getSideVec(face), face.getOpposite());
            }
        } else if (force) {
            for (BlockFace face : BlockFace.getValues()) {
                this.level.updateAroundRedstone(this.getSideVec(face), face.getOpposite());
            }
        }
    }

    private int getMaxCurrentStrength(Vector3 pos, int maxStrength) {
        Block block = level.getBlock(pos);
        if (block.getId() != this.getId()) {
            return maxStrength;
        } else {
            int strength = block.getDamage();
            return Math.max(strength, maxStrength);
        }
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        if (this.level.isRedstoneEnabled()) {
//            this.updateSurroundingRedstone(false);
            this.level.updateAroundRedstone(this, null);
            for (BlockFace blockFace : BlockFace.getValues()) {
                this.level.updateAroundRedstone(this.getSideVec(blockFace), null);
            }

            for (BlockFace blockFace : Plane.HORIZONTAL) {
                Vector3 v = this.getSideVec(blockFace);

                if (this.level.getBlock(v).isNormalBlock()) {
                    this.updateAround(v.up(), BlockFace.DOWN);
                } else {
                    this.updateAround(v.down(), BlockFace.UP);
                }
            }
        }
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.REDSTONE);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE) {
            return 0;
        }

        if (!this.level.isRedstoneEnabled()) {
            return 0;
        }

        // Redstone event
        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL && !this.canBePlacedOn(this.down())) {
            this.getLevel().useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        this.updateSurroundingRedstone(false);

        return Level.BLOCK_UPDATE_NORMAL;
    }

    public boolean canBePlacedOn(Block block) {
        return SupportType.hasFullSupport(block, BlockFace.UP);
    }

    public int getStrongPower(BlockFace side) {
        return !this.canProvidePower ? 0 : getWeakPower(side);
    }

    public int getWeakPower(BlockFace side) {
        if (!this.canProvidePower) {
            return 0;
        } else {
            int power = this.getDamage();

            if (power == 0) {
                return 0;
            } else if (side == BlockFace.UP) {
                return power;
            } else {
                Set<BlockFace> faces = EnumSet.noneOf(BlockFace.class);

                for (BlockFace face : Plane.HORIZONTAL) {
                    if (this.isPowerSourceAt(face)) {
                        faces.add(face);
                    }
                }

                if (side.getAxis().isHorizontal() && faces.isEmpty()) {
                    return power;
                } else if (faces.contains(side) && !faces.contains(side.rotateYCCW()) && !faces.contains(side.rotateY())) {
                    return power;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean isPowerSourceAt(BlockFace side) {
        Vector3 v = this.getSideVec(side);
        Block block = this.level.getBlock(v);
        boolean flag = block.isNormalBlock();
        boolean flag1 = this.up().isNormalBlock();
        return !flag1 && flag && canConnectUpwardsTo(this.level, v.upVec()) || (canConnectTo(block, side) || !flag && canConnectUpwardsTo(this.level, block.downVec()));
    }

    protected static boolean canConnectUpwardsTo(Level level, Vector3 pos) {
        return canConnectUpwardsTo(level.getBlock(pos));
    }

    protected static boolean canConnectUpwardsTo(Block block) {
        return canConnectTo(block, null);
    }

    protected static boolean canConnectTo(Block block, BlockFace side) {
        if (block.getId() == Block.REDSTONE_WIRE) {
            return true;
        } else if (BlockRedstoneDiode.isDiode(block)) {
            BlockFace face = ((BlockRedstoneDiode) block).getFacing();
            return face == side || face.getOpposite() == side;
        } else if (block instanceof BlockPistonBase) {
//            return ((BlockPistonBase) block).getBlockFace() != side.getOpposite();
            return true;
        } else {
            return block.isPowerSource() && side != null;
        }
    }

    @Override
    public boolean isPowerSource() {
        return this.canProvidePower;
    }

    private int getIndirectPower() {
        int power = 0;

        for (BlockFace face : BlockFace.getValues()) {
            int blockPower = this.getIndirectPower(this.getSideVec(face), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    private int getIndirectPower(Vector3 pos, BlockFace face) {
        Block block = this.level.getBlock(pos);
        if (block.getId() == Block.REDSTONE_WIRE) {
            return 0;
        }
        return block.isNormalBlock() ? getStrongPower(pos.getSideVec(face), face) : block.getWeakPower(face);
    }

    private int getStrongPower(Vector3 pos, BlockFace direction) {
        Block block = this.level.getBlock(pos);

        if (block.getId() == Block.REDSTONE_WIRE) {
            return 0;
        }

        return block.getStrongPower(direction);
    }
}
