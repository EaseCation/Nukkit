package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBed;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.event.player.PlayerSpawnChangeEvent.Cause;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockBed extends BlockTransparentMeta implements Faceable {

    public static final int DIRECTION_MASK = 0b11;
    public static final int OCCUPIED_BIT = 0b100;
    public static final int HEAD_PIECE_BIT = 0b1000;

    public BlockBed() {
        this(0);
    }

    public BlockBed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_BED;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BED;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public String getName() {
        return this.getDyeColor().getName() + " Bed Block";
    }

    @Override
    public double getMaxY() {
        return this.y + 0.5625;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (this.level.getDimension() != Dimension.OVERWORLD) {
            if (!this.level.getGameRules().getBoolean(GameRule.RESPAWN_BLOCKS_EXPLODE)) {
                return true;
            }

            Explosion explosion = new Explosion(this.add(0.5, 0.5, 0.5), 5, this, true);
            if (!level.getExtraBlock(this).isWater()) { //TODO: check head block only
                explosion.explodeA();
            }
            explosion.explodeB();
            return true;
        }

        if (player != null && !player.hasEffect(Effect.WATER_BREATHING) && !player.hasEffect(Effect.CONDUIT_POWER)
                && this.level.getExtraBlock(this).isWater()) {
            return false;
        }

        Block b;
        int meta = getDamage();
        if ((meta & HEAD_PIECE_BIT) != 0) {
            b = this;
        } else {
            int direction = meta & DIRECTION_MASK;
            Block pair = getSide(BlockFace.fromHorizontalIndex(direction));
            if (pair.getId() == BLOCK_BED && (pair.getDamage() & HEAD_PIECE_BIT) != 0 && (pair.getDamage() & DIRECTION_MASK) == direction) {
                b = pair;
            } else {
                if (player != null) {
                    player.sendMessage(new TranslationContainer("tile.bed.notValid"));
                }
                return true;
            }
        }

        if (player != null) {
            player.setSpawnBlockPosition(b, Cause.BED);

            int time = this.getLevel().getTime() % Level.TIME_FULL;
            boolean isNight = (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE);
            if (!isNight && !this.level.isRaining()) {
                player.sendMessage(new TranslationContainer("tile.bed.noSleep"));
                return true;
            }

            if (!player.sleepOn(b)) {
                player.sendMessage(new TranslationContainer("tile.bed.occupied"));
            }
        }

        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (SupportType.hasFullSupport(this.down(), BlockFace.UP)) {
            BlockFace direction = player.getDirection();
            Block head = this.getSide(direction);

            if (head.canBeReplaced() && SupportType.hasFullSupport(head.down(), BlockFace.UP)) {
                int meta = direction.getHorizontalIndex();

                this.getLevel().setBlock(block, Block.get(BLOCK_BED, meta), true, true);
                this.getLevel().setBlock(head, Block.get(BLOCK_BED, meta | HEAD_PIECE_BIT), true, true);

                createBlockEntity(this, item.getDamage());
                createBlockEntity(head, item.getDamage());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        int meta = getDamage();
        int direction = meta & DIRECTION_MASK;
        BlockFace face = BlockFace.fromHorizontalIndex(direction);
        int headPiece = meta & HEAD_PIECE_BIT;
        if (headPiece != 0) {
            face = face.getOpposite();
        }
        Block pair = getSide(face);
        if (pair.getId() == BLOCK_BED && (pair.getDamage() & DIRECTION_MASK) == direction && (pair.getDamage() & HEAD_PIECE_BIT) != headPiece) {
            this.getLevel().setBlock(pair, Block.get(BlockID.AIR), true, false);
        }

        return super.onBreak(item, player);
    }

    private void createBlockEntity(Vector3 pos, int color) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntity.BED);
        nbt.putByte("color", color);

        BlockEntities.createBlockEntity(BlockEntityType.BED, this.level.getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BED, this.getDyeColor().getWoolData());
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);

            if (blockEntity instanceof BlockEntityBed) {
                return ((BlockEntityBed) blockEntity).getDyeColor();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }
}
