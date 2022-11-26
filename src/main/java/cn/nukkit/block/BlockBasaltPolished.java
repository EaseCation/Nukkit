package cn.nukkit.block;

public class BlockBasaltPolished extends BlockBasalt {
    public BlockBasaltPolished() {
        this(0);
    }

    public BlockBasaltPolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BASALT;
    }

    @Override
    public String getName() {
        return "Polished Basalt";
    }
}
