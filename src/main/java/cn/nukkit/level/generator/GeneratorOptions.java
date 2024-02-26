package cn.nukkit.level.generator;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

@Builder
@Value
public class GeneratorOptions {
    @Default
    FlatGeneratorOptions flatOptions = FlatGeneratorOptions.DEFAULT;
    boolean bonusChest;
    @Default
    int biomeOverride = -1;
}
