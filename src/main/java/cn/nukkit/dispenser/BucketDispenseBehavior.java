package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (item.getDamage() != ItemBucket.EMPTY_BUCKET) {
            if (target.isAir() || item.getDamage() != ItemBucket.POWDER_SNOW_BUCKET && (target.canBeFlowedInto() || target.canContainWater())) {
                int replace = ItemBucket.getPlaceBlockFromMeta(item.getDamage());
                if (replace != Block.AIR) {
                    if (replace == Block.FLOWING_WATER && !target.isAir() && target.canContainWater()) {
                        target.level.setExtraBlock(target, Block.get(replace), true);
                    } else {
                        target.level.setBlock(target, Block.get(replace), true);
                    }

                    switch (item.getDamage()) {
                        case ItemBucket.COD_BUCKET:
                            Entity cod = new EntityCod(target.getChunk(), Entity.getDefaultNBT(target.add(0.5, 0, 0.5), null, face.getHorizontalAngle(), 0));
                            cod.spawnToAll();
                            break;
                        case ItemBucket.SALMON_BUCKET:
                            Entity salmon = new EntitySalmon(target.getChunk(), Entity.getDefaultNBT(target.add(0.5, 0, 0.5), null, face.getHorizontalAngle(), 0));
                            salmon.spawnToAll();
                            break;
                        case ItemBucket.TROPICAL_FISH_BUCKET:
                            Entity tropicalFish = new EntityTropicalFish(target.getChunk(), Entity.getDefaultNBT(target.add(0.5, 0, 0.5), null, face.getHorizontalAngle(), 0));
                            tropicalFish.spawnToAll();
                            break;
                        case ItemBucket.PUFFERFISH_BUCKET:
                            Entity pufferfish = new EntityPufferfish(target.getChunk(), Entity.getDefaultNBT(target.add(0.5, 0, 0.5), null, face.getHorizontalAngle(), 0));
                            pufferfish.spawnToAll();
                            break;
                        case ItemBucket.AXOLOTL_BUCKET:
                            Entity axolotl = new EntityAxolotl(target.getChunk(), Entity.getDefaultNBT(target.add(0.5, 0, 0.5), null, face.getHorizontalAngle(), 0));
                            axolotl.spawnToAll();
                            break;
                        case ItemBucket.TADPOLE_BUCKET:
                            Entity tadpole = new EntityTadpole(target.getChunk(), Entity.getDefaultNBT(target.add(0.5, 0, 0.5), null, face.getHorizontalAngle(), 0));
                            tadpole.spawnToAll();
                            break;
                    }

                    return Item.get(ItemID.BUCKET);
                }
            }
        } else if (target.isLiquidSource() || target.is(Block.POWDER_SNOW)) {
            target.level.setBlock(target, Block.get(Block.AIR), true);
            return Item.get(Item.BUCKET, ItemBucket.getMetaFromBlock(target.getId()));
        } else if (!target.isAir() && target.canContainWater()) {
            Block extra = target.level.getExtraBlock(target);
            if (extra.isWaterSource()) {
                target.level.setExtraBlock(target, Block.get(Block.AIR), true);
            }
            return Item.get(Item.BUCKET, ItemBucket.WATER_BUCKET);
        }

        return super.dispense(block, face, item);
    }
}
