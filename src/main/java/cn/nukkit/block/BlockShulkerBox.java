package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.ShulkerBoxInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import java.util.Map;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

/**
 * Created by PetteriM1
 */
public class BlockShulkerBox extends BlockTransparent {
    public static final int[] SHULKER_BOXES = {
            WHITE_SHULKER_BOX,
            ORANGE_SHULKER_BOX,
            MAGENTA_SHULKER_BOX,
            LIGHT_BLUE_SHULKER_BOX,
            YELLOW_SHULKER_BOX,
            LIME_SHULKER_BOX,
            PINK_SHULKER_BOX,
            GRAY_SHULKER_BOX,
            LIGHT_GRAY_SHULKER_BOX,
            CYAN_SHULKER_BOX,
            PURPLE_SHULKER_BOX,
            BLUE_SHULKER_BOX,
            BROWN_SHULKER_BOX,
            GREEN_SHULKER_BOX,
            RED_SHULKER_BOX,
            BLACK_SHULKER_BOX,
    };

    public BlockShulkerBox() {
    }

    @Override
    public int getId() {
        return UNDYED_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Shulker Box";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SHULKER_BOX;
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 2;
        }
        return 2.5f;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(this.getItemId(), this.getDamage());

        BlockEntityShulkerBox t = (BlockEntityShulkerBox) this.getLevel().getBlockEntity(this);

        if (t != null) {
            ShulkerBoxInventory i = t.getInventory();

            if (!i.isEmpty()) {
                CompoundTag nbt = item.getNamedTag();
                if (nbt == null)
                    nbt = new CompoundTag("");

                ListTag<CompoundTag> items = new ListTag<>();

                for (int it = 0; it < i.getSize(); it++) {
                    if (i.getItem(it).getId() != Item.AIR) {
                        CompoundTag d = NBTIO.putItemHelper(i.getItem(it), it);
                        items.add(d);
                    }
                }

                nbt.put("Items", items);

                item.setCompoundTag(nbt);
            }

            if (t.hasName()) {
                item.setCustomName(t.getName());
            }
        }

        return item;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.getLevel().setBlock(block, this, true);
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SHULKER_BOX)
                .putByte("facing", face.getIndex());

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        CompoundTag t = item.getNamedTag();

        // This code gets executed when the player has broken the shulker box and placed it back (©Kevims 2020)
        if (t != null && t.contains("Items")) {
            nbt.putList(t.getList("Items"));
        }

        // This code gets executed when the player has copied the shulker box in creative mode (©Kevims 2020)
        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityShulkerBox box = (BlockEntityShulkerBox) BlockEntities.createBlockEntity(BlockEntityType.SHULKER_BOX, this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4), nbt);
        return box != null;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityShulkerBox box;
            if (t instanceof BlockEntityShulkerBox) {
                box = (BlockEntityShulkerBox) t;
            } else {
                CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SHULKER_BOX);
                box = (BlockEntityShulkerBox) BlockEntities.createBlockEntity(BlockEntityType.SHULKER_BOX, getChunk(), nbt);
                if (box == null) {
                    return false;
                }
            }

            Block block = this.getSide(BlockFace.fromIndex(box.namedTag.getByte("facing")));
            if (!block.isAir() && !block.isLiquid() && !block.canBeFlowedInto()) {
                return true;
            }

            player.addWindow(box.getInventory());
        }

        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity be = this.getLevel().getBlockEntity(this);

        if (!(be instanceof InventoryHolder)) {
            return 0;
        }

        return ContainerInventory.calculateRedstone(((InventoryHolder) be).getInventory());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    public DyeColor getDyeColor() {
        return null;
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isShulkerBox() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
