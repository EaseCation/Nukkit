package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BlockChorusFlower extends BlockFlowable {

    public BlockChorusFlower() {
        this(0);
    }

    public BlockChorusFlower(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHORUS_FLOWER;
    }

    @Override
    public String getName() {
        return "Chorus Flower";
    }

    @Override
    public float getHardness() {
        return 0.4f;
    }

    @Override
    public float getResistance() {
        return 2;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (player == null || !player.isSurvivalLike()) {
            return new Item[0];
        }
        return new Item[]{toItem()};
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            grow();
            return type;
        }

        return 0;
    }

    private void grow() {
        int age = getDamage();
        if (age >= 5) {
            return;
        }

        if (getFloorY() + 1 >= level.getHeightRange().getMaxY()) {
            return;
        }

        Block above = up();
        if (!above.isAir()) {
            return;
        }

        boolean replaceAndGrow = false;
        boolean pillarOnEndStone = false;

        Block below = down();
        if (below.is(END_STONE) || below.isAir()) {
            replaceAndGrow = true;
        } else if (below.is(CHORUS_PLANT)) {
            int height = 1;
            for (int i = 0; i < 4; i++) {
                Block testBlock = level.getBlock(down(height + 1));
                if (testBlock.is(CHORUS_PLANT)) {
                    height++;
                    continue;
                }
                if (testBlock.is(END_STONE)) {
                    pillarOnEndStone = true;
                }
                break;
            }

            if (height < 2 || ThreadLocalRandom.current().nextInt(pillarOnEndStone ? 5 : 4) >= height) {
                replaceAndGrow = true;
            }
        }

        if (replaceAndGrow && allNeighborsEmpty(above, null) && above.up().isAir()) {
            level.setBlock(this, get(CHORUS_PLANT), true);
            placeGrownFlower(above, age);
        } else if (age < 4) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int numBranchAttempts = random.nextInt(4);
            if (pillarOnEndStone) {
                numBranchAttempts++;
            }
            boolean createdBranch = false;

            Set<BlockFace> failed = EnumSet.noneOf(BlockFace.class);
            for (int i = 0; i < numBranchAttempts; i++) {
                BlockFace direction = Plane.HORIZONTAL.random(random);
                if (failed.contains(direction)) {
                    continue;
                }
                Block target = getSide(direction);

                if (!target.isAir()) {
                    failed.add(direction);
                    continue;
                }
                if (!target.down().isAir()) {
                    failed.add(direction);
                    continue;
                }
                if (!allNeighborsEmpty(target, direction.getOpposite())) {
                    failed.add(direction);
                    continue;
                }

                placeGrownFlower(target, age + 1);
                createdBranch = true;
            }

            if (createdBranch) {
                level.setBlock(this, get(CHORUS_PLANT), true);
            } else {
                placeDeadFlower();
            }
        } else {
            placeDeadFlower();
        }
    }

    private static boolean allNeighborsEmpty(Block block, BlockFace ignore) {
        for (BlockFace face : Plane.HORIZONTAL) {
            if (face == ignore) {
                continue;
            }
            if (!block.getSide(face).isAir()) {
                return false;
            }
        }
        return true;
    }

    private void placeGrownFlower(Vector3 pos, int newAge) {
        level.setBlock(pos, get(CHORUS_FLOWER, newAge), true);

        level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_CHORUSGROW);
    }

    private void placeDeadFlower() {
        setDamage(5);
        level.setBlock(this, this, true);

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CHORUSDEATH);
    }

    private boolean canSurvive() {
        Block below = down();
        if (BlockChorusPlant.canGrowChorus(below)) {
            return true;
        }
        if (!below.isAir()) {
            return false;
        }

        boolean hasNeighbor = false;
        for (BlockFace face : Plane.HORIZONTAL) {
            Block neighbor = getSide(face);
            if (neighbor.is(CHORUS_PLANT)) {
                if (hasNeighbor) {
                    return false;
                }
                hasNeighbor = true;
                continue;
            }
            if (!neighbor.isAir()) {
                return false;
            }
        }
        return hasNeighbor;
    }
}
