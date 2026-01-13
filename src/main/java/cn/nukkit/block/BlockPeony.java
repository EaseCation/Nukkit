package cn.nukkit.block;

public class BlockPeony extends BlockDoublePlant {
    BlockPeony() {

    }

    @Override
    public int getId() {
        return PEONY;
    }

    @Override
    public String getName() {
        return "Peony";
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_plant.paeonia.name";
    }
}
