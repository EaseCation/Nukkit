package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by CreeperFace on 18.3.2017.
 */
public class ItemMap extends Item {
    public static final int NORMAL_MAP = 0;
    public static final int BASIC_MAP = 1;
    public static final int ENHANCED_MAP = 2;
    public static final int OCEAN_EXPLORER_MAP = 3;
    public static final int WOODLAND_EXPLORER_MAP = 4;
    public static final int TREASURE_EXPLORER_MAP = 5;
    public static final int LOCKED_MAP = 6;
    public static final int SNOWY_VILLAGE_MAP = 7;
    public static final int TAIGA_VILLAGE_MAP = 8;
    public static final int PLAINS_VILLAGE_MAP = 9;
    public static final int SAVANNA_VILLAGE_MAP = 10;
    public static final int DESERT_VILLAGE_MAP = 11;
    public static final int JUNGLE_EXPLORER_MAP = 12;
    public static final int SWAMP_EXPLORER_MAP = 13;
    public static final int TRIAL_EXPLORER_MAP = 14;
    public static final int UNDEFINED_MAP = 15;

    private static final Object2IntMap<String> DESTINATIONS = Utils.make(new Object2IntOpenHashMap<>(), lookup -> {
        lookup.put("monument", OCEAN_EXPLORER_MAP);
        lookup.put("mansion", WOODLAND_EXPLORER_MAP);
        lookup.put("buriedtreasure", TREASURE_EXPLORER_MAP);
        lookup.put("village_snowy", SNOWY_VILLAGE_MAP);
        lookup.put("village_taiga", TAIGA_VILLAGE_MAP);
        lookup.put("village_plains", PLAINS_VILLAGE_MAP);
        lookup.put("village_savanna", SAVANNA_VILLAGE_MAP);
        lookup.put("village_desert", DESERT_VILLAGE_MAP);
        lookup.put("jungle_temple", JUNGLE_EXPLORER_MAP);
        lookup.put("swamp_hut", SWAMP_EXPLORER_MAP);
        lookup.put("trial_chambers", TRIAL_EXPLORER_MAP);
    });

    public static int mapCount = 0;

    // not very pretty but definitely better than before.
    private BufferedImage image;

    public ItemMap() {
        this(0, 1);
    }

    public ItemMap(Integer meta) {
        this(meta, 1);
    }

    public ItemMap(Integer meta, int count) {
        super(FILLED_MAP, meta, count, "Map");

        if (!hasCompoundTag() || !getNamedTag().contains("map_uuid")) {
            CompoundTag tag = new CompoundTag();
            tag.putLong("map_uuid", mapCount++);
            tag.putBoolean("map_display_players", true);
            this.setNamedTag(tag);
        }
    }

    public void setImage(File file) throws IOException {
        setImage(ImageIO.read(file));
    }

    public void setImage(BufferedImage img) {
        try {
            if (img.getHeight() != 128 || img.getWidth() != 128) { //resize
                this.image = new BufferedImage(128, 128, img.getType());
                Graphics2D g = this.image.createGraphics();
                g.drawImage(img, 0, 0, 128, 128, null);
                g.dispose();
            } else {
                this.image = img;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(this.image, "png", baos);

            this.getNamedTag().putByteArray("Colors", baos.toByteArray());
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }
    }

    protected BufferedImage loadImageFromNBT() {
        try {
            byte[] data = getNamedTag().getByteArray("Colors");
            this.image = ImageIO.read(new ByteArrayInputStream(data));
            return image;
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }

        return null;
    }

    public long getMapId() {
        return getNamedTag().getLong("map_uuid");
    }

    public void sendImage(Player p) {
        // don't load the image from NBT if it has been done before.
        BufferedImage image = this.image != null ? this.image : loadImageFromNBT();

        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = getMapId();
        pk.parentMapIds = new long[]{getMapId()};
        pk.update = ClientboundMapItemDataPacket.TEXTURE_UPDATE;
        pk.scale = 0;
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;

        p.dataPacket(pk);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canDualWield() {
        return true;
    }

    public static int getDataByDestination(String destination) {
        return DESTINATIONS.getInt(destination);
    }
}
