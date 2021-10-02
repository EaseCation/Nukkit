package cn.nukkit.command.data;

import cn.nukkit.NukkitSharedConstants;
import com.google.common.base.CaseFormat;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author CreeperFace
 */
@Log4j2
@ToString
public class CommandEnum {

    private String name;
    private List<String> values;

    public CommandEnum(String name, List<String> values) {
        this.name = bedrockStyleEnumName(name);
        this.values = values.stream()
                .map(String::toLowerCase) // Keywords in commands need to be lower case.
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public int hashCode() {
        return name.hashCode();
    }

    private static String bedrockStyleEnumName(String name) {
        Objects.requireNonNull(name);
        if (NukkitSharedConstants.ENABLE_COMMAND_PARAMETER_NAME_WARNING) {
            if (!name.matches("[a-zA-Z]+")) {
                log.warn("Unexpected command enum name: {}", name, new Throwable());
            }
        }
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name.trim().replace(" ", "_"));
    }
}
