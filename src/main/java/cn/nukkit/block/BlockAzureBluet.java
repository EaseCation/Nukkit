package cn.nukkit.block;

public class BlockAzureBluet extends BlockFlower {
    BlockAzureBluet() {

    }

    @Override
    public int getId() {
        return AZURE_BLUET;
    }

    @Override
    public String getName() {
        return "Azure Bluet";
    }

    @Override
    public String getDescriptionId() {
        return "tile.red_flower.houstonia.name";
    }
}
