package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import cn.nukkit.math.Mth;
import cn.nukkit.resourcepacks.PackEncryptionUtil.EncryptedPack;
import cn.nukkit.resourcepacks.PackManifest.Module;
import com.google.common.io.Files;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static cn.nukkit.SharedConstants.*;

@Log4j2
public class ZippedResourcePack extends AbstractResourcePack {
    private final int size;
    private final byte[][] chunks;
    private final byte[] sha256;

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
            capabilities.addAll(manifest.getCapabilities());
            subPacks.addAll(manifest.getSubpacks());

            String encryptionKey = "";
            if (RESOURCE_PACK_ENCRYPTION && encrypt) {
                try {
                    EncryptedPack encrypted = PackEncryptionUtil.encrypt(zip, bytes, id, subPacks);
                    bytes = encrypted.bytes();
                    encryptionKey = encrypted.key();
                } catch (IOException e) {
                    log.error("Unable to encrypt mcpack", e);
                }
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
}
