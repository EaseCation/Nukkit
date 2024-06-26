package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by on 15-10-13.
 */
@ToString
public class LoginPacket extends DataPacket {
    private static final Gson GSON = new Gson();

    public static final int NETWORK_ID = ProtocolInfo.LOGIN_PACKET;

    public String username;
    public int protocol;
    public UUID clientUUID;
    public long clientId;

    public Skin skin;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.protocol = this.getInt();
        this.setBuffer(this.getByteArray(), 0);
        decodeChainData();
        decodeSkinData();
    }

    @Override
    public void encode() {

    }

    public int getProtocol() {
        return protocol;
    }

    private void decodeChainData() {
        Map<String, List<String>> map = GSON.fromJson(new String(this.get(getLInt()), StandardCharsets.UTF_8),
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
            }
        }
    }

    private void decodeSkinData() {
        JsonObject skinToken = decodeToken(new String(this.get(this.getLInt())));
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();
        skin = new Skin().setPlayerSkin(true);
        if (skinToken.has("SkinId")) {
            skin.setSkinId(skinToken.get("SkinId").getAsString());
        }
        if (skinToken.has("SkinData")) {
            skin.setSkinData(Base64.getDecoder().decode(skinToken.get("SkinData").getAsString()));
        }

        if (skinToken.has("CapeData")) {
            this.skin.setCapeData(Base64.getDecoder().decode(skinToken.get("CapeData").getAsString()));
        }

        if (skinToken.has("SkinGeometryName")) {
            skin.setGeometryName(skinToken.get("SkinGeometryName").getAsString());
        }

        if (skinToken.has("SkinGeometry")) {
            skin.setGeometryData(skinToken.get("SkinGeometry").getAsString());
        }
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.", 4);
        if (base.length < 2) return null;

        byte[] decode = null;
    	try {
    		decode = Base64.getUrlDecoder().decode(base[1]);
    	} catch(IllegalArgumentException e) {
    		decode = Base64.getDecoder().decode(base[1]);
    	}
        return GSON.fromJson(new String(decode, StandardCharsets.UTF_8), JsonObject.class);
    }
}
