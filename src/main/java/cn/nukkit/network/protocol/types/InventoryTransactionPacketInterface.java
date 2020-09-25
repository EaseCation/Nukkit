package cn.nukkit.network.protocol.types;

public interface InventoryTransactionPacketInterface {
    void setCraftingPart(boolean craftingPart);
    boolean isCraftingPart();
    void setEnchantingPart(boolean enchantingPart);
    boolean isEnchantingPart();
    void setRepairItemPart(boolean repairItemPart);
    boolean isRepairItemPart();
    boolean hasNetworkIds();
}