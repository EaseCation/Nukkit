package cn.nukkit.block.state.enumeration;

public enum MonsterEggStoneTypeState {
    STONE("stone"),
    COBBLESTONE("cobblestone"),
    STONE_BRICK("stone_brick"),
    MOSSY_STONE_BRICK("mossy_stone_brick"),
    CRACKED_STONE_BRICK("cracked_stone_brick"),
    CHISELED_STONE_BRICK("chiseled_stone_brick"),
    ;

    private final String name;

    MonsterEggStoneTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
