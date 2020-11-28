package cn.nukkit.utils;

import lombok.ToString;

@ToString
public class SkinAnimation {
    public final SerializedImage image;
    public final int type;
    public final float frames;
    public final int expressionType;

    public SkinAnimation(SerializedImage image, int type, float frames) {
        this(image, type, frames, 0);
    }

    public SkinAnimation(SerializedImage image, int type, float frames, int expressionType) {
        this.image = image;
        this.type = type;
        this.frames = frames;
        this.expressionType = expressionType;
    }
}
