package cn.nukkit.resourcepacks;

import cn.nukkit.resourcepacks.PackManifest.SubPack;
import cn.nukkit.utils.Hash;
import cn.nukkit.utils.JsonUtil;
import org.apache.commons.lang3.RandomStringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

final class PackEncryptionUtil {
    private static final String CONTENTS_FILE = "contents.json";
    private static final String SUBPACKS_DIRECTORY = "subpacks/";
    private static final int CONTENTS_HEADER_LENGTH = 256;
    private static final int CONTENTS_VERSION = 0;
    private static final int CONTENTS_MAGIC = 0xfcb9cf9b;
    private static final FileTime ENTRY_TIME = FileTime.fromMillis(0);

    static EncryptedPack encrypt(ZipFile archive, byte[] originalBytes, String packId, List<SubPack> subPacks) throws IOException {
        Random random = new Random(Hash.xxh64(originalBytes));
        String key = generateToken(random);

        Map<String, List<Map<String, String>>> indexes = new LinkedHashMap<>();
        indexes.put("", new ArrayList<>());
        for (SubPack subPack : subPacks) {
            indexes.put(SUBPACKS_DIRECTORY + subPack.folderName() + "/", new ArrayList<>());
        }

        Set<String> names = new HashSet<>();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream output = new ZipOutputStream(bytes)) {
            output.setLevel(Deflater.BEST_COMPRESSION);

            Iterator<? extends ZipEntry> entries = archive.entries().asIterator();
            while (entries.hasNext()) {
                ZipEntry entry = entries.next();
                if (entry.isDirectory()) {
                    continue;
                }

                String name = entry.getName().replace('\\', '/');
                if (!names.add(name)) {
                    throw new IOException("Duplicate resource pack archive path: " + name);
                }

                String prefix = getIndexPrefix(name, indexes);
                String relativePath = name.substring(prefix.length());
                byte[] content;
                try (InputStream input = archive.getInputStream(entry)) {
                    content = input.readAllBytes();
                }
                if (CONTENTS_FILE.equals(relativePath)) {
                    if (isEncryptedIndex(content)) {
                        throw new IOException("Cannot encrypt an already encrypted resource pack: " + name);
                    }
                    continue;
                }
                content = minimizeJson(name, content);

                Map<String, String> indexEntry = new HashMap<>();
                indexEntry.put("path", relativePath);
                if (!prefix.isEmpty() || !isPlaintextRootFile(name)) {
                    String fileKey = generateToken(random);
                    content = encryptBytes(content, fileKey);
                    indexEntry.put("key", fileKey);
                }
                indexes.get(prefix).add(indexEntry);
                writeEntry(output, name, content);
            }

            for (Entry<String, List<Map<String, String>>> index : indexes.entrySet()) {
                writeEntry(output, index.getKey() + CONTENTS_FILE, createIndex(packId, key, index.getValue()));
            }
        }
        return new EncryptedPack(bytes.toByteArray(), key);
    }

    private static String getIndexPrefix(String name, Map<String, List<Map<String, String>>> indexes) {
        if (name.startsWith(SUBPACKS_DIRECTORY)) {
            int separator = name.indexOf('/', SUBPACKS_DIRECTORY.length());
            if (separator >= 0) {
                String prefix = name.substring(0, separator + 1);
                if (indexes.containsKey(prefix)) {
                    return prefix;
                }
            }
        }
        return "";
    }

    private static boolean isPlaintextRootFile(String name) {
        return "manifest.json".equalsIgnoreCase(name)
                || "pack_manifest.json".equalsIgnoreCase(name)
                || "pack_icon.png".equalsIgnoreCase(name)
                || "bug_pack_icon.png".equalsIgnoreCase(name);
    }

    private static boolean isEncryptedIndex(byte[] bytes) {
        return bytes.length >= 8
                && bytes[4] == (byte) 0xfc
                && bytes[5] == (byte) 0xb9
                && bytes[6] == (byte) 0xcf
                && bytes[7] == (byte) 0x9b;
    }

    private static byte[] minimizeJson(String name, byte[] bytes) {
        if (name.endsWith(".json") || name.endsWith(".material")) {
            try {
                JsonNode root = JsonUtil.TRUSTED_JSON_MAPPER.readTree(bytes);
                return JsonUtil.TRUSTED_JSON_MAPPER.writeValueAsBytes(root);
            } catch (JacksonException ignored) {
            }
        }
        return bytes;
    }

    private static byte[] createIndex(String packId, String key, List<Map<String, String>> entries) throws IOException {
        byte[] contentId = packId.getBytes(StandardCharsets.UTF_8);
        if (contentId.length > CONTENTS_HEADER_LENGTH - 17) {
            throw new IOException("Resource pack content identity exceeds the index header size");
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeInt(CONTENTS_VERSION);
        output.writeInt(CONTENTS_MAGIC);
        output.writeLong(0);
        output.writeByte(contentId.length);
        output.write(contentId);
        output.write(new byte[CONTENTS_HEADER_LENGTH - 17 - contentId.length]); // 4 + 4 + 8 + 1 = 17
        byte[] json = JsonUtil.TRUSTED_JSON_MAPPER.writeValueAsBytes(Map.of("content", entries));
        output.write(encryptBytes(json, key));
        return bytes.toByteArray();
    }

    private static byte[] encryptBytes(byte[] bytes, String key) throws IOException {
        byte[] encodedKey = key.getBytes(StandardCharsets.UTF_8);
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encodedKey, "AES"), new IvParameterSpec(Arrays.copyOf(encodedKey, 16)));
            return cipher.doFinal(bytes);
        } catch (GeneralSecurityException exception) {
            throw new IOException("Unable to encrypt resource pack content", exception);
        }
    }

    private static void writeEntry(ZipOutputStream output, String name, byte[] bytes) throws IOException {
        output.putNextEntry(new ZipEntry(name)
                .setCreationTime(ENTRY_TIME)
                .setLastModifiedTime(ENTRY_TIME)
                .setLastAccessTime(ENTRY_TIME));
        output.write(bytes);
        output.closeEntry();
    }

    private static String generateToken(Random random) {
        return RandomStringUtils.random(32, 0, 0, true, true, null, random);
    }

    private PackEncryptionUtil() {
    }

    record EncryptedPack(byte[] bytes, String key) {
    }
}
