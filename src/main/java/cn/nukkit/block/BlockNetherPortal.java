package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.block in project nukkit .
 * The name NetherPortalBlock comes from minecraft wiki.
 */
public class BlockNetherPortal extends BlockTransparent implements Faceable {
    public static final int AXIS_UNKNOWN = 0;
    public static final int AXIS_X = 1;
    public static final int AXIS_Z = 2;

    public BlockNetherPortal() {
        this(0);
    }

    public BlockNetherPortal(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Portal";
    }

    @Override
    public int getId() {
        return PORTAL;
    }

    @Override
    public int getBlockDefaultMeta() {
        return 1;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        return this;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 11;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Items.air();
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        boolean result = super.onBreak(item, player);
        for (BlockFace face : BlockFace.getValues()) {
            Block b = this.getSide(face);
            if (b != null) {
                if (b instanceof BlockNetherPortal) {
                    result &= b.onBreak(item, player);
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    public static void spawnPortal(Position pos)   {
        Level lvl = pos.level;
        int x = pos.getFloorX(), y = pos.getFloorY(), z = pos.getFloorZ();

        for (int xx = -1; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++)  {
                for (int zz = -1; zz < 3; zz++) {
                    lvl.setBlockAt(0, x + xx, y + yy, z + zz, AIR);
                }
            }
        }

        lvl.setBlockAt(0, x + 1, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 2, y, z, OBSIDIAN);

        z += 1;
        lvl.setBlockAt(0, x, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 1, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 2, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 3, y, z, OBSIDIAN);

        z += 1;
        lvl.setBlockAt(0, x + 1, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 2, y, z, OBSIDIAN);

        z -= 1;
        y += 1;
        lvl.setBlockAt(0, x, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 1, y, z, PORTAL);
        lvl.setBlockAt(0, x + 2, y, z, PORTAL);
        lvl.setBlockAt(0, x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockAt(0, x, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 1, y, z, PORTAL);
        lvl.setBlockAt(0, x + 2, y, z, PORTAL);
        lvl.setBlockAt(0, x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockAt(0, x, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 1, y, z, PORTAL);
        lvl.setBlockAt(0, x + 2, y, z, PORTAL);
        lvl.setBlockAt(0, x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockAt(0, x, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 1, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 2, y, z, OBSIDIAN);
        lvl.setBlockAt(0, x + 3, y, z, OBSIDIAN);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    public static Position getSafePortal(Position portal) {
        Level level = portal.getLevel();
        Vector3 down = portal.getSideVec(BlockFace.DOWN);

        while (level.getBlock(down).getId() == PORTAL) {
            down = down.getSideVec(BlockFace.DOWN);
        }

        return Position.fromObject(down.up(), portal.getLevel());
    }

    public static Position findNearestPortal(Position pos) {
        //TODO: pair record
        Level level = pos.getLevel();
        Position found = null;
        HeightRange heightRange = level.getHeightRange();
        int minY = heightRange.getMinY();
        int maxY = heightRange.getMaxY();

        for (int xx = -16; xx <= 16; xx++) {
            for (int zz = -16; zz <= 16; zz++) {
                for (int y = minY; y < maxY; y++) {
                    int x = pos.getFloorX() + xx, z = pos.getFloorZ() + zz;
                    if (level.getBlock(x, y, z).getId() == PORTAL) {
                        found = new Position(x, y, z, level);
                        break;
                    }
                }
            }
        }

        if (found == null) {
            return null;
        }
        Vector3 up = found.up();
        int x = up.getFloorX(), y = up.getFloorY(), z = up.getFloorZ();
        int id = level.getBlock(x, y, z).getId();
        if (id != AIR && id != OBSIDIAN && id != PORTAL) {
            for (int xx = -1; xx < 4; xx++) {
                for (int yy = 1; yy < 4; yy++) {
                    for (int zz = -1; zz < 3; zz++) {
                        level.setBlock(x + xx, y + yy, z + zz, get(AIR));
                    }
                }
            }
        }
        return found;
    }
}
