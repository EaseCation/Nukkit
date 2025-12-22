package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBlock extends Item {
    public ItemBlock(Block block) {
        this(block, 0, 1);
    }

    public ItemBlock(Block block, Integer meta) {
        this(block, meta, 1);
    }

    public ItemBlock(Block block, Integer meta, int count) {
        super(block.getItemId(), meta == null ? null : block.getDamage(), count, block.getName());
        this.block = block;
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);

        this.block.setDamage(meta);
    }

    @Override
    public void setDamage(Integer meta) {
        super.setDamage(meta);

        this.block.setDamage(meta);
    }

    @Override
    public int getDefaultMeta() {
        return block.getItemDefaultMeta();
    }

    @Override
    public boolean isValidMeta(int meta) {
        return block.isValidMeta(meta);
    }

    @Override
    public boolean isStackedByData() {
        return block.isStackedByData();
    }

    @Override
    public boolean isItemBlock() {
        return block.isBlockItem();
    }

    @Override
    public ItemBlock clone() {
        ItemBlock block = (ItemBlock) super.clone();
        block.block = this.block.clone();
        return block;
    }

    @Override
    public Block getBlock() {
        return this.block.clone();
    }

    @Override
    public int getMaxStackSize() {
        return block.getMaxStackSize();
    }

    @Override
    public int getBlockId() {
        return this.block.getId();
    }

    @Override
    public boolean isFireResistant() {
        return block.isFireResistant();
    }

    @Override
    public boolean isExplodable() {
        return block.isExplodable();
    }

    @Override
    public int getEquippingSound() {
        if (getId() == ItemBlockID.CARVED_PUMPKIN) {
            return LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC;
        }

        return super.getEquippingSound();
    }

    @Override
    public int getFuelTime() {
        return block.getFuelTime();
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return block.getFurnaceXpMultiplier();
    }

    @Override
    public int getCompostableChance() {
        return block.getCompostableChance();
    }

    @Override
    public boolean isChemistryFeature() {
        return block.isChemistryFeature();
    }

    @Override
    public boolean isShulkerBox() {
        return block.isShulkerBox();
    }

    @Override
    public boolean isHangingSign() {
        return block.isHangingSign();
    }

    @Override
    public boolean isWool() {
        return block.isWool();
    }

    @Override
    public boolean isCarpet() {
        return block.isCarpet();
    }
}
