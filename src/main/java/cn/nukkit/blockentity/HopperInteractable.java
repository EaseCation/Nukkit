package cn.nukkit.blockentity;

public interface HopperInteractable {
    default boolean pull(BlockEntityHopper hopper) {
        return false;
    }

    default boolean push(BlockEntityHopper hopper) {
        return false;
    }
}
