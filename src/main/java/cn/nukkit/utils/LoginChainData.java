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

    String getNetEasePlatform();

    String getNetEaseClientOsName();

    String getNetEaseClientBit();

    String getNetEaseClientEngineVersion();

    String getNetEaseClientPatchVersion();

    String getNetEaseEnv();

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

    String getCapeData();

    int getUIProfile();

    /**
     * 获取 ViaProxy 认证令牌，用于判断玩家是否通过 ViaProxy 从 Java 版客户端连接
     * @return ViaProxy 认证令牌，如果不是 ViaProxy 客户端则返回 null
     */
    default String getViaProxyAuthToken() {
        return null;
    }
}
