package cn.nukkit.network.protocol;

import cn.nukkit.utils.Utils;
import lombok.ToString;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

/**
 * Created by CreeperFace on 5.3.2017.
 */
@ToString
public class ClientboundMapItemDataPacket extends DataPacket {

    public long[] parentMapIds = new long[0];

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
    public static final int CREATION = 8;

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
        if (parentMapIds.length > 0) {
            update |= CREATION;
        }
        if (decorators.length > 0 || trackedEntities.length > 0) {
            update |= DECORATIONS_UPDATE;
        }

        if (image != null || colors.length > 0) {
            update |= TEXTURE_UPDATE;
        }

        this.putUnsignedVarInt(update);
        this.putByte(this.dimensionId);

        if ((update & CREATION) != 0) {
            this.putUnsignedVarInt(parentMapIds.length);
            for (long parentMapId : parentMapIds) {
                this.putEntityUniqueId(parentMapId);
            }
        }
        if ((update & (TEXTURE_UPDATE | DECORATIONS_UPDATE)) != 0) {
            this.putByte(this.scale);
        }

        if ((update & DECORATIONS_UPDATE) != 0) {
            List<MapTrackedObject> objs = Arrays.stream(trackedEntities)
                    .filter(o -> o.type == MapTrackedObject.TYPE_ENTITY)
                    .toList();
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
            } else {
                for (int color : colors) {
                    putUnsignedVarInt(color);
                }
            }
        }
    }

    @ToString
    public static class MapDecorator {
        public static final int TYPE_MARKER_WHITE = 0; // player
        public static final int TYPE_MARKER_GREEN = 1; // item frame
        public static final int TYPE_MARKER_RED = 2;
        public static final int TYPE_MARKER_BLUE = 3;
        public static final int TYPE_X_WHITE = 4;
        public static final int TYPE_TRIANGLE_RED = 5;
        public static final int TYPE_SQUARE_WHITE = 6; // player off map
        public static final int TYPE_MARKER_SIGN = 7;
        public static final int TYPE_MARKER_PINK = 8;
        public static final int TYPE_MARKER_ORANGE = 9;
        public static final int TYPE_MARKER_YELLOW = 10;
        public static final int TYPE_MARKER_TEAL = 11;
        public static final int TYPE_TRIANGLE_GREEN = 12;
        public static final int TYPE_SMALL_SQUARE_WHITE = 13; // player off limits
        public static final int TYPE_MANSION = 14;
        public static final int TYPE_MONUMENT = 15;
        public static final int TYPE_NO_DRAW = 16; // player hidden
        public static final int TYPE_VILLAGE_DESERT = 17;
        public static final int TYPE_VILLAGE_PLAINS = 18;
        public static final int TYPE_VILLAGE_SAVANNA = 19;
        public static final int TYPE_VILLAGE_SNOWY = 20;
        public static final int TYPE_VILLAGE_TAIGA = 21;
        public static final int TYPE_JUNGLE_TEMPLE = 22;
        public static final int TYPE_WITCH_HUT = 23;
        public static final int TYPE_TRIAL_CHAMBERS = 24;

        public byte img = TYPE_MARKER_WHITE;
        public byte rot;
        public byte offsetX;
        public byte offsetZ;
        public String label = "";
        public Color color;
    }

    @ToString
    public static class MapTrackedObject implements Cloneable {
        public static final int TYPE_ENTITY = 0;
        public static final int TYPE_BLOCK = 1;
        public static final int TYPE_OTHER = 2;

        public int type = TYPE_OTHER;
        public long entityUniqueId;
        public int x;
        public int y;
        public int z;

        @Override
        public MapTrackedObject clone() {
            try {
                return (MapTrackedObject) super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

}
