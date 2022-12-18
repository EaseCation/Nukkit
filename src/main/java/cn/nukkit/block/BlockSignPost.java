package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.block.SignColorChangeEvent;
import cn.nukkit.event.block.SignGlowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemSign;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;

/**
 * @author Nukkit Project Team
 */
public class BlockSignPost extends BlockTransparentMeta implements Faceable {

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
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
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
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face != BlockFace.DOWN) {
            if (face == BlockFace.UP) {
                if (down().canBeFlowedInto()) {
                    return false;
                }

                setDamage(Mth.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f);
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

            BlockEntitySign sign = (BlockEntitySign) BlockEntity.createBlockEntity(BlockEntity.SIGN, getChunk(), nbt);
            return sign != null;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() == Block.AIR) {
                getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return new ItemSign();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == Item.DYE) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);
            if (!(blockEntity instanceof BlockEntitySign)) {
                return false;
            }
            BlockEntitySign sign = (BlockEntitySign) blockEntity;

            int meta = item.getDamage();
            if (meta == ItemDye.INK_SAC) {
                if (!sign.isGlowing()) {
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

                sign.setGlowing(false);
                sign.spawnToAll();

                this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_INK_SAC_USED);

                if (player != null && (player.getGamemode() & 0x01) == 0) {
                    item.count--;
                }

                return true;
            }

            BlockColor color = DyeColor.getByDyeNewData(meta).getSignColor();
            if (color.equals(sign.getColor())) {
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

            sign.setColor(color);
            sign.spawnToAll();

            this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_DYE_USED);

            if (player != null && (player.getGamemode() & 0x01) == 0) {
                item.count--;
            }

            return true;
        }

        if (item.getId() == Item.GLOW_INK_SAC) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);
            if (!(blockEntity instanceof BlockEntitySign)) {
                return false;
            }
            BlockEntitySign sign = (BlockEntitySign) blockEntity;

            if (sign.isGlowing()) {
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

            sign.setGlowing(true);
            sign.spawnToAll();

            this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_INK_SAC_USED);

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            return true;
        }

        return false;
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
}
