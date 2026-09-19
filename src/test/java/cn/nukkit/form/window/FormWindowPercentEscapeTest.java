package cn.nukkit.form.window;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementStepSlider;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormWindowPercentEscapeTest {

    @Test
    void simpleFormEscapesDisplayTextWithoutChangingSourceOrImageUrl() {
        FormWindowSimple form = new FormWindowSimple(
                "Progress 50%",
                "Rate 40% and 20%%; keep %s and %1$d",
                List.of(new ElementButton("Save 10%", new ElementButtonImageData(
                        ElementButtonImageData.IMAGE_DATA_TYPE_URL, "https://example.com/a%20b.png"
                )))
        );

        JsonObject json = JsonParser.parseString(form.getJSONData()).getAsJsonObject();
        assertEquals("Progress 50%%", json.get("title").getAsString());
        assertEquals("Rate 40%% and 20%%; keep %s and %1$d", json.get("content").getAsString());
        assertEquals("Save 10%%", json.getAsJsonArray("buttons").get(0).getAsJsonObject().get("text").getAsString());
        assertEquals("https://example.com/a%20b.png", json.getAsJsonArray("buttons").get(0)
                .getAsJsonObject().getAsJsonObject("image").get("data").getAsString());
        assertEquals("Rate 40% and 20%%; keep %s and %1$d", form.getContent());
        assertEquals(form.getJSONData(), form.getJSONData());
    }

    @Test
    void modalFormEscapesBothButtons() {
        FormWindowModal form = new FormWindowModal("100% done", "30% speed", "Yes 50%", "No 0%%");
        JsonObject json = JsonParser.parseString(form.getJSONData()).getAsJsonObject();

        assertEquals("100%% done", json.get("title").getAsString());
        assertEquals("30%% speed", json.get("content").getAsString());
        assertEquals("Yes 50%%", json.get("button1").getAsString());
        assertEquals("No 0%%", json.get("button2").getAsString());
    }

    @Test
    void customFormEscapesLabelsOptionsStepsAndInputText() {
        FormWindowCustom form = new FormWindowCustom("Status 75%", new ArrayList<>(), "https://example.com/icon%20a.png");
        form.setSubmitButton("Apply 5%");
        form.addElement(new ElementLabel("Success 100%"));
        ElementInput inputElement = new ElementInput("Value 20%", "Hint 30%", "Default 40%").setTooltip("Help 50%");
        form.addElement(inputElement);
        ElementDropdown dropdownElement = new ElementDropdown("Chance 60%", List.of("10%", "20%%")).setTooltip("Choice 70%");
        form.addElement(dropdownElement);
        form.addElement(new ElementStepSlider("Step 80%", List.of("90%", "100%%")));

        JsonObject json = JsonParser.parseString(form.getJSONData()).getAsJsonObject();
        assertEquals("Status 75%%", json.get("title").getAsString());
        assertEquals("Apply 5%%", json.get("submit").getAsString());
        assertEquals("https://example.com/icon%20a.png", json.getAsJsonObject("icon").get("data").getAsString());
        assertEquals("Success 100%%", json.getAsJsonArray("content").get(0).getAsJsonObject().get("text").getAsString());
        JsonObject input = json.getAsJsonArray("content").get(1).getAsJsonObject();
        assertEquals("Value 20%%", input.get("text").getAsString());
        assertEquals("Hint 30%%", input.get("placeholder").getAsString());
        assertEquals("Default 40%%", input.get("default").getAsString());
        assertEquals("Help 50%%", input.get("tooltip").getAsString());
        JsonObject dropdown = json.getAsJsonArray("content").get(2).getAsJsonObject();
        assertEquals("Chance 60%%", dropdown.get("text").getAsString());
        assertEquals("10%%", dropdown.getAsJsonArray("options").get(0).getAsString());
        assertEquals("20%%", dropdown.getAsJsonArray("options").get(1).getAsString());
        assertEquals("Choice 70%%", dropdown.get("tooltip").getAsString());
        JsonObject slider = json.getAsJsonArray("content").get(3).getAsJsonObject();
        assertEquals("Step 80%%", slider.get("text").getAsString());
        assertEquals("90%%", slider.getAsJsonArray("steps").get(0).getAsString());
        assertEquals("100%%", slider.getAsJsonArray("steps").get(1).getAsString());
        assertEquals("Default 40%", inputElement.getDefaultText());
        assertEquals("10%", dropdownElement.getOptions().get(0));
    }
}
