package cn.nukkit.block;

public class BlockQuartzPillar extends BlockQuartz {
    BlockQuartzPillar() {

    }

    @Override
    public int getId() {
        return QUARTZ_PILLAR;
    }

    @Override
    public String getName() {
        return "Quartz Pillar";
    }

    @Override
    public String getDescriptionId() {
        return "tile.quartz_block.lines.name";
    }
}
