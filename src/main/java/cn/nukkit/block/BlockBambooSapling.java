package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

public class BlockBambooSapling extends BlockFlowable {

    public static final int AGE_BIT = 0b1;

    public BlockBambooSapling() {
        this(0);
    }

    public BlockBambooSapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_SAPLING;
    }

    @Override
    public String getName() {
        return "Bamboo Sapling";
    }

    @Override
    public double getHardness() {
        if (V1_20_30.isAvailable()) {
            return 1;
        }
        return 0;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(BAMBOO));
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!BlockBamboo.canBeSupportedBy(down().getId())) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        int id = item.getId();
        if (id == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            if (!grow()) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            level.addParticle(new BoneMealParticle(this));
            return true;
        }

        if (id == getItemId(BAMBOO)) {
            if (!grow(true)) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (BlockBamboo.canBeSupportedBy(down().getId())) {
                return 0;
            }

            level.useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (isReady()) {
                if (/*level.getFullLight(this) < 9 ||*/ !grow()) { //TODO: light
                    setReady(false);
                    level.setBlock(this, this, true);
                }
                return type;
            }

            if (up().canBeReplaced()) {
                setReady(true);
                level.setBlock(this, this, true);
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean grow() {
        return grow(false);
    }

    private boolean grow(boolean playSound) {
        Block up = up();
        if (!up.canBeReplaced()) {
            return false;
        }

        level.setBlock(this, Block.get(BAMBOO), true);
        Block block = Block.get(BAMBOO, BlockBamboo.SMALL_LEAVES << 1);
        level.setBlock(up, block, true);

        if (playSound) {
            block.playPlaceSound(this);
        }
        return true;
    }

    public boolean isReady() {
        return (getDamage() & AGE_BIT) == AGE_BIT;
    }

    public void setReady(boolean ready) {
        setDamage(ready ? AGE_BIT : 0);
    }
}
