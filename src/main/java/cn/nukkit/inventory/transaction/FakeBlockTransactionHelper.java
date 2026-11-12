package cn.nukkit.inventory.transaction;

import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;

import java.util.ArrayList;
import java.util.List;

public final class FakeBlockTransactionHelper {
    private FakeBlockTransactionHelper() {
    }

    public static boolean matches(List<Item> actualInputs, List<ObjectBooleanPair<Item>> actualOutputs, List<Item> expectedInputs, List<Item> expectedOutputs) {
        // 实际投入加预期产物必须等于实际产物加预期投入，允许输入原样返还后在净差额中抵消。
        List<Item> available = new ArrayList<>(actualInputs.size() + expectedOutputs.size());
        available.addAll(actualInputs);
        available.addAll(expectedOutputs);
        List<Item> required = new ArrayList<>(actualOutputs.size() + expectedInputs.size());
        for (ObjectBooleanPair<Item> output : actualOutputs) {
            required.add(output.left());
        }
        required.addAll(expectedInputs);
        return matchesItems(available, required);
    }

    public static boolean matchesItems(List<Item> available, List<Item> required) {
        int[] remaining = new int[available.size()];
        for (int index = 0; index < available.size(); index++) {
            Item item = available.get(index);
            if (item.getCount() < 0) {
                return false;
            }
            remaining[index] = item.isNull() ? 0 : item.getCount();
        }
        for (Item item : required) {
            if (item.getCount() < 0) {
                return false;
            }
            if (item.isNull()) {
                continue;
            }
            int needed = item.getCount();
            for (int index = 0; index < available.size() && needed > 0; index++) {
                if (remaining[index] <= 0 || !item.equals(available.get(index), true, true, true)) {
                    continue;
                }
                int taken = Math.min(needed, remaining[index]);
                needed -= taken;
                remaining[index] -= taken;
            }
            if (needed != 0) {
                return false;
            }
        }
        for (int count : remaining) {
            if (count != 0) {
                return false;
            }
        }
        return true;
    }
}
