package cn.nukkit.block;

public class BlockLilyOfTheValley extends BlockFlower {
    BlockLilyOfTheValley() {

    }

    @Override
    public int getId() {
        return LILY_OF_THE_VALLEY;
    }

    @Override
    public String getName() {
        return "Lily of the Valley";
    }

    @Override
    public String getDescriptionId() {
        return "tile.red_flower.lilyOfTheValley.name";
    }
}
