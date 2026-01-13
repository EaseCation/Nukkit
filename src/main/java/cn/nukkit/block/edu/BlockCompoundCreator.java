package cn.nukkit.block.edu;

public class BlockCompoundCreator extends BlockChemistryTable {
    protected BlockCompoundCreator() {

    }

    @Override
    public int getId() {
        return COMPOUND_CREATOR;
    }

    @Override
    public String getName() {
        return "Compound Creator";
    }

    @Override
    public String getDescriptionId() {
        return "tile.compoundcreator.name";
    }
}
