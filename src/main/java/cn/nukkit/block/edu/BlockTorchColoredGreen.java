package cn.nukkit.block.edu;

public class BlockTorchColoredGreen extends BlockTorchColored {
    protected BlockTorchColoredGreen() {

    }

    @Override
    public int getId() {
        return COLORED_TORCH_GREEN;
    }

    @Override
    public String getName() {
        return "Green Torch";
    }

    @Override
    public String getDescriptionId() {
        return "tile.colored_torch.green.name";
    }
}
