package cn.nukkit.block;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public class BlockButtonWooden extends BlockButton {

    public BlockButtonWooden() {
        this(0);
    }

    public BlockButtonWooden(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOODEN_BUTTON;
    }

    @Override
    public String getName() {
        return "Oak Button";
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
