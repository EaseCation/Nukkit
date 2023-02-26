package cn.nukkit.potion;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InstantEffect extends Effect {
    InstantEffect(int id, String identifier, String name, int r, int g, int b) {
        super(id, identifier, name, r, g, b);
    }

    InstantEffect(int id, String identifier, String name, int r, int g, int b, boolean isBad) {
        super(id, identifier, name, r, g, b, isBad);
    }

    @Override
    public boolean isInstantaneous() {
        return true;
    }
}
