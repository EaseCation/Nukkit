package cn.nukkit.block;

public class BlockPumpkinCarved extends BlockPumpkin {

    public BlockPumpkinCarved() {
        this(0);
    }

    public BlockPumpkinCarved(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CARVED_PUMPKIN;
    }

    @Override
    public String getName() {
        return "Carved Pumpkin";
    }
}
