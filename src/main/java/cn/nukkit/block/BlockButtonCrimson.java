package cn.nukkit.block;

public class BlockButtonCrimson extends BlockButtonWooden {
    BlockButtonCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_BUTTON;
    }

    @Override
    public String getName() {
        return "Crimson Button";
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
