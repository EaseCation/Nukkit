package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntitySign extends BlockEntitySpawnable {

    private String[] text;

    public BlockEntitySign(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        text = new String[4];

        if (!namedTag.contains("Text")) {
            for (int i = 1; i <= 4; i++) {
                String key = "Text" + i;

                if (namedTag.contains(key)) {
                    String line = namedTag.getString(key);

                    this.text[i - 1] = line;

                    this.namedTag.remove(key);
                }
            }
        } else {
            String[] lines = namedTag.getString("Text").split("\n", 4);

            for (int i = 0; i < text.length; i++) {
                if (i < lines.length)
                    text[i] = lines[i];
                else
                    text[i] = "";
            }
        }

        // Check old text to sanitize
        if (text != null) {
            sanitizeText(text);
        }

        if (!this.namedTag.contains("SignTextColor") || !(this.namedTag.get("SignTextColor") instanceof IntTag)) {
            this.setColor(DyeColor.BLACK.getSignColor());
        }
        if (!this.namedTag.contains("IgnoreLighting") || !(this.namedTag.get("IgnoreLighting") instanceof ByteTag)) {
            this.setGlowing(false);
        }
        if (!this.namedTag.contains("PersistFormatting") || !(this.namedTag.get("PersistFormatting") instanceof ByteTag)) {
            this.namedTag.putBoolean("PersistFormatting", true);
        }
        if (!this.namedTag.contains("TextIgnoreLegacyBugResolved") || !(this.namedTag.get("TextIgnoreLegacyBugResolved") instanceof ByteTag)) {
            this.namedTag.putBoolean("TextIgnoreLegacyBugResolved", false);
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.remove("Creator");
//        this.namedTag.remove("TextOwner");
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.STANDING_SIGN || blockId == Block.WALL_SIGN
                || blockId == Block.SPRUCE_STANDING_SIGN || blockId == Block.SPRUCE_WALL_SIGN
                || blockId == Block.BIRCH_STANDING_SIGN || blockId == Block.BIRCH_WALL_SIGN
                || blockId == Block.JUNGLE_STANDING_SIGN || blockId == Block.JUNGLE_WALL_SIGN
                || blockId == Block.ACACIA_STANDING_SIGN || blockId == Block.ACACIA_WALL_SIGN
                || blockId == Block.DARKOAK_STANDING_SIGN || blockId == Block.DARKOAK_WALL_SIGN
                || blockId == Block.CRIMSON_STANDING_SIGN || blockId == Block.CRIMSON_WALL_SIGN
                || blockId == Block.WARPED_STANDING_SIGN || blockId == Block.WARPED_WALL_SIGN
                || blockId == Block.MANGROVE_STANDING_SIGN || blockId == Block.MANGROVE_WALL_SIGN
                || blockId == Block.BAMBOO_STANDING_SIGN || blockId == Block.BAMBOO_WALL_SIGN;
    }

    public boolean setText(String... lines) {
        for (int i = 0; i < 4; i++) {
            if (i < lines.length)
                text[i] = lines[i];
            else
                text[i] = "";
        }

        this.namedTag.putString("Text", String.join("\n", text));
        this.spawnToAll();

        if (this.chunk != null) {
            setDirty();
        }

        return true;
    }

    public String[] getText() {
        return text;
    }

    public BlockColor getColor() {
        return new BlockColor(this.namedTag.getInt("SignTextColor"), true);
    }

    public void setColor(BlockColor color) {
        this.namedTag.putInt("SignTextColor", color.getRGB());
    }

    public boolean isGlowing() {
        return this.namedTag.getBoolean("IgnoreLighting");
    }

    public void setGlowing(boolean glowing) {
        this.namedTag.putBoolean("IgnoreLighting", glowing);
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.SIGN)) {
            return false;
        }

        // multi-version compatibility (1.19.80+)
        String text = !nbt.contains("FrontText") ? nbt.getString("Text") : nbt.getCompound("FrontText").getString("Text");

        String[] lines = new String[4];
        Arrays.fill(lines, "");
        String[] splitLines = text.split("\n", 4);
        System.arraycopy(splitLines, 0, lines, 0, splitLines.length);

        sanitizeText(lines);

        SignChangeEvent signChangeEvent = new SignChangeEvent(this.getBlock(), player, this, SignChangeEvent.FLAG_FRONT_TEXT, lines);

        //TODO: XUID "TextOwner"
//        if (!this.namedTag.contains("Creator") || !Objects.equals(player.getUniqueId().toString(), this.namedTag.getString("Creator"))) {
//            signChangeEvent.setCancelled();
//        }

        if (player.getRemoveFormat()) {
            for (int i = 0; i < lines.length; i++) {
                lines[i] = TextFormat.clean(lines[i]);
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
    public CompoundTag getSpawnCompound() {
        String text = this.namedTag.getString("Text");
        int argb = this.getColor().getRGB();
        boolean glow = this.isGlowing();
        boolean persistFormatting = this.namedTag.getBoolean("PersistFormatting");

        return getDefaultCompound(this, SIGN)
                .putString("Text", text)
                .putInt("SignTextColor", argb)
                .putBoolean("IgnoreLighting", glow)
                .putBoolean("PersistFormatting", persistFormatting)
                .putBoolean("TextIgnoreLegacyBugResolved", this.namedTag.getBoolean("TextIgnoreLegacyBugResolved"))
                //TODO: 1.19.80+
                .putBoolean("IsWaxed", false)
                .putLong("LockedForEditingBy", -1) // editing player actor runtime id
                .putCompound("FrontText", new CompoundTag(5)
                        .putString("Text", text)
                        .putInt("SignTextColor", argb)
                        .putBoolean("IgnoreLighting", glow)
                        .putBoolean("PersistFormatting", persistFormatting)
                        .putString("TextOwner", ""))
                .putCompound("BackText", new CompoundTag(5)
                        .putString("Text", "")
                        .putInt("SignTextColor", 0xff_00_00_00)
                        .putBoolean("IgnoreLighting", false)
                        .putBoolean("PersistFormatting", true)
                        .putString("TextOwner", ""));
    }

    private static void sanitizeText(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            // Don't allow excessive text per line.
            if (lines[i] != null) {
                lines[i] = lines[i].substring(0, Math.min(255, lines[i].length()));
            }
        }
    }
}
