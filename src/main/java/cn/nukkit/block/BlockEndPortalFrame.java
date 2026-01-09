package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockEndPortalFrame extends BlockTransparent implements Faceable {
    public static final int DIRECTION_MASK = 0b11;
    public static final int EYE_BIT = 0b100;

    BlockEndPortalFrame() {

    }

    @Override
    public int getId() {
        return END_PORTAL_FRAME;
    }

    @Override
    public float getResistance() {
        return 18000000;
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "End Portal Frame";
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.8125;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride() {
        return (getDamage() & EYE_BIT) != 0 ? 15 : 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if ((this.getDamage() & EYE_BIT) == 0 && item.getId() == Item.ENDER_EYE) {
            this.setDamage(this.getDamage() | EYE_BIT);
            this.getLevel().setBlock(this, this, true, true);
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_END_PORTAL_FRAME_FILL);

            tryCreatePortal();
            return true;
        }
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & DIRECTION_MASK);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0);
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.DOWN;
    }

    private void tryCreatePortal() {
        BlockFace facing = getBlockFace();
        Vector3 pos = getSideVec(facing, 2);
        BlockFace side = facing.rotateY();
        TRY:
        for (int step = -1; step <= 1; step++) {
            if (step != 0 && !isSameDirectionEyedFrame(getSide(side, step), facing)) {
                continue;
            }
            Vector3 origin = pos.getSide(side, step);
            for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
                Vector3 edgeCenter = origin.getSide(face, 2);
                BlockFace opposite = face.getOpposite();
                BlockFace edge = face.rotateY();
                for (int offset = -1; offset <= 1; offset++) {
                    if (!isSameDirectionEyedFrame(level.getBlock(edgeCenter.getSide(edge, offset)), opposite)) {
                        continue TRY;
                    }
                }
            }

            createPortal(origin);
            return;
        }
    }

    private void createPortal(Vector3 origin) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlock(origin.add(x, 0, z), get(END_PORTAL), true);
            }
        }
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_END_PORTAL_SPAWN);
    }

    private static boolean isSameDirectionEyedFrame(Block block, BlockFace direction) {
        if (block.getId() != END_PORTAL_FRAME) {
            return false;
        }
        if ((block.getDamage() & EYE_BIT) == 0) {
            return false;
        }
        return (block.getDamage() & DIRECTION_MASK) == direction.getHorizontalIndex();
    }
}
