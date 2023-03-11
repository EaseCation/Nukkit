package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import cn.nukkit.math.Mth;
import cn.nukkit.resourcepacks.PackManifest.Module;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static cn.nukkit.SharedConstants.*;

public class ZippedResourcePack extends AbstractResourcePack {
    private final int size;
    private final byte[][] chunks;
    private final byte[] sha256;

    public ZippedResourcePack(File file) throws IOException {
        if (!file.exists()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translate("nukkit.resources.zip.not-found", file.getName()));
        }

        try (ZipFile zip = new ZipFile(file)) {
            ZipEntry entry = Optional.ofNullable(zip.getEntry("manifest.json"))
                    .orElse(zip.getEntry("pack_manifest.json"));

            if (entry == null) {
                throw new IllegalArgumentException(Server.getInstance().getLanguage()
                        .translate("nukkit.resources.zip.no-manifest"));
            }

            manifest = PackManifest.load(zip.getInputStream(entry));
        }

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

        byte[] bytes = Files.readAllBytes(file.toPath());
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
