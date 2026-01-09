package cn.nukkit.block;

public class BlockAnvilDamaged extends BlockAnvil {
    BlockAnvilDamaged() {

    }

    @Override
    public int getId() {
        return DAMAGED_ANVIL;
    }

    @Override
    public String getName() {
        return "Damaged Anvil";
    }

    @Override
    public int getDamagedBlockId() {
        return AIR;
    }
}
