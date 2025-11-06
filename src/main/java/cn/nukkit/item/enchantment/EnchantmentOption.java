package cn.nukkit.item.enchantment;

import lombok.ToString;

@ToString
public class EnchantmentOption implements Cloneable {
    public int cost;
    public int slotFlags;
    public Enchantment[] equipActivatedEnchantments = Enchantment.EMPTY;
    public Enchantment[] heldActivatedEnchantments = Enchantment.EMPTY;
    public Enchantment[] selfActivatedEnchantments = Enchantment.EMPTY;
    public String name = "";
    public int optionId;

    @Override
    public EnchantmentOption clone() {
        try {
            return (EnchantmentOption) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
