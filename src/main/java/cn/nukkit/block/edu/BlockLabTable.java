package cn.nukkit.block.edu;

public class BlockLabTable extends BlockChemistryTable {
    protected BlockLabTable() {

    }

    @Override
    public int getId() {
        return LAB_TABLE;
    }

    @Override
    public String getName() {
        return "Lab Table";
    }
}
