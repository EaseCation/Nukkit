package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockDoubleSlabCopperCut extends BlockDoubleSlab implements CopperBehavior {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockDoubleSlabCopperCut() {
        this(0);
    }

    public BlockDoubleSlabCopperCut(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Cut Copper Slab";
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_STONE) {
            return new Item[]{
                    Item.get(getItemId(getSlabBlockId()), getSlabType(), 2),
            };
        }
        return new Item[0];
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.getId() == Item.HONEYCOMB) {
            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_WAX_ON);

            level.setBlock(this, get(getWaxedBlockId(), getDamage()), true);
            return true;
        }

        if (item.isAxe() && getCopperAge() > 0) {
            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_SCRAPE);

            level.setBlock(this, get(getDecrementAgeBlockId(), getDamage()), true);
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (random.nextFloat() >= 1638.4f * 0.000041666666f) {
                return 0;
            }

            int thisAge = getCopperAge();
            int same = 0;
            int older = 0;

            BlockVector3 pos = asBlockVector3();
            for (int x = pos.x - 4; x <= pos.x + 4; x++) {
                for (int z = pos.z - 4; z <= pos.z + 4; z++) {
                    for (int y = pos.y - 4; y <= pos.y + 4; y++) {
                        if (pos.distanceManhattan(x, y, z) > 4) {
                            continue;
                        }

                        Block block = level.getBlock(x, y, z);
                        if (!block.hasCopperBehavior() || block.isWaxed()) {
                            continue;
                        }

                        int age = block.getCopperAge();
                        if (age < thisAge) {
                            return 0;
                        }

                        if (age == thisAge) {
                            same++;
                        } else {
                            older++;
                        }
                    }
                }
            }

            float ratio = (older + 1f) / (older + same + 1f);
            float chance = ratio * ratio;
            if (older == 0) {
                chance *= 0.75f;
            }
            if (random.nextFloat() >= chance) {
                return 0;
            }

            level.setBlock(this, get(getIncrementAgeBlockId(), getDamage()), true);
            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean hasCopperBehavior() {
        return true;
    }

    @Override
    public int getCopperAge() {
        return 0;
    }

    @Override
    public int getSlabType() {
        return 0;
    }

    @Override
    protected int getSlabBlockId() {
        return CUT_COPPER_SLAB;
    }

    public int getWaxedBlockId() {
        return WAXED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getDewaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    public int getIncrementAgeBlockId() {
        return EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    public int getDecrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }
}
