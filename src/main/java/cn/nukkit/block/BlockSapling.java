package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.level.generator.object.tree.*;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockSapling extends BlockFlowable {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    /**
     * placeholder
     */
    public static final int BIRCH_TALL = 8 | BIRCH;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    public static final int TYPE_MASK = 0b111;
    public static final int AGE_BIT = 0b1000;

    private static final String[] NAMES = new String[]{
            "Oak Sapling",
            "Spruce Sapling",
            "Birch Sapling",
            "Jungle Sapling",
            "Acacia Sapling",
            "Dark Oak Sapling",
            "Sapling",
            "Sapling",
    };

    public BlockSapling() {
        this(0);
    }

    public BlockSapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SAPLING;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & TYPE_MASK];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (canSurvive()) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (!item.is(Item.RAPID_FERTILIZER) && (player == null || !player.isCreative()) && ThreadLocalRandom.current().nextFloat() >= 0.45f) {
                return true;
            }

            this.grow();

            return true;
        }
        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canSurvive()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (ThreadLocalRandom.current().nextInt(1, 8) == 1) {
                int ageBit = getAgeBit();
                if ((this.getDamage() & ageBit) == ageBit) {
                    this.grow();
                } else {
                    this.setDamage(this.getDamage() | ageBit);
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    protected void grow() {
        BasicGenerator generator = null;
        boolean bigTree = false;

        int x = 0;
        int z = 0;

        switch (this.getDamage() & TYPE_MASK) {
            case JUNGLE:
                loop:
                for (x = 0; x >= -1; --x) {
                    for (z = 0; z >= -1; --z) {
                        if (this.findSaplings(x, z, JUNGLE)) {
                            generator = new ObjectJungleBigTree(10, 20, Block.get(JUNGLE_LOG), Block.get(BlockID.LEAVES, BlockLeaves.JUNGLE));
                            bigTree = true;
                            break loop;
                        }
                    }
                }

                if (!bigTree) {
                    x = 0;
                    z = 0;
                    generator = new NewJungleTree(4, 7);
                }
                break;
            case ACACIA:
                generator = new ObjectSavannaTree();
                break;
            case DARK_OAK:
                loop:
                for (x = 0; x >= -1; --x) {
                    for (z = 0; z >= -1; --z) {
                        if (this.findSaplings(x, z, DARK_OAK)) {
                            generator = new ObjectDarkOakTree();
                            bigTree = true;
                            break loop;
                        }
                    }
                }

                if (!bigTree) {
                    return;
                }
                break;
            //TODO: big spruce
            default:
                ObjectTree.growTree(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), NukkitRandom.current(), this.getDamage() & TYPE_MASK);
                return;
        }

        if (bigTree) {
            this.level.setBlock(this.add(x, 0, z), get(AIR), true, false);
            this.level.setBlock(this.add(x + 1, 0, z), get(AIR), true, false);
            this.level.setBlock(this.add(x, 0, z + 1), get(AIR), true, false);
            this.level.setBlock(this.add(x + 1, 0, z + 1), get(AIR), true, false);
        } else {
            this.level.setBlock(this, get(AIR), true, false);
        }

        if (!generator.generate(this.level, NukkitRandom.current(), new BlockVector3(this.getFloorX() + x, this.getFloorY(), this.getFloorZ() + z))) {
            if (bigTree) {
                this.level.setBlock(this.add(x, 0, z), this.clone(), true, false);
                this.level.setBlock(this.add(x + 1, 0, z), this.clone(), true, false);
                this.level.setBlock(this.add(x, 0, z + 1), this.clone(), true, false);
                this.level.setBlock(this.add(x + 1, 0, z + 1), this.clone(), true, false);
            } else {
                this.level.setBlock(this, this, true, false);
            }
        }
    }

    private boolean findSaplings(int x, int z, int type) {
        return this.isSameType(this.add(x, 0, z), type) && this.isSameType(this.add(x + 1, 0, z), type) && this.isSameType(this.add(x, 0, z + 1), type) && this.isSameType(this.add(x + 1, 0, z + 1), type);
    }

    private boolean isSameType(Vector3 pos, int type) {
        Block block = this.level.getBlock(pos);
        return block.getId() == this.getId() && (block.getDamage() & TYPE_MASK) == (type & TYPE_MASK);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(BlockID.SAPLING, this.getDamage() & TYPE_MASK);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 100;
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean isSapling() {
        return true;
    }

    private boolean canSurvive() {
        int id = down().getId();
        return id == Block.GRASS_BLOCK || id == Block.DIRT || id == Block.FARMLAND || id == Block.PODZOL || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }

    protected int getAgeBit() {
        return AGE_BIT;
    }
}
