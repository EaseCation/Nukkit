package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class GUIDataPickItemPacket extends DataPacket {

    public String itemName;
    public String itemEffectName = "";
    public int hotbarSlot;

    @Override
    public int pid() {
        return ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET;
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.itemName);
        this.putString(this.itemEffectName);
        this.putLInt(this.hotbarSlot);
    }

    @Override
    public void decode() {
    }
}
