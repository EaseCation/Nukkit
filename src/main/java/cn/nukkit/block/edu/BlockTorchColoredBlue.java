package cn.nukkit.block.edu;

public class BlockTorchColoredBlue extends BlockTorchColored {
    protected BlockTorchColoredBlue() {

    }

    @Override
    public int getId() {
        return COLORED_TORCH_BLUE;
    }

    @Override
    public String getName() {
        return "Blue Torch";
    }

    @Override
    public String getDescriptionId() {
        return "tile.colored_torch.blue.name";
    }
}
