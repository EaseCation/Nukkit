package cn.nukkit.block;

public class BlockGravelSuspicious extends BlockBrushable {
    public BlockGravelSuspicious() {
        this(0);
    }

    public BlockGravelSuspicious(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SUSPICIOUS_GRAVEL;
    }

    @Override
    public String getName() {
        return "Suspicious Gravel";
    }

    @Override
    public int getBrushedBlockId() {
        return GRAVEL;
    }
}
