package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityStructureBlock extends BlockEntitySpawnable {

    public BlockEntityStructureBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.STRUCTURE_BLOCK;
    }

    @Override
    protected void initBlockEntity() {
        if (!this.namedTag.contains("isPowered")) {
            this.namedTag.putBoolean("isPowered", false);
        }

        // StructureEditorData
        if (!this.namedTag.contains("data")) {
            this.namedTag.putInt("data", level.isInitialized() ? getBlock().getDamage() : 0);
        }
        if (!this.namedTag.contains("redstoneSaveMode")) {
            this.namedTag.putInt("redstoneSaveMode", 0);
        }
        if (!this.namedTag.contains("xStructureOffset")) {
            this.namedTag.putInt("xStructureOffset", 0);
        }
        if (!this.namedTag.contains("yStructureOffset")) {
            this.namedTag.putInt("yStructureOffset", -1);
        }
        if (!this.namedTag.contains("zStructureOffset")) {
            this.namedTag.putInt("zStructureOffset", 0);
        }
        if (!this.namedTag.contains("xStructureSize")) {
            this.namedTag.putInt("xStructureSize", 5);
        }
        if (!this.namedTag.contains("yStructureSize")) {
            this.namedTag.putInt("yStructureSize", 5);
        }
        if (!this.namedTag.contains("zStructureSize")) {
            this.namedTag.putInt("zStructureSize", 5);
        }
        if (!this.namedTag.contains("structureName")) {
            this.namedTag.putString("structureName", "");
        }
        if (!this.namedTag.contains("dataField")) {
            this.namedTag.putString("dataField", "");
        }
        if (!this.namedTag.contains("ignoreEntities")) {
            this.namedTag.putBoolean("ignoreEntities", false);
        }
        if (!this.namedTag.contains("includePlayers")) {
            this.namedTag.putBoolean("includePlayers", false);
        }
        if (!this.namedTag.contains("removeBlocks")) {
            this.namedTag.putBoolean("removeBlocks", false);
        }
        if (!this.namedTag.contains("showBoundingBox")) {
            this.namedTag.putBoolean("showBoundingBox", true);
        }
        if (!this.namedTag.contains("rotation")) {
            this.namedTag.putByte("rotation", 0);
        }
        if (!this.namedTag.contains("mirror")) {
            this.namedTag.putByte("mirror", 0);
        }
        if (!this.namedTag.contains("animationMode")) {
            this.namedTag.putByte("animationMode", 0);
        }
        if (!this.namedTag.contains("animationSeconds")) {
            this.namedTag.putFloat("animationSeconds", 0);
        }
        //TODO: int animationTicks
        if (!this.namedTag.contains("integrity")) {
            this.namedTag.putFloat("integrity", 100);
        }
        if (!this.namedTag.contains("seed")) {
            this.namedTag.putLong("seed", 0);
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putInt("data", this.getBlock().getDamage());
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.STRUCTURE_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, STRUCTURE_BLOCK)
                .putInt("data", this.getBlock().getDamage())
                .putInt("redstoneSaveMode", this.namedTag.getInt("redstoneSaveMode"))
                .putInt("xStructureOffset", this.namedTag.getInt("xStructureOffset"))
                .putInt("yStructureOffset", this.namedTag.getInt("yStructureOffset"))
                .putInt("zStructureOffset", this.namedTag.getInt("zStructureOffset"))
                .putInt("xStructureSize", this.namedTag.getInt("xStructureSize"))
                .putInt("yStructureSize", this.namedTag.getInt("yStructureSize"))
                .putInt("zStructureSize", this.namedTag.getInt("zStructureSize"))
                .putString("structureName", this.namedTag.getString("structureName"))
                .putString("dataField", this.namedTag.getString("dataField"))
                .putBoolean("ignoreEntities", this.namedTag.getBoolean("ignoreEntities"))
                .putBoolean("includePlayers", this.namedTag.getBoolean("includePlayers"))
                .putBoolean("removeBlocks", this.namedTag.getBoolean("removeBlocks"))
                .putBoolean("showBoundingBox", this.namedTag.getBoolean("showBoundingBox"))
                .putByte("rotation", this.namedTag.getByte("rotation"))
                .putByte("mirror", this.namedTag.getByte("mirror"))
                .putFloat("integrity", this.namedTag.getFloat("integrity"))
                .putLong("seed", this.namedTag.getLong("seed"))
                .putBoolean("isPowered", this.namedTag.getBoolean("isPowered"));
    }
}
