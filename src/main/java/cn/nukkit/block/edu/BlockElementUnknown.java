package cn.nukkit.block.edu;

public class BlockElementUnknown extends BlockElement {

    protected BlockElementUnknown() {

    }

    @Override
    public String getName() {
        return "???";
    }

    @Override
    public int getId() {
        return ELEMENT_0;
    }

    @Override
    public String getDescriptionId() {
        return "tile.element.unknown.name";
    }
}
