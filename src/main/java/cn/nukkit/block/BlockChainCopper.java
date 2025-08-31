package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class BlockChainCopper extends BlockChain implements CopperBehavior {
    public BlockChainCopper() {
        this(0);
    }

    public BlockChainCopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COPPER_CHAIN;
    }

    @Override
    public String getName() {
        return "Copper Chain";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        return CopperBehavior.use(this, this, item, player);
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
    public boolean hasCopperBehavior() {
        return true;
    }

    @Override
    public int getCopperAge() {
        return 0;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_COPPER_CHAIN;
    }

    @Override
    public int getDewaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return EXPOSED_COPPER_CHAIN;
    }

    @Override
    public int getDecrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }
}
