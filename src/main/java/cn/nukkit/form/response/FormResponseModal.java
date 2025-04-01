package cn.nukkit.form.response;

import lombok.ToString;

@ToString
public class FormResponseModal implements FormResponse {

    private final int clickedButtonId;
    private final String clickedButtonText;

    public FormResponseModal(int clickedButtonId, String clickedButtonText) {
        this.clickedButtonId = clickedButtonId;
        this.clickedButtonText = clickedButtonText;
    }

    public int getClickedButtonId() {
        return clickedButtonId;
    }

    public String getClickedButtonText() {
        return clickedButtonText;
    }

}
