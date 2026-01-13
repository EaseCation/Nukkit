package cn.nukkit.block;

public class BlockAnvilChipped extends BlockAnvil {
    BlockAnvilChipped() {

    }

    @Override
    public int getId() {
        return CHIPPED_ANVIL;
    }

    @Override
    public String getName() {
        return "Chipped Anvil";
    }

    @Override
    public int getDamagedBlockId() {
        return DAMAGED_ANVIL;
    }

    @Override
    public String getDescriptionId() {
        return "tile.anvil.slightlyDamaged.name";
    }
}
