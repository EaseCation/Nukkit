package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.state.BlockLegacy;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.BlockStates;
import cn.nukkit.block.state.enumeration.MinecraftBlockFaceState;
import cn.nukkit.block.state.enumeration.MinecraftCardinalDirectionState;
import cn.nukkit.block.state.enumeration.MinecraftFacingDirectionState;
import cn.nukkit.block.state.enumeration.MinecraftVerticalHalfState;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public abstract class CustomBlock extends Block {
    private final int id;
    private final BlockLegacy legacyBlock;

    protected CustomBlock(int id) {
        this.id = id;
        legacyBlock = new BlockLegacy(id, "UNUSED"); //TODO
        addStates();
        legacyBlock.createPermutations();
    }

    protected void addStates() {
    }

    protected final void addState(BlockState state) {
        legacyBlock.addState(state);
    }

    public final int getVariantCount() {
        return legacyBlock.getVariantCount();
    }

    @Override
    public final int getId() {
        return id;
    }

    @Override
    public final boolean isBlockItem() {
        return true;
    }

    @Override
    public final boolean isStackedByData() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    protected final BlockLegacy getBlockLegacy() {
        return legacyBlock;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

    @Override
    public final boolean isVanilla() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            if (legacyBlock.hasState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)) {
                setState(BlockStates.MINECRAFT_CARDINAL_DIRECTION, MinecraftCardinalDirectionState.getPlacementDirection(player));
            }
            if (legacyBlock.hasState(BlockStates.MINECRAFT_FACING_DIRECTION)) {
                setState(BlockStates.MINECRAFT_FACING_DIRECTION, MinecraftFacingDirectionState.getPlacementDirection(player, this));
            }
        }
        if (legacyBlock.hasState(BlockStates.MINECRAFT_BLOCK_FACE)) {
            setState(BlockStates.MINECRAFT_BLOCK_FACE, MinecraftBlockFaceState.from(face.getIndex()));
        }
        if (legacyBlock.hasState(BlockStates.MINECRAFT_VERTICAL_HALF)) {
            setState(BlockStates.MINECRAFT_VERTICAL_HALF, face.isHorizontal() ? fy < 0.5f ? MinecraftVerticalHalfState.BOTTOM : MinecraftVerticalHalfState.TOP
                    : face == BlockFace.UP ? MinecraftVerticalHalfState.BOTTOM : MinecraftVerticalHalfState.TOP);
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }
}
