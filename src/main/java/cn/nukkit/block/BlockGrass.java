package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.ObjectTallGrass;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockGrass extends BlockDirt {

    public BlockGrass() {
        this(0);
    }

    public BlockGrass(int meta) {
        // Grass can't have meta.
        super(0);
    }

    @Override
    public int getId() {
        return GRASS_BLOCK;
    }

    @Override
    public float getHardness() {
        return 0.6f;
    }

    @Override
    public float getResistance() {
        return 3;
    }

    @Override
    public String getName() {
        return "Grass Block";
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }
            this.level.addParticle(new BoneMealParticle(this));
            ObjectTallGrass.growGrass(this.getLevel(), this.asBlockVector3(), NukkitRandom.current());
            return true;
        } else if (item.isHoe()) {
            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }
            this.getLevel().setBlock(this, Block.get(BlockID.FARMLAND), true);
            return true;
        } else if (item.isShovel()) {
            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH), true);
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block above = up();
            if (above.isLiquid() || !above.isTransparent() && above.isSolid() || level.getExtraBlock(above).isWater()) {
                this.getLevel().setBlock(this, get(DIRT), true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            ThreadLocalRandom random = ThreadLocalRandom.current();
            int x = random.nextInt((int) this.x - 1, (int) this.x + 2);
            int y = random.nextInt((int) this.y - 2, (int) this.y + 3);
            int z = random.nextInt((int) this.z - 1, (int) this.z + 2);
            Block block = this.getLevel().getBlock(x, y, z);
            if (block.getId() == Block.DIRT && block.getDamage() == 0) {
                Block up = block.up();
                if (!up.isLiquid() && (up.isTransparent() || !up.isSolid()) && !level.getExtraBlock(up).isWater()) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(BlockID.GRASS_BLOCK));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block, ev.getNewState(), true);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        //TODO: biome blend
        return BlockColor.GRASS_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getFullId() {
        return this.getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {

    }
}
