package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.inventory.FurnaceBurnEvent;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.Items;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 */
public class BlockEntityFurnace extends BlockEntityAbstractContainer {

    protected static final int BURN_INTERVAL = 200;

    protected FurnaceInventory inventory;

    protected int burnTime;
    protected int burnDuration;
    protected int cookTime;
    protected int storedXp;
    protected int maxTime;

    private int crackledTime;

    public BlockEntityFurnace(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.FURNACE;
    }

    @Override
    protected void initBlockEntity() {
        this.inventory = createInventory();

        if (!this.namedTag.contains("BurnTime") || this.namedTag.getShort("BurnTime") < 0) {
            burnTime = 0;
        } else {
            burnTime = this.namedTag.getShort("BurnTime");
        }

        if (!this.namedTag.contains("CookTime") || this.namedTag.getShort("CookTime") < 0 || (this.namedTag.getShort("BurnTime") == 0 && this.namedTag.getShort("CookTime") > 0)) {
            cookTime = 0;
        } else {
            cookTime = this.namedTag.getShort("CookTime");
        }

        if (!this.namedTag.contains("BurnDuration") || this.namedTag.getShort("BurnDuration") < 0) {
            burnDuration = 0;
        } else {
            burnDuration = this.namedTag.getShort("BurnDuration");
        }

        if (this.namedTag.contains("StoredXP")) {
            storedXp = this.namedTag.getShort("StoredXP") & 0xffff;
            this.namedTag.remove("StoredXP");
            this.namedTag.putInt("StoredXPInt", storedXp);
        } else if (!this.namedTag.contains("StoredXPInt") || this.namedTag.getInt("StoredXPInt") < 0) {
            storedXp = 0;
        } else {
            storedXp = this.namedTag.getInt("StoredXPInt");
        }

        if (!this.namedTag.contains("MaxTime")) {
            maxTime = burnTime;
            burnDuration = 0;
        } else {
            maxTime = this.namedTag.getShort("MaxTime"); // Nukkit only
        }

        if (this.namedTag.contains("BurnTicks")) {
            burnDuration = this.namedTag.getShort("BurnTicks");
            this.namedTag.remove("BurnTicks");
        }

        if (burnTime > 0) {
            this.scheduleUpdate();
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putShort("CookTime", cookTime);
        this.namedTag.putShort("BurnTime", burnTime);
        this.namedTag.putShort("BurnDuration", burnDuration);
        this.namedTag.putInt("StoredXPInt", storedXp);
        this.namedTag.putShort("MaxTime", maxTime);
    }

    @Override
    public final boolean isValidBlock(int blockId) {
        return blockId == this.getUnlitBlockId() || blockId == this.getLitBlockId();
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public FurnaceInventory getInventory() {
        return inventory;
    }

    protected void checkFuel(Item fuel) {
        int duration = fuel.getFuelTime();
        FurnaceBurnEvent ev = new FurnaceBurnEvent(this, fuel, duration);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        maxTime = ev.getBurnTime();
        burnTime = ev.getBurnTime();
        burnDuration = 0;
        if (this.getBlock().getId() == this.getUnlitBlockId()) {
            this.getLevel().setBlock(this, Block.get(this.getLitBlockId(), this.getBlock().getDamage()), true);
        }

        if (burnTime > 0 && ev.isBurning()) {
            fuel.setCount(fuel.getCount() - 1);
            if (fuel.getCount() == 0) {
                if (fuel.getId() == Item.BUCKET && fuel.getDamage() == ItemBucket.LAVA_BUCKET) {
                    fuel.setDamage(0);
                    fuel.setCount(1);
                } else {
                    fuel = Items.air();
                }
            }
            this.inventory.setFuel(fuel);
        }
    }

    @Override
    public boolean onUpdate() {
        if (this.isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        boolean ret = false;
        Item fuel = this.inventory.getFuel();
        Item raw = this.inventory.getSmelting();
        Item product = this.inventory.getResult();
        FurnaceRecipe smelt = Server.getInstance().getCraftingManager().matchFurnaceRecipe(raw, getRecipeTag());
        boolean canSmelt = (smelt != null && raw.getCount() > 0 && ((smelt.getResult().equals(product, true) && product.getCount() < product.getMaxStackSize()) || product.getId() == Item.AIR));

        if (burnTime <= 0 && canSmelt && fuel.getFuelTime() != 0 && fuel.getCount() > 0) {
            this.checkFuel(fuel);
        }

        if (burnTime > 0) {
            burnTime--;
            if (burnTime < 0) {
                burnTime = 0;
            }

            int burnInterval = getBurnInterval();
            burnDuration = Mth.ceil((float) burnTime / maxTime * burnInterval);

            if (this.crackledTime-- <= 0) {
                this.crackledTime = ThreadLocalRandom.current().nextInt(20, 100);
                this.getLevel().addLevelSoundEvent(blockCenter(), getLitSoundEvent());
            }

            if (smelt != null && canSmelt) {
                cookTime++;
                if (cookTime >= burnInterval) {
                    product = Item.get(smelt.getResult().getId(), smelt.getResult().getDamage(), product.isNull() ? 1 : product.getCount() + 1);

                    FurnaceSmeltEvent ev = new FurnaceSmeltEvent(this, raw, product);
                    this.server.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.inventory.setResult(ev.getResult());
                        raw.setCount(raw.getCount() - 1);
                        if (raw.getCount() == 0) {
                            raw = Items.air();
                        }
                        this.inventory.setSmelting(raw);
                    }

                    cookTime -= burnInterval;
                    if (cookTime < 0) {
                        cookTime = 0;
                    }
                }
            } else if (burnTime <= 0) {
                burnTime = 0;
                cookTime = 0;
                burnDuration = 0;
                maxTime = 0;
            } else {
                cookTime = 0;
            }
            ret = true;
        } else {
            if (this.getBlock().getId() == this.getLitBlockId()) {
                this.getLevel().setBlock(this, Block.get(this.getUnlitBlockId(), this.getBlock().getDamage()), true);
            }
            burnTime = 0;
            cookTime = 0;
            burnDuration = 0;
            maxTime = 0;
        }

        for (Player player : this.getInventory().getViewers()) {
            int windowId = player.getWindowId(this.getInventory());
            if (windowId > 0) {
                ContainerSetDataPacket pk1 = new ContainerSetDataPacket();
                pk1.windowId = windowId;
                pk1.property = ContainerSetDataPacket.PROPERTY_FURNACE_SMELT_PROGRESS;
                pk1.value = cookTime;
                player.dataPacket(pk1);

                ContainerSetDataPacket pk2 = new ContainerSetDataPacket();
                pk2.windowId = windowId;
                pk2.property = ContainerSetDataPacket.PROPERTY_FURNACE_REMAINING_FUEL_TIME;
                pk2.value = burnDuration;
                player.dataPacket(pk2);
            }
        }

        return ret;
    }

    protected int getUnlitBlockId() {
        return BlockID.FURNACE;
    }

    protected int getLitBlockId() {
        return BlockID.LIT_FURNACE;
    }

    @Override
    public final CompoundTag getSpawnCompound() {
        CompoundTag nbt = createSpawnTag();

        if (this.hasName()) {
            nbt.putString("CustomName", this.getName());
        }

        return nbt;
    }

    protected CompoundTag createSpawnTag() {
        return getDefaultCompound(this, FURNACE);
    }

    private static int calculateXpReward(Item product) {
        if (product.isNull()) {
            return 0;
        }

        float xp = product.getCount() * product.getFurnaceXpMultiplier();
        if (xp == 0) {
            return 0;
        }

        int result = Mth.floor(xp);
        float frac = Mth.frac(xp);
        if (frac != 0 && ThreadLocalRandom.current().nextFloat() < frac) {
            result++;
        }
        return result;
    }

    public int getDropXp() {
        return storedXp + calculateXpReward(inventory.getResult());
    }

    public void postTakeResult(Item product) {
        storedXp += calculateXpReward(product);
    }

    public int withdrawStoredXpReward() {
        int result = storedXp;
        storedXp = 0;
        return result;
    }

    protected RecipeTag getRecipeTag() {
        return RecipeTag.FURNACE;
    }

    protected int getBurnInterval() {
        return BURN_INTERVAL;
    }

    protected FurnaceInventory createInventory() {
        return new FurnaceInventory(this);
    }

    protected int getLitSoundEvent() {
        return LevelSoundEventPacket.SOUND_BLOCK_FURNACE_LIT;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public int getBurnDuration() {
        return burnDuration;
    }

    public void setBurnDuration(int burnDuration) {
        this.burnDuration = burnDuration;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getStoredXp() {
        return storedXp;
    }

    public void setStoredXp(int storedXp) {
        this.storedXp = storedXp;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }
}
