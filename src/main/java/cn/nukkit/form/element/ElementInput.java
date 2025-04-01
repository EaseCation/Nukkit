package cn.nukkit.form.element;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;

import javax.annotation.Nullable;

@ToString
public class ElementInput implements Element {

    @SuppressWarnings("unused")
    private final String type = "input"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";
    private String placeholder = "";
    @SerializedName("default")
    private String defaultText = "";
    /**
     * @since 1.21.80
     */
    @Nullable
    private String tooltip;

    public ElementInput(String text) {
        this(text, "");
    }

    public ElementInput(String text, String placeholder) {
        this(text, placeholder, "");
    }

    public ElementInput(String text, String placeholder, String defaultText) {
        this.text = text;
        this.placeholder = placeholder;
        this.defaultText = defaultText;
    }

    public ElementInput setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPlaceHolder() {
        return placeholder;
    }

    public void setPlaceHolder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }
}
