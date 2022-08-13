package cn.nukkit.block;

import cn.nukkit.math.BlockFace;

public enum SupportType {
    FULL,
    CENTER,
    ;

    public static boolean hasFullSupport(Block block, BlockFace face) {
        return block.canProvideSupport(face, SupportType.FULL);
    }

    public static boolean hasCenterSupport(Block block, BlockFace face) {
        return block.canProvideSupport(face, SupportType.FULL) || block.canProvideSupport(face, SupportType.CENTER);
    }
}
