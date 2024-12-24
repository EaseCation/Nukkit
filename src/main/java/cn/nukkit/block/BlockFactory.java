package cn.nukkit.block;

@FunctionalInterface
public interface BlockFactory {
    Block create(int id);
}
