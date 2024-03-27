package cn.nukkit.block;

public interface BlockToolType {
    int NONE = 0;
    int SWORD = 1 << 0;
    int SHOVEL = 1 << 1;
    int PICKAXE = 1 << 2;
    int AXE = 1 << 3;
    int SHEARS = 1 << 4;
    int HOE = 1 << 5;
}
