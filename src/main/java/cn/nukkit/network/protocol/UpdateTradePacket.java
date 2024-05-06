package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.ContainerType;
import lombok.ToString;

@ToString(exclude = "offers")
public class UpdateTradePacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.UPDATE_TRADE_PACKET;

    public int windowId;
    public int windowType = ContainerType.TRADING;
    public int windowSize; // hardcoded to 0
    public int tradeTier;
    @Deprecated
    public boolean recipeAddedOnUpdate;
    public long trader;
    public long player;
    public String displayName = "";
    public boolean newTradingUi;
    public boolean usingEconomyTrade;
    public byte[] offers;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) windowId);
        this.putByte((byte) windowType);
        this.putVarInt(windowSize);
        this.putVarInt(usingEconomyTrade ? 40 : 0); // Merchant Timer
        this.putBoolean(recipeAddedOnUpdate);
        this.putEntityUniqueId(trader);
        this.putEntityUniqueId(player);
        this.putString(displayName);
        this.put(this.offers);
    }

}
