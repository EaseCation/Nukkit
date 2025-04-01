package cn.nukkit.form.element;

import cn.nukkit.math.Mth;
import com.google.gson.annotations.SerializedName;
import lombok.ToString;

import javax.annotation.Nullable;

@ToString
public class ElementSlider implements Element {

    @SuppressWarnings("unused")
    private final String type = "slider"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";
    private float min = 0f;
    private float max = 100f;
    private float step;
    @SerializedName("default")
    private float defaultValue = 0;
    /**
     * @since 1.21.80
     */
    @Nullable
    private String tooltip;

    public ElementSlider(String text, float min, float max) {
        this(text, min, max, 1);
    }

    public ElementSlider(String text, float min, float max, float step) {
        this(text, min, max, step, 0);
    }

    public ElementSlider(String text, float min, float max, float step, float defaultValue) {
        this.text = text;
        this.min = Math.max(min, 0f);
        this.max = Math.max(max, this.min);
        if (step > 0) this.step = Math.min(step, this.max);
        this.defaultValue = Mth.clamp(defaultValue, this.min, this.max);
    }

    public ElementSlider setTooltip(String tooltip) {
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

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
    }
}
