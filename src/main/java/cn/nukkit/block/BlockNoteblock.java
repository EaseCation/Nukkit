package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityMusic;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.sound.SoundEnum;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Snake1999 on 2016/1/17.
 * Package cn.nukkit.block in project nukkit.
 */
public class BlockNoteblock extends BlockSolid {

    public BlockNoteblock() {

    }

    @Override
    public String getName() {
        return "Note Block";
    }

    @Override
    public int getId() {
        return NOTEBLOCK;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.MUSIC;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 0.8D;
    }

    @Override
    public double getResistance() {
        return 4D;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.getLevel().setBlock(block, this, true);
        return this.createBlockEntity() != null;
    }

    public int getStrength() {
        BlockEntityMusic blockEntity = this.getBlockEntity();
        return blockEntity != null ? blockEntity.getPitch() : 0;
    }

    public void increaseStrength() {
        BlockEntityMusic blockEntity = this.getBlockEntity();
        if (blockEntity != null) {
            blockEntity.changePitch();
        }
    }

    public Instrument getInstrument() {
        switch (this.down().getId()) {
            case GOLD_BLOCK:
                return Instrument.GLOCKENSPIEL;
            case CLAY:
                return Instrument.FLUTE;
            case PACKED_ICE:
                return Instrument.CHIME;
            case WOOL:
                return Instrument.GUITAR;
            case BONE_BLOCK:
                return Instrument.XYLOPHONE;
            case IRON_BLOCK:
                return Instrument.VIBRAPHONE;
            case SOUL_SAND:
                return Instrument.COW_BELL;
            case PUMPKIN:
                return Instrument.DIDGERIDOO;
            case EMERALD_BLOCK:
                return Instrument.SQUARE_WAVE;
            case HAY_BLOCK:
                return Instrument.BANJO;
            case GLOWSTONE:
                return Instrument.ELECTRIC_PIANO;
            case LOG:
            case LOG2:
            case PLANKS:
            case DOUBLE_WOODEN_SLAB:
            case WOODEN_SLAB:
            case OAK_STAIRS:
            case SPRUCE_STAIRS:
            case BIRCH_STAIRS:
            case JUNGLE_STAIRS:
            case ACACIA_STAIRS:
            case DARK_OAK_STAIRS:
            case FENCE:
            case FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case BLOCK_WOODEN_DOOR:
            case BLOCK_SPRUCE_DOOR:
            case BLOCK_BIRCH_DOOR:
            case BLOCK_JUNGLE_DOOR:
            case BLOCK_ACACIA_DOOR:
            case BLOCK_DARK_OAK_DOOR:
            case WOODEN_PRESSURE_PLATE:
            case TRAPDOOR:
            case STANDING_SIGN:
            case WALL_SIGN:
            case NOTEBLOCK:
            case BOOKSHELF:
            case CHEST:
            case TRAPPED_CHEST:
            case CRAFTING_TABLE:
            case JUKEBOX:
            case BROWN_MUSHROOM_BLOCK:
            case RED_MUSHROOM_BLOCK:
            case DAYLIGHT_DETECTOR:
            case DAYLIGHT_DETECTOR_INVERTED:
            case STANDING_BANNER:
            case WALL_BANNER:
                return Instrument.BASS;
            case SAND:
            case GRAVEL:
            case CONCRETE_POWDER:
                return Instrument.DRUM;
            case GLASS:
            case GLASS_PANE:
            case STAINED_GLASS_PANE:
            case STAINED_GLASS:
            case BEACON:
            case SEA_LANTERN:
                return Instrument.STICKS;
            case STONE:
            case SANDSTONE:
            case RED_SANDSTONE:
            case COBBLESTONE:
            case MOSSY_COBBLESTONE:
            case BRICK_BLOCK:
            case STONEBRICK:
            case NETHER_BRICK:
            case RED_NETHER_BRICK:
            case QUARTZ_BLOCK:
            case DOUBLE_STONE_SLAB:
            case STONE_SLAB:
            case DOUBLE_STONE_SLAB2:
            case STONE_SLAB2:
            case STONE_STAIRS:
            case BRICK_STAIRS:
            case STONE_BRICK_STAIRS:
            case NETHER_BRICK_STAIRS:
            case SANDSTONE_STAIRS:
            case QUARTZ_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case PURPUR_STAIRS:
            case COBBLESTONE_WALL:
            case NETHER_BRICK_FENCE:
            case BEDROCK:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case LAPIS_ORE:
            case DIAMOND_ORE:
            case REDSTONE_ORE:
            case LIT_REDSTONE_ORE:
            case EMERALD_ORE:
            case DROPPER:
            case DISPENSER:
            case FURNACE:
            case LIT_FURNACE:
            case OBSIDIAN:
            case GLOWINGOBSIDIAN:
            case MOB_SPAWNER:
            case STONE_PRESSURE_PLATE:
            case NETHERRACK:
            case QUARTZ_ORE:
            case ENCHANTING_TABLE:
            case END_PORTAL_FRAME:
            case END_STONE:
            case END_BRICKS:
            case ENDER_CHEST:
            case STAINED_HARDENED_CLAY:
            case HARDENED_CLAY:
            case PRISMARINE:
            case COAL_BLOCK:
            case PURPUR_BLOCK:
            case MAGMA:
            case CONCRETE:
            case STONECUTTER:
            case OBSERVER:
                return Instrument.BASS_DRUM;
            default:
                return Instrument.PIANO;
        }
    }

    public void emitSound() {
        if (this.up().getId() != AIR) return;

        Instrument instrument = this.getInstrument();

        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_NOTE, instrument.ordinal() << 8 | this.getStrength());

        BlockEventPacket pk = new BlockEventPacket();
        pk.x = this.getFloorX();
        pk.y = this.getFloorY();
        pk.z = this.getFloorZ();
        pk.eventType = instrument.ordinal();
        pk.eventData = this.getStrength();
        this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        this.increaseStrength();
        this.emitSound();
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            BlockEntityMusic blockEntity = this.getBlockEntity();
            if (blockEntity != null) {
                if (this.getLevel().isBlockPowered(this)) {
                    if (!blockEntity.isPowered()) {
                        this.emitSound();
                    }
                    blockEntity.setPowered(true);
                } else {
                    blockEntity.setPowered(false);
                }
            }
        }
        return super.onUpdate(type);
    }

    private BlockEntityMusic getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityMusic) {
            return (BlockEntityMusic) blockEntity;
        }
        return null;
    }

    private BlockEntityMusic createBlockEntity() {
        return (BlockEntityMusic) BlockEntity.createBlockEntity(BlockEntity.MUSIC, getChunk(),
                                        BlockEntity.getDefaultCompound(this, BlockEntity.MUSIC));
    }

    public enum Instrument {
        PIANO(SoundEnum.NOTE_HARP),
        BASS_DRUM(SoundEnum.NOTE_BD),
        DRUM(SoundEnum.NOTE_SNARE),
        STICKS(SoundEnum.NOTE_HAT),
        BASS(SoundEnum.NOTE_BASS),
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
        ELECTRIC_PIANO(SoundEnum.NOTE_PLING);

        private final SoundEnum sound;

        Instrument(SoundEnum sound) {
            this.sound = sound;
        }

        public SoundEnum getSound() {
            return sound;
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
