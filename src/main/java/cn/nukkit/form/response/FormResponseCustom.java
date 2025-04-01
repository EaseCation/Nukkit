package cn.nukkit.form.response;

import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.ToString;

@ToString
public class FormResponseCustom implements FormResponse {

    private final Int2ObjectMap<Object> responses;
    private final Int2ObjectMap<FormResponseData> dropdownResponses;
    private final Int2ObjectMap<String> inputResponses;
    private final Int2FloatMap sliderResponses;
    private final Int2ObjectMap<FormResponseData> stepSliderResponses;
    private final Int2BooleanMap toggleResponses;
    private final Int2ObjectMap<String> labelResponses;
    private final Int2ObjectMap<String> headerResponses;
    private final Int2ObjectMap<String> dividerResponses;

    public FormResponseCustom(Int2ObjectMap<Object> responses,
                              Int2ObjectMap<FormResponseData> dropdownResponses,
                              Int2ObjectMap<String> inputResponses,
                              Int2FloatMap sliderResponses,
                              Int2ObjectMap<FormResponseData> stepSliderResponses,
                              Int2BooleanMap toggleResponses,
                              Int2ObjectMap<String> labelResponses,
                              Int2ObjectMap<String> headerResponses,
                              Int2ObjectMap<String> dividerResponses) {
        this.responses = responses;
        this.dropdownResponses = dropdownResponses;
        this.inputResponses = inputResponses;
        this.sliderResponses = sliderResponses;
        this.stepSliderResponses = stepSliderResponses;
        this.toggleResponses = toggleResponses;
        this.labelResponses = labelResponses;
        this.headerResponses = headerResponses;
        this.dividerResponses = dividerResponses;
    }

    public Int2ObjectMap<Object> getResponses() {
        return responses;
    }

    public Object getResponse(int id) {
        return responses.get(id);
    }

    public FormResponseData getDropdownResponse(int id) {
        return dropdownResponses.get(id);
    }

    public String getInputResponse(int id) {
        return inputResponses.get(id);
    }

    public float getSliderResponse(int id) {
        return sliderResponses.get(id);
    }

    public FormResponseData getStepSliderResponse(int id) {
        return stepSliderResponses.get(id);
    }

    public boolean getToggleResponse(int id) {
        return toggleResponses.get(id);
    }

    public String getLabelResponse(int id) {
        return labelResponses.get(id);
    }

    public String getHeaderResponse(int id) {
        return headerResponses.get(id);
    }

    public String getDividerResponses(int id) {
        return dividerResponses.get(id);
    }
}
