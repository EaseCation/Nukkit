package cn.nukkit.network.protocol.types;

import lombok.ToString;

/**
 * @since 1.12.0
 */
@ToString
public class StructureEditorData {
    public String name;
    /**
     * @since 1.19.10
     */
    public String filteredName;
    public String dataField;
    public boolean includePlayers;
    public boolean boundingBoxVisible;
    public StructureBlockType type;
    public StructureSettings settings;
    public boolean redstoneSaveToDisk;
}
