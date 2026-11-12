package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.EnchantItemEvent;
import cn.nukkit.event.player.PlayerItemEnchantEvent;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.EnchantingAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.item.enchantment.EnchantmentOption;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;

@Getter
@Setter
public class EnchantTransaction extends InventoryTransaction {
    private Item inputItem;
    private Item outputItem;
    private EnchantmentOption option;
    private int cost = -1;
    @Nullable
    private final EnchantInventory authoritativeInventory;
    @Nullable
    private final FakeBlockTransactionContext authoritativeContext;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Nullable
    private Integer eventXpCost;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Nullable
    private Item eventOutputItem;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Nullable
    private SlotChangeAction eventResultAction;

    public EnchantTransaction(Player source, EnchantmentOption option, int cost) {
        super(source);
        this.option = option;
        this.cost = cost;
        authoritativeInventory = source.getCurrentWindow() instanceof EnchantInventory inventory ? inventory : null;
        authoritativeContext = new FakeBlockTransactionContext(authoritativeInventory);
    }

    public EnchantTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
        authoritativeInventory = null;
        authoritativeContext = null;
    }

    @Override
    public boolean canExecute() {
        if (this.source.isSpectator()) {
            return false;
        }
        if (source.isServerAuthoritativeInventoryEnabled()) {
            return canExecuteAuthoritative();
        }

        Inventory inv = getSource().getWindowById(Player.ENCHANT_WINDOW_ID);
        if (inv == null) return false;
        EnchantInventory eInv = (EnchantInventory) inv;
        if (!getSource().isCreative()) {
            if (cost == -1 || !eInv.getReagentSlot().equals(Item.get(Item.LAPIS_LAZULI), true, false) || eInv.getReagentSlot().count < cost)
                return false;
        }
        return (inputItem != null && outputItem != null && inputItem.equals(eInv.getInputSlot(), true, true));
    }

    private boolean canExecuteAuthoritative() {
        if (option == null || cost < 1 || cost > 3 || authoritativeInventory == null
                || source.getCurrentWindow() != authoritativeInventory || authoritativeInventory.getOption(cost - 1) != option
                || actions.isEmpty()) {
            return false;
        }
        inputItem = null;
        outputItem = null;
        List<Item> inputs = new ObjectArrayList<>();
        List<ObjectBooleanPair<Item>> outputs = new ObjectArrayList<>();
        if (!matchItems(outputs, inputs)) {
            return false;
        }

        int lapisSpent = 0;
        for (Item input : inputs) {
            if (input.is(Item.LAPIS_LAZULI)) {
                lapisSpent = input.getCount();
            } else if (inputItem != null) {
                return false;
            } else {
                inputItem = input;
            }
        }

        if (inputItem == null) {
            return false;
        }

        if (outputs.size() != 1) {
            return false;
        }
        outputItem = outputs.getFirst().left();

        Item enchantedInput = EnchantInventory.getOutput(inputItem, option);
        if (enchantedInput == null || !enchantedInput.equalsExact(outputItem)) {
            return false;
        }

        if (!source.isCreative()) {
            if (lapisSpent != cost) {
                return false;
            }

            if (source.getExperienceLevel() < option.cost) {
                return false;
            }
            // XP level cost is intentionally not checked here, as the required level may be lower than the cost, allowing
            // the option to be used with less XP than the cost - in this case, as much XP as possible will be deducted.
        }

        return true;
    }

    @Override
    public boolean execute() {
        if (source.isServerAuthoritativeInventoryEnabled()) {
            return super.execute();
        }

        // This will validate the enchant conditions
        if (this.hasExecuted || !this.canExecute()) {
            source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }
        EnchantInventory inv = (EnchantInventory) getSource().getWindowById(Player.ENCHANT_WINDOW_ID);
        EnchantItemEvent ev = new EnchantItemEvent(inv, inputItem, outputItem, cost, source);
        source.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            source.removeAllWindows(false);
            this.sendInventories();
            // Cancelled by plugin, means handled OK
            return true;
        }
        // This will process all the slot changes
        for (InventoryAction a : this.actions) {
            if (a.execute(source)) {
                a.onExecuteSuccess(source);
            } else {
                a.onExecuteFail(source);
            }
        }

        if (!ev.getNewItem().equals(this.outputItem, true, true)) {
            // Plugin changed item, so the previous slot change is going to be invalid
            // Send the replaced item to the enchant inventory manually
            inv.setItem(0, ev.getNewItem(), true);
        }

        if (!source.isCreative()) {
            source.setExperience(source.getExperience(), source.getExperienceLevel() - ev.getXpCost());
        }
        return true;
    }

    @Override
    protected boolean validateAuthoritativeContext() {
        return authoritativeContext != null && authoritativeContext.isCurrent()
                && (eventXpCost == null || eventXpCost >= 0) && canExecuteAuthoritative();
    }

    @Override
    protected void onAuthoritativeCommit() {
        if (!source.isCreative()) {
            // If the required XP level is less than the XP cost, the option can be selected with less XP than the cost.
            // In this case, as much XP as possible will be taken.
            int level = source.getExperienceLevel();
            int xpCost = eventXpCost != null ? eventXpCost : cost;
            source.setExperience(source.getExperience(), level - Math.min(xpCost, level));
        }
        if (eventOutputItem != null) {
            outputItem = eventOutputItem.clone();
        }
        source.setEnchantmentSeed(ThreadLocalRandom.current().nextInt());
    }

    @Override
    protected boolean callExecuteEvent() {
        if (inputItem == null || outputItem == null || authoritativeInventory == null) {
            return false;
        }

        Item originalOutput = outputItem.clone();
        eventXpCost = null;
        eventOutputItem = null;
        eventResultAction = null;
        PlayerItemEnchantEvent event = new PlayerItemEnchantEvent(source, this, option, inputItem, outputItem, cost);
        source.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || inputItem == null || outputItem == null) {
            return false;
        }
        EnchantItemEvent legacyEvent = new EnchantItemEvent(authoritativeInventory, inputItem.clone(), outputItem.clone(), cost, source);
        source.getServer().getPluginManager().callEvent(legacyEvent);
        if (legacyEvent.isCancelled() || legacyEvent.getXpCost() < 0 || legacyEvent.getNewItem() == null) {
            return false;
        }
        eventXpCost = legacyEvent.getXpCost();
        eventOutputItem = legacyEvent.getNewItem().clone();
        eventResultAction = null;
        if (!eventOutputItem.equalsExact(originalOutput)) {
            for (InventoryAction action : actions) {
                if (action instanceof SlotChangeAction slotChange && action.getTargetItemUnsafe().equalsExact(originalOutput)) {
                    if (eventResultAction != null) {
                        return false;
                    }
                    eventResultAction = slotChange;
                }
            }
            return eventResultAction != null;
        }
        return true;
    }

    @Override
    protected Item getAuthoritativeTargetItem(SlotChangeAction action) {
        return action == eventResultAction ? eventOutputItem : super.getAuthoritativeTargetItem(action);
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof EnchantingAction) {
            switch (((EnchantingAction) action).getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_INPUT:
                    this.inputItem = action.getTargetItem(); // Input sent as newItem
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_OUTPUT:
                    this.outputItem = action.getSourceItem(); // Output sent as oldItem
                    break;
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_MATERIAL:
                    if (action.getTargetItem().equals(Items.air(), false, false)) {
                        this.cost = action.getSourceItem().count;
                    } else {
                        this.cost = action.getSourceItem().count - action.getTargetItem().count;
                    }
                    break;
            }

        }
    }

    public boolean checkForEnchantPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof EnchantingAction) return true;
        }
        return false;
    }
}
