package cn.nukkit.network.protocol.types;

import lombok.ToString;

@ToString
public class CommandOutputMessage {
    public final boolean successful;
    public final String messageId;
    public final String[] parameters;

    public CommandOutputMessage(boolean successful, String messageId, String... parameters) {
        this.successful = successful;
        this.messageId = messageId;
        this.parameters = parameters;
    }
}
