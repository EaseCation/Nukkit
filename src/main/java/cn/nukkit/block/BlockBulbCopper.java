package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

public class BlockBulbCopper extends BlockSolid implements CopperBehavior {
    public static final int LIT_BIT = 0b1;
    public static final int POWERED_BIT = 0b10;

    public BlockBulbCopper() {
        this(0);
    }

    public BlockBulbCopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COPPER_BULB;
    }

    @Override
    public String getName() {
        return "Copper Bulb";
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 30;
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
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_STONE) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        return CopperBehavior.use(this, this, item, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!level.isRedstoneEnabled()) {
                return 0;
            }

            RedstoneUpdateEvent event = new RedstoneUpdateEvent(this);
            event.call();
            if (event.isCancelled()) {
                return 0;
            }

            level.scheduleUpdate(this, 1);
            return Level.BLOCK_UPDATE_REDSTONE;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int oldMeta = getDamage();

            if (level.isBlockPowered(this)) {
                setPowered(true);

                if (getDamage() != oldMeta) {
                    boolean lit = !isLit();
                    setLit(lit);

                    level.addLevelSoundEvent(blockCenter(), lit ? LevelSoundEventPacket.SOUND_COPPER_BULB_ON : LevelSoundEventPacket.SOUND_COPPER_BULB_OFF, getFullId());
                }
            } else {
                setPowered(false);
            }

            if (getDamage() != oldMeta) {
                level.setBlock(this, this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            CopperBehavior.randomTick(this, this);
            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public int getLightLevel() {
        return isLit() ? 15 : 0;
    }

    @Override
    public boolean hasCopperBehavior() {
        return true;
    }

    @Override
    public int getCopperAge() {
        return 0;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_COPPER_BULB;
    }

    @Override
    public int getDewaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return EXPOSED_COPPER_BULB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    public boolean isLit() {
        return (getDamage() & LIT_BIT) != 0;
    }

    public void setLit(boolean lit) {
        setDamage(lit ? getDamage() | LIT_BIT : getDamage() & ~LIT_BIT);
    }

    public boolean isPowered() {
        return (getDamage() & POWERED_BIT) != 0;
    }

    public void setPowered(boolean powered) {
        setDamage(powered ? getDamage() | POWERED_BIT : getDamage() & ~POWERED_BIT);
    }
}
