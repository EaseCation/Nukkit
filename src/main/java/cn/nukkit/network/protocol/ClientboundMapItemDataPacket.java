package cn.nukkit.network.protocol;

import cn.nukkit.utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CreeperFace on 5.3.2017.
 */
public class ClientboundMapItemDataPacket extends DataPacket { //TODO: update to 1.2

    public long[] eids = new long[0];

    public long mapId;
    public int update;
    public byte scale;
    public int width;
    public int height;
    public int offsetX;
    public int offsetZ;

    public byte dimensionId;

    public MapDecorator[] decorators = new MapDecorator[0];

    public MapTrackedObject[] trackedEntities = new MapTrackedObject[0];

    public int[] colors = new int[0];
    public BufferedImage image = null;

    //update
    public static final int TEXTURE_UPDATE = 2;
    public static final int DECORATIONS_UPDATE = 4;
    public static final int ENTITIES_UPDATE = 8;

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(mapId);

        int update = 0;
        if (eids.length > 0) {
            update |= 0x08;
        }
        if (decorators.length > 0 || trackedEntities.length > 0) {
            update |= DECORATIONS_UPDATE;
        }

        if (image != null || colors.length > 0) {
            update |= TEXTURE_UPDATE;
        }

        this.putUnsignedVarInt(update);
        this.putByte(this.dimensionId);

        if ((update & 0x08) != 0) { //TODO: find out what these are for
            this.putUnsignedVarInt(eids.length);
            for (long eid : eids) {
                this.putEntityUniqueId(eid);
            }
        }
        if ((update & (TEXTURE_UPDATE | DECORATIONS_UPDATE)) != 0) {
            this.putByte(this.scale);
        }

        if ((update & DECORATIONS_UPDATE) != 0) {
            List<MapTrackedObject> objs = Arrays.stream(trackedEntities)
                    .filter(o -> o.type == MapTrackedObject.TYPE_ENTITY)
                    .collect(Collectors.toList());
            this.putUnsignedVarInt(objs.size());
            for (MapTrackedObject object : objs) {
                this.putEntityUniqueId(object.entityUniqueId);
            }

            this.putUnsignedVarInt(decorators.length);

            for (MapDecorator decorator : decorators) {
                this.putByte(decorator.img);
                this.putByte(decorator.rot);
                this.putByte(decorator.offsetX);
                this.putByte(decorator.offsetZ);
                this.putString(decorator.label == null ? "" : decorator.label);

                byte red = (byte) decorator.color.getRed();
                byte green = (byte) decorator.color.getGreen();
                byte blue = (byte) decorator.color.getBlue();

                this.putUnsignedVarInt(Utils.toRGB(red, green, blue, (byte) 0xff));
                //this.putUnsignedVarInt(decorator.color.getRGB());
            }
        }

        if ((update & TEXTURE_UPDATE) != 0) {
            this.putVarInt(width);
            this.putVarInt(height);
            this.putVarInt(offsetX);
            this.putVarInt(offsetZ);

            this.putUnsignedVarInt(width * height);

            if (image != null) {
                for (int y = 0; y < width; y++) {
                    for (int x = 0; x < height; x++) {
                        Color color = new Color(image.getRGB(x, y), true);
                        byte red = (byte) color.getRed();
                        byte green = (byte) color.getGreen();
                        byte blue = (byte) color.getBlue();

                        putUnsignedVarInt(Utils.toRGB(red, green, blue, (byte) 0xff));
                    }
                }

                image.flush();
            } else if (colors.length > 0) {
                for (int color : colors) {
                    putUnsignedVarInt(color);
                }
            }
        }
    }

    public static class MapDecorator {
        public byte img;
        public byte rot;
        public byte offsetX;
        public byte offsetZ;
        public String label = "";
        public Color color;
    }

    public static class MapTrackedObject {
        public static final int TYPE_ENTITY = 0;
        public static final int TYPE_BLOCK = 1;

        public int type;
        public long entityUniqueId;
        public int x;
        public int y;
        public int z;
    }

}
