package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockCactus extends BlockTransparent {

    public BlockCactus(int meta) {
        super(meta);
    }

    public BlockCactus() {
        this(0);
    }

    @Override
    public int getId() {
        return CACTUS;
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
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity.getAge() % 10 == 4) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.CONTACT, 1));
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (!canBeSupportedBy(down.getId())) {
                this.getLevel().useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            } else {
                for (int side = 2; side <= 5; ++side) {
                    Block block = getSide(BlockFace.fromIndex(side));
                    if (!block.canBeFlowedInto()) {
                        this.getLevel().useBreakOn(this, true);
                        return Level.BLOCK_UPDATE_NORMAL;
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down().getId() != CACTUS) {
                if (this.getDamage() == 0x0F) {
                    int blockX = getFloorX();
                    int blockY = getFloorY();
                    int blockZ = getFloorZ();
                    for (int y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(blockX, blockY + y, blockZ);
                        if (b.getId() == AIR) {
                            BlockGrowEvent event = new BlockGrowEvent(b, Block.get(BlockID.CACTUS));
                            Server.getInstance().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                this.getLevel().setBlock(b, event.getNewState(), true);
                            }
                            break;
                        } else if (b.getId() != CACTUS) {
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
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        Block down = this.down();
        if (canBeSupportedBy(down.getId())) {
            Block block0 = north();
            Block block1 = south();
            Block block2 = west();
            Block block3 = east();
            if (block0.canBeFlowedInto() && block1.canBeFlowedInto() && block2.canBeFlowedInto() && block3.canBeFlowedInto()) {
                this.getLevel().setBlock(this, this, true);

                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "Cactus";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
            Item.get(Item.CACTUS, 0, 1)
        };
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public int getCompostableChance() {
        return 50;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private static boolean canBeSupportedBy(int id) {
        return id == CACTUS || id == SAND || id == SUSPICIOUS_SAND;
    }
}
