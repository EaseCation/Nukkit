package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;

import java.util.concurrent.ThreadLocalRandom;

public class BlockComposter extends BlockTransparentMeta {

    private static final Int2FloatMap COMPOSTABLES = new Int2FloatOpenHashMap();

    public BlockComposter() {
        this(0);
    }

    public BlockComposter(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Composter";
    }

    @Override
    public int getId() {
        return COMPOSTER;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return Math.min(getDamage(), 7);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item) {
        Item drop = this.toItem(true);
        return getDamage() == 0x8 ? new Item[]{
                drop,
                Item.get(ItemID.DYE, ItemDye.BONE_MEAL),
        } : new Item[]{drop};
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (getDamage() == 0x7) {
            level.scheduleUpdate(this, 20);
        }

        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        int meta = getDamage();

        if (meta == 0x7) {
            return true;
        }

        if (meta > 0x7) {
            level.addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_EMPTY);

            if (level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                level.dropItem(this, Item.get(ItemID.DYE, ItemDye.BONE_MEAL));
            }

            setDamage(0);
            level.setBlock(this, this, true);
            return true;
        }

        float chance = COMPOSTABLES.get(item.getId());
        if (chance <= 0) {
            return true;
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        level.addLevelEvent(this.add(0, 0.75, 0), LevelEventPacket.EVENT_PARTICLE_BONEMEAL);
        if (chance > ThreadLocalRandom.current().nextFloat()) {
            level.addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_FILL);
            return true;
        }
        level.addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_FILL_SUCCESS);

        setDamage(++meta);
        level.setBlock(this, this, true);

        if (meta == 0x7) {
            level.scheduleUpdate(this, 20);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
         if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int meta = this.getDamage();
            if (meta == 0x7) {
                level.addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_READY);

                this.setDamage(meta + 1);
                this.level.setBlock(this, this, true);
                return Level.BLOCK_UPDATE_SCHEDULED;
            }
        }
        return 0;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face != BlockFace.UP;
    }

    private static void registerCompostableItems(float chance, int... itemIds) {
        for (int itemId : itemIds) {
            COMPOSTABLES.put(itemId, chance);
        }
    }

    static {
        COMPOSTABLES.defaultReturnValue(0);
        registerCompostableItems(0.3f,
                ItemID.BEETROOT_SEEDS,
                ItemID.DRIED_KELP,
                getItemId(TALLGRASS),
                getItemId(GRASS),
                getItemId(HANGING_ROOTS),
                getItemId(MANGROVE_ROOTS),
                ItemID.KELP,
                getItemId(LEAVES),
                getItemId(LEAVES2),
                getItemId(AZALEA_LEAVES),
                getItemId(MANGROVE_LEAVES),
                ItemID.MELON_SEEDS,
                getItemId(MOSS_CARPET),
                ItemID.PUMPKIN_SEEDS,
                getItemId(SAPLING),
                getItemId(MANGROVE_PROPAGULE),
                getItemId(SEAGRASS),
                getItemId(SMALL_DRIPLEAF_BLOCK),
                ItemID.SWEET_BERRIES,
                ItemID.WHEAT_SEEDS
        );
        registerCompostableItems(0.5f,
                getItemId(CACTUS),
                getItemId(DRIED_KELP_BLOCK),
                getItemId(AZALEA_LEAVES_FLOWERED),
                getItemId(GLOW_LICHEN),
                ItemID.MELON_SLICE,
                ItemID.NETHER_SPROUTS,
                ItemID.SUGAR_CANE,
                getItemId(DOUBLE_PLANT),
                getItemId(TWISTING_VINES),
                getItemId(VINE),
                getItemId(WEEPING_VINES)
        );
        registerCompostableItems(0.65f,
                ItemID.APPLE,
                getItemId(AZALEA),
                ItemID.BEETROOT,
                getItemId(BIG_DRIPLEAF),
                ItemID.CARROT,
                getItemId(YELLOW_FLOWER),
                getItemId(RED_FLOWER),
                getItemId(WITHER_ROSE),
                getItemId(CRIMSON_FUNGUS),
                getItemId(WARPED_FUNGUS),
                getItemId(WATERLILY),
                getItemId(MELON_BLOCK),
                getItemId(BROWN_MUSHROOM),
                getItemId(RED_MUSHROOM),
                ItemID.NETHER_WART,
                ItemID.POTATO,
                getItemId(PUMPKIN),
                getItemId(CARVED_PUMPKIN),
                getItemId(CRIMSON_ROOTS),
                getItemId(WARPED_ROOTS),
                getItemId(SEA_PICKLE),
                getItemId(SHROOMLIGHT),
                getItemId(SPORE_BLOSSOM),
                ItemID.WHEAT
        );
        registerCompostableItems(0.85f,
                ItemID.BAKED_POTATO,
                ItemID.BREAD,
                ItemID.COOKIE,
                getItemId(FLOWERING_AZALEA),
                getItemId(HAY_BLOCK),
                getItemId(BROWN_MUSHROOM_BLOCK),
                getItemId(RED_MUSHROOM_BLOCK),
                getItemId(NETHER_WART_BLOCK),
                getItemId(WARPED_WART_BLOCK)
        );
        registerCompostableItems(1,
                ItemID.CAKE,
                ItemID.PUMPKIN_PIE
        );
    }
}
