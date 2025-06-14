package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSkull;
import cn.nukkit.level.Level;
import cn.nukkit.level.sound.SoundEnum;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;

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
        return BlockToolType.AXE;
    }

    @Override
    public float getHardness() {
        return 0.8f;
    }

    @Override
    public float getResistance() {
        return 4;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.getLevel().setBlock(block, this, true);
        return this.createBlockEntity() != null;
    }

    public int getStrength() {
        BlockEntityMusic blockEntity = this.getBlockEntity();
        return blockEntity != null ? blockEntity.getPitch() : 0;
    }

    public void increaseStrength() {
        increaseStrength(this.getBlockEntity());
    }

    private void increaseStrength(BlockEntityMusic blockEntity) {
        if (blockEntity != null) {
            blockEntity.changePitch();
        }
    }

    @Nullable
    public Instrument getInstrument() {
        Block above = up();
        if (V1_21_40.isAvailable()) {
            if (above.isSkull()) {
                switch (above.getId()) {
                    case SKELETON_SKULL:
                        return Instrument.SKELETON;
                    case WITHER_SKELETON_SKULL:
                        return Instrument.WITHER_SKELETON;
                    case ZOMBIE_HEAD:
                        return Instrument.ZOMBIE;
                    case CREEPER_HEAD:
                        return Instrument.CREEPER;
                    case DRAGON_HEAD:
                        return Instrument.ENDER_DRAGON;
                    case PIGLIN_HEAD:
                        return Instrument.PIGLIN;
                }
            }
        } else if (above.is(BLOCK_SKULL) && level.getBlockEntity(above) instanceof BlockEntitySkull skull) {
            switch (skull.getSkullType()) {
                case ItemSkull.HEAD_SKELETON:
                    return Instrument.SKELETON;
                case ItemSkull.HEAD_WITHER_SKELETON:
                    return Instrument.WITHER_SKELETON;
                case ItemSkull.HEAD_ZOMBIE:
                    return Instrument.ZOMBIE;
                case ItemSkull.HEAD_CREEPER:
                    return Instrument.CREEPER;
                case ItemSkull.HEAD_DRAGON:
                    return Instrument.ENDER_DRAGON;
                case ItemSkull.HEAD_PIGLIN:
                    return Instrument.PIGLIN;
            }
        }
        if (!above.isAir()) {
            return null;
        }

        Block below = this.down();
        if (below.isWool()) {
            return Instrument.GUITAR;
        }
        switch (below.getId()) {
            case GOLD_BLOCK:
                return Instrument.GLOCKENSPIEL;
            case CLAY:
                return Instrument.FLUTE;
            case PACKED_ICE:
                return Instrument.CHIME;
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
        Instrument instrument = this.getInstrument();
        if (instrument == null) {
            return;
        }

        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_NOTE, -(instrument.ordinal() << 8 | this.getStrength()));

        BlockEventPacket pk = new BlockEventPacket();
        pk.x = this.getFloorX();
        pk.y = this.getFloorY();
        pk.z = this.getFloorZ();
        pk.eventType = instrument.ordinal();
        pk.eventData = this.getStrength();
        this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockEntityMusic blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity();
            if (blockEntity == null) {
                return true;
            }
        }
        this.increaseStrength(blockEntity);
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

    @Nullable
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
        return (BlockEntityMusic) BlockEntities.createBlockEntity(BlockEntityType.MUSIC, getChunk(),
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
        ELECTRIC_PIANO(SoundEnum.NOTE_PLING),
        SKELETON(SoundEnum.NOTE_SKELETON),
        WITHER_SKELETON(SoundEnum.NOTE_WITHERSKELETON),
        ZOMBIE(SoundEnum.NOTE_ZOMBIE),
        CREEPER(SoundEnum.NOTE_CREEPER),
        ENDER_DRAGON(SoundEnum.NOTE_ENDERDRAGON),
        PIGLIN(SoundEnum.NOTE_PIGLIN),
        ;

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

    @Override
    public int getFuelTime() {
        return 300;
    }
}
