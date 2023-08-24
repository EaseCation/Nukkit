package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.DoubleChestInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntityChest extends BlockEntityAbstractContainer {

    protected ChestInventory inventory;

    protected DoubleChestInventory doubleInventory;

    public BlockEntityChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.doubleInventory = null;
        this.inventory = new ChestInventory(this);

        //TODO: "ConvertedFromConsole"
        //TODO: bool "pairlead"
        //TODO: bool "forceunpair"

        super.initBlockEntity();
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        unpair();

        Inventory inv = getInventory();
        for (Player player : new ObjectArrayList<>(inv.getViewers())) {
            player.removeWindow(inv);
        }

        super.close();
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CHEST || blockId == Block.TRAPPED_CHEST;
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public BaseInventory getInventory() {
        if (this.doubleInventory == null && this.isPaired()) {
            this.checkPairing();
        }

        return this.doubleInventory != null ? this.doubleInventory : this.inventory;
    }

    @Override
    public ChestInventory getRealInventory() {
        return inventory;
    }

    protected void checkPairing() {
        BlockEntityChest pair = this.getPair();

        if (pair != null) {
            if (!pair.isPaired()) {
                pair.createPair(this);
                pair.checkPairing();
            }

            if (pair.doubleInventory != null) {
                this.doubleInventory = pair.doubleInventory;
            } else if (this.doubleInventory == null) {
                if ((pair.x + ((int) pair.z << 15)) > (this.x + ((int) this.z << 15))) { //Order them correctly
                    this.doubleInventory = new DoubleChestInventory(pair, this);
                } else {
                    this.doubleInventory = new DoubleChestInventory(this, pair);
                }
            }
        } else {
            if (level.isChunkLoaded(this.namedTag.getInt("pairx") >> 4, this.namedTag.getInt("pairz") >> 4)) {
                this.doubleInventory = null;
                this.namedTag.remove("pairx");
                this.namedTag.remove("pairz");
            }
        }
    }

    public boolean isPaired() {
        return this.namedTag.contains("pairx") && this.namedTag.contains("pairz");
    }

    public BlockEntityChest getPair() {
        if (this.isPaired()) {
            BlockEntity blockEntity = this.getLevel().getBlockEntityIfLoaded(this.namedTag.getInt("pairx"), this.getFloorY(), this.namedTag.getInt("pairz"));
            if (blockEntity instanceof BlockEntityChest) {
                return (BlockEntityChest) blockEntity;
            }
        }

        return null;
    }

    public boolean pairWith(BlockEntityChest chest) {
        if (this.isPaired() || chest.isPaired() || this.getBlock().getId() != chest.getBlock().getId()) {
            return false;
        }

        this.createPair(chest);

        chest.spawnToAll();
        this.spawnToAll();
        this.checkPairing();

        return true;
    }

    public void createPair(BlockEntityChest chest) {
        this.namedTag.putInt("pairx", (int) chest.x);
        this.namedTag.putInt("pairz", (int) chest.z);
        chest.namedTag.putInt("pairx", (int) this.x);
        chest.namedTag.putInt("pairz", (int) this.z);
    }

    public boolean unpair() {
        if (!this.isPaired()) {
            return false;
        }

        BlockEntityChest chest = this.getPair();

        this.doubleInventory = null;
        this.namedTag.remove("pairx");
        this.namedTag.remove("pairz");

        this.spawnToAll();

        if (chest != null) {
            chest.namedTag.remove("pairx");
            chest.namedTag.remove("pairz");
            chest.doubleInventory = null;
            chest.checkPairing();
            chest.spawnToAll();
        }
        this.checkPairing();

        return true;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, CHEST);

        if (this.isPaired()) {
            nbt.putInt("pairx", this.namedTag.getInt("pairx"));
            nbt.putInt("pairz", this.namedTag.getInt("pairz"));
        }

        if (this.hasName()) {
            nbt.put("CustomName", this.namedTag.get("CustomName"));
        }

        return nbt;
    }

}
