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
}
