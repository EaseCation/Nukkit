package cn.nukkit.entity.data;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.*;
import com.google.common.base.Preconditions;
import lombok.ToString;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = {"geometryData", "animationData"})
public class Skin {
    private static final int PIXEL_SIZE = 4;

    public static final int SINGLE_SKIN_SIZE = 64 * 32 * PIXEL_SIZE;
    public static final int DOUBLE_SKIN_SIZE = 64 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_64_SIZE = 128 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_128_SIZE = 128 * 128 * PIXEL_SIZE;

    public static final byte[] FULL_WHITE_SKIN = new byte[DOUBLE_SKIN_SIZE];

    public static final String GEOMETRY_CUSTOM = convertLegacyGeometryName("geometry.humanoid.custom");
    public static final String GEOMETRY_CUSTOM_SLIM = convertLegacyGeometryName("geometry.humanoid.customSlim");

    private boolean playerSkin = false; //如果是玩家皮肤，那么需要根据中国版规则进行皮肤反作弊检测
    private String skinId;
    private String skinResourcePatch = GEOMETRY_CUSTOM;
    private SerializedImage skinData;
    private final List<SkinAnimation> animations = new ArrayList<>();
    private final List<PersonaPiece> personaPieces = new ArrayList<>();
    private final List<PersonaPieceTint> tintColors = new ArrayList<>();
    private SerializedImage capeData;
    private String geometryData;
    private String animationData;
    private boolean premium;
    private boolean persona;
    private boolean capeOnClassic;
    private String capeId;
    private String skinColor = "#0";
    private String armSize = "wide";
    private boolean trusted = true;

    static {
        Arrays.fill(FULL_WHITE_SKIN, (byte) 0xff);
    }

    public Skin() { }

    public Skin(InputStream inputStream) {
        this.setSkinData(inputStream);
    }

    public Skin(InputStream inputStream, String skinId) {
        this(inputStream);
        this.setSkinId(skinId);
    }

    private static boolean isValidSkin(int length) {
        return length == SINGLE_SKIN_SIZE ||
                length == DOUBLE_SKIN_SIZE ||
                length == SKIN_128_64_SIZE ||
                length == SKIN_128_128_SIZE;
    }

    public boolean isValidLegacy() {
        return isValidSkin(skinData.data.length);
    }

    public boolean isValid() {
        return isValidSkin() && isValidResourcePatch();
    }

    private boolean isValidSkin() {
        return skinData != null && skinData.width >= 64 && skinData.height >= 32 &&
                skinData.data.length >= SINGLE_SKIN_SIZE && skinData.data.length == skinData.width * skinData.height * 4;
    }

