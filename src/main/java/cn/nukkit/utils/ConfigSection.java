package cn.nukkit.utils;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;

import java.util.*;

/**
 * Created by fromgate on 26.04.2016.
 */
public class ConfigSection extends LinkedHashMap<String, Object> {

    /**
     * Empty ConfigSection constructor
     */
    public ConfigSection() {
        super();
    }

    /**
     * Constructor of ConfigSection that contains initial key/value data
     *
     * @param key
     * @param value
     */
    public ConfigSection(String key, Object value) {
        this();
        this.set(key, value);
    }

    /**
     * Constructor of ConfigSection, based on values stored in map.
     *
     * @param map
     */
    public ConfigSection(Map<String, Object> map) {
        this();
        if (map == null || map.isEmpty()) return;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                super.put(entry.getKey(), new ConfigSection((Map) value));
            } else if (value instanceof List<?> list) {
                super.put(entry.getKey(), parseList(list));
            } else {
                super.put(entry.getKey(), value);
            }
        }
    }

    /**
     * Constructor of ConfigSection, based on values stored in map.
     *
     * @param map
     */
    public ConfigSection(LinkedHashMap<String, Object> map) {
        this((Map<String, Object>) map);
    }

    private List<?> parseList(List<?> list) {
        List<Object> newList = new ArrayList<>();

        for (Object o : list) {
            if (o instanceof Map) {
                newList.add(new ConfigSection((Map) o));
            } else {
                newList.add(o);
            }
        }

        return newList;
    }

    /**
     * Get root section as LinkedHashMap
     *
     * @return
     */
    public Map<String, Object> getAllMap() {
        return new LinkedHashMap<>(this);
    }

    /**
     * Get new instance of config section
     *
     * @return
     */
    public ConfigSection getAll() {
        return new ConfigSection(this);
    }

    /**
     * Get object by key. If section does not contain value, return null
     */
    public Object get(String key) {
        return this.get(key, null);
    }

    /**
     * Get object by key. If section does not contain value, return default value
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public <T> T get(String key, T defaultValue) {
        if (key == null || key.isEmpty()) {
            return defaultValue;
        }
        Object value = super.get(key);
        if (value != null) {
            return (T) value;
        }
        String[] keys = key.split("\\.", 2);
        value = super.get(keys[0]);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof ConfigSection section) {
            return section.get(keys[1], defaultValue);
        }
        return defaultValue;
    }

    public Object getValue(String key) {
        return this.getValue(key, null);
    }

    public Object getValue(String key, Object defaultValue) {
        if (key == null || key.isEmpty()) {
            return defaultValue;
        }
        Object value = super.get(key);
        if (value != null) {
            return value;
        }
        String[] keys = key.split("\\.", 2);
        value = super.get(keys[0]);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof ConfigSection section) {
            return section.getValue(keys[1], defaultValue);
        }
        return defaultValue;
    }

    /**
     * Store value into config section
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        String[] subKeys = key.split("\\.", 2);
        if (subKeys.length > 1) {
            String path = subKeys[0];
            ConfigSection childSection;
            if (super.get(path) instanceof ConfigSection section) {
                childSection = section;
            } else {
                childSection = new ConfigSection();
                super.put(path, childSection);
            }
            childSection.set(subKeys[1], value);
        } else {
            super.put(subKeys[0], value);
        }
    }

    /**
     * Check type of section element defined by key. Return true this element is ConfigSection
     *
     * @param key
     * @return
     */
    public boolean isSection(String key) {
        Object value = this.getValue(key);
        return value instanceof ConfigSection;
    }

    /**
     * Get config section element defined by key
     *
     * @param key
     * @return
     */
    public ConfigSection getSection(String key) {
        Object value = this.getValue(key, null);
        return value instanceof ConfigSection section ? section : new ConfigSection();
    }

    //@formatter:off

    /**
     * Get all ConfigSections in root path.
     * Example config:
     *  a1:
     *    b1:
     *      c1:
     *      c2:
     *  a2:
     *    b2:
     *      c3:
     *      c4:
     *  a3: true
     *  a4: "hello"
     *  a5: 100
     * <p>
     * getSections() will return new ConfigSection, that contains sections a1 and a2 only.
     *
     * @return
     */
    //@formatter:on
    public ConfigSection getSections() {
        return getSections(null);
    }

    /**
     * Get sections (and only sections) from provided path
     *
     * @param key - config section path, if null or empty root path will used.
     * @return
     */
    public ConfigSection getSections(String key) {
        ConfigSection sections = new ConfigSection();
        ConfigSection parent = key == null || key.isEmpty() ? this.getAll() : getSection(key);
        if (parent == null) return sections;
        parent.forEach((key1, value) -> {
            if (value instanceof ConfigSection)
                sections.put(key1, value);
        });
        return sections;
    }

    /**
     * Get int value of config section element
     *
     * @param key - key (inside) current section (default value equals to 0)
     * @return
     */
    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    /**
     * Get int value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    public int getInt(String key, int defaultValue) {
        Object value = this.getValue(key, defaultValue);
        return value instanceof Number number ? number.intValue() : defaultValue;
    }

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key
     * @return
     */
    public boolean isInt(String key) {
        Object val = getValue(key);
        return val instanceof Integer;
    }

    /**
     * Get long value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public long getLong(String key) {
        return this.getLong(key, 0);
    }

    /**
     * Get long value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    public long getLong(String key, long defaultValue) {
        Object value = this.getValue(key, defaultValue);
        return value instanceof Number number ? number.longValue() : defaultValue;
    }

    /**
     * Check type of section element defined by key. Return true this element is Long
     *
     * @param key
     * @return
     */
    public boolean isLong(String key) {
        Object val = getValue(key);
        return val instanceof Long;
    }

    /**
     * Get double value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public double getDouble(String key) {
        return this.getDouble(key, 0);
    }

    /**
     * Get double value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    public double getDouble(String key, double defaultValue) {
        Object value = this.getValue(key, defaultValue);
        return value instanceof Number number ? number.doubleValue() : defaultValue;
    }

    /**
     * Check type of section element defined by key. Return true this element is Double
     *
     * @param key
     * @return
     */
    public boolean isDouble(String key) {
        Object val = getValue(key);
        return val instanceof Double;
    }

    /**
     * Get String value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public String getString(String key) {
        return this.getString(key, "");
    }

    /**
     * Get String value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    public String getString(String key, String defaultValue) {
        Object value = this.getValue(key, defaultValue);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Check type of section element defined by key. Return true this element is String
     *
     * @param key
     * @return
     */
    public boolean isString(String key) {
        Object val = getValue(key);
        return val instanceof String;
    }

    /**
     * Get boolean value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    /**
     * Get boolean value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = this.getValue(key, defaultValue);
        return value instanceof Boolean bool ? bool : defaultValue;
    }

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key
     * @return
     */
    public boolean isBoolean(String key) {
        Object val = getValue(key);
        return val instanceof Boolean;
    }

    /**
     * Get List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public List<?> getList(String key) {
        return this.getList(key, null);
    }

    /**
     * Get List value of config section element
     *
     * @param key         - key (inside) current section
     * @param defaultList - default value that will returned if section element is not exists
     * @return
     */
    public List<?> getList(String key, List<?> defaultList) {
        Object value = this.getValue(key, defaultList);
        return value instanceof List<?> list ? list : defaultList;
    }

    /**
     * Check type of section element defined by key. Return true this element is List
     *
     * @param key
     * @return
     */
    public boolean isList(String key) {
        Object val = getValue(key);
        return val instanceof List;
    }

    /**
     * Get String List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public List<String> getStringList(String key) {
        List<?> value = this.getList(key);

        if (value == null) {
            return new ArrayList<>(0);
        }

        List<String> result = new ArrayList<>();

        for (Object o : value) {
            if (o instanceof String || o instanceof Number || o instanceof Boolean || o instanceof Character) {
                result.add(String.valueOf(o));
            }
        }

        return result;
    }

    /**
     * Get Integer List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public IntList getIntegerList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new IntArrayList(0);
        }

        IntList result = new IntArrayList();

        for (Object object : list) {
            if (object instanceof Number number) {
                result.add(number.intValue());
            } else if (object instanceof String string) {
                try {
                    result.add(Integer.parseInt(string));
                } catch (NumberFormatException ignored) {
                }
            } else if (object instanceof Character character) {
                result.add((int) character);
            }
        }

        return result;
    }

    /**
     * Get Boolean List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public BooleanList getBooleanList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new BooleanArrayList(0);
        }

        BooleanList result = new BooleanArrayList();

        for (Object object : list) {
            if (object instanceof Boolean bool) {
                result.add(bool);
            } else if (object instanceof String) {
                if ("true".equals(object)) {
                    result.add(true);
                } else if ("false".equals(object)) {
                    result.add(false);
                }
            }
        }

        return result;
    }

    /**
     * Get Double List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public DoubleList getDoubleList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new DoubleArrayList(0);
        }

        DoubleList result = new DoubleArrayList();

        for (Object object : list) {
            if (object instanceof Number number) {
                result.add(number.doubleValue());
            } else if (object instanceof String string) {
                try {
                    result.add(Double.parseDouble(string));
                } catch (NumberFormatException ignored) {
                }
            } else if (object instanceof Character character) {
                result.add((double) character);
            }
        }

        return result;
    }

    /**
     * Get Float List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public FloatList getFloatList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new FloatArrayList(0);
        }

        FloatList result = new FloatArrayList();

        for (Object object : list) {
            if (object instanceof Number number) {
                result.add(number.floatValue());
            } else if (object instanceof String string) {
                try {
                    result.add(Float.parseFloat(string));
                } catch (NumberFormatException ignored) {
                }
            } else if (object instanceof Character character) {
                result.add((float) character);
            }
        }

        return result;
    }

    /**
     * Get Long List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public LongList getLongList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new LongArrayList(0);
        }

        LongList result = new LongArrayList();

        for (Object object : list) {
            if (object instanceof Number number) {
                result.add(number.longValue());
            } else if (object instanceof String string) {
                try {
                    result.add(Long.parseLong(string));
                } catch (NumberFormatException ignored) {
                }
            } else if (object instanceof Character character) {
                result.add((long) character);
            }
        }

        return result;
    }

    /**
     * Get Byte List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public ByteList getByteList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new ByteArrayList(0);
        }

        ByteList result = new ByteArrayList();

        for (Object object : list) {
            if (object instanceof Number number) {
                result.add(number.byteValue());
            } else if (object instanceof String string) {
                try {
                    result.add(Byte.parseByte(string));
                } catch (NumberFormatException ignored) {
                }
            } else if (object instanceof Character character) {
                result.add((byte) character.charValue());
            }
        }

        return result;
    }

    /**
     * Get Character List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public CharList getCharacterList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new CharArrayList(0);
        }

        CharList result = new CharArrayList();

        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            } else if (object instanceof String str) {
                if (str.length() == 1) {
                    result.add(str.charAt(0));
                }
            } else if (object instanceof Number number) {
                result.add((char) number.intValue());
            }
        }

        return result;
    }

    /**
     * Get Short List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public ShortList getShortList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new ShortArrayList(0);
        }

        ShortList result = new ShortArrayList();

        for (Object object : list) {
            if (object instanceof Number number) {
                result.add(number.shortValue());
            } else if (object instanceof String string) {
                try {
                    result.add(Short.parseShort(string));
                } catch (NumberFormatException ignored) {
                }
            } else if (object instanceof Character character) {
                result.add((short) character.charValue());
            }
        }

        return result;
    }

    /**
     * Get Map List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    public List<Map<?, ?>> getMapList(String key) {
        List<?> list = getList(key);
        List<Map<?, ?>> result = new ArrayList<>();

        if (list == null) {
            return result;
        }

        for (Object object : list) {
            if (object instanceof Map<?, ?> map) {
                result.add(map);
            }
        }

        return result;
    }

    /**
     * Check existence of config section element
     *
     * @param key
     * @param ignoreCase
     * @return
     */
    public boolean exists(String key, boolean ignoreCase) {
        if (ignoreCase) key = key.toLowerCase();
        for (String existKey : this.getKeys(true)) {
            if (ignoreCase) existKey = existKey.toLowerCase();
            if (existKey.equals(key)) return true;
        }
        return false;
    }

    /**
     * Check existence of config section element
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return exists(key, false);
    }

    /**
     * Remove config section element
     *
     * @param key
     */
    public void remove(String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        Object value = super.remove(key);
        if (value != null) {
            return;
        }
        if (this.containsKey(".")) {
            String[] keys = key.split("\\.", 2);
            value = super.get(keys[0]);
            if (value instanceof ConfigSection section) {
                section.remove(keys[1]);
            }
        }
    }

    /**
     * Get all keys
     *
     * @param child - true = include child keys
     * @return
     */
    public Set<String> getKeys(boolean child) {
        Set<String> keys = new LinkedHashSet<>();
        this.forEach((key, value) -> {
            keys.add(key);
            if (child && value instanceof ConfigSection section) {
                section.getKeys(true).forEach(childKey -> keys.add(key + "." + childKey));
            }
        });
        return keys;
    }

    /**
     * Get all keys
     *
     * @return
     */
    public Set<String> getKeys() {
        return this.getKeys(true);
    }
}