package cn.nukkit.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonSerialize(using = SemVersion.Serializer.class)
@JsonDeserialize(using = SemVersion.Deserializer.class)
@Value
public class SemVersion {
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)"
            + "(?:-([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?"
            + "(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?");

    int major;
    int minor;
    int patch;
    String preRelease;
    String buildMetadata;

    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    boolean stringRepresentation;

    public SemVersion(int major, int minor, int patch) {
        this(major, minor, patch, "", "", false);
    }

    private SemVersion(int major, int minor, int patch, String preRelease, String buildMetadata, boolean stringRepresentation) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.buildMetadata = buildMetadata;
        this.stringRepresentation = stringRepresentation;
    }

    @Override
    public String toString() {
        String version = major + "." + minor + "." + patch;
        if (!preRelease.isEmpty()) {
            version += "-" + preRelease;
        }
        if (!buildMetadata.isEmpty()) {
            version += "+" + buildMetadata;
        }
        return version;
    }

    static class Serializer extends StdSerializer<SemVersion> {
        protected Serializer() {
            super(SemVersion.class);
        }

        @Override
        public void serialize(SemVersion value, JsonGenerator generator, SerializationContext context) throws JacksonException {
            if (value.stringRepresentation) {
                generator.writeString(value.toString());
                return;
            }

            generator.writeStartArray();
            generator.writeNumber(value.major);
            generator.writeNumber(value.minor);
            generator.writeNumber(value.patch);
            generator.writeEndArray();
        }
    }

    static class Deserializer extends StdDeserializer<SemVersion> {
        protected Deserializer() {
            super(SemVersion.class);
        }

        @Override
        public SemVersion deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            if (parser.currentToken() == JsonToken.VALUE_STRING) {
                String text = parser.getString();
                Matcher matcher = VERSION_PATTERN.matcher(text);
                if (!matcher.matches()) {
                    return context.reportInputMismatch(SemVersion.class, "Invalid semantic version: %s", text);
                }

                String preRelease = matcher.group(4);
                String buildMetadata = matcher.group(5);
                if (preRelease != null) {
                    for (String identifier : preRelease.split("\\.")) {
                        if (identifier.length() > 1 && identifier.charAt(0) == '0' && identifier.chars().allMatch(character -> character >= '0' && character <= '9')) {
                            return context.reportInputMismatch(SemVersion.class, "Numeric semantic version prerelease identifiers must not have leading zeros: %s", text);
                        }
                    }
                }

                return new SemVersion(
                        parseComponent(matcher.group(1), context),
                        parseComponent(matcher.group(2), context),
                        parseComponent(matcher.group(3), context),
                        preRelease == null ? "" : preRelease,
                        buildMetadata == null ? "" : buildMetadata, true);
            }

            if (parser.currentToken() != JsonToken.START_ARRAY) {
                return context.reportInputMismatch(SemVersion.class, "Expected a semantic version string or an integer array");
            }
            int[] version = parser.readValueAs(int[].class);
            if (version.length != 3) {
                return context.reportInputMismatch(SemVersion.class, "Semantic version arrays must contain exactly three components");
            }
            return new SemVersion(version[0], version[1], version[2]);
        }

        private static int parseComponent(String component, DeserializationContext context) throws JacksonException {
            String maximum = Integer.toString(Integer.MAX_VALUE);
            if (component.length() > maximum.length() || component.length() == maximum.length() && component.compareTo(maximum) > 0) {
                return context.reportInputMismatch(SemVersion.class, "Semantic version component exceeds the integer range: %s", component);
            }
            return Integer.parseInt(component);
        }
    }
}
