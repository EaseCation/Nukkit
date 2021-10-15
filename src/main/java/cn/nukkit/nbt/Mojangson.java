package cn.nukkit.nbt;

import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.IntArrayTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.regex.Pattern;

public class Mojangson {
	private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", Pattern.CASE_INSENSITIVE);
	private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", Pattern.CASE_INSENSITIVE);
	private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", Pattern.CASE_INSENSITIVE);
	private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", Pattern.CASE_INSENSITIVE);
	private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", Pattern.CASE_INSENSITIVE);
	private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", Pattern.CASE_INSENSITIVE);
	private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
	private final String string;
	private int cursor;

	private Mojangson(String s) {
		this.string = s;
	}

	public static CompoundTag read(String s) throws IllegalArgumentException {
		return new Mojangson(s).readSingleCompound();
	}

	private static String slice(String s, int pos) {
		StringBuilder stringbuilder = new StringBuilder();
		int length = Math.min(s.length(), pos);
		if (length > 35) stringbuilder.append("...");

		stringbuilder.append(s, Math.max(0, length - 35), length);
		stringbuilder.append("<--[HERE]");
		return stringbuilder.toString();
	}

	private CompoundTag readSingleCompound() throws IllegalArgumentException {
		CompoundTag tag = this.readCompound();
		this.skipWhitespace();

		if (!this.canRead()) return tag;

		this.cursor++;
		throw this.exception("Trailing data found");
	}

	private String readKey() throws IllegalArgumentException {
		this.skipWhitespace();

		if (!this.canRead()) throw this.exception("Expected key");
		return this.peek() == '"' ? this.readQuotedString() : this.readString();
	}

	private IllegalArgumentException exception(String message) {
		return new IllegalArgumentException(message + " at: " + slice(this.string, this.cursor + 1));
	}

	private Tag readTypedValue() throws IllegalArgumentException {
		this.skipWhitespace();

		if (this.peek() == '"') return new StringTag("", this.readQuotedString());
		String s = this.readString();

		if (s.isEmpty()) throw this.exception("Expected value");
		return this.type(s);
	}

	private Tag type(String s) {
		try {
			if (FLOAT_PATTERN.matcher(s).matches()) return new FloatTag("", Float.parseFloat(s.substring(0, s.length() - 1)));
			if (BYTE_PATTERN.matcher(s).matches()) return new ByteTag("", Byte.parseByte(s.substring(0, s.length() - 1)));
			if (LONG_PATTERN.matcher(s).matches()) return new LongTag("", Long.parseLong(s.substring(0, s.length() - 1)));
			if (SHORT_PATTERN.matcher(s).matches()) return new ShortTag("", Short.parseShort(s.substring(0, s.length() - 1)));
			if (INT_PATTERN.matcher(s).matches()) return new IntTag("", Integer.parseInt(s));
			if (DOUBLE_PATTERN.matcher(s).matches()) return new DoubleTag("", Double.parseDouble(s.substring(0, s.length() - 1)));
			if (DOUBLE_PATTERN_NOSUFFIX.matcher(s).matches()) return new DoubleTag("", Double.parseDouble(s));
			if ("true".equalsIgnoreCase(s)) return new ByteTag("", (byte) 1);
			if ("false".equalsIgnoreCase(s)) return new ByteTag("", (byte) 0);
		} catch (NumberFormatException ignored) {
		}
		return new StringTag("", s);
	}

	private String readQuotedString() throws IllegalArgumentException {
		int pos = ++this.cursor;
		StringBuilder stringbuilder = null;
		boolean flag = false;

		while (this.canRead()) {
			char c = this.pop();

			if (flag) {
				if (c != '\\' && c != '"') throw this.exception("Invalid escape of '" + c + "'");
				flag = false;
			} else {
				if (c == '\\') {
					flag = true;
					if (stringbuilder == null) stringbuilder = new StringBuilder(this.string.substring(pos, this.cursor - 1));
					continue;
				}
				if (c == '"') return stringbuilder == null ? this.string.substring(pos, this.cursor - 1) : stringbuilder.toString();
			}

			if (stringbuilder != null) stringbuilder.append(c);
		}

		throw this.exception("Missing termination quote");
	}

	private String readString() {
		int i = this.cursor;
		while (this.canRead() && this.isAllowedInKey(this.peek())) this.cursor++;
		return this.string.substring(i, this.cursor);
	}

	private Tag readValue() throws IllegalArgumentException {
		this.skipWhitespace();

		if (!this.canRead()) throw this.exception("Expected value");

		char c = this.peek();
		if (c == '{') return this.readCompound();
		if (c == '[') return this.readList();
		return this.readTypedValue();
	}

