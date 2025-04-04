package cn.nukkit.form.response;

import cn.nukkit.form.element.ElementButton;
import lombok.ToString;

@ToString(exclude = "clickedButton")
public class FormResponseSimple implements FormResponse {

    private final int clickedButtonId;
    private final ElementButton clickedButton;

    public FormResponseSimple(int clickedButtonId, ElementButton clickedButton) {
        this.clickedButtonId = clickedButtonId;
        this.clickedButton = clickedButton;
    }

    public int getClickedButtonId() {
        return clickedButtonId;
    }

    public ElementButton getClickedButton() {
        return clickedButton;
    }

}
