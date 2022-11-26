package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

public class BlockFireBlue extends BlockFire {
    public BlockFireBlue() {
        this(0);
    }

    public BlockFireBlue(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_FIRE;
    }

    @Override
    public String getName() {
        return "Soul Fire";
    }

    @Override
    public int getLightLevel() {
        return 10;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isValidBase(down())) {
                return 0;
            }

            BlockFadeEvent event = new BlockFadeEvent(this, Blocks.air());
            level.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return 0;
            }
            level.setBlock(this, event.getNewState(), true);
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_BLUE_BLOCK_COLOR;
    }

    @Override
    public boolean isValidBase(Block below) {
        int id = below.getId();
        return id == SOUL_SAND || id == SOUL_SOIL;
    }
}
