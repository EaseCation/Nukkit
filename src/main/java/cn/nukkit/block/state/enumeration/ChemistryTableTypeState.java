package cn.nukkit.block.state.enumeration;

public enum ChemistryTableTypeState {
    COMPOUND_CREATOR("compound_creator"),
    MATERIAL_REDUCER("material_reducer"),
    ELEMENT_CONSTRUCTOR("element_constructor"),
    LAB_TABLE("lab_table"),
    ;

    private final String name;

    ChemistryTableTypeState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
