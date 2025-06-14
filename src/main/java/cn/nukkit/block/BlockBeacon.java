package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

/**
 * author: Angelic47 Nukkit Project
 */
public class BlockBeacon extends BlockTransparent {

    public BlockBeacon() {
    }

    @Override
    public int getId() {
        return BEACON;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BEACON;
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
    public int getLightLevel() {
        return 15;
    }

    @Override
    public String getName() {
        return "Beacon";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityBeacon beacon;
            if (t instanceof BlockEntityBeacon) {
                beacon = (BlockEntityBeacon) t;
            } else {
                CompoundTag nbt = new CompoundTag("")
                        .putString("id", BlockEntity.BEACON)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                beacon = (BlockEntityBeacon) BlockEntities.createBlockEntity(BlockEntityType.BEACON, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
                if (beacon == null) {
                    return true;
                }
            }

            player.addWindow(new BeaconInventory(player.getUIInventory(), this), Player.BEACON_WINDOW_ID);
        }
        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        boolean blockSuccess = super.place(item, block, target, face, fx, fy, fz, player);

        if (blockSuccess) {
            CompoundTag nbt = new CompoundTag("")
                    .putString("id", BlockEntity.BEACON)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z);
            BlockEntityBeacon beacon = (BlockEntityBeacon) BlockEntities.createBlockEntity(BlockEntityType.BEACON, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            if (beacon == null) {
                return false;
            }
        }

        return blockSuccess;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }
}
