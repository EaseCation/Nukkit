package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBundle;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.ArrayList;
import java.util.List;

public class BundleTransaction extends InventoryTransaction {
    private static final int MAX_EXPANDED_STACKS = 4096;

    public BundleTransaction(Player source) {
        super(source);
    }

    @Override
    public boolean canExecute() {
        if (!source.isServerAuthoritativeInventoryEnabled() || source.isSpectator() || actions.isEmpty()) {
            return false;
        }
        List<Item> inputs = new ArrayList<>();
        List<Item> outputs = new ArrayList<>();
        for (InventoryAction action : actions) {
            if (!action.isValid(source)
                    || !expand(action.getSourceItemUnsafe(), inputs, 0)
                    || !expand(action.getTargetItemUnsafe(), outputs, 0)) {
                return false;
            }
        }
        return FakeBlockTransactionHelper.matchesItems(inputs, outputs);
    }

    private static boolean expand(Item item, List<Item> flattened, int depth) {
        if (depth > ItemBundle.MAX_NESTING_DEPTH || item.getCount() < 0 || flattened.size() >= MAX_EXPANDED_STACKS) {
            return false;
        }
        if (item.isNull()) {
            return true;
        }
        if (!(item instanceof ItemBundle bundle)) {
            flattened.add(item);
            return true;
        }
        Int2ObjectMap<Item> contents = bundle.getContents();
        if (contents == null) {
            return false;
        }
        // 袋壳与袋内物品分别参与守恒，派生重量及会话容器编号不构成可创造的物品属性。
        flattened.add(bundle.getShell());
        for (Item stored : contents.values()) {
            if (!expand(stored, flattened, depth + 1)) {
                return false;
            }
        }
        return true;
    }
}
