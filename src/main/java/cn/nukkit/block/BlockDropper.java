package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDropper;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.dispenser.DefaultDispenseBehavior;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;

public class BlockDropper extends BlockDispenser {
    private static final DispenseBehavior DISPENSE_BEHAVIOR = new DefaultDispenseBehavior();

    public BlockDropper() {
        this(0);
    }

    public BlockDropper(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dropper";
    }

    @Override
    public int getId() {
        return DROPPER;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.DROPPER;
    }

    @Override
    public void dispense() {
        super.dispense();
    }

    @Override
    @Nullable
    protected InventoryHolder getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (!(blockEntity instanceof BlockEntityDropper)) {
            return null;
        }

        return (InventoryHolder) blockEntity;
    }

    @Override
    protected DispenseBehavior getDispenseBehavior(Item item) {
        return DISPENSE_BEHAVIOR;
    }

    @Override
    protected String getBlockEntityId() {
        return BlockEntity.DROPPER;
    }
}