	private Tag readList() throws IllegalArgumentException {
		return this.canRead(2) && this.peek(1) != '"' && this.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
	}

	private CompoundTag readCompound() throws IllegalArgumentException {
		this.expect('{');
		CompoundTag tag = new CompoundTag();
		this.skipWhitespace();

		while (this.canRead() && this.peek() != '}') {
			String s = this.readKey();

			if (s.isEmpty()) throw this.exception("Expected non-empty key");

			this.expect(':');
			tag.put(s, this.readValue());

			if (!this.hasElementSeparator()) break;
			if (!this.canRead()) throw this.exception("Expected key");
		}

		this.expect('}');
		return tag;
	}

	private Tag readListTag() throws IllegalArgumentException {
		this.expect('[');
		this.skipWhitespace();

		if (!this.canRead()) throw this.exception("Expected value");

		ListTag<Tag> tagList = new ListTag<>();
		byte listType = -1;

		while (this.peek() != ']') {
			Tag tag = this.readValue();
			byte type = tag.getId();

			if (listType < 0) listType = type;
			else if (type != listType) throw this.exception("Unable to insert " + Tag.getTagName(type) + " into ListTag of type " + Tag.getTagName(listType));

			tagList.add(tag);

			if (!this.hasElementSeparator()) break;
			if (!this.canRead()) throw this.exception("Expected value");
		}

		this.expect(']');
		return tagList;
	}

	private Tag readArrayTag() throws IllegalArgumentException {
		this.expect('[');
		char c = this.pop();
		this.pop();
		this.skipWhitespace();

		if (!this.canRead()) throw this.exception("Expected value");
		if (c == 'B') return new ByteArrayTag("", this.readByteArray());
		if (c == 'I') return new IntArrayTag("", this.readIntArray());
		throw this.exception("Invalid array type '" + c + "' found");
	}

	private byte[] readByteArray() throws IllegalArgumentException {
		ByteArrayList list = new ByteArrayList();

		while (true) {
			if (this.peek() != ']') {
				Tag tag = this.readValue();
				byte i = tag.getId();

				if (i != Tag.TAG_Byte) throw this.exception("Unable to insert " + Tag.getTagName(i) + " into " + Tag.getTagName(Tag.TAG_Byte_Array));

				list.add((byte) ((ByteTag) tag).data);

				if (this.hasElementSeparator()) {
					if (!this.canRead()) throw this.exception("Expected value");
					continue;
				}
			}

			this.expect(']');
			return list.toByteArray();
		}
	}

	private int[] readIntArray() throws IllegalArgumentException {
		IntArrayList list = new IntArrayList();

		while (true) {
			if (this.peek() != ']') {
				Tag tag = this.readValue();
				byte i = tag.getId();

				if (i != Tag.TAG_Int) throw this.exception("Unable to insert " + Tag.getTagName(i) + " into " + Tag.getTagName(Tag.TAG_Int_Array));

				list.add(((IntTag) tag).data);

				if (this.hasElementSeparator()) {
					if (!this.canRead()) throw this.exception("Expected value");
					continue;
				}
			}

			this.expect(']');
			return list.toIntArray();
		}
	}

	private void skipWhitespace() {
		while (this.canRead() && Character.isWhitespace(this.peek())) this.cursor++;
	}

	private boolean hasElementSeparator() {
		this.skipWhitespace();
		if (!this.canRead() || this.peek() != ',') return false;

		this.cursor++;
		this.skipWhitespace();
		return true;
	}

	private void expect(char expected) throws IllegalArgumentException {
		this.skipWhitespace();
		boolean flag = this.canRead();

		if (!flag || this.peek() != expected) throw new IllegalArgumentException("Expected '" + expected + "' but got '" + (flag ? this.peek() : "<EOF>") + "'" + " at: " + slice(this.string, this.cursor + 1));
		this.cursor++;
	}

	private boolean isAllowedInKey(char charIn) {
		return charIn >= '0' && charIn <= '9' || charIn >= 'A' && charIn <= 'Z' || charIn >= 'a' && charIn <= 'z' || charIn == '_' || charIn == '-' || charIn == '.' || charIn == '+';
	}

	private boolean canRead(int length) {
		return this.cursor + length < this.string.length();
	}

	private boolean canRead() {
		return this.canRead(0);
	}

	private char peek(int length) {
		return this.string.charAt(this.cursor + length);
	}

	private char peek() {
		return this.peek(0);
	}

	private char pop() {
		return this.string.charAt(this.cursor++);
	}
}
