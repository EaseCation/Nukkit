package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockMagma extends BlockSolid {

    public BlockMagma(){

    }

    @Override
    public int getId() {
        return MAGMA;
    }

    @Override
    public String getName() {
        return "Magma Block";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getLightLevel() {
        return 3;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (p.getInventory().getBoots().hasEnchantment(Enchantment.FROST_WALKER)) {
                return;
            }
            if (p.isSurvivalLike() && !p.isSneaking() && p.level.gameRules.getBoolean(GameRule.FIRE_DAMAGE)) {
                entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.MAGMA, 1));
            }
        } else {
            entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.MAGMA, 1));
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        level.scheduleUpdate(this, 1);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block block = up();

            if (block.isWaterSource()) {
                level.scheduleRandomUpdate(this, 2);
                return type;
            }

            /*int id = block.getId();
            if (id == ICE || id == SNOW_LAYER) {
                level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(160, 160 * 2)); //TODO: melt
                return type;
            }*/

            return 0;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            Block up = up();

            if (up.isWaterSource()) {
                level.setExtraBlock(up, get(FLOWING_WATER), true, false);
                Block place = get(BUBBLE_COLUMN, BlockBubbleColumn.DRAG_DOWN_BIT);
                level.scheduleUpdate(place, up, 1);
                level.setBlock(up, place, true);
                return type;
            }

            /*int id = up.getId();
            if (id == ICE) {
                if (level.getDimension() == Level.DIMENSION_NETHER) {
                    level.setBlock(up, get(AIR), true);
                } else {
                    level.setBlock(up, get(WATER), true);
                }
                return type;
            }
            if (id == SNOW_LAYER) {
                level.setBlock(up, get(AIR), true);
                return type;
            }*/
        }

        return 0;
    }
}
