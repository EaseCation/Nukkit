package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerEnchantingOptionsRequestEvent;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentHelper;
import cn.nukkit.item.enchantment.EnchantmentOption;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.RandomSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = ENCHANTING_TABLE_INPUT - ENCHANTING_TABLE_INPUT_OFFSET;
    public static final int INGREDIENT_SLOT = ENCHANTING_TABLE_LAPIS_LAZULI - ENCHANTING_TABLE_INPUT_OFFSET;

    public static final int SIZE = 1 + ENCHANTING_TABLE_INPUT_LAST - ENCHANTING_TABLE_INPUT_OFFSET;

    private static final int MAX_BOOKSHELF_COUNT = 15;

    private final List<EnchantmentOption> options = new ArrayList<>(3);

    public EnchantInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ENCHANT_TABLE, ENCHANTING_TABLE_INPUT_OFFSET, SIZE, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ENCHANT;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;

        if (who.isServerAuthoritativeInventoryEnabled()) {
            return;
        }

        for (int i = 0; i < SIZE; i++) {
            for (Item drop : who.getInventory().addItem(getItem(i))) {
                who.dropItem(drop);
            }
            clear(i);
        }

        who.resetCraftingGridType();
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        if (isServerAuthoritativeInventoryEnabled() && index == INPUT_SLOT) {
            generateOptions(after);

            Player player = playerUI.getHolder();
            PlayerEnchantingOptionsRequestEvent event = new PlayerEnchantingOptionsRequestEvent(player, this, options);
            player.getServer().getPluginManager().callEvent(event);
            if (!getInputSlot().equalsExact(after == null ? Item.get(ItemID.AIR) : after)) {
                super.onSlotChange(index, before, after, send);
                return;
            }
            options.clear();
            if (!event.isCancelled()) {
                options.addAll(event.getOptions());
            }
            player.sendEnchantingTableOptions(options);
        }

        super.onSlotChange(index, before, after, send);
    }

    private void generateOptions(@Nullable Item input) {
        options.clear();

        if (!canEnchant(input)) {
            return;
        }

        if (input.is(ItemID.BOOK)) {
            input = Item.get(ItemID.ENCHANTED_BOOK);
        }
        RandomSource random = new NukkitRandom(playerUI.getHolder().getEnchantmentSeed());
        int bookshelfCount = countBookshelves();
        int baseRequiredLevel = random.nextRange(1, 8) + (bookshelfCount >> 1) + random.nextRange(0, bookshelfCount);
        int topRequiredLevel = Mth.floor(Math.max(baseRequiredLevel / 3f, 1));
        int middleRequiredLevel = Mth.floor(baseRequiredLevel * 2 / 3f + 1);
        int bottomRequiredLevel = Math.max(baseRequiredLevel, bookshelfCount * 2);

        options.add(createOption(random, input, topRequiredLevel));
        options.add(createOption(random, input, middleRequiredLevel));
        options.add(createOption(random, input, bottomRequiredLevel));
    }

    private int countBookshelves() {
        Position pos = getHolder();
        Level level = pos.level;
        int bookshelfCount = 0;
        for (int x = -2; x <= 2; x++) {
            XZ:
            for (int z = -2; z <= 2; z++) {
                // We only check blocks at a distance of 2 blocks from the enchanting table
                if (Math.abs(x) != 2 && Math.abs(z) != 2) {
                    continue;
                }

                // Ensure the space between the bookshelf stack at this X/Z and the enchanting table is empty
                for (int y = 0; y <= 1; y++) {
                    // Calculate the coordinates of the space between the bookshelf and the enchanting table
                    int spaceX = Mth.clamp(x, -1, 1);
                    int spaceZ = Mth.clamp(z, -1, 1);
                    Block spaceBlock = level.getBlock(pos.add(spaceX, y, spaceZ));
                    if (!spaceBlock.isAir()) {
                        continue XZ;
                    }
                }

                // Finally, check the number of bookshelves at the current position
                for (int y = 0; y <= 1; y++) {
                    Block block = level.getBlock(pos.add(x, y, z));
                    if (block.is(Block.BOOKSHELF)) {
                        bookshelfCount++;
                        if (bookshelfCount == MAX_BOOKSHELF_COUNT) {
                            return bookshelfCount;
                        }
                    }
                }
            }
        }
        return bookshelfCount;
    }

    private static EnchantmentOption createOption(RandomSource random, Item inputItem, int requiredXpLevel) {
        EnchantmentOption option = new EnchantmentOption();
        option.cost = requiredXpLevel;
        option.equipActivatedEnchantments = EnchantmentHelper.selectEnchantments(random, inputItem, requiredXpLevel, false).toArray(Enchantment.EMPTY);
        option.name = Enchantment.getRandomName(random);
        return option;
    }

    public Item getInputSlot() {
        return this.getItem(INPUT_SLOT);
    }

    public Item getReagentSlot() {
        return this.getItem(INGREDIENT_SLOT);
    }

    @Nullable
    public Item getOutput(int optionId) {
        EnchantmentOption option = getOption(optionId);
        if (option == null) {
            return null;
        }
        return getOutput(getInputSlot(), option);
    }

    public static boolean canEnchant(@Nullable Item input) {
        return input != null && !input.isNull() && input.getCount() == 1
                && input.getEnchantAbility() > 0 && !input.is(ItemID.ENCHANTED_BOOK) && !input.hasEnchantments();
    }

    @Nullable
    public static Item getOutput(Item input, EnchantmentOption option) {
        if (!canEnchant(input) || option.cost < 0 || option.equipActivatedEnchantments.length == 0) {
            return null;
        }
        Item output;
        if (input.is(ItemID.BOOK)) {
            output = Item.get(ItemID.ENCHANTED_BOOK);
            output.setCompoundTag(input.getCompoundTag());
            output.setCanPlaceOnBlocks(input.getCanPlaceOnBlocks());
            output.setCanDestroyBlocks(input.getCanDestroyBlocks());
        } else {
            output = input.clone();
        }
        return output.addEnchantment(option.equipActivatedEnchantments);
    }

    @Nullable
    public EnchantmentOption getOption(int optionId) {
        if (optionId < 0 || optionId >= options.size()) {
            return null;
        }
        return options.get(optionId);
    }
}
