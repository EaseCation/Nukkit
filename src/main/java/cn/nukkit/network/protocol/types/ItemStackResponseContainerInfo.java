package cn.nukkit.network.protocol.types;

import lombok.ToString;

import javax.annotation.Nullable;

@ToString
public class ItemStackResponseContainerInfo {
    private static final ItemStackResponseSlotInfo[] EMPTY_SLOTS = new ItemStackResponseSlotInfo[0];

    public int containerId;
    /**
     * @since 1.21.20
     */
    @Nullable
    public Integer dynamicContainerId;
    public ItemStackResponseSlotInfo[] slots = EMPTY_SLOTS;
}
