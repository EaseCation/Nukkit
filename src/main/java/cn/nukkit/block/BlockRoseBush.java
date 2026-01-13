package cn.nukkit.block;

public class BlockRoseBush extends BlockDoublePlant {
    BlockRoseBush() {

    }

    @Override
    public int getId() {
        return ROSE_BUSH;
    }

    @Override
    public String getName() {
        return "Rose Bush";
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_plant.rose.name";
    }
}
