package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPainting;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPainting extends Item {

    public ItemPainting() {
        this(0, 1);
    }

    public ItemPainting(Integer meta) {
        this(meta, 1);
    }

    public ItemPainting(Integer meta, int count) {
        super(PAINTING, meta, count, "Painting");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
//        if (player.isAdventure()) {
//            return false;
//        }

        FullChunk chunk = level.getChunk(block.getChunkX(), block.getChunkZ());
        if (chunk == null || face.getHorizontalIndex() == -1 || block.isSolid() || level.getBlockEntity(target) != null) {
            return false;
        }

        int direction = face.getHorizontalIndex();
        CompoundTag nbt = Entity.getDefaultNBT(block, null, direction * 90, 0)
                .putByte("Direction", direction)
                .putInt("TileX", (int) block.x)
                .putInt("TileY", (int) block.y)
                .putInt("TileZ", (int) block.z);
        EntityPainting entity = new EntityPainting(chunk, nbt);

        if (entity.getMotive() == null) {
            entity.close();
            return false;
        }
        entity.spawnToAll();

        if (player.isSurvival()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_ADDED);
        return true;
    }
}
