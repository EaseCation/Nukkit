package cn.nukkit.block.state.enumeration;

public enum FlowerTypeState {
    POPPY("poppy"),
    ORCHID("orchid"),
    ALLIUM("allium"),
    HOUSTONIA("houstonia"),
    TULIP_RED("tulip_red"),
    TULIP_ORANGE("tulip_orange"),
    TULIP_WHITE("tulip_white"),
    TULIP_PINK("tulip_pink"),
    OXEYE("oxeye"),
    CORNFLOWER("cornflower"),
    LILY_OF_THE_VALLEY("lily_of_the_valley"),
    ;

    private final String name;

    FlowerTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
