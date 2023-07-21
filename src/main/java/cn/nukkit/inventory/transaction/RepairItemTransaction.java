package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.RepairItemEvent;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SmithingTableInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.RepairItemAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Mth;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RepairItemTransaction extends InventoryTransaction {

    private Item inputItem;
    private Item materialItem;
    private Item outputItem;
    private Item templateItem;

    private int cost;

    public RepairItemTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public boolean canExecute() {
        Inventory inventory = getSource().getWindowById(Player.ANVIL_WINDOW_ID);
        if (inventory == null) {
            return false;
        }
        if (inventory instanceof AnvilInventory) {
            AnvilInventory anvilInventory = (AnvilInventory) inventory;
            this.cost = anvilInventory.getCost();
            return this.hasInput() && this.hasOutput() && this.inputItem.equals(anvilInventory.getInputSlot(), true, true)
                    && (!this.hasMaterial() || this.materialItem.equals(anvilInventory.getMaterialSlot(), true, true))
                    && this.checkRecipeValid() && this.checkRenameValid();
        } else if (inventory instanceof GrindstoneInventory) {
            GrindstoneInventory grindstoneInventory = (GrindstoneInventory) inventory;

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
        } else if (inventory instanceof SmithingTableInventory) {
            SmithingTableInventory smithingInventory = (SmithingTableInventory) inventory;
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
        if (inventory instanceof AnvilInventory) {
            AnvilInventory anvilInventory = (AnvilInventory) getSource().getWindowById(Player.ANVIL_WINDOW_ID);

            RepairItemEvent event = new RepairItemEvent(anvilInventory, this.inputItem, this.outputItem, this.materialItem, this.cost, this.source);
            this.source.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                this.source.removeAllWindows(false);
                this.sendInventories();
                return true;
            }

            for (InventoryAction action : this.actions) {
                if (action.execute(this.source)) {
                    action.onExecuteSuccess(this.source);
                } else {
                    action.onExecuteFail(this.source);
                }
            }

            if (!this.source.isCreative()) {
                this.source.setExperience(this.source.getExperience(), this.source.getExperienceLevel() - event.getCost());
            }
            this.source.level.addLevelEvent(anvilInventory.getHolder(), LevelEventPacket.EVENT_SOUND_ANVIL_USE);
            return true;
        } else if (inventory instanceof GrindstoneInventory) {
            GrindstoneInventory grindstoneInventory = (GrindstoneInventory) inventory;

            for (InventoryAction action : this.actions) {
                if (action.execute(this.source)) {
                    action.onExecuteSuccess(this.source);
                } else {
                    action.onExecuteFail(this.source);
                }
            }

            // we don't use SpawnExperienceOrbPacket
            int xp = 0;
            if (hasInput()) {
                for (Enchantment ench : inputItem.getEnchantments()) {
                    if (ench.isCurse() || ench.getLevel() <= 0) {
                        continue;
                    }
                    int min = Mth.ceil(ench.getMinEnchantAbility(ench.getLevel()) / 2.0);
                    xp += ThreadLocalRandom.current().nextInt(min, min * 2);
                }
            }
            if (hasMaterial()) {
                for (Enchantment ench : materialItem.getEnchantments()) {
                    if (ench.isCurse() || ench.getLevel() <= 0) {
                        continue;
                    }
                    int min = Mth.ceil(ench.getMinEnchantAbility(ench.getLevel()) / 2.0);
                    xp += ThreadLocalRandom.current().nextInt(min, min * 2);
                }
            }
            if (xp != 0) {
                source.level.dropExpOrb(source, xp);
            }
            return true;
        } else if (inventory instanceof SmithingTableInventory) {
            SmithingTableInventory smithingInventory = (SmithingTableInventory) inventory;

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
        super.addAction(action);
        if (action instanceof RepairItemAction) {
            switch (((RepairItemAction) action).getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT:
                    this.inputItem = action.getTargetItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT:
                    this.outputItem = action.getSourceItem();
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL:
                    this.materialItem = action.getTargetItem();
                    break;
//                case NetworkInventoryAction.SOURCE_TYPE_ANVIL_OUTPUT:
//                    this.templateItem = action.getTargetItem();
//                    break;
            }
        }
    }

    private boolean checkRecipeValid() {
        if (this.cost < 0 || this.outputItem.getRepairCost() < this.inputItem.getRepairCost()) {
            return false;
        }
        if (this.isMapRecipe()) {
            return this.matchMapRecipe();
        } else if (this.isEnchantedBookCombine()) {
            return this.matchEnchantedBookRecipe();
        }
        //TODO: Check item has valid enchantments
        return (this.hasMaterial() && (!(this.materialItem instanceof ItemDurable) || this.materialItem.getId() == this.inputItem.getId()
                && this.materialItem.getCount() == 1 && this.inputItem.getCount() == 1) || this.inputItem.equals(this.outputItem, true, false)
                && this.inputItem.getCount() == this.outputItem.getCount()) && (this.cost < 40 || this.source.isCreative());
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

    private boolean checkRenameValid() {
        return this.inputItem.getCustomName().equals(this.outputItem.getCustomName())
                || this.outputItem.getCustomName().length() <= 30 && (this.cost > 0 || this.source.isCreative());
    }

    private boolean isEnchantedBookCombine() {
        return this.inputItem.getId() == Item.ENCHANTED_BOOK && this.outputItem.getId() == Item.ENCHANTED_BOOK
                && this.hasMaterial() && this.materialItem.getId() == Item.ENCHANTED_BOOK;
    }

    private boolean matchEnchantedBookRecipe() {
        if (!this.materialItem.hasEnchantments()) {
            return false;
        }

        for (Enchantment ench : this.inputItem.getEnchantments()) {
            if (this.outputItem.getEnchantment(ench.getId()) == null) {
                return false;
            }
        }
        for (Enchantment ench : this.materialItem.getEnchantments()) {
            if (this.outputItem.getEnchantment(ench.getId()) == null) {
                return false;
            }
        }

        boolean combine = false;
        for (Enchantment ench : this.outputItem.getEnchantments()) {
            if (ench.getLevel() > ench.getMaxLevel()) {
                return false;
            }
            Enchantment inputEnch = this.inputItem.getEnchantment(ench.getId());
            Enchantment materialEnch = this.materialItem.getEnchantment(ench.getId());
            if (inputEnch == null && materialEnch == null) {
                return false;
            } else if (inputEnch != null && materialEnch != null) {
                if (inputEnch.getLevel() > materialEnch.getLevel()) {
                    //return false;
                } else if (inputEnch.getLevel() < materialEnch.getLevel()) {
                    if (ench.getLevel() != materialEnch.getLevel()) {
                        return false;
                    }
                    combine = true;
                } else if (inputEnch.getLevel() == materialEnch.getLevel() && inputEnch.getLevel() + 1 <= ench.getMaxLevel()) {
                    if (ench.getLevel() != inputEnch.getLevel() + 1) {
                        return false;
                    }
                    combine = true;
                }
            } else if (inputEnch != null) {
                if (inputEnch.getLevel() != ench.getLevel()) {
                    return false;
                }
                combine = true;
            } else {
                if (materialEnch.getLevel() != ench.getLevel()) {
                    return false;
                }
                combine = true;
            }
        }
        return combine;
    }

    private boolean isMapRecipe() {
        return this.hasMaterial() && (this.inputItem.getId() == Item.FILLED_MAP || this.inputItem.getId() == Item.EMPTY_MAP)
                && (this.materialItem.getId() == Item.EMPTY_MAP || this.materialItem.getId() == Item.PAPER || this.materialItem.getId() == Item.COMPASS);
    }

    private boolean matchMapRecipe() {
        if (this.inputItem.getId() == Item.EMPTY_MAP) {
            return this.inputItem.getDamage() != 2 && this.materialItem.getId() == Item.COMPASS // locator
                    && this.outputItem.getId() == Item.EMPTY_MAP && this.outputItem.getDamage() == 2 && this.outputItem.getCount() == 1;
        } else if (this.inputItem.getId() == Item.FILLED_MAP) {
            if (this.materialItem.getId() == Item.COMPASS) { // locator
                return this.inputItem.getDamage() < 2 && this.outputItem.getId() == Item.FILLED_MAP
                        && this.outputItem.getDamage() == 2 && this.outputItem.getCount() == 1;
            } else if (this.materialItem.getId() == Item.EMPTY_MAP) { // clone
                return this.outputItem.getId() == Item.FILLED_MAP && this.outputItem.getDamage() == this.inputItem.getDamage() && this.outputItem.getCount() == 2;
            } else if (this.materialItem.getId() == Item.PAPER && this.materialItem.getCount() >= 8) { // zoom out
                return this.inputItem.getDamage() < 3 && this.outputItem.getId() == Item.FILLED_MAP
                        && this.outputItem.getDamage() == this.inputItem.getDamage() && this.outputItem.getCount() == 1;
            }
        }
        return false;
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

    public static boolean checkForRepairItemPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof RepairItemAction) {
                return true;
            }
        }
        return false;
    }
}
