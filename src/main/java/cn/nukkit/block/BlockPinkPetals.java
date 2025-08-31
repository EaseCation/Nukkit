package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockPinkPetals extends BlockFlowable implements Faceable {
    public static final int GROWTH_MASK = 0b011;
    public static final int DIRECTION_MASK = 0b11000;
    public static final int DIRECTION_START = 3;

    public BlockPinkPetals() {
        this(0);
    }

    public BlockPinkPetals(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PINK_PETALS;
    }

    @Override
    public String getName() {
        return "Pink Petals";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(getItemId(), 0, 1 + getGrowth())
        };
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }
        if (!canSurvive()) {
            return false;
        }

        if (player != null) {
            setDamage(player.getHorizontalFacing().getOpposite().getHorizontalIndex() << DIRECTION_START);
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.is(getItemId())) {
            int growth = getGrowth();
            if (growth >= 3) {
                return false;
            }

            if (player != null && player.isSurvivalLike()) {
                item.pop();
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId());

            setGrowth(growth + 1);
            level.setBlock(this, this, true);
            return true;
        }

        if (isFertilizable() && item.isFertilizer()) {
            if (player != null && player.isSurvivalLike()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            int growth = getGrowth();
            if (growth >= 3) {
                level.dropItem(this, toItem());
                return true;
            }

            setGrowth(growth + 1);
            level.setBlock(this, this, true);
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
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex((getDamage() & DIRECTION_MASK) >> DIRECTION_START);
    }

    public int getGrowth() {
        return getDamage() & GROWTH_MASK;
    }

    public void setGrowth(int growth) {
        setDamage((getDamage() & ~GROWTH_MASK) | (growth & GROWTH_MASK));
    }

    protected boolean canSurvive() {
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == COARSE_DIRT || id == MYCELIUM || id == PODZOL || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }
}
