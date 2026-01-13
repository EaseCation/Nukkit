package cn.nukkit.block.edu;

public class BlockTorchColoredRed extends BlockTorchColored {
    protected BlockTorchColoredRed() {

    }

    @Override
    public int getId() {
        return COLORED_TORCH_RED;
    }

    @Override
    public String getName() {
        return "Red Torch";
    }

    @Override
    public String getDescriptionId() {
        return "tile.colored_torch.red.name";
    }
}
