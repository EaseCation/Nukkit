package cn.nukkit.utils;

import java.util.UUID;

/**
 * @author CreeperFace
 */
public interface LoginChainData {

    String getUsername();

    UUID getClientUUID();

    String getIdentityPublicKey();

    String getNetEaseUID();

    String getNetEaseSid();

    String getNetEaseDataVersion();

    String getNetEasePlatform();

    String getNetEaseClientOsName();

    String getNetEaseClientBit();

    String getNetEaseClientEngineVersion();

    String getNetEaseClientPatchVersion();

    String getNetEaseEnv();

    String getNetEaseGameType();

    long getClientId();

    String getServerAddress();

    String getDeviceId();

    String getDeviceModel();

    int getDeviceOS();

    String getGameVersion();

    int getGuiScale();

    String getLanguageCode();

    String getXUID();

    int getCurrentInputMode();

    void setCurrentInputMode(int mode);

    int getDefaultInputMode();

    int getUIProfile();

    String getPlatformOfflineId();

    String getPlatformOnlineId();

    boolean isEditorMode();

    boolean isEditorCapable();

    int isEditorConnectionIntent();

    boolean isSupportClientChunkGeneration();

    int getPlatformType();

    int getMemoryTier();

    int getMaxViewDistance();

    int getGraphicsMode();

    String getPartyId();

    boolean isPartyLeader();

    boolean isFilterProfanity();

    boolean isNetEaseReconnect();

    String getNetEaseSkinIID();

    int getNetEaseGrowthLevel();

    String getSubject();

    String getPlayFabId();

    Integer getPfcd();

    String getTitleId();

    String getSandboxId();

    /**
     * 获取 ViaProxy 认证令牌，用于判断玩家是否通过 ViaProxy 从 Java 版客户端连接
     * @return ViaProxy 认证令牌，如果不是 ViaProxy 客户端则返回 null
     */
    default String getViaProxyAuthToken() {
        return null;
    }
}
