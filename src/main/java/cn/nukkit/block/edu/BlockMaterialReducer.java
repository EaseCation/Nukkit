package cn.nukkit.block.edu;

public class BlockMaterialReducer extends BlockChemistryTable {
    protected BlockMaterialReducer() {

    }

    @Override
    public int getId() {
        return MATERIAL_REDUCER;
    }

    @Override
    public String getName() {
        return "Material Reducer";
    }

    @Override
    public String getDescriptionId() {
        return "tile.materialreducer.name";
    }
}
