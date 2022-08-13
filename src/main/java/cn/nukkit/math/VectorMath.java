package cn.nukkit.math;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Deprecated
public abstract class VectorMath {

    public static Vector2 getDirection2D(double azimuth) {
        return new Vector2(Mth.cos(azimuth), Mth.sin(azimuth));
    }

}
