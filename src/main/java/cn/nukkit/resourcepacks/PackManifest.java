package cn.nukkit.resourcepacks;

import cn.nukkit.utils.JsonUtil;
import cn.nukkit.utils.SemVersion;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    public static PackManifest load(InputStream stream) throws IOException {
        return JsonUtil.COMMON_JSON_MAPPER.readValue(stream, PackManifest.class);
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
        private String packScope = "global";
        @JsonProperty("directory_load")
        private boolean directoryLoad;
        @JsonProperty("load_before_game")
        private boolean loadBeforeGame;
        @JsonProperty("lock_template_options")
        private boolean lockTemplateOptions;
        @JsonProperty("population_control")
        private boolean populationControl;
    }

    @Data
    public static class Module {
        private UUID uuid;
        private String description;
        private SemVersion version;
        private String type;
    }

    @Data
    public static class Metadata {
        private List<String> authors;
        private String license;
        private String url;
    }

    @Data
    public static class Dependency {
        private UUID uuid;
        private SemVersion version;
    }

    @Data
    public static class SubPack {
        @JsonProperty("folder_name")
        private String folderName;
        private String name;
        @JsonProperty("memory_tier")
        private int memoryTier;
    }
}
