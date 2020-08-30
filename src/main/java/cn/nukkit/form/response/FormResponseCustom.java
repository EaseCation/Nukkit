package cn.nukkit.form.response;

import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Map;

public class FormResponseCustom extends FormResponse {

    private final Map<Integer, Object> responses;
    private final Int2ObjectMap<FormResponseData> dropdownResponses;
    private final Int2ObjectMap<String> inputResponses;
    private final Int2FloatMap sliderResponses;
    private final Int2ObjectMap<FormResponseData> stepSliderResponses;
    private final Int2BooleanMap toggleResponses;
    private final Int2ObjectMap<String> labelResponses;

    public FormResponseCustom(Map<Integer, Object> responses, Int2ObjectMap<FormResponseData> dropdownResponses,
                              Int2ObjectMap<String> inputResponses, Int2FloatMap sliderResponses,
                              Int2ObjectMap<FormResponseData> stepSliderResponses,
                              Int2BooleanMap toggleResponses,
                              Int2ObjectMap<String> labelResponses) {
        this.responses = responses;
        this.dropdownResponses = dropdownResponses;
        this.inputResponses = inputResponses;
        this.sliderResponses = sliderResponses;
        this.stepSliderResponses = stepSliderResponses;
        this.toggleResponses = toggleResponses;
        this.labelResponses = labelResponses;
    }

    public Map<Integer, Object> getResponses() {
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
}
