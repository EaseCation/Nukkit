package cn.nukkit.form.window;

import cn.nukkit.form.response.FormResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public abstract class FormWindow {

    private static final Gson GSON = new Gson();
    private static final Pattern FORMAT_PLACEHOLDER = Pattern.compile("%(?:[1-9]\\d*\\$)?[sd]");

    protected transient boolean closed = false;

    public String getJSONData() {
        JsonObject form = GSON.toJsonTree(this).getAsJsonObject();
        escapeField(form, "title");
        escapeField(form, "content");
        escapeField(form, "button1");
        escapeField(form, "button2");
        escapeField(form, "submit");

        if (form.has("buttons")) {
            for (JsonElement button : form.getAsJsonArray("buttons")) {
                escapeField(button.getAsJsonObject(), "text");
            }
        }
        JsonElement content = form.get("content");
        if (content != null && content.isJsonArray()) {
            for (JsonElement element : content.getAsJsonArray()) {
                JsonObject field = element.getAsJsonObject();
                escapeField(field, "text");
                escapeField(field, "placeholder");
                escapeField(field, "default");
                escapeField(field, "tooltip");
                escapeArray(field, "options");
                escapeArray(field, "steps");
            }
        }
        return GSON.toJson(form);
    }

    private static void escapeField(JsonObject object, String name) {
        JsonElement value = object.get(name);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            object.addProperty(name, escapePercents(value.getAsString()));
        }
    }

    private static void escapeArray(JsonObject object, String name) {
        JsonElement value = object.get(name);
        if (value == null || !value.isJsonArray()) return;
        JsonArray values = value.getAsJsonArray();
        for (int i = 0; i < values.size(); i++) {
            JsonElement entry = values.get(i);
            if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) {
                values.set(i, new JsonPrimitive(escapePercents(entry.getAsString())));
            }
        }
    }

    //  解决 EaseCation 单百分号不显示需要转义的问题
    private static String escapePercents(String text) {
        if (text.indexOf('%') < 0) return text;
        StringBuilder result = new StringBuilder(text.length());
        Matcher placeholder = FORMAT_PLACEHOLDER.matcher(text);
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != '%') {
                result.append(text.charAt(i));
            } else if (i + 1 < text.length() && text.charAt(i + 1) == '%') {
                result.append("%%");
                i++;
            } else if (placeholder.find(i) && placeholder.start() == i) {
                result.append(text, i, placeholder.end());
                i = placeholder.end() - 1;
            } else {
                result.append("%%");
            }
        }
        return result.toString();
    }

    public abstract String getTitle();

    public abstract boolean setResponse(@Nullable Object data, int protocol);

    public abstract FormResponse getResponse();

    public boolean wasClosed() {
        return closed;
    }

}
