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
    COMPARE_OPERATOR(ARG_TYPE_STRING),
    OPERATOR(ARG_TYPE_STRING), // backwards compatibility
    TARGET(ARG_TYPE_TARGET),
    WILDCARD_TARGET(ARG_TYPE_TARGET),
    EQUIPMENT_SLOT(ARG_TYPE_EQUIPMENT_SLOT),
    STRING(ARG_TYPE_STRING),
    BLOCK_POSITION(ARG_TYPE_BLOCK_POSITION),
    POSITION(ARG_TYPE_POSITION),
    MESSAGE(ARG_TYPE_RAWTEXT), // backwards compatibility
    RAWTEXT(ARG_TYPE_RAWTEXT),
    JSON(ARG_TYPE_JSON),
    TEXT(ARG_TYPE_RAWTEXT), // backwards compatibility
    COMMAND(ARG_TYPE_COMMAND),
    FILE_PATH(ARG_TYPE_STRING),
    INTEGER_RANGE(ARG_TYPE_INT),
    BLOCK_STATES(ARG_TYPE_BLOCK_STATES),
    ;

    private final int id;

    CommandParamType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
