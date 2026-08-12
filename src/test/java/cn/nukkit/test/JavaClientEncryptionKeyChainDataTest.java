package cn.nukkit.test;

import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ClientChainData;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JavaClientEncryptionKeyChainDataTest {

    private static final String ENCODED_KEY = "AAECAwQFBgcICQoLDA0ODw==";

    @Test
    void readsJavaClientEncryptionKeyFromSkinJwt() {
        ClientChainData chainData = ClientChainData.of(createLoginData(
                "{\"JavaClientEncryptionKey\":\"" + ENCODED_KEY + "\"}"
        ));

        assertEquals(ENCODED_KEY, chainData.getJavaClientEncryptionKey());
    }

    @Test
    void returnsNullWhenSkinJwtOmitsJavaClientEncryptionKey() {
        ClientChainData chainData = ClientChainData.of(createLoginData("{}"));

        assertNull(chainData.getJavaClientEncryptionKey());
    }

    private static byte[] createLoginData(String skinPayload) {
        byte[] chainData = "{\"chain\":[\"e30.e30.x\"]}".getBytes(StandardCharsets.UTF_8);
        byte[] skinJwt = createJwt(skinPayload).getBytes(StandardCharsets.UTF_8);
        BinaryStream stream = new BinaryStream();
        stream.putLInt(chainData.length);
        stream.put(chainData);
        stream.putLInt(skinJwt.length);
        stream.put(skinJwt);
        return stream.getBuffer();
    }

    private static String createJwt(String payload) {
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return "e30." + encodedPayload + ".x";
    }
}
