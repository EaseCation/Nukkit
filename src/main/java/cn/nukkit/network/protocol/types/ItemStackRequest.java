package cn.nukkit.network.protocol.types;

import lombok.ToString;

@ToString
public class ItemStackRequest {
    public int requestId;
    public ItemStackRequestAction[] actions;
    public String[] filterStrings;
    /**
     * TextProcessingEventOrigin
     */
    public int filterStringCause;
}
