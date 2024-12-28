package cn.nukkit.block.state.enumeration;

public enum DoublePlantTypeState {
    SUNFLOWER("sunflower"),
    SYRINGA("syringa"),
    GRASS("grass"),
    FERN("fern"),
    ROSE("rose"),
    PAEONIA("paeonia"),
    ;

    private final String name;

    DoublePlantTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
