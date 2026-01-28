package cn.nukkit.network.protocol.types;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Rotation;
import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * @since 1.12.0
 */
@ToString
public class StructureSettings {
    public String paletteName;
    public boolean ignoreEntities;
    public boolean ignoreBlocks;
    /**
     * @since 1.18.30
     */
    public boolean allowNonTickingChunks;
    public BlockVector3 size;
    public BlockVector3 offset;
    public long lastEditedByEntityUniqueId;
    public Rotation rotation;
    public StructureMirror mirror;
    /**
     * @since 1.17.10
     */
    public StructureAnimationMode animationMode = StructureAnimationMode.NONE;
    /**
     * @since 1.17.10
     */
    public float animationSeconds;
    public float integrityValue;
    public int integritySeed;
    /**
     * @since 1.13.0
     */
    public Vector3f pivot = new Vector3f();
}
