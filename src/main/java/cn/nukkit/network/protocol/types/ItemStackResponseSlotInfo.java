package cn.nukkit.network.protocol.types;

import lombok.ToString;

@ToString
public class ItemStackResponseSlotInfo {
    public int slot;
    public int hotbarSlot;
    public int count;
    public int itemStackId;
    /**
     * @since 1.16.200
     */
    public String customName = "";
    /**
     * @since 1.21.50
     */
    public String filteredCustomName = "";
    /**
     * @since 1.16.210
     */
    public int durabilityCorrection;
}
