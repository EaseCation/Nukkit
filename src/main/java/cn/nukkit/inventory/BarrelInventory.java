package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBarrel;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntityBarrel;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BarrelInventory extends ContainerInventory {

    public BarrelInventory(BlockEntityBarrel chest) {
        super(chest, InventoryType.BARREL);
    }

    @Override
    public BlockEntityBarrel getHolder() {
        return (BlockEntityBarrel) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() != 1) {
            return;
        }

        BlockEntityBarrel holder = this.getHolder();
        Level level = holder.getLevel();
        if (level == null) {
            return;
        }

        int x = holder.getFloorX();
        int y = holder.getFloorY();
        int z = holder.getFloorZ();

        BlockEventPacket pk = new BlockEventPacket();
        pk.x = x;
        pk.y = y;
        pk.z = z;
        pk.eventType = 1;
        pk.eventData = 1;
        level.addChunkPacket(holder.getChunkX(), holder.getChunkZ(), pk);

        Block block = level.getBlock(x, y, z);
        if (block.getId() == BlockID.BARREL) {
            block.setDamage(block.getDamage() | BlockBarrel.OPEN_BIT);
            level.setBlock(x, y, z, block, true, false);
        }

        level.addLevelSoundEvent(holder, LevelSoundEventPacket.SOUND_BLOCK_BARREL_OPEN);
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            BlockEntityBarrel holder = this.getHolder();
            Level level = holder.getLevel();
            if (level != null) {
                int x = holder.getFloorX();
                int y = holder.getFloorY();
                int z = holder.getFloorZ();

                BlockEventPacket pk = new BlockEventPacket();
                pk.x = x;
                pk.y = y;
                pk.z = z;
                pk.eventType = 1;
                pk.eventData = 0;
                level.addChunkPacket(holder.getChunkX(), holder.getChunkZ(), pk);

                Block block = level.getBlock(x, y, z);
                if (block.getId() == BlockID.BARREL) {
                    block.setDamage(block.getDamage() & BlockBarrel.FACING_DIRECTION_MASK);
                    level.setBlock(x, y, z, block, true, false);
                }

                level.addLevelSoundEvent(holder, LevelSoundEventPacket.SOUND_BLOCK_BARREL_CLOSE);
            }
        }

        super.onClose(who);
    }
}
