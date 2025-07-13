package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockChestCopper extends BlockChest implements CopperBehavior {
    public BlockChestCopper() {
        this(0);
    }

    public BlockChestCopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COPPER_CHEST;
    }

    @Override
    public String getName() {
        return "Copper Chest";
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
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null && !item.isNull() && player.isSneaking()) {
            return CopperBehavior.use(this, this, item, player);
        }
        return super.onActivate(item, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            CopperBehavior.randomTick(this, this);
            return type;
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
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
    public int getWaxedBlockId() {
        return WAXED_COPPER_CHEST;
    }

    @Override
    public int getDewaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return EXPOSED_COPPER_CHEST;
    }

    @Override
    public int getDecrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }
}
