package cn.nukkit.network.protocol.types;

public interface HudElement {
    int PAPER_DOLL = 0;
    int ARMOR = 1;
    int TOOL_TIPS = 2;
    int TOUCH_CONTROLS = 3;
    int CROSSHAIR = 4;
    int HOTBAR = 5;
    int HEALTH = 6;
    int XP = 7;
    int FOOD = 8;
    int AIR_BUBBLES = 9;
    int HORSE_HEALTH = 10;
    /**
     * @since 1.20.80
     */
    int STATUS_EFFECTS = 11;
    /**
     * @since 1.20.80
     */
    int ITEM_TEXT = 12;

    int[] ALL = {
            PAPER_DOLL,
            ARMOR,
            TOOL_TIPS,
            TOUCH_CONTROLS,
            CROSSHAIR,
            HOTBAR,
            HEALTH,
            XP,
            FOOD,
            AIR_BUBBLES,
            HORSE_HEALTH,
            STATUS_EFFECTS,
            ITEM_TEXT,
    };
}
