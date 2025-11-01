package cn.nukkit.loot.functions;

import cn.nukkit.block.Block;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.BlockStates;
import cn.nukkit.block.state.IntegerBlockState;
import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.providers.NumberProvider;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@ToString(callSuper = true)
public class RandomBlockStateFunction extends LootItemFunction {
    @JsonProperty("block_state")
    private final String blockState;
    private final NumberProvider values;

    private transient Optional<List<BlockState>> parsedBlockStates;

    @JsonCreator
    public RandomBlockStateFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("block_state")
            String blockState,
            @JsonProperty("values")
            NumberProvider values
    ) {
        super(conditions);
        this.blockState = blockState;
        this.values = values;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (parsedBlockStates == null) {
            parsedBlockStates = Optional.ofNullable(BlockStates.get(blockState));
        }
        parsedBlockStates.ifPresent(states -> {
            if (values == null) {
                return;
            }
            Block block = item.getBlock();
            if (block.isAir()) {
                return;
            }
            int oldData = block.getDamage();
            for (BlockState state : states) {
                if (!block.hasState(state)) {
                    continue;
                }
                if (state instanceof IntegerBlockState intState) {
                    block.setState(intState, values.getInt(random));
                }
                break;
            }
            if (block.getDamage() != oldData) {
                item.setDamage(block.getDamage());
            }
        });
        return item;
    }
}
