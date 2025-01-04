package cn.nukkit.network.protocol.types;

import lombok.ToString;

@ToString
public class ItemStackResponse {
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = 1;

    private static final ItemStackResponseContainerInfo[] EMPTY_CONTAINERS = new ItemStackResponseContainerInfo[0];

    public int result = RESULT_OK;
    public int requestId;
    public ItemStackResponseContainerInfo[] containerInfos = EMPTY_CONTAINERS;
}
