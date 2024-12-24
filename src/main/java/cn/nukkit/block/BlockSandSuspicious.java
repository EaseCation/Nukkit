package cn.nukkit.block;

public class BlockSandSuspicious extends BlockBrushable {
    public BlockSandSuspicious() {
        this(0);
    }

    public BlockSandSuspicious(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SUSPICIOUS_SAND;
    }

    @Override
    public String getName() {
        return "Suspicious Sand";
    }

    @Override
    public int getBrushedBlockId() {
        return SAND;
    }
}
