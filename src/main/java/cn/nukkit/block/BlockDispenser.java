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
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BlockDispenser extends BlockSolidMeta implements Faceable {

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
        return Item.get(this.getItemId());
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
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (player == null) {
            return false;
        }

        InventoryHolder blockEntity = getBlockEntity();

        if (blockEntity == null) {
            return false;
        }

        player.addWindow(blockEntity.getInventory());
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
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

        createBlockEntity();
        return true;
    }

    protected void createBlockEntity() {
        BlockEntities.createBlockEntity(BlockEntityType.DISPENSER, getChunk(), BlockEntity.getDefaultCompound(this, BlockEntity.DISPENSER));
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

        LevelEventPacket pk = new LevelEventPacket();

        BlockFace facing = getBlockFace();

        pk.x = 0.5f + facing.getXOffset() * 0.7f;
        pk.y = 0.5f + facing.getYOffset() * 0.7f;
        pk.z = 0.5f + facing.getZOffset() * 0.7f;

        if (target == null) {
            pk.evid = LevelEventPacket.EVENT_SOUND_CLICK_FAIL;
            pk.data = 1200;

            this.level.addChunkPacket(getChunkX(), getChunkZ(), pk.clone());
            return;
        } else {
            pk.evid = LevelEventPacket.EVENT_SOUND_CLICK;
            pk.data = 1000;

            this.level.addChunkPacket(getChunkX(), getChunkZ(), pk.clone());
        }

        pk.evid = LevelEventPacket.EVENT_PARTICLE_SHOOT;
        pk.data = 7;
        this.level.addChunkPacket(getChunkX(), getChunkZ(), pk);

        Item origin = target;
        target = target.clone();

        DispenseBehavior behavior = getDispenseBehavior(target);
        Item result = behavior.dispense(this, facing, target);


        pk.evid = LevelEventPacket.EVENT_SOUND_CLICK;

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
}
