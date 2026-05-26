package cn.nukkit.block;

import cn.nukkit.level.sound.SoundEnum;

public enum Instrument {
    PIANO(SoundEnum.NOTE_HARP),
    BASS_DRUM(SoundEnum.NOTE_BD),
    DRUM(SoundEnum.NOTE_SNARE),
    STICKS(SoundEnum.NOTE_HAT),
    BASS(SoundEnum.NOTE_BASSATTACK),
    GLOCKENSPIEL(SoundEnum.NOTE_BELL),
    FLUTE(SoundEnum.NOTE_FLUTE),
    CHIME(SoundEnum.NOTE_CHIME),
    GUITAR(SoundEnum.NOTE_GUITAR),
    XYLOPHONE(SoundEnum.NOTE_XYLOPHONE),
    VIBRAPHONE(SoundEnum.NOTE_IRON_XYLOPHONE),
    COW_BELL(SoundEnum.NOTE_COW_BELL),
    DIDGERIDOO(SoundEnum.NOTE_DIDGERIDOO),
    SQUARE_WAVE(SoundEnum.NOTE_BIT),
    BANJO(SoundEnum.NOTE_BANJO),
    ELECTRIC_PIANO(SoundEnum.NOTE_PLING),
    SKELETON(SoundEnum.NOTE_SKELETON),
    WITHER_SKELETON(SoundEnum.NOTE_WITHERSKELETON),
    ZOMBIE(SoundEnum.NOTE_ZOMBIE),
    CREEPER(SoundEnum.NOTE_CREEPER),
    ENDER_DRAGON(SoundEnum.NOTE_ENDERDRAGON),
    PIGLIN(SoundEnum.NOTE_PIGLIN),
    TRUMPET(SoundEnum.NOTE_TRUMPET),
    TRUMPET_EXPOSED(SoundEnum.NOTE_TRUMPET_EXPOSED),
    TRUMPET_WEATHERED(SoundEnum.NOTE_TRUMPET_WEATHERED),
    TRUMPET_OXIDIZED(SoundEnum.NOTE_TRUMPET_OXIDIZED),
    ;

    private final SoundEnum sound;

    Instrument(SoundEnum sound) {
        this.sound = sound;
    }

    public SoundEnum getSound() {
        return sound;
    }

    @Override
    public String toString() {
        return sound.getSound();
    }
}
