package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityComparator;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 */
public abstract class BlockRedstoneComparator extends BlockRedstoneDiode {

    public static final int DIRECTION_MASK = 0b11;
    public static final int SUBTRACT_BIT = 0b100;
    public static final int LIT_BIT = 0b1000;

    public BlockRedstoneComparator() {
        this(0);
    }

    public BlockRedstoneComparator(int meta) {
        super(meta);
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.COMPARATOR;
    }

    @Override
    protected int getDelay() {
        return 2;
    }

    @Override
    public BlockFace getFacing() {
        return BlockFace.fromHorizontalIndex(this.getDamage());
    }

    public Mode getMode() {
        return (getDamage() & 4) > 0 ? Mode.SUBTRACT : Mode.COMPARE;
    }

    @Override
    protected BlockRedstoneComparator getUnpowered() {
        return (BlockRedstoneComparator) Block.get(BlockID.UNPOWERED_COMPARATOR, this.getDamage());
    }

    @Override
    protected BlockRedstoneComparator getPowered() {
        return (BlockRedstoneComparator) Block.get(BlockID.POWERED_COMPARATOR, this.getDamage());
    }

    @Override
    protected int getRedstoneSignal() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        return blockEntity instanceof BlockEntityComparator ? ((BlockEntityComparator) blockEntity).getOutputSignal() : 0;
    }

    @Override
    public void updateState() {
        if (!this.level.isBlockTickPending(this, this)) {
            int output = this.calculateOutput();
            BlockEntity blockEntity = this.level.getBlockEntity(this);
            int power = blockEntity instanceof BlockEntityComparator ? ((BlockEntityComparator) blockEntity).getOutputSignal() : 0;

            if (output != power || this.isPowered() != this.shouldBePowered()) {
                /*if(isFacingTowardsRepeater()) {
                    this.level.scheduleUpdate(this, this, 2, -1);
                } else {
                    this.level.scheduleUpdate(this, this, 2, 0);
                }*/

                this.level.scheduleUpdate(this, this, 2);
            }
        }
    }

    protected int calculateInputStrength() {
        int power = super.calculateInputStrength();
        BlockFace face = getFacing();
        Block block = this.getSide(face);

        if (block.hasComparatorInputOverride()) {
            power = block.getComparatorInputOverride();
        } else if (power < 15 && block.isNormalBlock()) {
            block = block.getSide(face);

            if (block.hasComparatorInputOverride()) {
                power = block.getComparatorInputOverride();
            }
        }

        return power;
    }

    protected boolean shouldBePowered() {
        int input = this.calculateInputStrength();

        if (input >= 15) {
            return true;
        } else if (input == 0) {
            return false;
        } else {
            int sidePower = this.getPowerOnSides();
            return sidePower == 0 || input >= sidePower;
        }
    }

    private int calculateOutput() {
        return getMode() == Mode.SUBTRACT ? Math.max(this.calculateInputStrength() - this.getPowerOnSides(), 0) : this.calculateInputStrength();
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (getMode() == Mode.SUBTRACT) {
            this.setDamage(this.getDamage() - 4);
        } else {
            this.setDamage(this.getDamage() + 4);
        }

        this.level.addLevelSoundEvent(this.blockCenter(), this.getMode() == Mode.SUBTRACT ? LevelSoundEventPacket.SOUND_POWER_OFF : LevelSoundEventPacket.SOUND_POWER_ON, getFullId());
        this.level.setBlock(this, this, true, false);
        //bug?

        BlockEntityComparator blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity();
        }
        this.onChange(blockEntity);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.onChange(getBlockEntity());
            return type;
        }

        return super.onUpdate(type);
    }

    private void onChange(BlockEntityComparator blockEntity) {
        if (!this.level.isRedstoneEnabled()) {
            return;
        }

        int output = this.calculateOutput();
        int currentOutput = 0;

        if (blockEntity != null) {
            currentOutput = blockEntity.getOutputSignal();
            blockEntity.setOutputSignal(output);
        }

        if (currentOutput != output || getMode() == Mode.COMPARE) {
            boolean shouldBePowered = this.shouldBePowered();
            boolean isPowered = this.isPowered();

            if (isPowered && !shouldBePowered) {
                this.level.setBlock(this, getUnpowered(), true, false);
            } else if (!isPowered && shouldBePowered) {
                this.level.setBlock(this, getPowered(), true, false);
            }

            this.level.updateAroundRedstone(this, null);
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            BlockEntityComparator comparator = createBlockEntity();
            if (comparator == null) {
                return false;
            }
            onUpdate(Level.BLOCK_UPDATE_REDSTONE);
            return true;
        }

        return false;
    }

    @Override
    public boolean isPowered() {
        return this.isPoweredBlock() || (this.getDamage() & 8) > 0;
    }

    @Override
    public boolean isPoweredBlock() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.COMPARATOR);
    }

    protected BlockEntityComparator createBlockEntity() {
        return (BlockEntityComparator) BlockEntities.createBlockEntity(BlockEntityType.COMPARATOR, getChunk(),
                BlockEntity.getDefaultCompound(this, BlockEntity.COMPARATOR));
    }

    @Nullable
    protected BlockEntityComparator getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityComparator blockEntity) {
            return blockEntity;
        }
        return null;
    }

    public enum Mode {
        COMPARE,
        SUBTRACT
    }
}
