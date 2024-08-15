package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 09.01.2016.
 */
public class BlockSugarcane extends BlockFlowable {

    public BlockSugarcane() {
        this(0);
    }

    public BlockSugarcane(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Sugar Cane";
    }

    @Override
    public int getId() {
        return BLOCK_REEDS;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.SUGAR_CANE);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            int count = 1;

            for (int i = 1; i <= 2; i++) {
                int id = this.level.getBlock(this.getFloorX(), this.getFloorY() - i, this.getFloorZ()).getId();

                if (id == BLOCK_REEDS) {
                    count++;
                }
            }

            if (count < 3) {
                boolean success = false;
                int toGrow = 3 - count;

                for (int i = 1; i <= toGrow; i++) {
                    Block block = this.up(i);
                    if (block.getId() == AIR) {
                        BlockGrowEvent ev = new BlockGrowEvent(block, Block.get(BlockID.BLOCK_REEDS));
                        Server.getInstance().getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState(), true);
                            success = true;
                        }
                    } else if (block.getId() != BLOCK_REEDS) {
                        break;
                    }
                }

                if (success) {
                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }

                    this.level.addParticle(new BoneMealParticle(this));
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.down();
            int id = down.getId();
            if (id != BLOCK_REEDS && !canSurvive(id)) {
                this.getLevel().useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block down = this.down();
            if (down.getId() != BLOCK_REEDS) {
                if (!hasWaterAround(down)) {
                    level.useBreakOn(this, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }

                if (this.getDamage() == 0x0F) {
                    int blockX = getFloorX();
                    int blockY = getFloorY();
                    int blockZ = getFloorZ();
                    for (int y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(blockX, blockY + y, blockZ);
                        if (b.getId() == AIR) {
                            BlockGrowEvent ev = new BlockGrowEvent(b, Block.get(BlockID.BLOCK_REEDS));
                            Server.getInstance().getPluginManager().callEvent(ev);

                            if (!ev.isCancelled()) {
                                this.getLevel().setBlock(b, ev.getNewState(), true);
                            }
                            break;
                        } else if (b.getId() != BLOCK_REEDS) {
                            break;
                        }
                    }
                    this.setDamage(0);
                } else {
                    this.setDamage(this.getDamage() + 1);
                }
                this.getLevel().setBlock(this, this, true);
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        Block down = this.down();
        int id = down.getId();
        if (id == BLOCK_REEDS) {
            this.getLevel().setBlock(block, Block.get(BlockID.BLOCK_REEDS), true);
            return true;
        }

        if (!canSurvive(id)) {
            return false;
        }

        if (!hasWaterAround(down)) {
            return false;
        }

        level.setBlock(block, get(BLOCK_REEDS), true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean hasWaterAround(Block below) {
        for (BlockFace side : Plane.HORIZONTAL) {
            Block block = below.getSide(side);
            if (block.isWaterSource() || block.is(FROSTED_ICE) || !block.isAir() && block.canContainWater() && level.getExtraBlock(block).isWaterSource()) {
                return true;
            }
        }
        return false;
    }

    private boolean canSurvive(int id) {
        return id == GRASS_BLOCK || id == DIRT || id == SAND || id == PODZOL || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }
}
