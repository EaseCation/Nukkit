package cn.nukkit.block;

public class BlockPitcherPlant extends BlockDoublePlant {
    BlockPitcherPlant() {

    }

    @Override
    public int getId() {
        return PITCHER_PLANT;
    }

    @Override
    public String getName() {
        return "Pitcher";
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public int getCompostableChance() {
        return 85;
    }
}
