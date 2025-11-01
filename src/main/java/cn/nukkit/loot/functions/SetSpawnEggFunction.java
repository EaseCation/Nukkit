package cn.nukkit.loot.functions;

import cn.nukkit.entity.Entities;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import javax.annotation.Nullable;

@ToString(callSuper = true)
public class SetSpawnEggFunction extends LootItemFunction {
    @Nullable
    private final String id;

    private transient Integer parsedType;

    @JsonCreator
    public SetSpawnEggFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("id")
            String id
    ) {
        super(conditions);
        this.id = id;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (item.is(Item.SPAWN_EGG)) {
            if (id != null) {
                if (parsedType == null) {
                    parsedType = Entities.getTypeByIdentifier(id);
                }
                item.setDamage(parsedType.intValue());
            } else {
                int data = 0;
                Entity entity = context.getThisEntity();
                if (entity != null) {
                    int type = entity.getNetworkId();
                    if (type >= 0) {
                        data = type;
                    }
                }
                item.setDamage(data);
            }
        }
        return item;
    }
}