    private boolean isValidResourcePatch() {
        if (skinResourcePatch == null) {
            return false;
        }
        try {
            JSONObject object = (JSONObject) JSONValue.parse(skinResourcePatch);
            JSONObject geometry = (JSONObject) object.get("geometry");
            return geometry.containsKey("default") && geometry.get("default") instanceof String;
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    public SerializedImage getSkinData() {
        if (skinData == null) {
            return SerializedImage.EMPTY;
        }
        return skinData;
    }

    public boolean isPlayerSkin() {
        return playerSkin;
    }

    public Skin setPlayerSkin(boolean playerSkin) {
        this.playerSkin = playerSkin;
        return this;
    }

    public String getSkinId() {
        return skinId != null ? skinId : getGeometryName();
    }

    public Skin setSkinId(String skinId) {
        if (skinId == null || skinId.trim().isEmpty()) {
            return this;
        }
        this.skinId = skinId;
        return this;
    }

    public Skin generateSkinId(String name) {
        byte[] data = Binary.appendBytes(getSkinData().data, getSkinResourcePatch().getBytes(StandardCharsets.UTF_8));
        this.skinId = UUID.nameUUIDFromBytes(data) + "." + name;
        return this;
    }

    public Skin setSkinData(InputStream inputStream) {
        BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setSkinData(image);
        return this;
    }

    public Skin setSkinData(byte[] skinData) {
        return setSkinData(SerializedImage.fromLegacy(skinData));
    }

    public Skin setSkinData(BufferedImage image) {
        return setSkinData(parseBufferedImage(image));
    }

    public Skin setSkinData(SerializedImage skinData) {
        Objects.requireNonNull(skinData, "skinData");
        this.skinData = skinData;
        return this;
    }

    public Skin setSkinResourcePatch(String skinResourcePatch) {
        if (skinResourcePatch == null || skinResourcePatch.trim().isEmpty()) {
            skinResourcePatch = GEOMETRY_CUSTOM;
        }
        this.skinResourcePatch = skinResourcePatch;
        return this;
    }

    public String getGeometryName() {
        if (skinResourcePatch == null) {
            return "geometry.humanoid.custom";
        }
        try {
            JSONObject object = (JSONObject) JSONValue.parse(skinResourcePatch);
            JSONObject geometry = (JSONObject) object.get("geometry");
            return geometry.getAsString("default");
        } catch (ClassCastException | NullPointerException e) {
            return "geometry.humanoid.custom";
        }
    }

    public Skin setGeometryName(String geometryName) {
        if (geometryName == null || geometryName.trim().isEmpty()) {
            skinResourcePatch = GEOMETRY_CUSTOM;
            return this;
        }
        this.skinResourcePatch = "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
        return this;
    }

    public String getSkinResourcePatch() {
        if (this.skinResourcePatch == null) {
            return "";
        }
        return skinResourcePatch;
    }

    public SerializedImage getCapeData() {
        if (capeData == null) {
            return SerializedImage.EMPTY;
        }
        return capeData;
    }

    public String getCapeId() {
        if (capeId == null) {
            return "";
        }
        return capeId;
    }

    public Skin setCapeId(String capeId) {
        if (capeId == null || capeId.trim().isEmpty()) {
            capeId = null;
        }
        this.capeId = capeId;
        return this;
    }

    public Skin setCapeData(InputStream inputStream) {
        BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setCapeData(image);
        return this;
    }

    public Skin setCapeData(byte[] capeData) {
        Objects.requireNonNull(capeData, "capeData");
        Preconditions.checkArgument(capeData.length == SINGLE_SKIN_SIZE || capeData.length == 0, "Invalid legacy cape");
        return setCapeData(new SerializedImage(64, 32, capeData));
    }


    public Skin setCapeData(BufferedImage image) {
        return setCapeData(parseBufferedImage(image));
    }

    public Skin setCapeData(SerializedImage capeData) {
        Objects.requireNonNull(capeData, "capeData");
        this.capeData = capeData;
        return this;
    }

    public String getGeometryData() {
        if (geometryData == null) {
            return "";
        }
        return geometryData;
    }

    public Skin setGeometryData(String geometryData) {
        Preconditions.checkNotNull(geometryData, "geometryData");
        if (!geometryData.equals(this.geometryData)) {
            this.geometryData = geometryData;
        }
        return this;
    }

    public String getAnimationData() {
        if (animationData == null) {
            return "";
        }
        return animationData;
    }

    public Skin setAnimationData(String animationData) {
        Preconditions.checkNotNull(animationData, "animationData");
        if (!animationData.equals(this.animationData)) {
            this.animationData = animationData;
        }
        return this;
    }

    public List<SkinAnimation> getAnimations() {
        return animations;
    }

    public List<PersonaPiece> getPersonaPieces() {
        return personaPieces;
    }

    public List<PersonaPieceTint> getTintColors() {
        return tintColors;
    }

    public boolean isPremium() {
        return premium;
    }

    public Skin setPremium(boolean premium) {
        this.premium = premium;
        return this;
    }

    public boolean isPersona() {
        return persona;
    }

    public Skin setPersona(boolean persona) {
        this.persona = persona;
        return this;
    }

    public boolean isCapeOnClassic() {
        return capeOnClassic;
    }

    public Skin setCapeOnClassic(boolean capeOnClassic) {
        this.capeOnClassic = capeOnClassic;
        return this;
    }

    public boolean isTrusted() {
        return trusted;
    }

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public String getArmSize() {
        return armSize;
    }

    public void setArmSize(String armSize) {
        this.armSize = armSize;
    }

    public String getFullSkinId() {
        String skinId = getSkinId();
        String full = UUID.nameUUIDFromBytes(Binary.appendBytes(skinId.getBytes(), getSkinData().data)).toString();
        return full; //TODO: Client sends full skin ID as normal skin ID. Find out what this is actually for.
    }

    private static SerializedImage parseBufferedImage(BufferedImage image) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                outputStream.write(color.getRed());
                outputStream.write(color.getGreen());
                outputStream.write(color.getBlue());
                outputStream.write(color.getAlpha());
            }
        }
        image.flush();
        return new SerializedImage(image.getWidth(), image.getHeight(), outputStream.toByteArray());
    }

    private static String convertLegacyGeometryName(String geometryName) {
        return "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }
}