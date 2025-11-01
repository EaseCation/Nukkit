package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFullNames;
import cn.nukkit.block.Blocks;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.utils.JsonUtil;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Value
public class FlatGeneratorOptions {
    public static final String DEFAULT_FLAT_WORLD_LAYERS = "{\"biome_id\":1,\"block_layers\":[{\"block_name\":\"minecraft:bedrock\",\"count\":1},{\"block_name\":\"minecraft:dirt\",\"count\":2},{\"block_name\":\"minecraft:grass\",\"count\":1}],\"encoding_version\":6,\"structure_options\":null,\"world_version\":\"version.post_1_18\"}";
    public static final String LEGACY_FLAT_WORLD_LAYERS = "{\"biome_id\":1,\"block_layers\":[{\"block_name\":\"minecraft:bedrock\",\"count\":1},{\"block_name\":\"minecraft:dirt\",\"count\":2},{\"block_name\":\"minecraft:grass\",\"count\":1}],\"encoding_version\":6,\"structure_options\":null,\"world_version\":\"version.pre_1_18\"}";

    private static final int CURRENT_ENCODING_VERSION = 6;
    private static final WorldVersion CURRENT_WORLD_VERSION = WorldVersion.POST_1_18;

    public static final FlatGeneratorOptions DEFAULT = load(DEFAULT_FLAT_WORLD_LAYERS);
    public static final FlatGeneratorOptions LEGACY = load(LEGACY_FLAT_WORLD_LAYERS);

    private static final BlockLayer AIR_BUFFER = new BlockLayer(Block.AIR, 0, 64);

    int encodingVersion;
    int biomeId;
    @Default
    BlockLayer[] blockLayers = BlockLayer.EMPTY;
    @Nullable
    JsonNode structureOptions;
    @Default
    WorldVersion worldVersion = CURRENT_WORLD_VERSION;

    int totalLayers;
    @Default
    Populator[] structures = new Populator[0];

    public String save() {
        Map<String, Object> root = new HashMap<>();
        root.put("encoding_version", CURRENT_ENCODING_VERSION);
        root.put("biome_id", biomeId);
        List<Map<String, Object>> blockLayers = new ArrayList<>();
        for (BlockLayer layer : this.blockLayers) {
            Map<String, Object> blockLayer = new HashMap<>();
            String blockName = Blocks.getBlockFullNameById(layer.blockId);
            if (blockName == null) {
                blockName = BlockFullNames.UNKNOWN;
            } else {
                int blockData = layer.blockData;
                if (blockData != 0) {
                    blockLayer.put("block_data", blockData);
                }
            }
            blockLayer.put("block_name", blockName);
            blockLayer.put("count", layer.numLayers);
            blockLayers.add(blockLayer);
        }
        root.put("block_layers", blockLayers);
        root.put("structure_options", structureOptions);
        root.put("world_version", CURRENT_WORLD_VERSION.toString());

        try {
            return JsonUtil.TRUSTED_JSON_MAPPER.writeValueAsString(root);
        } catch (JacksonException e) {
            return DEFAULT_FLAT_WORLD_LAYERS;
        }
    }

    public static FlatGeneratorOptions load(String json) {
        try {
            JsonNode root = JsonUtil.TRUSTED_JSON_MAPPER.readTree(json);
            FlatGeneratorOptionsBuilder builder = builder();

            JsonNode encodingVersionNode = root.get("encoding_version");
            int encodingVersion = encodingVersionNode.asInt();
            builder.encodingVersion(encodingVersion);

            JsonNode biomeIdNode = root.get("biome_id");
            int biomeId = biomeIdNode != null ? Biomes.toValid(biomeIdNode.asInt()) : BiomeID.PLAINS;
            builder.biomeId(biomeId);

            JsonNode structureOptions = root.get("structure_options");
            builder.structureOptions(structureOptions);
            //TODO: structures

            JsonNode worldVersionNode = root.get("world_version");
            WorldVersion worldVersion;
            if (worldVersionNode != null) {
                String version = worldVersionNode.asString();
                worldVersion = WorldVersion.byName(version);
                if (worldVersion == null) { // unknown version
                    worldVersion = WorldVersion.POST_1_18;
                }
            } else {
                worldVersion = WorldVersion.PRE_1_18;
            }
            builder.worldVersion(worldVersion);

            BlockLayer[] blockLayers;
            if (encodingVersion >= 6) {
                blockLayers = parseLayersV6(root, worldVersion == WorldVersion.PRE_1_18);
            } else if (encodingVersion == 5) {
                blockLayers = parseLayersV5(root);
            } else if (encodingVersion == 4) {
                blockLayers = parseLayersV4(root);
            } else {
                blockLayers = parseLayersV3(root);
            }
            builder.blockLayers(blockLayers);

            builder.totalLayers(calculateTotalLayers(blockLayers));

            return builder.build();
        } catch (Exception e) {
            return DEFAULT;
        }
    }

