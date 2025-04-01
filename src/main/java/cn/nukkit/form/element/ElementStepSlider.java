package cn.nukkit.form.element;

import cn.nukkit.math.Mth;
import com.google.gson.annotations.SerializedName;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ToString
public class ElementStepSlider implements Element {

    @SuppressWarnings("unused")
    private final String type = "step_slider"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";
    private List<String> steps;
    @SerializedName("default")
    private int defaultStepIndex = 0;
    /**
     * @since 1.21.80
     */
    @Nullable
    private String tooltip;

    public ElementStepSlider(String text) {
        this(text, new ArrayList<>());
    }

    public ElementStepSlider(String text, List<String> steps) {
        this(text, steps, 0);
    }

    public ElementStepSlider(String text, List<String> steps, int defaultStep) {
        this.text = text;
        this.steps = steps;
        this.defaultStepIndex = steps.isEmpty() ? 0 : Mth.clamp(defaultStep, 0, steps.size() - 1);
    }

    public ElementStepSlider setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public String getTooltip() {
        return tooltip;
    }

    public int getDefaultStepIndex() {
        return defaultStepIndex;
    }

    public void setDefaultOptionIndex(int index) {
        if (index >= steps.size()) return;
        this.defaultStepIndex = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void addStep(String step) {
        addStep(step, false);
    }

    public void addStep(String step, boolean isDefault) {
        steps.add(step);
        if (isDefault) this.defaultStepIndex = steps.size() - 1;
    }

}
