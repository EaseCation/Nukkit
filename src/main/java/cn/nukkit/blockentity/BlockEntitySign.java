package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.TextFormat;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntitySign extends BlockEntitySpawnable {

    private TextData frontText;
    private TextData backText;

    private boolean waxed;

    @Nullable
    public Player lockedForEditingBy;

    public BlockEntitySign(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.SIGN;
    }

    @Override
    protected void initBlockEntity() {
        frontText = new TextData();
        backText = new TextData();

        if (namedTag.contains("FrontText")) {
            frontText.load(namedTag.getCompound("FrontText"));

            if (namedTag.contains("BackText")) {
                backText.load(namedTag.getCompound("BackText"));
            }
        } else {
            frontText.load(namedTag);

            namedTag.remove("Text");
            namedTag.remove("SignTextColor");
            namedTag.remove("IgnoreLighting");
            namedTag.remove("PersistFormatting");
            namedTag.remove("TextOwner");
            namedTag.remove("TextIgnoreLegacyBugResolved");
        }

        waxed = namedTag.getBoolean("IsWaxed");

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putCompound("FrontText", frontText.save(namedTag.getCompound("FrontText")));
        namedTag.putCompound("BackText", backText.save(namedTag.getCompound("BackText")));

        namedTag.putBoolean("IsWaxed", waxed);

        namedTag.remove("LockedForEditingBy");
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
                || blockId == Block.BAMBOO_STANDING_SIGN || blockId == Block.BAMBOO_WALL_SIGN
                || blockId == Block.CHERRY_STANDING_SIGN || blockId == Block.CHERRY_WALL_SIGN
                || blockId == Block.PALE_OAK_STANDING_SIGN || blockId == Block.PALE_OAK_WALL_SIGN
                ;
    }

    public boolean setText(String... lines) {
        return setText(true, lines);
    }

    public boolean setText(boolean front, String... lines) {
        TextData data = getData(front);
        String[] text = data.text;
        for (int i = 0; i < 4; i++) {
            if (i < lines.length) {
                text[i] = lines[i];
            } else {
                text[i] = "";
            }
        }
        return true;
    }

    public String[] getText() {
        return getText(true);
    }

    public String[] getText(boolean front) {
        return getData(front).text;
    }

    public BlockColor getColor() {
        return getColor(true);
    }

    public BlockColor getColor(boolean front) {
        return new BlockColor(getData(front).signTextColor, true);
    }

    public void setColor(BlockColor color) {
        setColor(true, color);
    }

    public void setColor(boolean front, BlockColor color) {
        getData(front).signTextColor = color.getRGB();
    }

    public boolean isGlowing() {
        return isGlowing(true);
    }

    public boolean isGlowing(boolean front) {
        return getData(front).ignoreLighting;
    }

    public void setGlowing(boolean glowing) {
        setGlowing(true, glowing);
    }

    public void setGlowing(boolean front, boolean glowing) {
        getData(front).ignoreLighting = glowing;
    }

    public boolean isHideGlowOutline() {
        return isHideGlowOutline(true);
    }

    public boolean isHideGlowOutline(boolean front) {
        return getData(front).hideGlowOutline;
    }

    public void setHideGlowOutline(boolean hide) {
        setHideGlowOutline(true, hide);
    }

    public void setHideGlowOutline(boolean front, boolean hide) {
        getData(front).hideGlowOutline = hide;
    }

    public boolean isPersistFormatting() {
        return isPersistFormatting(true);
    }

    public boolean isPersistFormatting(boolean front) {
        return getData(front).persistFormatting;
    }

    public void setPersistFormatting(boolean persistFormatting) {
        setPersistFormatting(true, persistFormatting);
    }

    public void setPersistFormatting(boolean front, boolean persistFormatting) {
        getData(front).persistFormatting = persistFormatting;
    }

    public boolean isEmpty(boolean front) {
        return getData(front).isEmpty();
    }

    public boolean isWaxed() {
        return waxed;
    }

    public void setWaxed(boolean waxed) {
        this.waxed = waxed;
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!nbt.getString("id").equals(getBlockEntityId())) {
            return false;
        }

        if (lockedForEditingBy != player) {
            return false;
        }
        lockedForEditingBy = null;

        String[] front = new String[4];
        Arrays.fill(front, "");
        String[] back = front.clone();

        String[] frontText = nbt.getCompound("FrontText").getString("Text").split("\n", 5);
        System.arraycopy(frontText, 0, front, 0, Math.min(4, frontText.length));
        String[] backText = nbt.getCompound("BackText").getString("Text").split("\n", 5);
        System.arraycopy(backText, 0, back, 0, Math.min(4, backText.length));

        sanitizeText(frontText);
        sanitizeText(backText);
        if (player.getRemoveFormat()) {
            for (int i = 0; i < 4; i++) {
                front[i] = TextFormat.clean(front[i]);
                back[i] = TextFormat.clean(back[i]);
            }
        }

        SignChangeEvent signChangeEvent = new SignChangeEvent(this.getBlock(), player, this, SignChangeEvent.FLAG_TEXT, front, back);
        this.server.getPluginManager().callEvent(signChangeEvent);
        if (!signChangeEvent.isCancelled()) {
            this.setText(true, signChangeEvent.getLines());
            this.setText(false, signChangeEvent.getBackLines());

            spawnToAll();
            setDirty();
            return true;
        }
        return false;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, getBlockEntityId())
                .putBoolean("IsWaxed", waxed)
                .putLong("LockedForEditingBy", -1) // editing player actor unique id
                .putCompound("FrontText", frontText.save(new CompoundTag()))
                .putCompound("BackText", backText.save(new CompoundTag()));
    }

    protected String getBlockEntityId() {
        return SIGN;
    }

    private static void sanitizeText(String[] lines) {
        for (int i = 0; i < Math.min(4, lines.length); i++) {
            // Don't allow excessive text per line.
            if (lines[i] != null) {
                lines[i] = lines[i].substring(0, Math.min(255, lines[i].length()));
            }
        }
    }

    private TextData getData(boolean front) {
        return front ? frontText : backText;
    }

    private static class TextData {
        String[] text = new String[4];
        int signTextColor = 0xff000000;
        boolean ignoreLighting;
        boolean hideGlowOutline;
        boolean persistFormatting = true;
        String textOwner = "";

        TextData() {
            Arrays.fill(text, "");
        }

        boolean isEmpty() {
            for (String s : text) {
                if (!s.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        CompoundTag load(CompoundTag nbt) {
            String text = nbt.getString("Text");
            if (!text.isEmpty()) {
                String[] lines = text.split("\n", 5);
                System.arraycopy(lines, 0, this.text, 0, Math.min(4, lines.length));
                sanitizeText(this.text);
            }
            signTextColor = nbt.getInt("SignTextColor", 0xff000000);
            ignoreLighting = nbt.getBoolean("IgnoreLighting");
            hideGlowOutline = nbt.getBoolean("HideGlowOutline");
            persistFormatting = nbt.getBoolean("PersistFormatting", true);
            textOwner = nbt.getString("TextOwner");
            return nbt;
        }

        CompoundTag save(CompoundTag nbt) {
            int n = 3;
            for (; n >= 0; n--) {
                String line = this.text[n];
                if (!line.isEmpty()) {
                    break;
                }
            }
            nbt.putString("Text", String.join("\n", n == 3 ? this.text : Arrays.copyOf(this.text, 1 + n)));
            nbt.putInt("SignTextColor", signTextColor);
            nbt.putBoolean("IgnoreLighting", ignoreLighting);
            nbt.putBoolean("HideGlowOutline", hideGlowOutline);
            nbt.putBoolean("PersistFormatting", persistFormatting);
            nbt.putString("TextOwner", textOwner);
            return nbt;
        }
    }
}
