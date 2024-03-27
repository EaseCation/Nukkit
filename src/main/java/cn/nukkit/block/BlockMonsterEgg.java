package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;

public class BlockMonsterEgg extends BlockSolidMeta {
    public static final int STONE = 0;
    public static final int COBBLESTONE = 1;
    public static final int STONE_BRICK = 2;
    public static final int MOSSY_BRICK = 3;
    public static final int CRACKED_BRICK = 4;
    public static final int CHISELED_BRICK = 5;

    private static final String[] NAMES = new String[]{
            "Infested Stone",
            "Infested Cobblestone",
            "Infested Stone Brick",
            "Infested Mossy Stone Brick",
            "Infested Cracked Stone Brick",
            "Infested Chiseled Stone Brick",
            "Infested Block",
            "Infested Block",
    };

    public BlockMonsterEgg() {
        this(0);
    }

    public BlockMonsterEgg(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MONSTER_EGG;
    }

    @Override
    public float getHardness() {
        return 0.75f;
    }

    @Override
    public float getResistance() {
        return 3.75f;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x7];
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN && item.hasEnchantment(Enchantment.SILK_TOUCH)) {
            switch (getDamage()) {
                case STONE:
                    return new Item[]{Item.get(Block.STONE)};
                case COBBLESTONE:
                    return new Item[]{Item.get(Block.COBBLESTONE)};
                case STONE_BRICK:
                    return new Item[]{Item.get(Block.STONEBRICK, BlockBricksStone.NORMAL)};
                case MOSSY_BRICK:
                    return new Item[]{Item.get(Block.STONEBRICK, BlockBricksStone.MOSSY)};
                case CRACKED_BRICK:
                    return new Item[]{Item.get(Block.STONEBRICK, BlockBricksStone.CRACKED)};
                case CHISELED_BRICK:
                    return new Item[]{Item.get(Block.STONEBRICK, BlockBricksStone.CHISELED)};
            }
        }
        return new Item[0];
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
