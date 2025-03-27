package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.Map;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockEnderChest extends BlockTransparent implements Faceable {

    private static final int[] FACES = {2, 5, 3, 4};

    public BlockEnderChest() {
        this(0);
    }

    public BlockEnderChest(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getId() {
        return ENDER_CHEST;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.ENDER_CHEST;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public String getName() {
        return "Ender Chest";
    }

    @Override
    public float getHardness() {
        return 22.5f;
    }

    @Override
    public float getResistance() {
        return 3000;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9475;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(FACES[player != null ? player.getDirection().getHorizontalIndex() : 0]);

        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.ENDER_CHEST);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityEnderChest ender = (BlockEntityEnderChest) BlockEntities.createBlockEntity(BlockEntityType.ENDER_CHEST, this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        return ender != null;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            Block top = this.up();
            if (!top.isTransparent()) {
                return true;
            }

            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityEnderChest chest;
            if (t instanceof BlockEntityEnderChest) {
                chest = (BlockEntityEnderChest) t;
            } else {
                CompoundTag nbt = new CompoundTag("")
                        .putString("id", BlockEntity.ENDER_CHEST)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                chest = (BlockEntityEnderChest) BlockEntities.createBlockEntity(BlockEntityType.ENDER_CHEST, this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
                if (chest == null) {
                    return false;
                }
            }

            if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag) {
                if (!chest.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.setViewingEnderChest(chest);
            player.addWindow(player.getEnderChestInventory());
        }

        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(getItemId(OBSIDIAN), 0, 8),
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_50.isAvailable();
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }
}
