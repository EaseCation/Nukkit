package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

/**
 * Created by Snake1999 on 2016/1/17.
 * Package cn.nukkit.block in project nukkit.
 */
public class BlockNoteblock extends BlockSolid {

    BlockNoteblock() {

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
    public Instrument getInstrumentSound() {
        Block above = up();
        Instrument topInstrument = above.getTopInstrument();
        if (topInstrument != null) {
            return topInstrument;
        }
        if (!above.isAir()) {
            return null;
        }
        return this.down().getInstrument();
    }

    public void emitSound() {
        Instrument instrument = this.getInstrumentSound();
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

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Instrument getInstrument() {
        return Instrument.BASS;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
