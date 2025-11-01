package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTable;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.LootTables;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@ToString(callSuper = true)
public class FillContainerFunction extends LootItemFunction {
    @JsonProperty("loot_table")
    private final String lootTableName;

    private transient Optional<LootTable> lootTable;

    @JsonCreator
    public FillContainerFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("loot_table")
            String lootTableName
    ) {
        super(conditions);
        this.lootTableName = lootTableName;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (lootTable == null) {
            lootTable = Optional.ofNullable(LootTables.lookupByName(lootTableName));
        }
        lootTable.ifPresent(table -> {
            List<Item> items = table.getRandomItems(random, context);
            if (items.isEmpty()) {
                return;
            }
            ListTag<CompoundTag> itemsTag = new ListTag<>();
            for (int i = 0; i < items.size(); i++) {
                itemsTag.addCompound(NBTIO.putItemHelper(items.get(i), i));
            }
            item.setCompoundTag(item.getOrCreateNamedTag().putList("Items", itemsTag));
        });
        return item;
    }
}
