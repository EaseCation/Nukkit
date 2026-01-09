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
    public int getTypeId() {
        return BlockEntityType.CAULDRON;
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
        int color = 0xff << 24 | r << 16 | g << 8 | b;

        namedTag.putInt("CustomColor", color);
    }

    public boolean clearCustomColor() {
        return namedTag.remove("CustomColor") != null;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CAULDRON || blockId == Block.LAVA_CAULDRON;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag nbt = getDefaultCompound(this, CAULDRON)
                .putShort("PotionId", namedTag.getShort("PotionId"))
                .putShort("PotionType", namedTag.getShort("PotionType"))
                .putList(namedTag.getList("Items"));

        if (namedTag.contains("CustomColor")) {
            nbt.putInt("CustomColor", namedTag.getInt("CustomColor"));
        }

        return nbt;
    }

    public void resetCauldron() {
        namedTag.putShort("PotionType", POTION_TYPE_NONE);
        namedTag.putShort("PotionId", -1);
        namedTag.remove("CustomColor");
    }

    public boolean mixColor(Color color) {
        int rgb = namedTag.getInt("CustomColor");

        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        r += color.getRed();
        g += color.getGreen();
        b += color.getBlue();

        if ((rgb & 0xff000000) != 0) {
            r >>= 1;
            g >>= 1;
            b >>= 1;
        }

        int newColor = 0xff << 24 | r << 16 | g << 8 | b;
        if (newColor == rgb) {
            return false;
        }

        namedTag.putInt("CustomColor", newColor);
        return true;
    }
}
