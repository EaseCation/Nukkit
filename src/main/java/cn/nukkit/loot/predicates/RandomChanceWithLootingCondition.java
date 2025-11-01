package cn.nukkit.loot.predicates;

import cn.nukkit.Player;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RandomChanceWithLootingCondition(
        float chance,
        @JsonProperty("looting_multiplier")
        float lootingMultiplier
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        int looting;
        if (context.getKillerEntity() instanceof Player player) {
            looting = player.getInventory().getItemInHand().getValidEnchantmentLevel(Enchantment.LOOTING);
        } else {
            looting = 0;
        }
        return chance + looting * lootingMultiplier > random.nextFloat();
    }
}
