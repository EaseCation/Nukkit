package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockCampfireSoul extends BlockCampfire {
    public BlockCampfireSoul() {
        this(0);
    }

    public BlockCampfireSoul(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_SOUL_CAMPFIRE;
    }

    @Override
    public String getName() {
        return "Soul Campfire";
    }

    @Override
    public int getLightLevel() {
        return isExtinguished() ? 0 : 10;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.SOUL_CAMPFIRE);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(getItemId(SOUL_SOIL)),
        };
    }

    @Override
    protected int getEntityDamage() {
        return 2;
    }

    @Override
    protected RecipeTag getRecipeTag() {
        return RecipeTag.SOUL_CAMPFIRE;
    }
}
