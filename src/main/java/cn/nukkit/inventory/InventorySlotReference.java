package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;

import javax.annotation.Nullable;

/** 一次动作持有的服务端原槽引用；同值替换也不能获得旧动作的写回。 */
public final class InventorySlotReference {
    private final BaseInventory inventory;
    private final int slot;
    @Nullable
    private final Item original;
    @Nullable
    private final Item snapshot;

    InventorySlotReference(BaseInventory inventory, int slot) {
        this.inventory = inventory;
        this.slot = slot;
        this.original = inventory.slots.get(slot);
        this.snapshot = this.original == null ? null : this.original.clone();
    }

    public boolean isSelectedBy(PlayerInventory inventory) {
        return inventory.isHeldItemSource(this.inventory, this.slot);
    }

    public boolean isCurrent() {
        return this.inventory.slots.get(this.slot) == this.original
                && (this.original == null || this.snapshot.equalsExact(this.original));
    }

    /** 返回取得来源时的值副本；开始副作用前仍需检查来源是否有效。 */
    public Item getSnapshot() {
        return this.snapshot == null ? Items.air() : this.snapshot.clone();
    }

    /** 不重新搜索其他槽；事件取消或改写结果不能当作成功扣除。 */
    public boolean consume(int amount) {
        if (amount <= 0 || this.snapshot == null || this.snapshot.getCount() < amount || !this.isCurrent()) {
            return false;
        }
        Item remaining = this.snapshot.clone();
        remaining.setCount(remaining.getCount() - amount);
        if (!this.setItem(remaining)) {
            return false;
        }
        Item actual = this.inventory.getItem(this.slot);
        return remaining.isNull() ? actual.isNull() : remaining.equalsExact(actual);
    }

    /** 仍经过库存事件；成功写入或来源变化后，该引用不能再次消费。 */
    public boolean setItem(Item item) {
        return this.inventory.setItem(this.slot, item, true, this);
    }

    /** 效果发生前的结算还须核对事件最终写入值，不能把取消或改写当作成功。 */
    public boolean setItemAndVerify(Item item) {
        if (!this.setItem(item)) {
            return false;
        }
        Item actual = this.inventory.getItem(this.slot);
        return item.isNull() ? actual.isNull() : item.equalsExact(actual);
    }

    /** 拒绝后回发原库存的当前内容，不跟随后续主副手路由。 */
    public void sendContents(Player player) {
        this.inventory.sendContents(player);
    }
}
