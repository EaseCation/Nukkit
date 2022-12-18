package cn.nukkit.block;

public class BlockCaveVinesBerriesHead extends BlockCaveVinesBerriesBody {
    public BlockCaveVinesBerriesHead() {
        this(0);
    }

    public BlockCaveVinesBerriesHead(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CAVE_VINES_HEAD_WITH_BERRIES;
    }

    @Override
    public String getName() {
        return "Cave Vines Head with Berries";
    }
}
