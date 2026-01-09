package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

public class BlockBush extends BlockFlowable {
    BlockBush() {

    }

    @Override
    public int getId() {
        return BUSH;
    }

    @Override
    public String getName() {
        return "Bush";
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
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canContainSnow() {
        return true;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHEARS;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return new Item[]{toItem(true)};
        }
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }
        if (!canSurvive(down())) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            return onFertilize(item, player);
        }

        if (item.is(ItemBlockID.SNOW_LAYER)) {
            level.setExtraBlock(this, this, true, false);
            level.setBlock(this, get(SNOW_LAYER, BlockSnowLayer.COVERED_BIT), true);

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLACE, getFullId(SNOW_LAYER, BlockSnowLayer.COVERED_BIT));
            return true;
        }

        return false;
    }

    protected boolean onFertilize(Item item, Player player) {
        for (BlockFace side : BlockFace.Plane.HORIZONTAL.shuffle()) {
            Block block = getSide(side);
            if (!canReplace(block) || !canSurvive(block.down())) {
                continue;
            }

            spread(block);

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));
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
            if (canSurvive(down())) {
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
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected boolean canSurvive(Block below) {
        int id = below.getId();
        return id == Block.GRASS_BLOCK || id == Block.DIRT || id == COARSE_DIRT || id == Block.PODZOL || id == FARMLAND || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }

    protected boolean canReplace(Block block) {
        return block.isAir();
    }

    protected void spread(Block block) {
        level.setBlock(block, get(getId()), true);
    }
}
