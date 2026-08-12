package cn.nukkit.utils;

import cn.nukkit.network.protocol.LoginPacket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ClientChainData is a container of chain data sent from clients.
 * <p>
 * Device information such as client UUID, xuid and serverAddress, can be
 * read from instances of this object.
 * <p>
 * To get chain data, you can use player.getLoginChainData() or read(loginPacket)
 * <p>
 * ===============
 * author: boybook
 * Nukkit Project
 * ===============
 */
public final class ClientChainData implements LoginChainData {
    private static final Gson GSON = new Gson();

    public static ClientChainData of(byte[] buffer) {
        return new ClientChainData(buffer);
    }

    public static ClientChainData read(LoginPacket pk) {
        return of(pk.getBuffer());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public UUID getClientUUID() {
        return clientUUID;
    }

    @Override
    public String getIdentityPublicKey() {
        return identityPublicKey;
    }

    @Override
    public String getNetEaseUID() {
        return "";
    }

    @Override
    public String getNetEaseSid() {
        return "";
    }

    @Override
    public String getNetEaseDataVersion() {
        return "";
    }

    @Override
    public String getNetEasePlatform() {
        return "";
    }

    @Override
    public String getNetEaseClientOsName() {
        return "";
    }

    @Override
    public String getNetEaseEnv() {
        return "";
    }

    @Override
    public String getNetEaseClientEngineVersion() {
        return "";
    }

    @Override
    public String getNetEaseClientPatchVersion() {
        return "";
    }

    @Override
    public String getNetEaseClientBit() {
        return "";
    }

    @Override
    public String getNetEaseGameType() {
        return "";
    }

    @Override
    public long getClientId() {
        return clientId;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getDeviceModel() {
        return deviceModel;
    }

    @Override
    public int getDeviceOS() {
        return deviceOS;
    }

    @Override
    public String getGameVersion() {
        return gameVersion;
    }

    @Override
    public int getGuiScale() {
        return guiScale;
    }

    @Override
    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public String getXUID() {
        return xuid;
    }

    @Override
    public int getCurrentInputMode() {
        return currentInputMode;
    }

    @Override
    public void setCurrentInputMode(int mode) {
        this.currentInputMode = mode;
    }

    @Override
    public int getDefaultInputMode() {
        return defaultInputMode;
    }

    public final static int UI_PROFILE_CLASSIC = 0;
    public final static int UI_PROFILE_POCKET = 1;

    public final static int INPUT_MOUSE = 1;
    public final static int INPUT_TOUCH = 2;
    public final static int INPUT_GAME_PAD = 3;
    public final static int INPUT_MOTION_CONTROLLER = 4;

    @Override
    public int getUIProfile() {
        return UIProfile;
    }

    @Override
    public String getPlatformOfflineId() {
        return platformOfflineId;
    }

    @Override
    public String getPlatformOnlineId() {
        return platformOnlineId;
    }

    @Override
    public boolean isEditorMode() {
        return editorMode;
    }

    @Override
    public boolean isEditorCapable() {
        return editorCapable;
    }

    @Override
    public int isEditorConnectionIntent() {
        return editorConnectionIntent;
    }

    @Override
    public boolean isSupportClientChunkGeneration() {
        return supportClientChunkGeneration;
    }

    @Override
    public int getPlatformType() {
        return platformType;
    }

    @Override
    public int getMemoryTier() {
        return memoryTier;
    }

    @Override
    public int getMaxViewDistance() {
        return maxViewDistance;
    }

    @Override
    public int getGraphicsMode() {
        return graphicsMode;
    }

    @Override
    public String getPartyId() {
        return partyId;
    }

    @Override
    public boolean isPartyLeader() {
        return partyLeader;
    }

    @Override
    public boolean isFilterProfanity() {
        return filterProfanity;
    }

    @Override
    public boolean isNetEaseReconnect() {
        return false;
    }

    @Override
    public String getNetEaseSkinIID() {
        return "";
    }

    @Override
    public int getNetEaseGrowthLevel() {
        return 0;
    }

    @Override
    public String getSubject() {
        return "";
    }

    @Override
    public String getPlayFabId() {
        return "";
    }

    @Override
    public Integer getPfcd() {
        return null;
    }

    @Override
    public String getTitleId() {
        return "";
    }

    @Override
    public String getSandboxId() {
        return "";
    }

    ///////////////////////////////////////////////////////////////////////////
    // Override
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientChainData && Objects.equals(bs, ((ClientChainData) obj).bs);
    }

    @Override
    public int hashCode() {
        return bs.hashCode();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal
    ///////////////////////////////////////////////////////////////////////////

    private String username;
    private UUID clientUUID;
    private String xuid;
    private String identityPublicKey;

    private long clientId;
    private String serverAddress;
    private String deviceId;
    private String deviceModel;
    private int deviceOS;
    private String gameVersion;
    private int guiScale;
    private String languageCode;
    private int currentInputMode;
    private int defaultInputMode;
    private int UIProfile;
    private String platformOfflineId;
    private String platformOnlineId;
    private boolean editorMode;
    private boolean editorCapable;
    private int editorConnectionIntent;
    private boolean supportClientChunkGeneration;
    private int platformType;
    private int memoryTier;
    private int maxViewDistance;
    private int graphicsMode;
    private String partyId;
    private boolean partyLeader;
    private boolean filterProfanity;

    private String viaProxyAuthToken;
    @Nullable
    private String javaClientEncryptionKey;

    private final transient BinaryStream bs = new BinaryStream();

    private ClientChainData(byte[] buffer) {
        bs.setBuffer(buffer, 0);
        decodeChainData();
        decodeSkinData();
    }

    private void decodeChainData() {
        Map<String, List<String>> map = GSON.fromJson(new String(bs.get(bs.getLInt()), StandardCharsets.UTF_8),
                new TypeToken<Map<String, List<String>>>() {
                }.getType());
        if (map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) return;
        List<String> chains = map.get("chain");
        for (String c : chains) {
            JsonObject chainMap = decodeToken(c);
            if (chainMap == null) continue;
            if (chainMap.has("extraData")) {
                JsonObject extra = chainMap.get("extraData").getAsJsonObject();
                if (extra.has("displayName")) this.username = extra.get("displayName").getAsString();
                if (extra.has("identity")) this.clientUUID = UUID.fromString(extra.get("identity").getAsString());
                if (extra.has("XUID")) this.xuid = extra.get("XUID").getAsString();
            }
            if (chainMap.has("identityPublicKey"))
                this.identityPublicKey = chainMap.get("identityPublicKey").getAsString();
        }
    }

    private void decodeSkinData() {
        JsonObject skinToken = decodeToken(new String(bs.get(bs.getLInt())));
        if (skinToken == null) return;
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();
        if (skinToken.has("DeviceId")) this.deviceId = skinToken.get("DeviceId").getAsString();
        if (skinToken.has("ServerAddress")) this.serverAddress = skinToken.get("ServerAddress").getAsString();
        if (skinToken.has("DeviceModel")) this.deviceModel = skinToken.get("DeviceModel").getAsString();
        if (skinToken.has("DeviceOS")) this.deviceOS = skinToken.get("DeviceOS").getAsInt();
        if (skinToken.has("GameVersion")) this.gameVersion = skinToken.get("GameVersion").getAsString();
        if (skinToken.has("GuiScale")) this.guiScale = skinToken.get("GuiScale").getAsInt();
        if (skinToken.has("LanguageCode")) this.languageCode = skinToken.get("LanguageCode").getAsString();
        if (skinToken.has("CurrentInputMode")) this.currentInputMode = skinToken.get("CurrentInputMode").getAsInt();
        if (skinToken.has("DefaultInputMode")) this.defaultInputMode = skinToken.get("DefaultInputMode").getAsInt();
        if (skinToken.has("UIProfile")) this.UIProfile = skinToken.get("UIProfile").getAsInt();
        if (skinToken.has("PlatformOfflineId")) this.platformOfflineId = skinToken.get("PlatformOfflineId").getAsString();
        if (skinToken.has("PlatformOnlineId")) this.platformOnlineId = skinToken.get("PlatformOnlineId").getAsString();
        if (skinToken.has("IsEditorMode")) this.editorMode = skinToken.get("IsEditorMode").getAsBoolean();
        if (skinToken.has("CompatibleWithClientSideChunkGen")) this.supportClientChunkGeneration = skinToken.get("CompatibleWithClientSideChunkGen").getAsBoolean();
        if (skinToken.has("PlatformType")) this.platformType = skinToken.get("PlatformType").getAsInt();
        if (skinToken.has("MemoryTier")) this.memoryTier = skinToken.get("MemoryTier").getAsInt();
        if (skinToken.has("MaxViewDistance")) this.maxViewDistance = skinToken.get("MaxViewDistance").getAsInt();
        if (skinToken.has("GraphicsMode")) this.graphicsMode = skinToken.get("GraphicsMode").getAsInt();
        if (skinToken.has("PartyId")) this.partyId = skinToken.get("PartyId").getAsString();
        if (skinToken.has("IsPartyLeader")) this.partyLeader = skinToken.get("IsPartyLeader").getAsBoolean();
        if (skinToken.has("FilterProfanity")) this.filterProfanity = skinToken.get("FilterProfanity").getAsBoolean();
        if (skinToken.has("ClientIsEditorCapable")) this.editorCapable = skinToken.get("ClientIsEditorCapable").getAsBoolean();
        if (skinToken.has("ClientEditorConnectionIntent")) this.editorConnectionIntent = skinToken.get("ClientEditorConnectionIntent").getAsInt();
        if (skinToken.has("ViaProxyAuthToken")) this.viaProxyAuthToken = skinToken.get("ViaProxyAuthToken").getAsString();
        if (skinToken.has("JavaClientEncryptionKey")) this.javaClientEncryptionKey = skinToken.get("JavaClientEncryptionKey").getAsString();
    }

    @Override
    public String getViaProxyAuthToken() {
        return viaProxyAuthToken;
    }

    @Nullable
    @Override
    public String getJavaClientEncryptionKey() {
        return javaClientEncryptionKey;
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.", 4);
        if (base.length < 2) return null;

        byte[] decode;
    	try {
        	decode = Base64.getUrlDecoder().decode(base[1]);
        } catch(IllegalArgumentException e) {
        	decode = Base64.getDecoder().decode(base[1]);
        }
        String json = new String(decode, StandardCharsets.UTF_8);
        return GSON.fromJson(json, JsonObject.class);
    }

}
