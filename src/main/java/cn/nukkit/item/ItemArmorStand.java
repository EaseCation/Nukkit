package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.network.protocol.LevelEventPacket;

public class ItemArmorStand extends Item {

    public ItemArmorStand() {
        this(0);
    }

    public ItemArmorStand(Integer meta) {
        this(meta, 1);
    }

    public ItemArmorStand(Integer meta, int count) {
        super(ARMOR_STAND, meta, count, "Armor Stand");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
//        if (player.isAdventure()) {
//            return false;
//        }

        if (target.canBeReplaced()) {
            block = target;
        }

        FullChunk chunk = level.getChunk(block.getChunkX(), block.getChunkZ());
        if (chunk == null) {
            return false;
        }

        if (!block.canBeReplaced()) {
            return false;
        }
        Block above = block.up();
        if (!above.canBeReplaced()) {
            return false;
        }

        if (level.hasEntity(new SimpleAxisAlignedBB(block, block.add(1, 2, 1)))) {
            return false;
        }

        if (!block.isAir() && !block.isLiquid() && block.getId() != Block.SNOW_LAYER && level.getExtraBlock(block).isAir()) {
            level.setBlock(block, Blocks.air(), true);
        }
        if (!above.isAir() && !above.isLiquid() && above.getId() != Block.SNOW_LAYER && level.getExtraBlock(above).isAir()) {
            level.setBlock(above, Blocks.air(), true);
        }

        EntityArmorStand entity = new EntityArmorStand(chunk, Entity.getDefaultNBT(block.add(0.5, 0, 0.5), null,
                Mth.floor((Mth.wrapDegrees((float) player.yaw - 180) + 22.5f) / 45) * 45, 0));
        entity.spawnToAll();

        if (player.isSurvival()) {
            Item item = player.getInventory().getItemInHand();
            item.pop();
            player.getInventory().setItemInHand(item);
        }

        level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ARMOR_STAND_PLACE);
        return true;
    }
}
