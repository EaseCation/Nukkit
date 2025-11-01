package cn.nukkit.utils;

import lombok.Value;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;

@JsonSerialize(using = SemVersion.Serializer.class)
@JsonDeserialize(using = SemVersion.Deserializer.class)
@Value
public class SemVersion {
    int major;
    int minor;
    int patch;

    public SemVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    static class Serializer extends StdSerializer<SemVersion> {
        protected Serializer() {
            super(SemVersion.class);
        }

        @Override
        public void serialize(SemVersion value, JsonGenerator generator, SerializationContext context) throws JacksonException {
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
            int[] version = parser.readValueAs(int[].class);
            return new SemVersion(version[0], version[1], version[2]);
        }
    }
}
