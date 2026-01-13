package cn.nukkit.block;

public class BlockGranitePolished extends BlockGranite {
    BlockGranitePolished() {

    }

    @Override
    public int getId() {
        return POLISHED_GRANITE;
    }

    @Override
    public String getName() {
        return "Polished Granite";
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone.graniteSmooth.name";
    }
}
