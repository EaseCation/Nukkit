package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUndyedShulkerBox;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

public class ShulkerBoxDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        Block shulkerBox = item.getBlock();

        if (shulkerBox instanceof BlockUndyedShulkerBox) {
            shulkerBox = shulkerBox.clone();
        } else {
            return super.dispense(block, face, item);
        }

        boolean success = target.getId() == BlockID.AIR;

        if (success) {
            block.level.setBlock(target, shulkerBox, true);

            BlockFace shulkerBoxFace = target.down().getId() == BlockID.AIR ? face : BlockFace.UP;

            CompoundTag nbt = BlockEntity.getDefaultCompound(target, BlockEntity.SHULKER_BOX);
            nbt.putByte("facing", shulkerBoxFace.getIndex());

            if (item.hasCustomName()) {
                nbt.putString("CustomName", item.getCustomName());
            }

            CompoundTag tag = item.getNamedTag();

            if (tag != null) {
                if (tag.contains("Items")) {
                    nbt.putList(tag.getList("Items"));
                }
            }

            new BlockEntityShulkerBox(block.level.getChunk(target.getChunkX(), target.getChunkZ()), nbt);
            block.level.updateComparatorOutputLevel(target);
        } else {
            item.count++;
        }

        return null;
    }
}
