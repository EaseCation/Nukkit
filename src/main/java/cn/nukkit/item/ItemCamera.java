package cn.nukkit.item;

public class ItemCamera extends Item {
    public ItemCamera() {
        this(0, 1);
    }

    public ItemCamera(Integer meta) {
        this(meta, 1);
    }

    public ItemCamera(Integer meta, int count) {
        super(CAMERA, meta, count, "Camera");
    }
}
