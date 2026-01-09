package cn.nukkit.block;

public class BlockSandSuspicious extends BlockBrushable {
    BlockSandSuspicious() {

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

    @Override
    public boolean isSand() {
        return true;
    }
}
