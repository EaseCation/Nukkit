package cn.nukkit.form.window;

import cn.nukkit.form.element.*;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.math.Mth;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.GameVersion.*;

@ToString
public class FormWindowCustom extends FormWindow {
    private static final Gson GSON = new Gson();

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

    public FormResponseCustom getResponse() {
        return response;
    }

    public void setResponse(String data, int protocol) {
        if (data.equals("null")) {
            this.closed = true;
            return;
        }

        List<String> elementResponses;
        try {
            elementResponses = GSON.fromJson(data, new TypeToken<List<String>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            this.closed = true;
            return;
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

        boolean not12170 = protocol != V1_21_70.getProtocol();

        int responseIndex = 0;
        for (int i = 0; i < content.size(); i++) {
            Element e = content.get(i);
            String elementData = responseIndex >= elementResponses.size() ? "" : elementResponses.get(responseIndex);
            if (e instanceof ElementLabel) {
                labelResponses.put(i, ((ElementLabel) e).getText());
                responses.put(i, ((ElementLabel) e).getText());
                if (not12170) {
                    responseIndex++;
                }
            } else if (e instanceof ElementDropdown) {
                int index = Integer.parseInt(elementData);
                List<String> options = ((ElementDropdown) e).getOptions();
                String answer = options.isEmpty() ? "" : options.get(Mth.clamp(index, 0, options.size() - 1));
                dropdownResponses.put(i, new FormResponseData(index, answer));
                responses.put(i, answer);
                responseIndex++;
            } else if (e instanceof ElementInput) {
                inputResponses.put(i, elementData);
                responses.put(i, elementData);
                responseIndex++;
            } else if (e instanceof ElementSlider) {
                float answer = Float.parseFloat(elementData);
                sliderResponses.put(i, answer);
                responses.put(i, Float.valueOf(answer));
                responseIndex++;
            } else if (e instanceof ElementStepSlider) {
                int index = Integer.parseInt(elementData);
                List<String> steps = ((ElementStepSlider) e).getSteps();
                String answer = steps.isEmpty() ? "" : steps.get(Mth.clamp(index, 0, steps.size() - 1));
                stepSliderResponses.put(i, new FormResponseData(index, answer));
                responses.put(i, answer);
                responseIndex++;
            } else if (e instanceof ElementToggle) {
                boolean answer = Boolean.parseBoolean(elementData);
                toggleResponses.put(i, answer);
                responses.put(i, Boolean.valueOf(answer));
                responseIndex++;
            } else if (e instanceof ElementHeader header) {
                headerResponses.put(i, header.getText());
                responses.put(i, header.getText());
                if (not12170) {
                    responseIndex++;
                }
            } else if (e instanceof ElementDivider divider) {
                dividerResponses.put(i, divider.getText());
                responses.put(i, divider.getText());
                if (not12170) {
                    responseIndex++;
                }
            }
        }

        this.response = new FormResponseCustom(responses, dropdownResponses, inputResponses,
                sliderResponses, stepSliderResponses, toggleResponses, labelResponses, headerResponses, dividerResponses);
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
