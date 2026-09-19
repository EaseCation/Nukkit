package cn.nukkit.form.window;

import cn.nukkit.form.element.*;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.GameVersion.*;

@ToString
public class FormWindowCustom extends FormWindow {

    @SuppressWarnings("unused")
    private final String type = "custom_form"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String title = "";
    @Nullable
    private ElementButtonImageData icon;
    private List<Element> content;
    /**
     * @since 1.20.80
     */
    @Nullable
    private String submit;

    private transient FormResponseCustom response;

    public FormWindowCustom(String title) {
        this(title, new ArrayList<>());
    }

    public FormWindowCustom(String title, List<Element> contents) {
        this(title, contents, (ElementButtonImageData) null);
    }

    public FormWindowCustom(String title, List<Element> contents, String icon) {
        this(title, contents, icon.isEmpty() ? null : new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL, icon));
    }

    public FormWindowCustom(String title, List<Element> contents, ElementButtonImageData icon) {
        this.title = title;
        this.content = contents;
        this.icon = icon;
    }

    public FormWindowCustom setSubmitButton(String submit) {
        this.submit = submit;
        return this;
    }

    public String getSubmitButton() {
        return submit;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Element> getElements() {
        return content;
    }

    public void addElement(Element element) {
        content.add(element);
    }

    public ElementButtonImageData getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        if (!icon.isEmpty()) this.icon = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL, icon);
    }

    public void setIcon(ElementButtonImageData icon) {
        this.icon = icon;
    }

    @Override
    public FormResponseCustom getResponse() {
        return response;
    }

    @Override
    public boolean setResponse(Object data, int protocol) {
        this.closed = false;
        this.response = null;

        if (data == null) {
            this.closed = true;
            return true;
        }
        if (!(data instanceof List<?> elementResponses)) {
            return false;
        }

        boolean includesStaticResponses = protocol != V1_21_70.getProtocol();
        int expectedResponseCount = 0;
        for (Element element : content) {
            if (element instanceof ElementDropdown
                    || element instanceof ElementInput
                    || element instanceof ElementSlider
                    || element instanceof ElementStepSlider
                    || element instanceof ElementToggle) {
                expectedResponseCount++;
            } else if (element instanceof ElementLabel
                    || element instanceof ElementHeader
                    || element instanceof ElementDivider) {
                if (includesStaticResponses) {
                    expectedResponseCount++;
                }
            } else {
                return false;
            }
        }
        if (elementResponses.size() != expectedResponseCount) {
            return false;
        }

        Int2ObjectMap<Object> responses = new Int2ObjectOpenHashMap<>();

        Int2ObjectMap<FormResponseData> dropdownResponses = new Int2ObjectOpenHashMap<>();
        Int2ObjectMap<String> inputResponses = new Int2ObjectOpenHashMap<>();
        Int2FloatMap sliderResponses = new Int2FloatOpenHashMap();
        Int2ObjectMap<FormResponseData> stepSliderResponses = new Int2ObjectOpenHashMap<>();
        Int2BooleanMap toggleResponses = new Int2BooleanOpenHashMap();

        Int2ObjectMap<String> labelResponses = new Int2ObjectOpenHashMap<>();
        Int2ObjectMap<String> headerResponses = new Int2ObjectOpenHashMap<>();
        Int2ObjectMap<String> dividerResponses = new Int2ObjectOpenHashMap<>();

        int responseIndex = 0;
        for (int i = 0; i < content.size(); i++) {
            Element element = content.get(i);
            if (element instanceof ElementLabel label) {
                labelResponses.put(i, label.getText());
                responses.put(i, label.getText());
                if (includesStaticResponses) {
                    if (elementResponses.get(responseIndex) != null) {
                        return false;
                    }
                    responseIndex++;
                }
            } else if (element instanceof ElementDropdown dropdown) {
                Object elementData = elementResponses.get(responseIndex++);
                if (!(elementData instanceof Integer index)) {
                    return false;
                }
                List<String> options = dropdown.getOptions();
                if (index < 0 || index >= options.size()) {
                    return false;
                }
                String answer = options.get(index);
                dropdownResponses.put(i, new FormResponseData(index, answer));
                responses.put(i, answer);
            } else if (element instanceof ElementInput) {
                Object elementData = elementResponses.get(responseIndex++);
                if (!(elementData instanceof String answer)) {
                    return false;
                }
                inputResponses.put(i, answer);
                responses.put(i, answer);
            } else if (element instanceof ElementSlider slider) {
                Object elementData = elementResponses.get(responseIndex++);
                if (!(elementData instanceof Number number)) {
                    return false;
                }
                float answer = number.floatValue();
                if (!Float.isFinite(answer) || answer < slider.getMin() || answer > slider.getMax()) {
                    return false;
                }
                sliderResponses.put(i, answer);
                responses.put(i, Float.valueOf(answer));
            } else if (element instanceof ElementStepSlider stepSlider) {
                Object elementData = elementResponses.get(responseIndex++);
                if (!(elementData instanceof Integer index)) {
                    return false;
                }
                List<String> steps = stepSlider.getSteps();
                if (index < 0 || index >= steps.size()) {
                    return false;
                }
                String answer = steps.get(index);
                stepSliderResponses.put(i, new FormResponseData(index, answer));
                responses.put(i, answer);
            } else if (element instanceof ElementToggle) {
                Object elementData = elementResponses.get(responseIndex++);
                if (!(elementData instanceof Boolean answer)) {
                    return false;
                }
                boolean selected = answer;
                toggleResponses.put(i, selected);
                responses.put(i, Boolean.valueOf(selected));
            } else if (element instanceof ElementHeader header) {
                headerResponses.put(i, header.getText());
                responses.put(i, header.getText());
                if (includesStaticResponses) {
                    if (elementResponses.get(responseIndex) != null) {
                        return false;
                    }
                    responseIndex++;
                }
            } else if (element instanceof ElementDivider divider) {
                dividerResponses.put(i, divider.getText());
                responses.put(i, divider.getText());
                if (includesStaticResponses) {
                    if (elementResponses.get(responseIndex) != null) {
                        return false;
                    }
                    responseIndex++;
                }
            }
        }

        this.response = new FormResponseCustom(responses, dropdownResponses, inputResponses,
                sliderResponses, stepSliderResponses, toggleResponses, labelResponses, headerResponses, dividerResponses);
        return true;
    }

    /**
     * Set Elements from Response
     * Used on ServerSettings Form Response. After players set settings, we need to sync these settings to the server.
     */
    public void setElementsFromResponse() {
        if (this.response != null) {
            for (Entry<Object> entry : this.response.getResponses().int2ObjectEntrySet()) {
                int i = entry.getIntKey();
                Object response = entry.getValue();
                Element e = content.get(i);
                if (e != null) {
                    if (e instanceof ElementDropdown) {
                        ((ElementDropdown) e).setDefaultOptionIndex(((ElementDropdown) e).getOptions().indexOf(response));
                    } else if (e instanceof ElementInput) {
                        ((ElementInput) e).setDefaultText((String) response);
                    } else if (e instanceof ElementSlider) {
                        ((ElementSlider) e).setDefaultValue((Float) response);
                    } else if (e instanceof ElementStepSlider) {
                        ((ElementStepSlider) e).setDefaultOptionIndex(((ElementStepSlider) e).getSteps().indexOf(response));
                    } else if (e instanceof ElementToggle) {
                        ((ElementToggle) e).setDefaultValue((Boolean) response);
                    }
                }
            }
        }
    }

}
