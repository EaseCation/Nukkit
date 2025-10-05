package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

public class BlockRespawnAnchor extends BlockSolid {
    public static final int MAX_CHARGE = 4;

    public BlockRespawnAnchor() {
        this(0);
    }

    public BlockRespawnAnchor(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RESPAWN_ANCHOR;
    }

    @Override
    public String getName() {
        return "Respawn Anchor";
    }

    @Override
    public float getHardness() {
        return 50;
    }

    @Override
    public float getResistance() {
        return 6000;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_DIAMOND) {
            return new Item[]{
                    Item.get(getItemId()),
            };
        }
        return new Item[0];
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
    public int getLightLevel() {
        return getScaledChargeLevel();
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return getScaledChargeLevel();
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        int charge = getDamage();
        if (charge < MAX_CHARGE && item.getId() == GLOWSTONE) {
            setDamage(charge + 1);
            level.setBlock(this, this, true);
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RESPAWN_ANCHOR_CHARGE);

            item.pop();
            return true;
        }

        if (charge == 0) {
            return false;
        }

        if (level.getDimension() != Dimension.NETHER) {
            if (!this.level.getGameRules().getBoolean(GameRule.RESPAWN_BLOCKS_EXPLODE)) {
                return true;
            }

            int water = 0;
            for (BlockFace side : BlockFace.getValues()) {
                Block block = getSide(side);
                if (block.isWater() || !block.isAir() && block.canContainWater() && level.getExtraBlock(block).isWater()) {
                    water++;
                }
            }

            level.setBlock(this, Blocks.air(), true);

            Explosion explosion = new Explosion(add(0.5, 0.5, 0.5), 5, this, true);
            if (water < 2) {
                explosion.explodeA();
            }
            explosion.explodeB();
            return true;
        }

        //TODO: try set nether respawn point
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    private int getScaledChargeLevel() {
        return Mth.floor(getDamage() / 4f * 15);
    }
}
