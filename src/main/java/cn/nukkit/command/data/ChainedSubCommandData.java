package cn.nukkit.command.data;

import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@ToString
public class ChainedSubCommandData {
    public String name;
    public List<ObjectIntPair<String>> values;

    public ChainedSubCommandData(String name, ObjectIntPair<String>... values) {
        this(name, Arrays.asList(values));
    }

    public ChainedSubCommandData(String name, List<ObjectIntPair<String>> values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChainedSubCommandData that)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
