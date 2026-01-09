package cn.nukkit.item;

public enum CooldownCategory {
    ENDER_PEARL("ender_pearl"),
    CHORUS_FRUIT("chorusfruit"),
    ICE_BOMB("ice_bomb"),
    GOAT_HORN("goat_horn"),
    WIND_CHARGE("wind_charge"),
    SPEAR("spear"),
    ;

    private final String name;

    CooldownCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
