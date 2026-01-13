package cn.nukkit.block;

public class BlockAnvilBroken extends BlockAnvil {
    BlockAnvilBroken() {

    }

    @Override
    public int getId() {
        return DEPRECATED_ANVIL;
    }

    @Override
    public String getDescriptionId() {
        return "tile.anvil.name";
    }
}
