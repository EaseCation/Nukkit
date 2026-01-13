package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;

public class ItemMedicine extends ItemEdible {
    public static final int EYE_DROPS = 0;
    public static final int TONIC = 1;
    public static final int ANTIDOTE = 2;
    public static final int ELIXIR = 3;

    private static final String[] MEDICINE_TYPE_NAMES = {
            "blindness",
            "nausea",
            "poison",
            "weakness",
    };

    private static final String[] NAMES = {
            "Eye Drops",
            "Tonic",
            "Antidote",
            "Elixir"
    };

    public ItemMedicine() {
        this(0, 1);
    }

    public ItemMedicine(Integer meta) {
        this(meta, 1);
    }

    public ItemMedicine(Integer meta, int count) {
        super(MEDICINE, meta, count, NAMES[meta != null ? meta & 0x3 : 0]);
    }

    @Override
    public String getDescriptionId() {
        int type = getDamage();
        if (type >= 0 && type < MEDICINE_TYPE_NAMES.length) {
            return "item.medicine." + MEDICINE_TYPE_NAMES[type] + ".name";
        }
        return "item.medicine.blindness.name";
    }

    @Override
    public boolean isStackedByData() {
        return true;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        switch (getDamage()) {
            case EYE_DROPS:
                return player.hasEffect(Effect.BLINDNESS);
            case TONIC:
                return player.hasEffect(Effect.NAUSEA);
            case ANTIDOTE:
                return player.hasEffect(Effect.POISON);
            case ELIXIR:
                return player.hasEffect(Effect.WEAKNESS);
        }
        return false;
    }

    @Override
    protected int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_DRINK;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
