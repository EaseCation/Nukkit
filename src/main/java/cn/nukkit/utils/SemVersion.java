package cn.nukkit.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Value;

import java.io.IOException;

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
        public void serialize(SemVersion value, JsonGenerator generator, SerializerProvider provider) throws IOException {
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
        public SemVersion deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            int[] version = parser.readValueAs(int[].class);
            return new SemVersion(version[0], version[1], version[2]);
        }
    }
}
