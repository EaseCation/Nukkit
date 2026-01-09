package cn.nukkit.block;

public class BlockGravelSuspicious extends BlockBrushable {
    BlockGravelSuspicious() {

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

    @Override
    public boolean isGravel() {
        return true;
    }
}