    private static BlockLayer[] parseLayersV6(JsonNode root, boolean shouldHaveAirBuffer) {
        JsonNode blockLayersNode = root.get("block_layers");
        if (blockLayersNode == null) {
            return BlockLayer.EMPTY;
        }

        List<BlockLayer> blockLayers = new ArrayList<>();
        for (JsonNode element : blockLayersNode) {
            JsonNode countNode = element.get("count");
            int count = countNode != null ? countNode.asInt() : 0;
            if (count <= 0) {
                continue;
            }

            int blockFullId = loadLayerBlock(element);
            blockLayers.add(new BlockLayer(Block.getIdFromFullId(blockFullId), Block.getDamageFromFullId(blockFullId), count));
        }

        if (shouldHaveAirBuffer && !blockLayers.isEmpty()) {
            BlockLayer bottomLayer = blockLayers.get(0);
            if (bottomLayer.blockId != AIR_BUFFER.blockId || bottomLayer.numLayers != AIR_BUFFER.numLayers) {
                blockLayers.add(0, AIR_BUFFER);
            }
        }

        return blockLayers.toArray(BlockLayer.EMPTY);
    }

    private static BlockLayer[] parseLayersV5(JsonNode root) {
        JsonNode blockLayersNode = root.get("block_layers");
        if (blockLayersNode == null) {
            return BlockLayer.EMPTY;
        }

        List<BlockLayer> blockLayers = new ArrayList<>();
        for (JsonNode element : blockLayersNode) {
            JsonNode countNode = element.get("count");
            int count = countNode != null ? element.asInt() : 0;
            if (count <= 0) {
                continue;
            }

            int blockFullId = loadLayerBlock(element);
            blockLayers.add(new BlockLayer(Block.getIdFromFullId(blockFullId), Block.getDamageFromFullId(blockFullId), count));
        }

        if (!blockLayers.isEmpty()) {
            blockLayers.add(0, AIR_BUFFER);
        }

        return blockLayers.toArray(BlockLayer.EMPTY);
    }

    private static BlockLayer[] parseLayersV4(JsonNode root) {
        JsonNode blockLayersNode = root.get("block_layers");
        if (blockLayersNode == null) {
            return BlockLayer.EMPTY;
        }

        List<BlockLayer> blockLayers = new ArrayList<>();
        for (JsonNode element : blockLayersNode) {
            JsonNode countNode = element.get("count");
            int count = countNode != null ? element.asInt() : 0;
            if (count <= 0) {
                continue;
            }

            int blockId;
            int blockData;
            JsonNode blockIdNode = element.get("block_id");
            if (blockIdNode != null) {
                JsonNode blockDataNode = element.get("block_data");
                blockData = blockDataNode != null ? blockDataNode.asInt() : 0;

                blockId = blockIdNode.asInt();
                try {
                    Block block = Block.get(blockId, blockData);
                    blockData = block.getDamage();
                } catch (Exception e) {
                    blockId = Block.UNKNOWN;
                    blockData = 0;
                }
            } else {
                int blockFullId = loadLayerBlock(element);
                blockId = Block.getIdFromFullId(blockFullId);
                blockData = Block.getDamageFromFullId(blockFullId);
            }
            blockLayers.add(new BlockLayer(blockId, blockData, count));
        }

        if (!blockLayers.isEmpty()) {
            blockLayers.add(0, AIR_BUFFER);
        }

        return blockLayers.toArray(BlockLayer.EMPTY);
    }

    private static BlockLayer[] parseLayersV3(JsonNode root) {
        return parseLayersV4(root);
    }

    private static int loadLayerBlock(JsonNode element) {
        int blockId;
        int meta;

        JsonNode blockNameNode = element.get("block_name");
        if (blockNameNode != null) {
            JsonNode blockDataNode = element.get("block_data");
            int blockData = blockDataNode != null ? blockDataNode.asInt() : 0;

            String blockName = blockNameNode.asString();
            blockId = Blocks.getIdByBlockName(blockName, true);
            if (blockId == -1) {
                blockId = Block.UNKNOWN;
                meta = 0;
            } else {
                Block block = Block.get(blockId, blockData);
                meta = block.getDamage();
            }
        } else {
            blockId = Block.AIR;
            meta = 0;
        }

        return Block.getFullId(blockId, meta);
    }

    public static int calculateTotalLayers(BlockLayer... blockLayers) {
        int totalLayers = 0;
        for (BlockLayer layer : blockLayers) {
            totalLayers += layer.numLayers;
        }
        return totalLayers;
    }

    public record BlockLayer(int blockId, int blockData, int numLayers) {
        public static final BlockLayer[] EMPTY = new BlockLayer[0];
    }

    public enum WorldVersion {
        PRE_1_18("version.pre_1_18"),
        POST_1_18("version.post_1_18"),
        ;

        private static final WorldVersion[] VALUES = values();
        private static final Map<String, WorldVersion> BY_NAME = new HashMap<>();

        static {
            for (WorldVersion version : VALUES) {
                BY_NAME.put(version.name, version);
            }
        }

        private final String name;

        WorldVersion(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Nullable
        public static WorldVersion byName(String name) {
            return BY_NAME.get(name);
        }
    }
}
