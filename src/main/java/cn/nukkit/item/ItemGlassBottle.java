package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle() {
        this(0, 1);
    }

    public ItemGlassBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemGlassBottle(Integer meta, int count) {
        super(GLASS_BOTTLE, meta, count, "Glass Bottle");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        if (target.isWater()) {
            Item potion = get(POTION);

            if (!player.isCreative()) {
                pop();
            }

            if (this.count <= 0) {
                player.getInventory().setItemInHand(potion);
            } else {
                if (!player.isCreative()) {
                    player.getInventory().setItemInHand(this);
                }

                for (Item drop : player.getInventory().addItem(potion)) {
                    player.dropItem(drop);
                }
            }
        }
        return false;
    }
}
