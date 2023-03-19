package cn.nukkit.network.protocol.types;

public enum ItemDescriptorType {
    NONE, // empty
    INTERNAL, // num id + meta
    MOLANG,
    ITEM_TAG,
    DEFERRED, // name id + meta
    COMPLEX_ALIAS,
}
