package cn.nukkit.command.data;

import static cn.nukkit.network.protocol.AvailableCommandsPacket.*;

/**
 * @author CreeperFace
 */
public enum CommandParamType {
    INT(ARG_TYPE_INT),
    FLOAT(ARG_TYPE_FLOAT),
    VALUE(ARG_TYPE_VALUE),
    WILDCARD_INT(ARG_TYPE_INT), // backwards compatibility
    OPERATOR(ARG_TYPE_STRING), // backwards compatibility
    TARGET(ARG_TYPE_TARGET),
    STRING(ARG_TYPE_STRING),
    POSITION(ARG_TYPE_POSITION),
    MESSAGE(ARG_TYPE_RAWTEXT), // backwards compatibility
    RAWTEXT(ARG_TYPE_RAWTEXT),
    JSON(ARG_TYPE_JSON),
    TEXT(ARG_TYPE_RAWTEXT), // backwards compatibility
    COMMAND(ARG_TYPE_COMMAND),
    BLOCK_STATES(ARG_TYPE_BLOCK_STATES),
    BLOCK_POSITION(ARG_TYPE_BLOCK_POSITION),
    EQUIPMENT_SLOT(ARG_TYPE_EQUIPMENT_SLOT),
    ;

    private final int id;

    CommandParamType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
