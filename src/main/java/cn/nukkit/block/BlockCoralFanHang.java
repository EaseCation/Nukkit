package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoralFanHang extends BlockFlowable implements Faceable {

    public static final int TYPE_BIT = 0b1;
    public static final int DEAD_BIT = 0b10;
    public static final int TYPE_DEAD_BITS = 2;
    public static final int DIRECTION_MASK = 0b1100;

    public static final int BLUE = 0;
    public static final int PINK = 1;

    public BlockCoralFanHang() {
        this(0);
    }

    public BlockCoralFanHang(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL_FAN_HANG;
    }

    @Override
    public String getName() {
        return getCoralType() == BLUE ? "Hang Tube Coral Fan" : "Hang Brain Coral Fan";
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        switch (getCoralType()) {
            default:
            case BLUE:
                return BlockColor.BLUE_BLOCK_COLOR;
            case PINK:
                return BlockColor.PINK_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(isDead() ? CORAL_FAN_DEAD : CORAL_FAN), getCoralType() == BLUE ? BlockCoralFan.BLUE : BlockCoralFan.PINK);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = getBlockFace();
            if (!SupportType.hasFullSupport(getSide(face.getOpposite()), face)) {
                level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            if (isDead()) {
                return 0;
            }

            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getExtraBlock(this).isWaterSource()) {
                return 0;
            }

            setDead(true);
            level.setBlock(this, this, true);
            return type;
        }

        return 0;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        switch (getDirection()) {
            default:
            case 0:
                return BlockFace.WEST;
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.SOUTH;
        }
    }

    public int getCoralType() {
        return getDamage() & TYPE_BIT;
    }

    public void setCoralType(int type) {
        setDamage((getDamage() & ~TYPE_BIT) | (type & TYPE_BIT));
    }

    public boolean isDead() {
        return (getDamage() & DEAD_BIT) == DEAD_BIT;
    }

    public void setDead(boolean dead) {
        setDamage(dead ? getDamage() | DEAD_BIT : getDamage() & ~DEAD_BIT);
    }

    public int getDirection() {
        return (getDamage() & DIRECTION_MASK) >> TYPE_DEAD_BITS;
    }

    public void setDirection(int direction) {
        setDamage((getDamage() & ~DIRECTION_MASK) | ((direction << TYPE_DEAD_BITS) & DIRECTION_MASK));
    }
}
