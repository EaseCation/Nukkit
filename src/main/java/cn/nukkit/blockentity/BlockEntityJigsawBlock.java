package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockEntityJigsawBlock extends BlockEntitySpawnable {
    /**
     * string.
     */
    public static final String TAG_NAME = "name";
    /**
     * string.
     */
    public static final String TAG_TARGET = "target";
    /**
     * string.
     */
    public static final String TAG_TARGET_POOL = "target_pool";
    /**
     * string.
     */
    public static final String TAG_FINAL_STATE = "final_state";
    /**
     * string.
     */
    public static final String TAG_JOINT = "joint";

    public static final String EMPTY = "minecraft:empty";

    // JigsawEditorData
    protected String name;
    protected String target;
    protected String targetPool;
    protected String finalState;
    protected JointType joint;

    public BlockEntityJigsawBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.JIGSAW_BLOCK;
    }

    @Override
    protected void initBlockEntity() {
        boolean upgrade = false;
        if (namedTag.contains(TAG_NAME)) {
            name = namedTag.getString(TAG_NAME);
        } else if (namedTag.contains("attachement_type")) {
            String attachementType = namedTag.getString("attachement_type");
            namedTag.remove("attachement_type");
            upgrade = true;

            name = attachementType;
            target = attachementType;
        } else {
            name = EMPTY;
        }

        if (!upgrade) {
            if (namedTag.contains(TAG_TARGET)) {
                target = namedTag.getString(TAG_TARGET);
            } else {
                target = EMPTY;
            }
        }

        if (namedTag.contains(TAG_TARGET_POOL)) {
            targetPool = namedTag.getString(TAG_TARGET_POOL);
        } else {
            targetPool = EMPTY;
        }

        if (namedTag.contains(TAG_FINAL_STATE)) {
            finalState = namedTag.getString(TAG_FINAL_STATE);
        } else {
            finalState = "minecraft:air";
        }

        if (namedTag.contains(TAG_JOINT)) {
            joint = JointType.byName(namedTag.getString(TAG_JOINT));
            if (joint == null) {
                joint = JointType.ROLLABLE;
            }
        } else {
            joint = JointType.ROLLABLE;
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putString(TAG_NAME, name);
        namedTag.putString(TAG_TARGET, target);
        namedTag.putString(TAG_TARGET_POOL, targetPool);
        namedTag.putString(TAG_FINAL_STATE, finalState);
        namedTag.putString(TAG_JOINT, joint.name);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.JIGSAW;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        return getDefaultCompound(this, JIGSAW_BLOCK)
                .putString(TAG_NAME, name)
                .putString(TAG_TARGET, target)
                .putString(TAG_TARGET_POOL, targetPool)
                .putString(TAG_FINAL_STATE, finalState)
                .putString(TAG_JOINT, joint.name);
    }

    public enum JointType {
        ROLLABLE("rollable"),
        ALIGNED("aligned");

        private static final JointType[] VALUES = values();
        private static final Map<String, JointType> BY_NAME = new Object2ObjectOpenHashMap<>();

        static {
            for (JointType type : VALUES) {
                BY_NAME.put(type.name, type);
            }
        }

        private final String name;

        JointType(String name) {
            this.name = name;
        }

        @Nullable
        public static JointType byName(String name) {
            return BY_NAME.get(name);
        }
    }
}
