package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.awt.Color;

/**
 * author: CreeperFace
 * Nukkit Project
 */
public class BlockEntityCauldron extends BlockEntitySpawnable {

    public static final int POTION_TYPE_NONE = -1;
    public static final int POTION_TYPE_NORMAL = 0;
    public static final int POTION_TYPE_SPLASH = 1;
    public static final int POTION_TYPE_LINGERING = 2;

    public BlockEntityCauldron(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("PotionType")) {
            if (namedTag.contains("SplashPotion")) {
                namedTag.putShort("PotionType", namedTag.getByte("SplashPotion"));
                namedTag.remove("SplashPotion");
            } else {
                namedTag.putShort("PotionType", POTION_TYPE_NORMAL);
            }
        }

        if (!namedTag.contains("PotionId")) {
            namedTag.putShort("PotionId", -1);
            namedTag.putShort("PotionType", -1);
        }

        if (!namedTag.contains("Items")) {
            namedTag.putList(new ListTag<>("Items"));
        }

        super.initBlockEntity();
    }

    public int getPotionId() {
        return namedTag.getShort("PotionId");
    }

    public void setPotionId(int potionId) {
        namedTag.putShort("PotionId", potionId);
        this.spawnToAll();
    }

    public int getPotionType() {
        return namedTag.getShort("PotionType");
    }

    public void setPotionType(int potionType) {
        namedTag.putShort("PotionType", potionType);
    }

    public boolean hasPotion() {
        return getPotionId() != -1 && getPotionType() != POTION_TYPE_NONE;
    }

    public boolean isSplashPotion() {
        return getPotionType() == POTION_TYPE_SPLASH;
    }

    public void setSplashPotion() {
        setPotionType(POTION_TYPE_SPLASH);
    }

    public boolean isLingeringPotion() {
        return getPotionType() == POTION_TYPE_LINGERING;
    }

    public void setLingeringPotion() {
        setPotionType(POTION_TYPE_LINGERING);
    }

    public Color getCustomColor() {
        if (isCustomColor()) {
            int color = namedTag.getInt("CustomColor");

            int red = (color >> 16) & 0xff;
            int green = (color >> 8) & 0xff;
            int blue = (color) & 0xff;

            return new Color(red, green, blue);
        }

        return null;
    }

    public boolean isCustomColor() {
        return namedTag.contains("CustomColor");
    }

    public void setCustomColor(Color color) {
        setCustomColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public void setCustomColor(int r, int g, int b) {
        int color = (r << 16 | g << 8 | b) & 0xffffff;

        namedTag.putInt("CustomColor", color);
        spawnToAll();
    }

    public void clearCustomColor() {
        namedTag.remove("CustomColor");
        spawnToAll();
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BLOCK_CAULDRON;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, CAULDRON)
                .putShort("PotionId", namedTag.getShort("PotionId"))
                .putShort("PotionType", namedTag.getShort("PotionType"))
                .putList(namedTag.getList("Items"));
    }
}
