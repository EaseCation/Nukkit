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
import cn.nukkit.inventory.MixRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockEntityBrewingStand extends BlockEntityAbstractContainer {

    protected BrewingInventory inventory;

    public static final int MAX_BREW_TIME = 400;

    public int brewTime;
    public int fuelTotal;
    public int fuelAmount;

    private boolean brewing;

    public BlockEntityBrewingStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        inventory = new BrewingInventory(this);

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
    public void saveNBT() {
        super.saveNBT();

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

    @Override
    public BrewingInventory getInventory() {
        return inventory;
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

        boolean hasUpdate = false;

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

        if (this.fuelAmount > 0 && this.brewTime <= MAX_BREW_TIME && !ingredient.isNull()) {
            for (int i = 1; i <= 3; i++) {
                Item potion = this.inventory.getItem(i);
                if (!potion.isPotion()) {
                    continue;
                }

                MixRecipe recipe = Server.getInstance().getCraftingManager().matchContainerRecipe(ingredient, potion);
                if (recipe != null) {
                    canBrew = true;
                    break;
                }

                recipe = Server.getInstance().getCraftingManager().matchBrewingRecipe(ingredient, potion);
                if (recipe != null) {
                    canBrew = true;
                    break;
                }
            }
        }

        if (canBrew) {
            if (this.brewTime == MAX_BREW_TIME) {
                this.sendBrewTime();

                StartBrewEvent e = new StartBrewEvent(this);
                this.server.getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                    brewing = false;
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
                                Item result = recipe.getResultUnsafe();
                                this.inventory.setItem(i, Item.get(potion.getId(), result.getDamage()));
                            }
                        }
                    }

                    level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_POTION_BREWED);

                    ingredient.count--;
                    this.inventory.setIngredient(ingredient);

                    this.fuelAmount--;
                    this.sendFuel();
                }

                this.brewTime = MAX_BREW_TIME;
            }

            hasUpdate = true;
        } else {
            this.brewTime = MAX_BREW_TIME;

            if (brewing) {
                this.sendBrewTime(0);
            }
        }
        brewing = canBrew;

        return hasUpdate;
    }

    protected void sendFuel() {
        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                ContainerSetDataPacket pk1 = new ContainerSetDataPacket();
                pk1.windowId = windowId;
                pk1.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_AMOUNT;
                pk1.value = this.fuelAmount;
                p.dataPacket(pk1);

                ContainerSetDataPacket pk2 = new ContainerSetDataPacket();
                pk2.windowId = windowId;
                pk2.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_TOTAL;
                pk2.value = this.fuelTotal;
                p.dataPacket(pk2);
            }
        }
    }

    public void sendBrewTime() {
        sendBrewTime(brewTime);
    }

    protected void sendBrewTime(int brewTime) {
        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                ContainerSetDataPacket pk = new ContainerSetDataPacket();
                pk.windowId = windowId;
                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_BREW_TIME;
                pk.value = brewTime;
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
        this.level.setBlock(block, block, true, false);
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
