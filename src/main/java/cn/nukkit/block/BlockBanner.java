package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by PetteriM1
 */
public class BlockBanner extends BlockTransparent implements Faceable {

    public BlockBanner() {
        this(0);
    }

    public BlockBanner(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STANDING_BANNER;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BANNER;
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
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public String getName() {
        return "Banner";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (face != BlockFace.DOWN) {
            if (face == BlockFace.UP) {
                if (down().canBeFlowedInto()) {
                    return false;
                }

                this.setDamage(Mth.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f);
                this.getLevel().setBlock(block, this, true);
            } else {
                if (getSide(face.getOpposite()).canBeFlowedInto()) {
                    return false;
                }

                this.setDamage(face.getIndex());
                this.getLevel().setBlock(block, Block.get(BlockID.WALL_BANNER, this.getDamage()), true);
            }

            CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BANNER)
                    .putInt("Base", item.getDamage() & 0xf);

            Tag type = item.getNamedTagEntry("Type");
            if (type instanceof IntTag) {
                nbt.put("Type", type);
            }
            Tag patterns = item.getNamedTagEntry("Patterns");
            if (patterns instanceof ListTag) {
                nbt.put("Patterns", patterns);
            }

            BlockEntityBanner banner = (BlockEntityBanner) BlockEntities.createBlockEntity(BlockEntityType.BANNER, this.getChunk(), nbt);
            return banner != null;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == Block.AIR) {
                this.getLevel().useBreakOn(this, true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        Item item = Item.get(Item.BANNER);
        if (blockEntity instanceof BlockEntityBanner) {
            BlockEntityBanner banner = (BlockEntityBanner) blockEntity;
            item.setDamage(banner.getBaseColor() & 0xf);
            int type = banner.namedTag.getInt("Type");
            if (type > 0) {
                item.setNamedTag((item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag())
                        .putInt("Type", type));
            }
            ListTag<CompoundTag> patterns = banner.namedTag.getList("Patterns", CompoundTag.class);
            if (!patterns.isEmpty()) {
                item.setNamedTag((item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag())
                        .putList(patterns));
            }
        }
        return item;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex((this.getDamage() + 1) / 4);
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);

            if (blockEntity instanceof BlockEntityBanner) {
                return ((BlockEntityBanner) blockEntity).getDyeColor();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public boolean isSolid() {
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
