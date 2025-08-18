package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class BlockAnvil extends BlockFallable implements Faceable {
    public static final int DIRECTION_MASK = 0b11;
    public static final int DIRECTION_BITS = 2;
    public static final int DAMAGE_MASK = 0b1100;

    public static final int UNDAMAGED = 0 << 2;
    public static final int SLIGHTLY_DAMAGED = 1 << 2;
    public static final int VERY_DAMAGED = 2 << 2;
    public static final int BROKEN = 3 << 2;

    private static final String[] NAMES = new String[]{
            "Anvil",
            "Chipped Anvil",
            "Damaged Anvil",
            "Anvil",
    };

    private static final int[] FACES = {1, 2, 3, 0};

    public BlockAnvil() {
        this(0);
    }

    public BlockAnvil(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ANVIL;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public float getHardness() {
        return 5;
    }

    @Override
    public float getResistance() {
        return 6000;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return NAMES[(this.getDamage() & DAMAGE_MASK) >> DIRECTION_BITS];
    }

    @Override
    public Block getPlacementBlock(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        return get(getId(), getDamage() & ~DIRECTION_MASK | FACES[player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0]);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        setDamage(getDamage() & ~DIRECTION_MASK | FACES[player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            player.addWindow(new AnvilInventory(player.getUIInventory(), this), Player.ANVIL_WINDOW_ID);
        }
        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId(), getDamage() & ~DIRECTION_MASK);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    this.toItem(true)
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.METAL_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & DIRECTION_MASK);
    }

    @Override
    public double getMinX() {
        return this.x + (this.getBlockFace().getAxis() == BlockFace.Axis.X ? 0 : 2 / 16.0);
    }

    @Override
    public double getMinZ() {
        return this.z + (this.getBlockFace().getAxis() == BlockFace.Axis.Z ? 0 : 2 / 16.0);
    }

    @Override
    public double getMaxX() {
        return this.x + (this.getBlockFace().getAxis() == BlockFace.Axis.X ? 1 : 1 - 2 / 16.0);
    }

    @Override
    public double getMaxZ() {
        return this.z + (this.getBlockFace().getAxis() == BlockFace.Axis.Z ? 1 : 1 - 2 / 16.0);
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
