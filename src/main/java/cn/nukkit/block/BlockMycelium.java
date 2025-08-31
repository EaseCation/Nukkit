package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 03.01.2016.
 */
public class BlockMycelium extends BlockSolid {

    public BlockMycelium() {
    }

    @Override
    public String getName() {
        return "Mycelium";
    }

    @Override
    public int getId() {
        return MYCELIUM;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }

    @Override
    public float getHardness() {
        return 0.6f;
    }

    @Override
    public float getResistance() {
        return 3;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(DIRT)
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block above = up();
            if (!above.isTransparent() && above.isSolid()) {
                this.getLevel().setBlock(this, get(DIRT), true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            //TODO: light levels
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int x = random.nextInt((int) this.x - 1, (int) this.x + 2);
            int y = random.nextInt((int) this.y - 1, (int) this.y + 2);
            int z = random.nextInt((int) this.z - 1, (int) this.z + 2);
            Block block = this.getLevel().getBlock(x, y, z);
            if (block.getId() == Block.DIRT && block.getDamage() == 0) {
                Block up = block.up();
                if ((up.isTransparent() || !up.isSolid()) && !up.isLiquid() && !level.getExtraBlock(up).isWater()) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(BlockID.MYCELIUM));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block, ev.getNewState(), true);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean isDirt() {
        return true;
    }
}
