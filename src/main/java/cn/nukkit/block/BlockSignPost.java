package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.block.SignColorChangeEvent;
import cn.nukkit.event.block.SignGlowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;

import static cn.nukkit.GameVersion.*;

/**
 * @author Nukkit Project Team
 */
public class BlockSignPost extends BlockTransparent implements Faceable {
    private static final Vector3 CENTER = new Vector3(0.5, 0.5, 0.5);

    public BlockSignPost() {
        this(0);
    }

    public BlockSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STANDING_SIGN;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SIGN;
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public String getName() {
        return "Oak Sign";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (face != BlockFace.DOWN) {
            if (face == BlockFace.UP) {
                if (down().canBeFlowedInto()) {
                    return false;
                }

                if (player != null) {
                    setDamage(Mth.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f);
                }
                getLevel().setBlock(block, Block.get(getStandingBlockId(), getDamage()), true);
            } else {
                if (getSide(face.getOpposite()).canBeFlowedInto()) {
                    return false;
                }

                setDamage(face.getIndex());
                getLevel().setBlock(block, Block.get(getWallBlockId(), getDamage()), true);
            }

            CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SIGN)
                    .putString("Text1", "")
                    .putString("Text2", "")
                    .putString("Text3", "")
                    .putString("Text4", "");

            if (player != null) {
                nbt.putString("Creator", player.getUniqueId().toString());
            }

            if (item.hasCustomBlockData()) {
                for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(aTag.getName(), aTag);
                }
            }

            BlockEntitySign sign = (BlockEntitySign) BlockEntities.createBlockEntity(BlockEntityType.SIGN, getChunk(), nbt);
            if (sign == null) {
                return false;
            }

            if (player != null) {
                sign.lockedForEditingBy = player;
                player.openSignEditor(getFloorX(), getFloorY(), getFloorZ());
            }
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() == Block.AIR) {
                getLevel().useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.OAK_SIGN);
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex((this.getDamage() + 1) / 4);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!(level.getBlockEntity(this) instanceof BlockEntitySign sign)) {
            return false;
        }

        if (sign.isWaxed()) {
            return true;
        }

        if (item.is(Item.HONEYCOMB)) {
            sign.setWaxed(true);
            sign.spawnToAll();

            if (player != null && player.isSurvivalLike()) {
                item.pop();
            }
            return true;
        }

        boolean front = player == null || isFacingFront(player);

        if (item.getId() == Item.DYE && !sign.isEmpty(front)) {
            int meta = item.getDamage();
            if (V1_20_10.isAvailable() && (meta == ItemDye.COCOA_BEANS || meta == ItemDye.LAPIS_LAZULI || meta == ItemDye.BONE_MEAL)) {
                return true;
            }

            if (meta == ItemDye.INK_SAC) {
                if (!sign.isGlowing(front)) {
                    if (player != null) {
                        sign.spawnTo(player);
                    }
                    return false;
                }

                SignGlowEvent event = new SignGlowEvent(this, player, false);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    if (player != null) {
                        sign.spawnTo(player);
                    }
                    return false;
                }

                sign.setGlowing(front, false);
                sign.spawnToAll();

                this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_INK_SAC_USED);

                if (player != null && (player.getGamemode() & 0x01) == 0) {
                    item.count--;
                }

                return true;
            }

            BlockColor color = DyeColor.getByDyeNewData(meta).getSignColor();
            if (color.equals(sign.getColor(front))) {
                if (player != null) {
                    sign.spawnTo(player);
                }
                return false;
            }

            SignColorChangeEvent event = new SignColorChangeEvent(this, player, color);
            this.level.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                if (player != null) {
                    sign.spawnTo(player);
                }
                return false;
            }

            sign.setColor(front, color);
            sign.spawnToAll();

            this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_DYE_USED);

            if (player != null && (player.getGamemode() & 0x01) == 0) {
                item.count--;
            }

            return true;
        }

        if (item.getId() == Item.GLOW_INK_SAC && !sign.isEmpty(front)) {
            if (sign.isGlowing(front)) {
                if (player != null) {
                    sign.spawnTo(player);
                }
                return false;
            }

            SignGlowEvent event = new SignGlowEvent(this, player, true);
            this.level.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                if (player != null) {
                    sign.spawnTo(player);
                }
                return false;
            }

            sign.setGlowing(front, true);
            sign.spawnToAll();

            this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_INK_SAC_USED);

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            return true;
        }

        if (player == null || face == null) {
            return true;
        }

        Player lockedForEditingBy = sign.lockedForEditingBy;
        if (lockedForEditingBy != null && lockedForEditingBy.isOnline() && level == lockedForEditingBy.level
                && lockedForEditingBy.canInteract(this, lockedForEditingBy.isCreativeLike() ? Player.MAX_REACH_DISTANCE_CREATIVE : Player.MAX_REACH_DISTANCE_SURVIVAL)) {
            return true;
        }
        sign.lockedForEditingBy = player;

        player.openSignEditor(getFloorX(), getFloorY(), getFloorZ(), front);
        return true;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public int getFuelTime() {
        return 200;
    }

    protected int getStandingBlockId() {
        return STANDING_SIGN;
    }

    protected int getWallBlockId() {
        return WALL_SIGN;
    }

    @Override
    public boolean isSign() {
        return true;
    }

    @Override
    public boolean isStandingSign() {
        return true;
    }

    public boolean isFacingFront(Player player) {
        Vector3 center = getSignCenter();
        return Mth.degreesDifferenceAbs(getRotationDegrees(), (float) Mth.atan2(player.z - (z + center.z), player.x - (x + center.x)) * Mth.RAD_TO_DEG - 90) <= 90;
    }

    protected float getRotationDegrees() {
        return getDamage() * 360f / 16;
    }

    protected Vector3 getSignCenter() {
        return CENTER;
    }
}
