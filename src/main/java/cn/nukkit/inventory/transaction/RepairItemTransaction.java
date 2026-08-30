package cn.nukkit.inventory.transaction;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.event.inventory.GrindstoneEvent;
import cn.nukkit.event.inventory.RepairItemEvent;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SmithingTableInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.RepairItemAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class RepairItemTransaction extends InventoryTransaction {

    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_SURVIVAL_COST = 39;
    private static final int MATERIAL_REPAIR_DIVISOR = 4;
    private static final int COMBINE_DURABILITY_BONUS_PERCENT = 12;

    private Item inputItem;
    private Item materialItem;
    private Item outputItem;
    private Item templateItem;

    private int cost;

    private boolean invalid;
    private boolean complete;

    public RepairItemTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public boolean canExecute() {
        this.complete = false;
        if (this.source.isSpectator()) {
            return false;
        }

        Inventory inventory = getSource().getWindowById(Player.ANVIL_WINDOW_ID);
        if (inventory == null) {
            return false;
        }
        if (inventory instanceof AnvilInventory anvilInventory) {
            if (this.invalid || !this.hasInput() || !this.hasOutput() || !super.canExecute()) {
                return false;
            }
            this.complete = true;

            if (!this.inputItem.equals(anvilInventory.getInputSlot(), true, true)
                    || this.hasMaterial() && !this.materialItem.equals(anvilInventory.getMaterialSlot(), true, true)
                    || !this.checkOutputTargets(anvilInventory)
                    || !this.checkRecipeValid()) {
                return false;
            }

            return this.source.isCreative() || this.cost <= this.source.getExperienceLevel();
        } else if (inventory instanceof GrindstoneInventory grindstoneInventory) {
            boolean hasItem = false;
            if (hasInput()) {
                hasItem = true;
                if (!inputItem.equals(grindstoneInventory.getInputSlot(), true, true)) {
                    return false;
                }
            }
            if (hasMaterial()) {
                hasItem = true;
                if (!materialItem.equals(grindstoneInventory.getMaterialSlot(), true, true)) {
                    return false;
                }
            }
            if (!hasItem) {
                return false;
            }
            return hasOutput() && checkGrindstoneValid();
        } else if (inventory instanceof SmithingTableInventory smithingInventory) {
            return inputItem instanceof ItemDurable && hasMaterial() && hasOutput()
                    && inputItem.equals(smithingInventory.getInputSlot(), true, true)
                    && materialItem.equals(smithingInventory.getMaterialSlot(), true, true)
                    && checkSmithingValid();
        }
        return false;
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }

        Inventory inventory = getSource().getWindowById(Player.ANVIL_WINDOW_ID);
        if (inventory instanceof AnvilInventory anvilInventory) {
            anvilInventory.setCost(this.cost);
            this.normalizeNetEaseOutputEnchantments();

            RepairItemEvent event = new RepairItemEvent(anvilInventory, this.inputItem, this.outputItem, this.materialItem, this.cost, this.source);
            this.source.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                this.source.removeAllWindows(false);
                this.sendInventories();
                return true;
            }

            int eventCost = event.getCost();
            if (eventCost < 0 || !this.source.isCreative() && eventCost > this.source.getExperienceLevel()) {
                this.source.getServer().getLogger().debug("Rejected invalid anvil event cost {} for player {}", eventCost, this.source.getName());
                this.source.removeAllWindows(false);
                this.sendInventories();
                return true;
            }

            if (!this.executeAnvilActions()) {
                this.source.removeAllWindows(false);
                this.sendInventories();
                return false;
            }

            boolean broken = false;
            if (!this.source.isCreative()) {
                this.source.setExperience(this.source.getExperience(), this.source.getExperienceLevel() - eventCost, true);

                if (ThreadLocalRandom.current().nextInt(25) < 3) {
                    Block block = source.level.getBlock(anvilInventory.getHolder());
                    if (block instanceof BlockAnvil anvil) {
                        int newId = anvil.getDamagedBlockId();
                        source.level.setBlock(block, Block.get(newId), true);
                        if (newId == Block.AIR) {
                            broken = true;
                        }
                    }
                }
            }
            this.source.level.addLevelEvent(anvilInventory.getHolder(), broken ? LevelEventPacket.EVENT_SOUND_ANVIL_BREAK : LevelEventPacket.EVENT_SOUND_ANVIL_USE);
            this.hasExecuted = true;
            return true;
        } else if (inventory instanceof GrindstoneInventory grindstoneInventory) {
            // 先计算经验返还值
            int xp = 0;
            if (hasInput()) {
                for (Enchantment ench : inputItem.getEnchantments()) {
                    if (ench.isCurse() || ench.getLevel() <= 0) {
                        continue;
                    }
                    int min = Mth.ceil(ench.getMinEnchantAbility(ench.getValidLevel()) / 2.0);
                    xp += ThreadLocalRandom.current().nextInt(min, min * 2);
                }
            }
            if (hasMaterial()) {
                for (Enchantment ench : materialItem.getEnchantments()) {
                    if (ench.isCurse() || ench.getLevel() <= 0) {
                        continue;
                    }
                    int min = Mth.ceil(ench.getMinEnchantAbility(ench.getValidLevel()) / 2.0);
                    xp += ThreadLocalRandom.current().nextInt(min, min * 2);
                }
            }

            // 抛出砂轮事件，允许插件调整经验或取消
            GrindstoneEvent grindEvent = new GrindstoneEvent(
                    grindstoneInventory,
                    this.inputItem,
                    this.outputItem,
                    this.materialItem,
                    xp,
                    this.source
            );
            this.source.getServer().getPluginManager().callEvent(grindEvent);
            if (grindEvent.isCancelled()) {
                // 取消则不执行变更，回滚窗口
                this.source.removeAllWindows(false);
                this.sendInventories();
                return true;
            }

            // 执行所有变更动作
            for (InventoryAction action : this.actions) {
                if (action.execute(this.source)) {
                    action.onExecuteSuccess(this.source);
                } else {
                    action.onExecuteFail(this.source);
                }
            }

            // 经验返还：统一以经验球掉落（插件可通过事件将返还设为0以自行处理）
            int finalXp = Math.max(0, grindEvent.getExperienceDropped());
            if (finalXp > 0) {
                this.source.level.dropExpOrb(this.source, finalXp);
            }
            return true;
        } else if (inventory instanceof SmithingTableInventory smithingInventory) {
            for (InventoryAction action : this.actions) {
                if (action.execute(this.source)) {
                    action.onExecuteSuccess(this.source);
                } else {
                    action.onExecuteFail(this.source);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void addAction(InventoryAction action) {
        boolean anvilTransaction = this.source.getWindowById(Player.ANVIL_WINDOW_ID) instanceof AnvilInventory;
        if (action instanceof RepairItemAction repair) {
            switch (repair.getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT:
                    if (anvilTransaction && this.inputItem != null) {
                        this.rejectDuplicateAction("input");
                        return;
                    }
                    this.inputItem = action.getTargetItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT:
                    if (anvilTransaction && this.outputItem != null) {
                        this.rejectDuplicateAction("output");
                        return;
                    }
                    this.outputItem = action.getSourceItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL:
                    if (anvilTransaction && this.materialItem != null) {
                        this.rejectDuplicateAction("material");
                        return;
                    }
                    this.materialItem = action.getTargetItem();
                    break;
//                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_OUTPUT:
//                    this.templateItem = action.getTargetItem();
//                    break;
            }
        } else if (anvilTransaction && !(action instanceof SlotChangeAction)) {
            this.invalid = true;
            if (Nukkit.DEBUG > 1) {
                this.source.getServer().getLogger().debug("Unexpected action in anvil transaction: {}", action);
            }
            return;
        }
        super.addAction(action);
    }

    private boolean executeAnvilActions() {
        for (InventoryAction action : this.actions) {
            if (!action.isValid(this.source)) {
                action.onExecuteFail(this.source);
                return false;
            }
        }

        List<SlotChangeAction> executedActions = new ArrayList<>();
        for (InventoryAction action : this.actions) {
            if (!action.execute(this.source)) {
                action.onExecuteFail(this.source);
                this.rollbackAnvilActions(executedActions);
                return false;
            }
            if (action instanceof SlotChangeAction slotChange) {
                executedActions.add(slotChange);
            }
        }

        for (InventoryAction action : this.actions) {
            action.onExecuteSuccess(this.source);
        }
        return true;
    }

    private void rollbackAnvilActions(List<SlotChangeAction> executedActions) {
        for (int index = executedActions.size() - 1; index >= 0; index--) {
            SlotChangeAction action = executedActions.get(index);
            if (!action.getInventory().setItem(action.getSlot(), action.getSourceItem(), false)) {
                this.source.getServer().getLogger().error("Failed to roll back anvil slot {} for player {}", action.getSlot(), this.source.getName());
            }
        }
    }

    private void rejectDuplicateAction(String actionType) {
        this.invalid = true;
        if (Nukkit.DEBUG > 1) {
            this.source.getServer().getLogger().debug("Duplicate {} action in anvil transaction", actionType);
        }
    }

    private boolean checkOutputTargets(AnvilInventory anvilInventory) {
        int deliveredCount = 0;

        for (InventoryAction action : this.actions) {
            if (!(action instanceof SlotChangeAction slotChangeAction)) {
                continue;
            }

            if (slotChangeAction.getInventory() == anvilInventory) {
                continue;
            }

            Item targetItem = slotChangeAction.getTargetItemUnsafe();
            if (targetItem == null || targetItem.isNull()) {
                continue;
            }

            Item sourceItem = slotChangeAction.getSourceItemUnsafe();
            int sourceCount = 0;
            if (sourceItem != null && !sourceItem.isNull()) {
                if (!sourceItem.equals(this.outputItem, true, true)) {
                    this.logValidationFailure("output target source");
                    return false;
                }
                sourceCount = sourceItem.getCount();
            }

            if (!targetItem.equals(this.outputItem, true, true)) {
                this.logValidationFailure("output target item");
                return false;
            }

            int addedCount = targetItem.getCount() - sourceCount;
            if (addedCount <= 0) {
                this.logValidationFailure("output target count");
                return false;
            }
            deliveredCount += addedCount;
        }

        if (deliveredCount != this.outputItem.getCount()) {
            this.logValidationFailure("delivered output count");
            return false;
        }
        return true;
    }

    private boolean checkRecipeValid() {
        int operationCost = 0;
        int baseRepairCost = this.inputItem.getRepairCost();
        if (!this.hasValidRepairCost(this.inputItem) || !this.hasValidRepairCost(this.outputItem)
                || !this.hasValidCustomName(this.inputItem) || !this.hasValidCustomName(this.outputItem)
                || baseRepairCost < 0) {
            this.logValidationFailure("item metadata");
            return false;
        }

        boolean mapRecipe = this.isMapRecipe();
        Int2IntMap expectedEnchantments = new Int2IntOpenHashMap();
        expectedEnchantments.defaultReturnValue(-1);

        if (mapRecipe) {
            if (!this.matchMapRecipe()) {
                this.logValidationFailure("map recipe");
                return false;
            }
            if (!this.hasSameMapData()
                    || !this.readEnchantments(this.inputItem, expectedEnchantments)
                    || !this.matchesExpectedEnchantments(expectedEnchantments)) {
                this.logValidationFailure("map output data");
                return false;
            }
            baseRepairCost = 0;
        } else {
            if (this.inputItem.getId() != this.outputItem.getId()
                    || this.inputItem.getCount() != this.outputItem.getCount()
                    || !this.hasSameImmutableData()
                    || !this.readEnchantments(this.inputItem, expectedEnchantments)) {
                this.logValidationFailure("base output item");
                return false;
            }

            if (this.hasMaterial()) {
                int materialRepairCost = this.materialItem.getRepairCost();
                if (!this.hasValidRepairCost(this.materialItem) || materialRepairCost < 0) {
                    this.logValidationFailure("material repair cost");
                    return false;
                }
                baseRepairCost += materialRepairCost;

                if (this.inputItem.getMaxDurability() != -1 && this.matchRepairItem()) {
                    int maxRepairDamage = this.inputItem.getMaxDurability() / MATERIAL_REPAIR_DIVISOR;
                    int repairDamage = Math.min(this.inputItem.getDamage(), maxRepairDamage);
                    if (repairDamage <= 0) {
                        this.logValidationFailure("repair damage");
                        return false;
                    }

                    int damage = this.inputItem.getDamage();
                    int materialCount = 0;
                    while (repairDamage > 0 && materialCount < this.materialItem.getCount()) {
                        damage -= repairDamage;
                        materialCount++;
                        repairDamage = Math.min(damage, maxRepairDamage);
                    }

                    if (this.outputItem.getDamage() != damage && this.outputItem.getDamage() != damage + 1) {
                        this.logValidationFailure("material repair output damage");
                        return false;
                    }
                    operationCost += materialCount;
                } else {
                    boolean consumeEnchantedBook = this.materialItem.getId() == ItemID.ENCHANTED_BOOK && this.materialItem.hasEnchantments();
                    if (!consumeEnchantedBook && (this.inputItem.getMaxDurability() == -1 || this.inputItem.getId() != this.materialItem.getId())) {
                        this.logValidationFailure("material type");
                        return false;
                    }

                    int expectedDamage = this.inputItem.getDamage();
                    if (!consumeEnchantedBook && this.inputItem.getMaxDurability() != -1) {
                        int combinedDamage = this.inputItem.getDamage()
                                - this.inputItem.getMaxDurability()
                                + this.materialItem.getDamage()
                                - this.inputItem.getMaxDurability() * COMBINE_DURABILITY_BONUS_PERCENT / 100;
                        combinedDamage = Math.max(0, combinedDamage);
                        if (combinedDamage < expectedDamage) {
                            expectedDamage = combinedDamage;
                            operationCost += 2;
                        }
                    }

                    if (this.outputItem.getDamage() != expectedDamage) {
                        this.logValidationFailure("combined output damage");
                        return false;
                    }

                    Int2IntMap materialEnchantments = new Int2IntOpenHashMap();
                    materialEnchantments.defaultReturnValue(-1);
                    if (!this.readEnchantments(this.materialItem, materialEnchantments)) {
                        this.logValidationFailure("material enchantments");
                        return false;
                    }

                    boolean hasCompatibleEnchantments = false;
                    boolean hasIncompatibleEnchantments = false;
                    for (Enchantment materialEnchantment : this.materialItem.getEnchantments()) {
                        int inputLevel = expectedEnchantments.get(materialEnchantment.getId());
                        if (inputLevel < 0) {
                            inputLevel = 0;
                        }
                        int materialLevel = materialEnchantment.getLevel();
                        int outputLevel = inputLevel == materialLevel ? materialLevel + 1 : Math.max(materialLevel, inputLevel);

                        boolean canEnchant = materialEnchantment.canEnchant(this.inputItem) || this.inputItem.getId() == ItemID.ENCHANTED_BOOK;
                        for (Int2IntMap.Entry entry : expectedEnchantments.int2IntEntrySet()) {
                            if (entry.getIntKey() == materialEnchantment.getId()) {
                                continue;
                            }
                            Enchantment existingEnchantment = Enchantment.getEnchantment(entry.getIntKey());
                            existingEnchantment.setLevel(entry.getIntValue(), false);
                            if (!materialEnchantment.isCompatibleWith(existingEnchantment)) {
                                canEnchant = false;
                                operationCost++;
                            }
                        }

                        if (!canEnchant) {
                            hasIncompatibleEnchantments = true;
                            continue;
                        }

                        hasCompatibleEnchantments = true;
                        outputLevel = Math.min(outputLevel, materialEnchantment.getMaxLevel());
                        expectedEnchantments.put(materialEnchantment.getId(), outputLevel);

                        int rarityFactor = switch (materialEnchantment.getRarity()) {
                            case COMMON -> 1;
                            case UNCOMMON -> 2;
                            case RARE -> 4;
                            case VERY_RARE -> 8;
                        };
                        if (consumeEnchantedBook) {
                            rarityFactor = Math.max(1, rarityFactor / 2);
                        }

                        operationCost += rarityFactor * Math.max(0, outputLevel - inputLevel);
                        if (this.inputItem.getCount() > 1) {
                            operationCost = MAX_SURVIVAL_COST + 1;
                        }
                    }

                    if (hasIncompatibleEnchantments && !hasCompatibleEnchantments) {
                        this.logValidationFailure("incompatible enchantments");
                        return false;
                    }
                }
            } else if (this.outputItem.getDamage() != this.inputItem.getDamage()) {
                this.logValidationFailure("rename output damage");
                return false;
            }

            if (!this.matchesExpectedEnchantments(expectedEnchantments)) {
                this.logValidationFailure("output enchantments");
                return false;
            }
        }

        int renameCost = 0;
        if (!this.inputItem.getCustomName().equals(this.outputItem.getCustomName())) {
            if (this.outputItem.getCustomName().length() > MAX_NAME_LENGTH) {
                this.logValidationFailure("name length");
                return false;
            }
            renameCost = 1;
            operationCost++;
        }

        int totalCost = baseRepairCost + operationCost;
        if (renameCost == operationCost && renameCost > 0 && totalCost >= MAX_SURVIVAL_COST + 1) {
            totalCost = MAX_SURVIVAL_COST;
        }
        if (operationCost == 0 && !mapRecipe || totalCost < 0 || totalCost > MAX_SURVIVAL_COST && !this.source.isCreative()) {
            this.logValidationFailure("total cost");
            return false;
        }
        this.cost = totalCost;

        int nextBaseRepairCost = this.inputItem.getRepairCost();
        if (!mapRecipe) {
            if (this.hasMaterial()) {
                nextBaseRepairCost = Math.max(nextBaseRepairCost, this.materialItem.getRepairCost());
            }
            if (renameCost == 0 || renameCost != operationCost) {
                nextBaseRepairCost = nextBaseRepairCost * 2 + 1;
            }
        }
        if (nextBaseRepairCost < 0 || this.outputItem.getRepairCost() != nextBaseRepairCost) {
            this.logValidationFailure("output repair cost");
            return false;
        }

        return true;
    }

    private boolean hasInput() {
        return this.inputItem != null && !this.inputItem.isNull();
    }

    private boolean hasMaterial() {
        return this.materialItem != null && !this.materialItem.isNull();
    }

    private boolean hasOutput() {
        return this.outputItem != null && !this.outputItem.isNull();
    }

    private boolean hasTemplate() {
        return this.templateItem != null && !this.templateItem.isNull();
    }

    private boolean isMapRecipe() {
        return this.hasMaterial()
                && (this.inputItem.getId() == ItemID.FILLED_MAP || this.inputItem.getId() == ItemID.EMPTY_MAP)
                && (this.materialItem.getId() == ItemID.EMPTY_MAP || this.materialItem.getId() == ItemID.PAPER || this.materialItem.getId() == ItemID.COMPASS);
    }

    private boolean matchMapRecipe() {
        if (this.inputItem.getId() == ItemID.EMPTY_MAP) {
            return this.inputItem.getDamage() != 2 && this.materialItem.getId() == ItemID.COMPASS // locator
                    && this.outputItem.getId() == ItemID.EMPTY_MAP && this.outputItem.getDamage() == 2 && this.outputItem.getCount() == 1;
        } else if (this.inputItem.getId() == ItemID.FILLED_MAP) {
            if (this.materialItem.getId() == ItemID.COMPASS) { // locator
                return this.inputItem.getDamage() < 2 && this.outputItem.getId() == ItemID.FILLED_MAP
                        && this.outputItem.getDamage() == 2 && this.outputItem.getCount() == 1;
            } else if (this.materialItem.getId() == ItemID.EMPTY_MAP) { // clone
                return this.outputItem.getId() == ItemID.FILLED_MAP && this.outputItem.getDamage() == this.inputItem.getDamage() && this.outputItem.getCount() == 2;
            } else if (this.materialItem.getId() == ItemID.PAPER && this.materialItem.getCount() >= 8) { // zoom out
                return this.inputItem.getDamage() < 3 && this.outputItem.getId() == ItemID.FILLED_MAP
                        && this.outputItem.getDamage() == this.inputItem.getDamage() && this.outputItem.getCount() == 1;
            }
        }
        return false;
    }

    private boolean hasSameImmutableData() {
        if (!this.hasSameComponents()) {
            return false;
        }

        return this.createComparableTag(this.inputItem).equals(this.createComparableTag(this.outputItem));
    }

    private boolean hasSameMapData() {
        if (!this.hasSameComponents()) {
            return false;
        }

        CompoundTag inputTag = this.createComparableTag(this.inputItem);
        CompoundTag outputTag = this.createComparableTag(this.outputItem);
        if (this.inputItem.getId() == ItemID.FILLED_MAP && this.materialItem.getId() == ItemID.COMPASS) {
            inputTag.putBoolean("map_display_players", true);
        }
        return inputTag.equals(outputTag);
    }

    private boolean hasSameComponents() {
        return Objects.equals(this.inputItem.getCanPlaceOnBlocks(), this.outputItem.getCanPlaceOnBlocks())
                && Objects.equals(this.inputItem.getCanDestroyBlocks(), this.outputItem.getCanDestroyBlocks());
    }

    private boolean hasValidRepairCost(Item item) {
        CompoundTag nbt = item.getNamedTag();
        if (nbt == null) {
            return true;
        }
        Tag repairCostTag = nbt.get("RepairCost");
        if (repairCostTag == null) {
            return true;
        }
        return repairCostTag instanceof IntTag repairCost && repairCost.data >= 0;
    }

    private boolean hasValidCustomName(Item item) {
        CompoundTag nbt = item.getNamedTag();
        if (nbt == null) {
            return true;
        }
        Tag displayTag = nbt.get("display");
        if (!(displayTag instanceof CompoundTag display)) {
            return true;
        }
        Tag nameTag = display.get("Name");
        return nameTag == null || nameTag instanceof StringTag;
    }

    private CompoundTag createComparableTag(Item item) {
        CompoundTag nbt = item.getNamedTag();
        CompoundTag result = nbt == null ? new CompoundTag() : nbt.clone();

        result.remove("RepairCost");
        result.remove("ench");

        Tag displayTag = result.get("display");
        if (displayTag instanceof CompoundTag display) {
            display.remove("Name");
            if (display.isEmpty()) {
                result.remove("display");
            }
        }

        return result;
    }

    private boolean readEnchantments(Item item, Int2IntMap enchantments) {
        return this.readEnchantments(item, enchantments, false);
    }

    private boolean readEnchantments(Item item, Int2IntMap enchantments, boolean clientOutput) {
        if (!this.hasWellFormedEnchantments(item, clientOutput)) {
            return false;
        }

        Enchantment[] itemEnchantments = item.getEnchantments();
        for (Enchantment enchantment : itemEnchantments) {
            if (enchantments.containsKey(enchantment.getId())) {
                return false;
            }
            enchantments.put(enchantment.getId(), enchantment.getLevel());
        }
        return true;
    }

    private boolean hasWellFormedEnchantments(Item item, boolean clientOutput) {
        CompoundTag nbt = item.getNamedTag();
        if (nbt == null) {
            return true;
        }
        Tag enchTag = nbt.get("ench");
        if (enchTag == null) {
            return true;
        }
        if (!(enchTag instanceof ListTag<?> enchantList)) {
            return false;
        }
        if (enchantList.isEmpty() || enchantList.type != Tag.TAG_Compound) {
            return false;
        }
        for (Object entry : enchantList.getAllUnsafe()) {
            if (!(entry instanceof CompoundTag enchant)) {
                return false;
            }
            Tag modEnchantTag = enchant.get("modEnchant");
            int expectedSize = modEnchantTag == null ? 2 : 3;
            if (enchant.size() != expectedSize
                    || !(enchant.get("id") instanceof ShortTag)
                    || !(enchant.get("lvl") instanceof ShortTag lvl) || lvl.data <= 0) {
                return false;
            }
            if (modEnchantTag != null && (!(modEnchantTag instanceof StringTag modEnchant) || !modEnchant.data.isEmpty()
                    || clientOutput && !this.isModEnchantmentAllowed(enchant.getShort("id")))) {
                return false;
            }
        }
        return true;
    }

    private boolean isModEnchantmentAllowed(int enchantmentId) {
        return this.source.isNetEaseClient()
                || this.hasEmptyModEnchantment(this.inputItem, enchantmentId)
                || this.hasEmptyModEnchantment(this.materialItem, enchantmentId);
    }

    private boolean hasEmptyModEnchantment(Item item, int enchantmentId) {
        if (item == null || item.isNull()) {
            return false;
        }
        CompoundTag nbt = item.getNamedTag();
        if (nbt == null) {
            return false;
        }
        if (!(nbt.get("ench") instanceof ListTag<?> enchantList)) {
            return false;
        }
        for (Object entry : enchantList.getAllUnsafe()) {
            if (!(entry instanceof CompoundTag enchant)) {
                continue;
            }
            if (enchant.getShort("id") == enchantmentId
                    && enchant.get("modEnchant") instanceof StringTag modEnchant && modEnchant.data.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void normalizeNetEaseOutputEnchantments() {
        Item clientOutput = this.outputItem.clone();
        this.removeEmptyModEnchantments(this.outputItem);

        for (InventoryAction action : this.actions) {
            if (!(action instanceof SlotChangeAction)) {
                continue;
            }
            Item targetItem = action.getTargetItemUnsafe();
            if (targetItem != null && !targetItem.isNull() && targetItem.equals(clientOutput, true, true)) {
                this.removeEmptyModEnchantments(targetItem);
            }
        }
    }

    private void removeEmptyModEnchantments(Item item) {
        CompoundTag nbt = item.getNamedTag();
        if (nbt == null) {
            return;
        }
        if (!(nbt.get("ench") instanceof ListTag<?> enchantList)) {
            return;
        }

        boolean changed = false;
        for (Object entry : enchantList.getAllUnsafe()) {
            if (!(entry instanceof CompoundTag enchant)) {
                continue;
            }
            Tag modEnchantTag = enchant.get("modEnchant");
            if (modEnchantTag instanceof StringTag modEnchant && modEnchant.data.isEmpty()) {
                enchant.remove("modEnchant");
                changed = true;
            }
        }
        if (changed) {
            item.setNamedTag(nbt);
        }
    }

    private boolean matchesExpectedEnchantments(Int2IntMap expectedEnchantments) {
        Int2IntMap outputEnchantments = new Int2IntOpenHashMap();
        outputEnchantments.defaultReturnValue(-1);
        return this.readEnchantments(this.outputItem, outputEnchantments, true) && expectedEnchantments.equals(outputEnchantments);
    }

    private boolean matchRepairItem() {
        int inputId = this.inputItem.getId();
        int materialId = this.materialItem.getId();

        switch (inputId) {
            case ItemID.ELYTRA -> {
                return materialId == ItemID.PHANTOM_MEMBRANE;
            }
            case ItemID.TURTLE_HELMET -> {
                return materialId == ItemID.TURTLE_SCUTE;
            }
            case ItemID.SHIELD -> {
                return this.materialItem.hasItemTag(ItemTags.PLANKS);
            }
            case ItemID.WOLF_ARMOR -> {
                return materialId == ItemID.ARMADILLO_SCUTE;
            }
            case ItemID.MACE -> {
                return materialId == ItemID.BREEZE_ROD;
            }
        }
        if (this.inputItem instanceof ItemTool tool) {
            return switch (tool.getTier()) {
                case ItemTool.TIER_WOODEN -> this.materialItem.hasItemTag(ItemTags.PLANKS);
                case ItemTool.TIER_STONE -> materialId == ItemID.COBBLESTONE || materialId == ItemID.BLACKSTONE || materialId == ItemID.COBBLED_DEEPSLATE;
                case ItemTool.TIER_COPPER -> materialId == ItemID.COPPER_INGOT;
                case ItemTool.TIER_IRON -> materialId == ItemID.IRON_INGOT;
                case ItemTool.TIER_GOLD -> materialId == ItemID.GOLD_INGOT;
                case ItemTool.TIER_DIAMOND -> materialId == ItemID.DIAMOND;
                case ItemTool.TIER_NETHERITE -> materialId == ItemID.NETHERITE_INGOT;
                default -> false;
            };
        } else if (this.inputItem instanceof ItemArmor armor) {
            return switch (armor.getTier()) {
                case ItemArmor.TIER_LEATHER -> materialId == ItemID.LEATHER;
                case ItemArmor.TIER_COPPER -> materialId == ItemID.COPPER_INGOT;
                case ItemArmor.TIER_CHAIN, ItemArmor.TIER_IRON -> materialId == ItemID.IRON_INGOT;
                case ItemArmor.TIER_GOLD -> materialId == ItemID.GOLD_INGOT;
                case ItemArmor.TIER_DIAMOND -> materialId == ItemID.DIAMOND;
                case ItemArmor.TIER_NETHERITE -> materialId == ItemID.NETHERITE_INGOT;
                default -> false;
            };
        }
        return false;
    }

    private void logValidationFailure(String reason) {
        if (Nukkit.DEBUG > 1) {
            this.source.getServer().getLogger().debug("Anvil transaction validation failed for player {}: {}", this.source.getName(), reason);
        }
    }

    private boolean checkGrindstoneValid() {
        boolean hasEnchant = false;
        for (Enchantment ench : outputItem.getEnchantments()) {
            if (!ench.isCurse()) {
                return false;
            }
            hasEnchant = true;
        }

        Item input;
        Item material;
        if (hasInput()) {
            input = inputItem;
            material = materialItem;
        } else {
            input = materialItem;
            material = null;
        }

        int outputId = outputItem.getId();
        int inputId = input.getId();

        if (inputId == ItemID.ENCHANTED_BOOK) {
            if (hasEnchant) {
                if (outputId != ItemID.ENCHANTED_BOOK) {
                    return false;
                }
            } else if (outputId != ItemID.BOOK) {
                return false;
            }

            return material == null || material.isNull();
        }

        if (outputId != inputId) {
            return false;
        }

        return (material == null || material.isNull() || inputId == material.getId())
                && input.getCustomName().equals(outputItem.getCustomName());
    }

    //TODO: match SmithingTransformRecipe
    private boolean checkSmithingValid() {
        int materialId = Integer.MIN_VALUE;
        switch (inputItem.getId()) {
            case ItemID.DIAMOND_SWORD:
                if (outputItem.getId() == ItemID.NETHERITE_SWORD) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_SHOVEL:
                if (outputItem.getId() == ItemID.NETHERITE_SHOVEL) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_PICKAXE:
                if (outputItem.getId() == ItemID.NETHERITE_PICKAXE) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_AXE:
                if (outputItem.getId() == ItemID.NETHERITE_AXE) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_HOE:
                if (outputItem.getId() == ItemID.NETHERITE_HOE) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_HELMET:
                if (outputItem.getId() == ItemID.NETHERITE_HELMET) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_CHESTPLATE:
                if (outputItem.getId() == ItemID.NETHERITE_CHESTPLATE) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_LEGGINGS:
                if (outputItem.getId() == ItemID.NETHERITE_LEGGINGS) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_BOOTS:
                if (outputItem.getId() == ItemID.NETHERITE_BOOTS) {
                    materialId = ItemID.NETHERITE_INGOT;
                }
                break;
        }
        if (materialId == Integer.MIN_VALUE) {
            return false;
        }

        return materialId == materialItem.getId() && inputItem.getDamage() == outputItem.getDamage()
                && Arrays.equals(inputItem.getCompoundTag(), outputItem.getCompoundTag());
    }

    public Item getInputItem() {
        return this.inputItem;
    }

    public Item getMaterialItem() {
        return this.materialItem;
    }

    public Item getOutputItem() {
        return this.outputItem;
    }

    public Item getTemplateItem() {
        return this.templateItem;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isInvalid() {
        return this.invalid;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public static boolean checkForRepairItemPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof RepairItemAction) {
                return true;
            }
        }
        return false;
    }
}
