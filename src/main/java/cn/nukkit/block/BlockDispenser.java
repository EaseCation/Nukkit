package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDispenser;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BlockDispenser extends BlockSolid implements Faceable {

    public BlockDispenser() {
        this(0);
    }

    public BlockDispenser(int meta) {
        super(meta);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public String getName() {
        return "Dispenser";
    }

    @Override
    public int getId() {
        return DISPENSER;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.DISPENSER;
    }

    @Override
    public float getHardness() {
        return 3.5f;
    }

    @Override
    public float getResistance() {
        return 17.5f;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(this.getItemId());
        if (addUserData) {
            if (getBlockEntity() instanceof BlockEntity blockEntity) {
                item.setCustomName(blockEntity.getName());
                item.setRepairCost(blockEntity.getRepairCost());
            }
        }
        return item;
    }

    @Override
    public int getComparatorInputOverride() {
        InventoryHolder blockEntity = this.getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return 0;
    }

    public boolean isTriggered() {
        return (this.getDamage() & 8) > 0;
    }

    public void setTriggered(boolean value) {
        int i = 0;
        i |= getBlockFace().getIndex();

        if (value) {
            i |= 8;
        }

        this.setDamage(i);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player == null) {
            return false;
        }

        InventoryHolder blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }

        player.addWindow(blockEntity.getInventory());
        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
                double y = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    this.setDamage(BlockFace.UP.getIndex());
                } else if (this.y - y > 0) {
                    this.setDamage(BlockFace.DOWN.getIndex());
                } else {
                    this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
            }
        }

        this.getLevel().setBlock(block, this, true);

        createBlockEntity(item);
        return true;
    }

    protected InventoryHolder createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, getBlockEntityId());

        if (item != null) {
            if (item.hasCustomName()) {
                nbt.putString("CustomName", item.getCustomName());
            }

            if (item.hasCustomBlockData()) {
                for (Tag tag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(tag.getName(), tag);
                }
            }
        }

        return (InventoryHolder) BlockEntities.createBlockEntity(getBlockEntityType(), getChunk(), nbt);
    }

    @Nullable
    protected InventoryHolder getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (!(blockEntity instanceof BlockEntityDispenser)) {
            return null;
        }

        return (InventoryHolder) blockEntity;
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.setTriggered(false);
            this.level.setBlock(this, this, true, false);

            dispense();
            return type;
        } else if (type == Level.BLOCK_UPDATE_REDSTONE) {
            boolean powered = level.isBlockPowered(this.copyPos()) || level.isBlockPowered(this.upVec());
            boolean triggered = isTriggered();

            if (powered && !triggered) {
                this.setTriggered(true);
                this.level.setBlock(this, this, true, false);
                level.scheduleUpdate(this, this, 4);
            }

            return type;
        }

        return 0;
    }

    public void dispense() {
        InventoryHolder blockEntity = getBlockEntity();

        if (blockEntity == null) {
            return;
        }

        Random rand = ThreadLocalRandom.current();
        int r = 1;
        int slot = -1;
        Item target = null;

        Inventory inv = blockEntity.getInventory();
        for (Entry<Integer, Item> entry : inv.getContents().entrySet()) {
            Item item = entry.getValue();

            if (!item.isNull() && rand.nextInt(r++) == 0) {
                target = item;
                slot = entry.getKey();
            }
        }

        BlockFace facing = getBlockFace();

        Vector3 pos = add(0.5f + facing.getXOffset() * 0.7f, 0.3f + facing.getYOffset() * 0.7f, 0.5f + facing.getZOffset() * 0.7f);

        if (target == null) {
            this.level.addLevelEvent(pos, LevelEventPacket.EVENT_SOUND_CLICK_FAIL, 1200);
            return;
        } else {
            this.level.addLevelEvent(pos, LevelEventPacket.EVENT_SOUND_CLICK, 1000);
        }

        this.level.addLevelEvent(pos, LevelEventPacket.EVENT_PARTICLE_SHOOT, 3 * (facing.getZOffset() + 1) + facing.getXOffset() + 1);

        Item origin = target;
        target = target.clone();

        DispenseBehavior behavior = getDispenseBehavior(target);
        Item result = behavior.dispense(this, facing, target);

        target.count--;
        inv.setItem(slot, target);

        if (result != null) {
            if (result.getId() != origin.getId() || result.getDamage() != origin.getDamage()) {
                Item[] fit = inv.addItem(result);

                for (Item drop : fit) {
                    this.level.dropItem(this, drop);
                }
            } else {
                inv.setItem(slot, result);
            }
        }
    }

    protected DispenseBehavior getDispenseBehavior(Item item) {
        return DispenseBehaviorRegister.getBehavior(item.getId());
    }

    protected String getBlockEntityId() {
        return BlockEntity.DISPENSER;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    public Vector3 getDispensePosition() {
        BlockFace facing = getBlockFace();
        return this.add(
                0.5 + 0.7 * facing.getXOffset(),
                0.5 + 0.7 * facing.getYOffset(),
                0.5 + 0.7 * facing.getZOffset()
        );
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }
}
