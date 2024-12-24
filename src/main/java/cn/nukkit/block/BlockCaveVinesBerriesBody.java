package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockCaveVinesBerriesBody extends BlockCaveVines {
    public BlockCaveVinesBerriesBody() {
        this(0);
    }

    public BlockCaveVinesBerriesBody(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CAVE_VINES_BODY_WITH_BERRIES;
    }

    @Override
    public String getName() {
        return "Cave Vines Body with Berries";
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                toItem(true),
        };
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
            level.dropItem(this, toItem(true));
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CAVE_VINES_PICK_BERRIES);

        level.setBlock(this, get(CAVE_VINES, getDamage()), true);
        return true;
    }
}
