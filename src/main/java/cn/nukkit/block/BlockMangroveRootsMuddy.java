package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

public class BlockMangroveRootsMuddy extends BlockRotatedPillar {
    public BlockMangroveRootsMuddy() {
        this(0);
    }

    public BlockMangroveRootsMuddy(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MUDDY_MANGROVE_ROOTS;
    }

    @Override
    public String getName() {
        return "Muddy Mangrove Roots";
    }

    @Override
    public float getHardness() {
        return 0.7f;
    }

    @Override
    public float getResistance() {
        return 3.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!V1_19_20.isAvailable()) {
            return level.setBlock(this, this, true);
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }
}
