package cn.nukkit.network.protocol;

import cn.nukkit.utils.JsonUtil;
import lombok.ToString;
import tools.jackson.core.JacksonException;

import javax.annotation.Nullable;

import static cn.nukkit.SharedConstants.*;

@ToString
public class ModalFormResponsePacket extends DataPacket {

    public int formId;
    @Nullable
    public Object data;

    @Override
    public int pid() {
        return ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;
    }

    @Override
    public void decode() {
        this.formId = this.getVarInt();
        this.data = this.getJson(); //Data will be null if player close form without submit (by cross button or ESC)
    }

    @Override
    public void encode() {

    }

    @Nullable
    private Object getJson() {
        int length =  (int) this.getUnsignedVarInt();
        if (length < 0 || length > MAX_MODAL_FORM_RESPONSE_DATA_LENGTH) {
            throw new IllegalArgumentException("Form response exceeds maximum length");
        }
        if (!this.isReadable(length)) {
            throw new IllegalArgumentException("Form response length exceeds packet data");
        }

        byte[] jsonData = this.get(length);
        try {
            return JsonUtil.UNTRUSTED_JSON_MAPPER.readValue(jsonData, Object.class);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Invalid form response JSON", e);
        }
    }
}
