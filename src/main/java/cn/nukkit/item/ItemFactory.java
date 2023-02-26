package cn.nukkit.item;

@FunctionalInterface
public interface ItemFactory {
    Item create(Integer meta, int count);
}
