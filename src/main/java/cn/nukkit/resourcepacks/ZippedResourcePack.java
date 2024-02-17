package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import cn.nukkit.math.Mth;
import cn.nukkit.resourcepacks.PackManifest.Module;
import cn.nukkit.utils.Hash;
import cn.nukkit.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Files;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static cn.nukkit.SharedConstants.*;

@Log4j2
public class ZippedResourcePack extends AbstractResourcePack {
    private final int size;
    private final byte[][] chunks;
    private final byte[] sha256;

    private final String encryptionKey;

    public ZippedResourcePack(File file) throws IOException {
        this(file, true);
    }

    public ZippedResourcePack(File file, boolean encrypt) throws IOException {
        if (!file.exists()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translate("nukkit.resources.zip.not-found", file.getName()));
        }

        byte[] bytes = Files.toByteArray(file);
        try (ZipFile zip = new ZipFile(file)) {
            ZipEntry manifestEntry = Optional.ofNullable(zip.getEntry("manifest.json"))
                    .orElse(zip.getEntry("pack_manifest.json"));

            if (manifestEntry == null) {
                throw new IllegalArgumentException(Server.getInstance().getLanguage()
                        .translate("nukkit.resources.zip.no-manifest"));
            }

            manifest = PackManifest.load(zip.getInputStream(manifestEntry));

            if (!manifest.isValid()) {
                throw new IllegalArgumentException(Server.getInstance().getLanguage()
                        .translate("nukkit.resources.zip.invalid-manifest"));
            }

            id = manifest.getHeader().getUuid().toString();
            version = manifest.getHeader().getVersion().toString();
            type = manifest.getModules().stream()
                    .findFirst()
                    .map(Module::getType)
                    .orElse("resources");

            String encryptionKey;
            if (RESOURCE_PACK_ENCRYPTION && encrypt) {
                Random random = new Random(Hash.xxh64(bytes));
                encryptionKey = generateToken(random);

                FileTime time = FileTime.fromMillis(0);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    zos.setLevel(Deflater.BEST_COMPRESSION);

                    List<Map<String, String>> pairs = new ArrayList<>();
                    Iterator<? extends ZipEntry> iterator = zip.entries().asIterator();
                    while (iterator.hasNext()) {
                        ZipEntry entry = iterator.next();
                        String name = entry.getName().replace('\\', '/');
                        byte[] content = zip.getInputStream(entry).readAllBytes();

                        if (name.endsWith(".json") || name.endsWith(".material")) {
                            try {
                                JsonNode root = JsonUtil.TRUSTED_JSON_MAPPER.readTree(content);
                                // minimize JSON file content
                                content = JsonUtil.TRUSTED_JSON_MAPPER.writeValueAsBytes(root);
                            } catch (Exception ignored) {
                            }
                        } else if (name.startsWith("subpacks/")) {
                            //TODO: sub-packs
                        }

                        byte[] data;
                        if (!"manifest.json".equalsIgnoreCase(name)
                                && !"pack_icon.png".equalsIgnoreCase(name)
                                && !"bug_pack_icon.png".equalsIgnoreCase(name)) {
                            String token = generateToken(random);
                            byte[] encodedToken = token.getBytes(StandardCharsets.UTF_8);
                            SecretKeySpec secretKey = new SecretKeySpec(encodedToken, "AES");
                            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
                            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(Arrays.copyOf(encodedToken, 16)));
                            data = cipher.doFinal(content);

                            Map<String, String> pair = new HashMap<>();
                            pair.put("path", name);
                            pair.put("key", token);
                            pairs.add(pair);
                        } else {
                            data = content;

                            pairs.add(Collections.singletonMap("path", name));
                        }

                        zos.putNextEntry(new ZipEntry(entry)
                                .setCreationTime(time)
                                .setLastModifiedTime(time)
                                .setLastAccessTime(time));
                        zos.write(data);
                        zos.closeEntry();
                    }

                    zos.putNextEntry(new ZipEntry("contents.json")
                            .setCreationTime(time)
                            .setLastModifiedTime(time)
                            .setLastAccessTime(time));

                    DataOutputStream dos = new DataOutputStream(zos);
                    dos.writeInt(0); // version
                    dos.writeInt(0xfcb9cf9b); // magic
                    dos.writeLong(0);

                    byte[] encodedId = id.getBytes(StandardCharsets.UTF_8);
                    int length = encodedId.length;
                    zos.write(length);
                    zos.write(encodedId);
                    zos.write(new byte[256 - (4 + 4 + 8 + 1 + length)]);

                    byte[] encodedKey = encryptionKey.getBytes(StandardCharsets.UTF_8);
                    SecretKeySpec secretKey = new SecretKeySpec(encodedKey, "AES");
                    Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(Arrays.copyOf(encodedKey, 16)));
                    String json = JsonUtil.TRUSTED_JSON_MAPPER.writeValueAsString(Collections.singletonMap("content", pairs));
                    zos.write(cipher.doFinal(json.getBytes(StandardCharsets.UTF_8)));

                    zos.closeEntry();

                    zos.finish();
                    bytes = baos.toByteArray();
                } catch (Exception e) {
                    encryptionKey = "";
                    log.error("Unable to encrypt mcpack", e);
                }
            } else {
                encryptionKey = "";
            }
            this.encryptionKey = encryptionKey;
        }
        size = bytes.length;

        int count = Mth.ceil(size / (float) RESOURCE_PACK_CHUNK_SIZE);
        chunks = new byte[count][];
        for (int i = 0; i < count; i++) {
            int offset = i * RESOURCE_PACK_CHUNK_SIZE;
            int length = Math.min(size - offset, RESOURCE_PACK_CHUNK_SIZE);
            chunks[i] = Arrays.copyOfRange(bytes, offset, offset + length);
        }

        try {
            sha256 = MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPackSize() {
        return size;
    }

    @Override
    public byte[] getSha256() {
        return this.sha256;
    }

    @Override
    public int getChunkCount() {
        return chunks.length;
    }

    @Override
    public byte[] getPackChunk(int index) {
        if (index < 0 || index >= chunks.length) {
            return new byte[0];
        }

        return chunks[index];
    }

    @Override
    public String getEncryptionKey() {
        return this.encryptionKey;
    }

    private static String generateToken(Random random) {
        return RandomStringUtils.random(32, 0, 0, true, true, null, random);
    }
}
