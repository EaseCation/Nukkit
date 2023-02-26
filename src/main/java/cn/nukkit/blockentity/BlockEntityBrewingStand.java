package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBrewingStand;
import cn.nukkit.event.inventory.BrewEvent;
import cn.nukkit.event.inventory.StartBrewEvent;
import cn.nukkit.inventory.BrewingInventory;
import cn.nukkit.inventory.BrewingRecipe;
import cn.nukkit.inventory.ContainerRecipe;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class BlockEntityBrewingStand extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected BrewingInventory inventory;

    public static final int MAX_BREW_TIME = 400;

    public int brewTime;
    public int fuelTotal;
    public int fuelAmount;

    public static final IntList ingredients = IntArrayList.of(
            Item.NETHER_WART,
            Item.GHAST_TEAR,
            Item.GLOWSTONE_DUST,
            Item.REDSTONE,
            Item.GUNPOWDER,
            Item.MAGMA_CREAM,
            Item.BLAZE_POWDER,
            Item.GOLDEN_CARROT,
            Item.SPIDER_EYE,
            Item.FERMENTED_SPIDER_EYE,
            Item.GLISTERING_MELON_SLICE,
            Item.SUGAR,
            Item.RABBIT_FOOT,
            Item.PUFFERFISH,
            Item.TURTLE_HELMET,
            Item.PHANTOM_MEMBRANE,
            Item.DRAGON_BREATH);

    public BlockEntityBrewingStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        inventory = new BrewingInventory(this);

        if (!namedTag.contains("Items") || !(namedTag.get("Items") instanceof ListTag)) {
            namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, this.getItem(i));
        }

        if (!namedTag.contains("CookTime") || namedTag.getShort("CookTime") > MAX_BREW_TIME) {
            this.brewTime = MAX_BREW_TIME;
        } else {
            this.brewTime = namedTag.getShort("CookTime");
        }

        this.fuelAmount = namedTag.getShort("FuelAmount");
        this.fuelTotal = namedTag.getShort("FuelTotal");

        if (brewTime < MAX_BREW_TIME) {
            this.scheduleUpdate();
        }

        super.initBlockEntity();
    }

    @Override
    public void close() {
        if (!isClosed()) {
            for (Player player : new ObjectArrayList<>(getInventory().getViewers())) {
                player.removeWindow(getInventory());
            }
            super.close();
        }
    }

    @Override
    public void onBreak() {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        this.inventory.clearAll();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < getSize(); index++) {
            this.setItem(index, inventory.getItem(index));
        }

        namedTag.putShort("CookTime", brewTime);
        namedTag.putShort("FuelAmount", this.fuelAmount);
        namedTag.putShort("FuelTotal", this.fuelTotal);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BLOCK_BREWING_STAND;
    }

    @Override
    public int getSize() {
        return 5;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return Items.air();
        } else {
            CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = NBTIO.putItemHelper(item, index);

        if (item.getId() == Item.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public BrewingInventory getInventory() {
        return inventory;
    }

    protected boolean checkIngredient(Item ingredient) {
        return ingredients.contains(ingredient.getId());
    }

    @Override
    public boolean onUpdate() {
        if (isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;
        timing.startTiming();

        boolean ret = false;

        Item ingredient = this.inventory.getIngredient();
        boolean canBrew = false;

        Item fuel = this.getInventory().getFuel();
        if (this.fuelAmount <= 0 && fuel.getId() == Item.BLAZE_POWDER && fuel.getCount() > 0) {
            fuel.count--;
            this.fuelAmount = 20;
            this.fuelTotal = 20;

            this.inventory.setFuel(fuel);
            this.sendFuel();
        }

        if (this.fuelAmount > 0) {
            for (int i = 1; i <= 3; i++) {
                if (this.inventory.getItem(i).getId() == Item.POTION) {
                    canBrew = true;
                }
            }

            if (this.brewTime <= MAX_BREW_TIME && canBrew && ingredient.getCount() > 0) {
                if (!this.checkIngredient(ingredient)) {
                    canBrew = false;
                }
            } else {
                canBrew = false;
            }
        }

        if (canBrew) {
            if (this.brewTime == MAX_BREW_TIME) {
                this.sendBrewTime();
                StartBrewEvent e = new StartBrewEvent(this);
                this.server.getPluginManager().callEvent(e);

                if (e.isCancelled()) {
                    return false;
                }
            }

            this.brewTime--;

            if (this.brewTime <= 0) { //20 seconds
                BrewEvent e = new BrewEvent(this);
                this.server.getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    for (int i = 1; i <= 3; i++) {
                        Item potion = this.inventory.getItem(i);

                        ContainerRecipe containerRecipe = Server.getInstance().getCraftingManager().matchContainerRecipe(ingredient, potion);
                        if (containerRecipe != null) {
                            Item result = containerRecipe.getResult();
                            result.setDamage(potion.getDamage());
                            this.inventory.setItem(i, result);
                        } else {
                            BrewingRecipe recipe = Server.getInstance().getCraftingManager().matchBrewingRecipe(ingredient, potion);
                            if (recipe != null) {
                                this.inventory.setItem(i, recipe.getResult());
                            }
                        }
                    }

                    ingredient.count--;
                    this.inventory.setIngredient(ingredient);

                    this.fuelAmount--;
                    this.sendFuel();
                }

                this.brewTime = MAX_BREW_TIME;
            }

            ret = true;
        } else {
            this.brewTime = MAX_BREW_TIME;
        }

        //this.sendBrewTime();

        timing.stopTiming();
        return ret;
    }

    protected void sendFuel() {
        ContainerSetDataPacket pk = new ContainerSetDataPacket();

        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                pk.windowId = windowId;

                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_AMOUNT;
                pk.value = this.fuelAmount;
                p.dataPacket(pk);

                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_TOTAL;
                pk.value = this.fuelTotal;
                p.dataPacket(pk);
            }
        }
    }

    protected void sendBrewTime() {
        ContainerSetDataPacket pk = new ContainerSetDataPacket();
        pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_BREW_TIME;
        pk.value = this.brewTime;

        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                pk.windowId = windowId;

                p.dataPacket(pk);
            }
        }
    }

    public void updateBlock() {
        if (!level.isInitialized()) {
            return;
        }

        Block block = this.getLevelBlock();

        if (!(block instanceof BlockBrewingStand)) {
            return;
        }

        int meta = 0;

        for (int i = 1; i <= 3; ++i) {
            Item potion = this.inventory.getItem(i);

            int id = potion.getId();
            if ((id == Item.POTION || id == Item.SPLASH_POTION || id == Item.LINGERING_POTION) && potion.getCount() > 0) {
                meta |= 1 << (i - 1);
            }
        }

        block.setDamage(meta);
        this.level.setBlock(block, block, false, false);
    }

    public int getFuel() {
        return fuelAmount;
    }

    public void setFuel(int fuel) {
        this.fuelAmount = fuel;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, BREWING_STAND)
                .putShort("FuelTotal", this.fuelTotal)
                .putShort("FuelAmount", this.fuelAmount);

        if (this.brewTime < MAX_BREW_TIME) {
            nbt.putShort("CookTime", this.brewTime);
        }

        if (this.hasName()) {
            nbt.put("CustomName", namedTag.get("CustomName"));
        }

        return nbt;
    }
}
