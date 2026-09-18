package cn.nukkit.form.window;

import cn.nukkit.form.response.FormResponse;
import com.google.gson.Gson;

import javax.annotation.Nullable;

public abstract class FormWindow {

    private static final Gson GSON = new Gson();

    protected transient boolean closed = false;

    public String getJSONData() {
        return FormWindow.GSON.toJson(this);
    }

    public abstract String getTitle();

    public abstract boolean setResponse(@Nullable Object data, int protocol);

    public abstract FormResponse getResponse();

    public boolean wasClosed() {
        return closed;
    }

}
