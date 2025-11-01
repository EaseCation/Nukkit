package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.ToString;

import javax.annotation.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "function")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SetItemCountFunction.class, names = {"set_count", "minecraft:set_count"}),
        @JsonSubTypes.Type(value = SetItemDataFunction.class, names = {"set_data", "minecraft:set_data"}),
        @JsonSubTypes.Type(value = SetItemDamageFunction.class, names = {"set_damage", "minecraft:set_damage"}),
        @JsonSubTypes.Type(value = LootingEnchantFunction.class, names = {"looting_enchant", "minecraft:looting_enchant"}),
        @JsonSubTypes.Type(value = EnchantWithLevelsFunction.class, names = {"enchant_with_levels", "minecraft:enchant_with_levels"}),
        @JsonSubTypes.Type(value = EnchantBookForTradingFunction.class, names = {"enchant_book_for_trading", "minecraft:enchant_book_for_trading"}),
        @JsonSubTypes.Type(value = EnchantRandomlyFunction.class, names = {"enchant_randomly", "minecraft:enchant_randomly"}),
        @JsonSubTypes.Type(value = SmeltItemFunction.class, names = {"furnace_smelt", "minecraft:furnace_smelt"}),
        @JsonSubTypes.Type(value = SetDataFromColorIndexFunction.class, names = {"set_data_from_color_index", "minecraft:set_data_from_color_index"}),
        @JsonSubTypes.Type(value = EnchantRandomEquipmentFunction.class, names = {"enchant_random_gear", "minecraft:enchant_random_gear"}),
        @JsonSubTypes.Type(value = RandomAuxValueFunction.class, names = {"random_aux_value", "minecraft:random_aux_value"}),
        @JsonSubTypes.Type(value = RandomBlockStateFunction.class, names = {"random_block_state", "minecraft:random_block_state"}),
        @JsonSubTypes.Type(value = RandomDyeFunction.class, names = {"random_dye", "minecraft:random_dye"}),
        @JsonSubTypes.Type(value = ExplorationMapFunction.class, names = {"exploration_map", "minecraft:exploration_map"}),
        @JsonSubTypes.Type(value = SetBannerDetailsFunction.class, names = {"set_banner_details", "minecraft:set_banner_details"}),
        @JsonSubTypes.Type(value = ExplosionDecayFunction.class, names = {"explosion_decay", "minecraft:explosion_decay"}),
        @JsonSubTypes.Type(value = SetItemNameFunction.class, names = {"set_name", "minecraft:set_name"}),
        @JsonSubTypes.Type(value = SetItemLoreFunction.class, names = {"set_lore", "minecraft:set_lore"}),
        @JsonSubTypes.Type(value = SpecificEnchantFunction.class, names = {"specific_enchants", "minecraft:specific_enchants"}),
        @JsonSubTypes.Type(value = FillContainerFunction.class, names = {"fill_container", "minecraft:fill_container"}),
        @JsonSubTypes.Type(value = SetSpawnEggFunction.class, names = {"set_actor_id", "minecraft:set_actor_id"}),
        @JsonSubTypes.Type(value = SetBookContentsFunction.class, names = {"set_book_contents", "minecraft:set_book_contents"}),
        @JsonSubTypes.Type(value = SetStewEffectFunction.class, names = {"set_stew_effect", "minecraft:set_stew_effect"}),
        @JsonSubTypes.Type(value = SetOminousBottleFunction.class, names = {"set_ominous_bottle_amplifier", "minecraft:set_ominous_bottle_amplifier"}),
        @JsonSubTypes.Type(value = SetArmorTrimFunction.class, names = {"set_armor_trim", "minecraft:set_armor_trim"}),
        @JsonSubTypes.Type(value = SetPotionFunction.class, names = {"set_potion", "minecraft:set_potion"}),
})
@ToString
public abstract class LootItemFunction {
    @Nullable
    private final LootItemCondition[] conditions;

    protected LootItemFunction(LootItemCondition[] conditions) {
        this.conditions = conditions;
    }

    @Nullable
    public LootItemCondition[] getConditions() {
        return conditions;
    }

    public abstract Item apply(Item item, RandomSource random, LootTableContext context);
}
