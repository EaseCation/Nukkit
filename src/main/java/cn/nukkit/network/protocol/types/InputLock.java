package cn.nukkit.network.protocol.types;

public interface InputLock {
    int NONE = 0;
    int CAMERA = 1 << 1;
    int MOVEMENT = 1 << 2;
    int ALL = CAMERA | MOVEMENT;
    /**
     * @since 1.21.50
     */
    int LATERAL_MOVEMENT = 1 << 4;
    /**
     * @since 1.21.50
     */
    int SNEAK = 1 << 5;
    /**
     * @since 1.21.50
     */
    int JUMP = 1 << 6;
    /**
     * @since 1.21.50
     */
    int MOUNT = 1 << 7;
    /**
     * @since 1.21.50
     */
    int DISMOUNT = 1 << 8;
    /**
     * @since 1.21.50
     */
    int MOVE_FORWARD = 1 << 9;
    /**
     * @since 1.21.50
     */
    int MOVE_BACKWARD = 1 << 10;
    /**
     * @since 1.21.50
     */
    int MOVE_LEFT = 1 << 11;
    /**
     * @since 1.21.50
     */
    int MOVE_RIGHT = 1 << 12;
}
