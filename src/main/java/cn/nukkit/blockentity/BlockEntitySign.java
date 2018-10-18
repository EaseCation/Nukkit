package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.utils.TextFormat;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntitySign extends BlockEntitySpawnable {

    public BlockEntitySign(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (!nbt.contains("Text")) {
            List<String> lines = new ArrayList<>();

            for (int i = 1; i <= 4; i++) {
                String key = "Text" + i;

                if (nbt.contains(key)) {
                    String line = nbt.getString(key);

                    lines.add(line);

                    nbt.remove(key);
                }
            }

            nbt.putString("Text", String.join("\n", lines));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.remove("Creator");
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = getBlock().getId();
        return blockID == Block.SIGN_POST || blockID == Block.WALL_SIGN;
    }

    public boolean setText(String... lines) {
        String[] text = new String[4];
        Arrays.fill(text, "");

        System.arraycopy(lines, 0, text, 0, Math.min(lines.length, 4));

        this.namedTag.putString("Text", String.join("\n", text));
        this.spawnToAll();

        if (this.chunk != null) {
            this.chunk.setChanged();
        }

        return true;
    }

    public String[] getText() {
        String[] text = new String[4];
        Arrays.fill(text, "");

        String[] origin = this.namedTag.getString("Text").split("\n");

        System.arraycopy(origin, 0, text, 0, Math.min(4, origin.length));

        return text;
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.SIGN)) {
            return false;
        }
        String[] text = nbt.getString("Text").split("\n", 4);

        SignChangeEvent signChangeEvent = new SignChangeEvent(this.getBlock(), player, text);

        if (!this.namedTag.contains("Creator") || !Objects.equals(player.getUniqueId().toString(), this.namedTag.getString("Creator"))) {
            signChangeEvent.setCancelled();
        }

        if (player.getRemoveFormat()) {
            for (int i = 0; i < text.length; i++) {
                text[i] = TextFormat.clean(text[i]);
            }
        }

        this.server.getPluginManager().callEvent(signChangeEvent);

        if (!signChangeEvent.isCancelled()) {
            this.setText(signChangeEvent.getLines());
            return true;
        }

        return false;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.closed) {
            return;
        }

        CompoundTag tag = player.getProtocol() <= 113 ? this.getSpawnCompound11() : this.getSpawnCompound();
        BlockEntityDataPacket pk = new BlockEntityDataPacket();
        pk.x = (int) this.x;
        pk.y = (int) this.y;
        pk.z = (int) this.z;
        try {
            pk.namedTag = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player.dataPacket(pk);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.SIGN)
                .putString("Text", this.namedTag.getString("Text"))
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

    }

    public CompoundTag getSpawnCompound11() {
        String[] text = this.getText();

        return new CompoundTag()
                .putString("id", BlockEntity.SIGN)
                .putString("Text1", text[0])
                .putString("Text2", text[1])
                .putString("Text3", text[2])
                .putString("Text4", text[3])
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

    }
}
