package cn.nukkit.block.edu;

public class BlockTorchColoredPurple extends BlockTorchColored {
    protected BlockTorchColoredPurple() {

    }

    @Override
    public int getId() {
        return COLORED_TORCH_PURPLE;
    }

    @Override
    public String getName() {
        return "Purple Torch";
    }

    @Override
    public String getDescriptionId() {
        return "tile.colored_torch.purple.name";
    }
}
