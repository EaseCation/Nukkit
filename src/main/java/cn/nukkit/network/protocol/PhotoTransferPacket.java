package cn.nukkit.network.protocol;

public class PhotoTransferPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.PHOTO_TRANSFER_PACKET;

    public String photoName;
    public String photoData;
    public String bookId;

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
        this.putString(photoName);
        this.putString(photoData);
        this.putString(this.bookId);
    }

}