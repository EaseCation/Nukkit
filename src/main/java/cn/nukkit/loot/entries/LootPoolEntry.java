package cn.nukkit.loot.entries;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmptyLootItem.class, name = "empty"),
        @JsonSubTypes.Type(value = LootItem.class, name = "item"),
        @JsonSubTypes.Type(value = LootTableReference.class, name = "loot_table"),
})
@ToString
public abstract class LootPoolEntry {
    protected final int weight;
    protected final int quality;
    @Nullable
    protected final LootItemCondition[] conditions;
    @JsonProperty("pools")
    @Nullable
    protected final LootTableEntry subTable;

    protected LootPoolEntry(int weight, int quality, LootItemCondition[] conditions, LootTableEntry subTable) {
        this.weight = weight;
        this.quality = quality;
        this.conditions = conditions;
        this.subTable = subTable;
    }

    protected abstract boolean createItemInternal(List<Item> output, RandomSource random, LootTableContext context);

    public void createItem(List<Item> output, RandomSource random, LootTableContext context) {
        if (!createItemInternal(output, random, context)) {
            return;
        }

        if (subTable != null) {
            subTable.createItem(output, random, context);
        }
    }

    public LootItemCondition[] getConditions() {
        return conditions;
    }

    public int getWeight(float luck) {
        return Math.max(Mth.floor(weight + quality * luck), 0);
    }
}
