package cn.nukkit.resourcepacks;

import cn.nukkit.utils.JsonUtil;
import cn.nukkit.utils.SemVersion;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Data
public class PackManifest {
    @JsonProperty("format_version")
    private String formatVersion;

    private Header header;

    private List<Module> modules = Collections.emptyList();

    private Metadata metadata;

    private List<Dependency> dependencies = Collections.emptyList();

    private List<String> capabilities = Collections.emptyList();

    private List<SubPack> subpacks = Collections.emptyList();

    /**
     * @since format version 3 (1.21.110)
     */
    private List<Map<String, ?>> settings = Collections.emptyList();

    public static PackManifest load(InputStream stream) throws IOException {
        PackManifest manifest = JsonUtil.TRUSTED_JSON_MAPPER.readValue(stream, PackManifest.class);
        manifest.validateSubPacks();
        return manifest;
    }

    private void validateSubPacks() {
        if (subpacks == null) {
            throw new IllegalArgumentException("Resource pack subpacks must be an array");
        }
        Set<String> folders = new HashSet<>();
        for (SubPack subPack : subpacks) {
            if (subPack == null) {
                throw new IllegalArgumentException("Resource pack subpack must not be null");
            }
            String folderName = subPack.folderName();
            if (folderName == null || folderName.isBlank() || folderName.equals(".") || folderName.equals("..")
                    || folderName.indexOf('/') >= 0 || folderName.indexOf('\\') >= 0 || folderName.indexOf(':') >= 0
                    || folderName.chars().anyMatch(Character::isISOControl)) {
                throw new IllegalArgumentException("Invalid resource pack subpack folder name: " + folderName);
            }
            if (!folders.add(folderName)) {
                throw new IllegalArgumentException("Duplicate resource pack subpack folder name: " + folderName);
            }
            if (subPack.name() == null || subPack.name().isBlank()) {
                throw new IllegalArgumentException("Resource pack subpack name must not be empty: " + folderName);
            }
            if (subPack.memoryTier() < 0) {
                throw new IllegalArgumentException("Resource pack subpack memory_tier must not be negative: " + folderName);
            }
            Integer performanceTier = subPack.memoryPerformanceTier();
            if (performanceTier != null && (performanceTier < 1 || performanceTier > 5)) {
                throw new IllegalArgumentException("Resource pack subpack memory_performance_tier must be between 1 and 5: " + folderName);
            }
        }
        subpacks = List.copyOf(subpacks);
    }

    public boolean isValid() {
        if (this.formatVersion != null && this.header != null && this.modules != null) {
            return header.description != null &&
                    header.name != null &&
                    header.uuid != null &&
                    header.version != null;
        }
        return false;
    }

    @Data
    public static class Header {
        private String name;
        private String description;
        private UUID uuid;
        @JsonProperty("platform_locked")
        private boolean platformLocked;
        private SemVersion version;
        @JsonProperty("min_engine_version")
        private SemVersion minEngineVersion;
        @JsonProperty("pack_scope")
        private String packScope;
        @JsonProperty("directory_load")
        private boolean directoryLoad;
        @JsonProperty("load_before_game")
        private boolean loadBeforeGame;
        @JsonProperty("lock_template_options")
        private boolean lockTemplateOptions;
        @JsonProperty("population_control")
        private boolean populationControl;
        @JsonProperty("allow_random_seed")
        private boolean allowRandomSeed;
        @JsonProperty("base_game_version")
        private SemVersion baseGameVersion;
    }

    @Data
    public static class Module {
        private UUID uuid;
        private String description;
        private SemVersion version;
        private String type;
        private String language;
    }

    @Data
    public static class Metadata {
        private List<String> authors;
        private String license;
        private String url;
        @JsonProperty("product_type")
        private String productType;
        @JsonProperty("generated_with")
        private Map<String, List<String>> generatedWith;
    }

    @Data
    public static class Dependency {
        private UUID uuid;
        private SemVersion version;
        @JsonProperty("module_name")
        private String moduleName;
    }

    public record SubPack(
            @JsonProperty("folder_name")
            String folderName,
            String name,
            @JsonProperty("memory_tier")
            int memoryTier,
            @Nullable
            @JsonProperty("memory_performance_tier")
            Integer memoryPerformanceTier
    ) {
        @JsonCreator
        public SubPack(
                @JsonProperty("folder_name")
                String folderName,
                @JsonProperty("name")
                String name,
                @JsonProperty("memory_tier")
                int memoryTier,
                @Nullable
                @JsonProperty("memory_performance_tier")
                Integer memoryPerformanceTier
        ) {
            this.folderName = folderName;
            this.name = name;
            this.memoryTier = memoryTier;
            this.memoryPerformanceTier = memoryPerformanceTier;
        }
    }
}
