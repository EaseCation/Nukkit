package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Hash;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.zip.Deflater;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class CraftingManager {

    public static BatchPacket packet = null;
    public static BatchPacket packetRaw;

    private static long RECIPE_COUNT = 1;

    public final Collection<Recipe> recipes = new ArrayDeque<>();

    protected final Long2ObjectMap<Long2ObjectMap<ShapelessRecipe>> shapelessBasedRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<Long2ObjectMap<ShapelessRecipe>> shapelessRecipes = new Long2ObjectOpenHashMap<>();
    protected final Map<RecipeTag, Long2ObjectMap<Long2ObjectMap<ShapelessRecipe>>> taggedShapelessRecipes = new EnumMap<>(RecipeTag.class);
    protected final Long2ObjectMap<Long2ObjectMap<ShulkerBoxRecipe>> shulkerBoxRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<Long2ObjectMap<ShapelessChemistryRecipe>> shapelessChemistryRecipes = new Long2ObjectOpenHashMap<>();

    protected final Long2ObjectMap<Long2ObjectMap<ShapedRecipe>> shapedBasedRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<Long2ObjectMap<ShapedRecipe>> shapedRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<Long2ObjectMap<ShapedChemistryRecipe>> shapedChemistryRecipes = new Long2ObjectOpenHashMap<>();

    protected final Long2ObjectMap<FurnaceRecipe> furnaceRecipes = new Long2ObjectOpenHashMap<>();
    protected final Map<RecipeTag, Long2ObjectMap<FurnaceRecipe>> taggedFurnaceRecipes = new EnumMap<>(RecipeTag.class);

    protected final Object2ObjectMap<UUID, MultiRecipe> multiRecipes = new Object2ObjectOpenHashMap<>();

    protected final Long2ObjectMap<BrewingRecipe> brewingRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<ContainerRecipe> containerRecipes = new Long2ObjectOpenHashMap<>();

    protected final Long2ObjectMap<MaterialReducerRecipe> materialReducerRecipes = new Long2ObjectOpenHashMap<>();

    public static final Comparator<Item> recipeComparator = (i1, i2) -> {
        if (i1.getId() > i2.getId()) {
            return 1;
        } else if (i1.getId() < i2.getId()) {
            return -1;
        } else if (i1.getDamage() > i2.getDamage()) {
            return 1;
        } else if (i1.getDamage() < i2.getDamage()) {
            return -1;
        } else return Integer.compare(i1.getCount(), i2.getCount());
    };

    public CraftingManager() {
        InputStream recipesStream = Server.class.getClassLoader().getResourceAsStream("recipes.json");
        if (recipesStream == null) {
            throw new AssertionError("Unable to find recipes.json");
        }

        Config recipesConfig = new Config(Config.JSON);
        recipesConfig.load(recipesStream);
        this.loadRecipes(recipesConfig);

        String path = Server.getInstance().getDataPath() + "custom_recipes.json";
        File filePath = new File(path);

        if (filePath.exists()) {
            Config customRecipes = new Config(filePath, Config.JSON);
            this.loadRecipes(customRecipes);
        }
        this.rebuildPacket();

        log.info("Loaded " + this.recipes.size() + " recipes.");
    }

    @SuppressWarnings("unchecked")
    private void loadRecipes(Config config) {
        List<Map> recipes = config.getMapList("recipes");
        log.info("Loading recipes...");
        for (Map<String, Object> recipe : recipes) {
            try {
                int type = Utils.toInt(recipe.get("type"));
                switch (type) {
                    case 0: // shapeless
//                    case 5: // shapeless Shulker Box //TODO: nbt
                    case 6: // shapeless chemistry
                        String craftingBlock = (String) recipe.get("block");
                        RecipeTag tag = RecipeTag.byName(craftingBlock);
                        if (tag != RecipeTag.CRAFTING_TABLE && (type != 0 || tag != RecipeTag.CARTOGRAPHY_TABLE && tag != RecipeTag.STONECUTTER && tag != RecipeTag.SMITHING_TABLE)) {
                            log.trace("Skip an unknown shapeless recipe (type {}) tag: {}", type, craftingBlock);
                            continue;
                        }
                        // TODO: handle multiple result items
                        List<Map> outputs = ((List<Map>) recipe.get("output"));
                        if (outputs.size() > 1) {
                            continue;
                        }
                        Map<String, Object> first = outputs.get(0);
                        List<Item> sorted = new ArrayList<>();
                        for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                            sorted.add(Item.fromJson(ingredient));
                        }
                        // Bake sorted list
                        sorted.sort(recipeComparator);

                        String recipeId = (String) recipe.get("id");
                        int priority = Utils.toInt(recipe.get("priority"));

                        switch (type) {
                            case 0:
                                this.registerRecipe(new ShapelessRecipe(recipeId, priority, Item.fromJson(first), sorted, tag));
                                break;
                            case 5:
                                this.registerRecipe(new ShulkerBoxRecipe(recipeId, priority, Item.fromJson(first), sorted, tag));
                                break;
                            case 6:
                                this.registerRecipe(new ShapelessChemistryRecipe(recipeId, priority, Item.fromJson(first), sorted, tag));
                                break;
                        }
                        break;
                    case 1: // shaped
                    case 7: // shaped chemistry
                        craftingBlock = (String) recipe.get("block");
                        tag = RecipeTag.byName(craftingBlock);
                        if (tag != RecipeTag.CRAFTING_TABLE) {
                            log.warn("Unexpected shaped recipe (type {}) tag: {}", type, craftingBlock);
                            continue;
                        }
                        outputs = (List<Map>) recipe.get("output");

                        first = outputs.remove(0);
                        String[] shape = ((List<String>) recipe.get("shape")).toArray(new String[0]);
                        Char2ObjectMap<Item> ingredients = new Char2ObjectOpenHashMap<>();
                        List<Item> extraResults = new ArrayList<>();

                        Map<String, Map<String, Object>> input = (Map) recipe.get("input");
                        for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
                            char ingredientChar = ingredientEntry.getKey().charAt(0);
                            Item ingredient = Item.fromJson(ingredientEntry.getValue());

                            ingredients.put(ingredientChar, ingredient);
                        }

                        for (Map<String, Object> data : outputs) {
                            extraResults.add(Item.fromJson(data));
                        }

                        recipeId = (String) recipe.get("id");
                        priority = Utils.toInt(recipe.get("priority"));

                        switch (type) {
                            case 1:
                                this.registerRecipe(new ShapedRecipe(recipeId, priority, Item.fromJson(first), shape, ingredients, extraResults, tag));
                                break;
                            case 7:
                                this.registerRecipe(new ShapedChemistryRecipe(recipeId, priority, Item.fromJson(first), shape, ingredients, extraResults, tag));
                                break;
                        }
                        break;
                    case 2: // smelting
                    case 3: // smelting data
                        craftingBlock = (String) recipe.get("block");
                        tag = RecipeTag.byName(craftingBlock);
                        if (tag != RecipeTag.FURNACE && tag != RecipeTag.BLAST_FURNACE && tag != RecipeTag.SMOKER && tag != RecipeTag.CAMPFIRE && tag != RecipeTag.SOUL_CAMPFIRE) {
                            log.trace("Skip an unknown smelting recipe tag: {}", craftingBlock);
                            continue;
                        }
                        Map<String, Object> resultMap = (Map) recipe.get("output");
                        Item resultItem = Item.fromJson(resultMap);
                        Item inputItem;
                        try {
                            Map<String, Object> inputMap = (Map) recipe.get("input");
                            inputItem = Item.fromJson(inputMap);
                        } catch (Exception old) {
                            inputItem = Item.get(Utils.toInt(recipe.get("inputId")), recipe.containsKey("inputDamage") ? Utils.toInt(recipe.get("inputDamage")) : -1, 1);
                        }
                        this.registerRecipe(new FurnaceRecipe(resultItem, inputItem, tag));
                        break;
                    case 4: // special hardcoded (e.g. Anvil)
                        this.registerRecipe(new MultiRecipe(UUID.fromString((String) recipe.get("uuid"))));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("Exception during registering recipe", e);
            }
        }

        // Load brewing recipes
        List<Map> potionMixes = config.getMapList("potionMixes");

        for (Map potionMix : potionMixes) {
            int fromPotionId = ((Number) potionMix.get("fromPotionId")).intValue(); // gson returns doubles...
            int ingredient = ((Number) potionMix.get("ingredient")).intValue();
            int toPotionId = ((Number) potionMix.get("toPotionId")).intValue();

            registerBrewingRecipe(new BrewingRecipe(Item.get(ItemID.POTION, fromPotionId), Item.get(ingredient), Item.get(ItemID.POTION, toPotionId)));
        }

        List<Map> containerMixes = config.getMapList("containerMixes");

        for (Map containerMix : containerMixes) {
            int fromItemId = ((Number) containerMix.get("fromItemId")).intValue();
            int ingredient = ((Number) containerMix.get("ingredient")).intValue();
            int toItemId = ((Number) containerMix.get("toItemId")).intValue();

            registerContainerRecipe(new ContainerRecipe(Item.get(fromItemId), Item.get(ingredient), Item.get(toItemId)));
        }
    }

    public void rebuildPacket() {
        CraftingDataPacket pk = new CraftingDataPacket();
        pk.cleanRecipes = true;

        for (Recipe recipe : this.getRecipes()) {
            if (recipe instanceof ShapedRecipe) {
                pk.addShapedRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                pk.addShapelessRecipe((ShapelessRecipe) recipe);
            }
        }

        for (FurnaceRecipe recipe : this.furnaceRecipes.values()) {
            pk.addFurnaceRecipe(recipe);
        }

        for (MultiRecipe recipe : multiRecipes.values()) {
            pk.addMultiRecipe(recipe);
        }

        for (BrewingRecipe recipe : brewingRecipes.values()) {
            pk.addBrewingRecipe(recipe);
        }

        for (ContainerRecipe recipe : containerRecipes.values()) {
            pk.addContainerRecipe(recipe);
        }

        for (MaterialReducerRecipe recipe : materialReducerRecipes.values()) {
            pk.addMaterialReducerRecipe(recipe);
        }

        pk.tryEncode();

        packet = pk.compress(Deflater.BEST_COMPRESSION);
        packetRaw = pk.compress(Deflater.BEST_COMPRESSION, true);
    }

    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    public Long2ObjectMap<FurnaceRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }

    public Object2ObjectMap<UUID, MultiRecipe> getMultiRecipes() {
        return multiRecipes;
    }

    public Long2ObjectMap<BrewingRecipe> getBrewingRecipes() {
        return brewingRecipes;
    }

    public Long2ObjectMap<ContainerRecipe> getContainerRecipes() {
        return containerRecipes;
    }

    public Long2ObjectMap<MaterialReducerRecipe> getMaterialReducerRecipes() {
        return materialReducerRecipes;
    }

    /**
     * @deprecated use {#matchFurnaceRecipe(Item, RecipeTag)} instead
     */
    @Deprecated
    public FurnaceRecipe matchFurnaceRecipe(Item input) {
        return matchFurnaceRecipe(input, RecipeTag.FURNACE);
    }

    public FurnaceRecipe matchFurnaceRecipe(Item input, RecipeTag tag) {
        Long2ObjectMap<FurnaceRecipe> recipes = taggedFurnaceRecipes.get(tag);
        if (recipes == null) {
            return null;
        }

        FurnaceRecipe recipe = recipes.get(input.asHash());
        if (recipe == null) recipe = recipes.get(Item.toHash(input.getId(), 0));
        return recipe;
    }

    private static long getMultiItemHash(Collection<Item> items) {
        if (items.isEmpty()) {
            return Hash.xxh64Void();
        }

        BinaryStream stream = new BinaryStream();
        for (Item item : items) {
            stream.putLong(item.asHashWithCount());
        }
        return Hash.xxh64(stream.getBufferUnsafe(), 0, stream.getCount());
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        long hash = recipe.getInput().asHash();
        this.furnaceRecipes.put(hash, recipe);

        RecipeTag tag = recipe.getTag();
        Long2ObjectMap<FurnaceRecipe> recipes = taggedFurnaceRecipes.get(tag);
        if (recipes == null) {
            recipes = new Long2ObjectOpenHashMap<>();
            taggedFurnaceRecipes.put(tag, recipes);
        }
        recipes.put(hash, recipe);
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        long inputHash = getMultiItemHash(recipe.getIngredientList());
        long outputHash = recipe.getResult().asHash();
        shapedBasedRecipes.computeIfAbsent(outputHash, k -> new Long2ObjectOpenHashMap<>())
                .put(inputHash, recipe);

        switch (recipe.getType()) {
            case SHAPED:
                shapedRecipes.computeIfAbsent(outputHash, k -> new Long2ObjectOpenHashMap<>())
                        .put(inputHash, recipe);
                break;
            case SHAPED_CHEMISTRY:
                shapedChemistryRecipes.computeIfAbsent(outputHash, k -> new Long2ObjectOpenHashMap<>())
                        .put(inputHash, (ShapedChemistryRecipe) recipe);
                break;
        }
    }

    public void registerRecipe(Recipe recipe) {
        if (recipe instanceof CraftingRecipe) {
//            UUID id = Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()));
            long runtimeId = ++RECIPE_COUNT;
            UUID id = new UUID(Hash.xxh64(runtimeId), runtimeId);
            ((CraftingRecipe) recipe).setId(id);
            this.recipes.add(recipe);
        }

        recipe.registerToCraftingManager(this);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        List<Item> list = recipe.getIngredientList();
        list.sort(recipeComparator);

        long inputHash = getMultiItemHash(list);
        long outputHash = recipe.getResult().asHash();
        shapelessBasedRecipes.computeIfAbsent(outputHash, k -> new Long2ObjectOpenHashMap<>())
                .put(inputHash, recipe);

        switch (recipe.getType()) {
            case SHAPELESS:
                shapelessRecipes.computeIfAbsent(outputHash, k -> new Long2ObjectOpenHashMap<>())
                        .put(inputHash, recipe);
                break;
            case SHULKER_BOX:
                shulkerBoxRecipes.computeIfAbsent(outputHash, k -> new Long2ObjectOpenHashMap<>())
                        .put(inputHash, (ShulkerBoxRecipe) recipe);
                break;
            case SHAPELESS_CHEMISTRY:
                shapelessChemistryRecipes.computeIfAbsent(outputHash, k -> new Long2ObjectOpenHashMap<>())
                        .put(inputHash, (ShapelessChemistryRecipe) recipe);
                break;
        }

        RecipeTag tag = recipe.getTag();
        Long2ObjectMap<Long2ObjectMap<ShapelessRecipe>> shapelessRecipes = taggedShapelessRecipes.get(tag);
        if (shapelessRecipes == null) {
            shapelessRecipes = new Long2ObjectOpenHashMap<>();
            taggedShapelessRecipes.put(tag, shapelessRecipes);
        }
        Long2ObjectMap<ShapelessRecipe> recipes = shapelessRecipes.get(outputHash);
        if (recipes == null) {
            recipes = new Long2ObjectOpenHashMap<>();
            shapelessRecipes.put(outputHash, recipes);
        }
        recipes.put(inputHash, recipe);
    }

    private static long getContainerHash(int ingredientId, int containerId) {
        return ((long) ingredientId << 32) | (containerId & 0xffffffffL);
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        this.brewingRecipes.put(Item.toHash(input.getId(), potion.getDamage()), recipe);
    }

    public void registerContainerRecipe(ContainerRecipe recipe) {
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        this.containerRecipes.put(getContainerHash(input.getId(), potion.getId()), recipe);
    }

    public BrewingRecipe matchBrewingRecipe(Item input, Item potion) {
        int id = potion.getId();
        if (id == Item.POTION || id == Item.SPLASH_POTION || id == Item.LINGERING_POTION) {
            return this.brewingRecipes.get(Item.toHash(input.getId(), potion.getDamage()));
        }

        return null;
    }

    public ContainerRecipe matchContainerRecipe(Item input, Item potion) {
        return this.containerRecipes.get(getContainerHash(input.getId(), potion.getId()));
    }

    /**
     * @deprecated use {#matchRecipe(List, Item, List, RecipeTag)} instead
     */
    @Deprecated
    public CraftingRecipe matchRecipe(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        return matchRecipe(inputList, primaryOutput, extraOutputList, RecipeTag.CRAFTING_TABLE);
    }

    public CraftingRecipe matchRecipe(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList, RecipeTag tag) {
        //TODO: try to match special recipes before anything else (first they need to be implemented!)

        long outputHash = primaryOutput.asHash();
        long inputHash = -1;
        boolean tried = false;

        if (tag == RecipeTag.CRAFTING_TABLE) {
            Long2ObjectMap<ShapedRecipe> recipeMap = shapedBasedRecipes.get(outputHash);
            if (recipeMap != null) {
                inputList.sort(recipeComparator);
                inputHash = getMultiItemHash(inputList);
                tried = true;

                ShapedRecipe recipe = recipeMap.get(inputHash);
                if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                    return recipe;
                }

                for (ShapedRecipe shapedRecipe : recipeMap.values()) {
                    if (shapedRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapedRecipe, inputList, primaryOutput, extraOutputList)) {
                        return shapedRecipe;
                    }
                }
            }
        }

        Long2ObjectMap<Long2ObjectMap<ShapelessRecipe>> shapelessRecipes = taggedShapelessRecipes.get(tag);
        if (shapelessRecipes == null) {
            return null;
        }

        Long2ObjectMap<ShapelessRecipe> recipes = shapelessRecipes.get(outputHash);
        if (recipes != null) {
            if (!tried) {
                inputList.sort(recipeComparator);
                inputHash = getMultiItemHash(inputList);
            }

            ShapelessRecipe recipe = recipes.get(inputHash);
            if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                return recipe;
            }

            for (ShapelessRecipe shapelessRecipe : recipes.values()) {
                if (shapelessRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapelessRecipe, inputList, primaryOutput, extraOutputList)) {
                    return shapelessRecipe;
                }
            }
        }

        return null;
    }

    private boolean matchItemsAccumulation(CraftingRecipe recipe, List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        Item recipeResult = recipe.getResult();
        if (primaryOutput.equals(recipeResult, recipeResult.hasMeta(), recipeResult.hasCompoundTag()) && primaryOutput.getCount() % recipeResult.getCount() == 0) {
            int multiplier = primaryOutput.getCount() / recipeResult.getCount();
            return recipe.matchItems(inputList, extraOutputList, multiplier);
        }
        return false;
    }

    public void registerMultiRecipe(MultiRecipe recipe) {
        this.multiRecipes.put(recipe.getId(), recipe);
    }

    public void registerMaterialReducerRecipe(MaterialReducerRecipe recipe) {
        this.materialReducerRecipes.put(recipe.getInput().asHash(), recipe);
    }

    public static class Entry {
        final int resultItemId;
        final int resultMeta;
        final int ingredientItemId;
        final int ingredientMeta;
        final String recipeShape;
        final int resultAmount;

        public Entry(int resultItemId, int resultMeta, int ingredientItemId, int ingredientMeta, String recipeShape, int resultAmount) {
            this.resultItemId = resultItemId;
            this.resultMeta = resultMeta;
            this.ingredientItemId = ingredientItemId;
            this.ingredientMeta = ingredientMeta;
            this.recipeShape = recipeShape;
            this.resultAmount = resultAmount;
        }
    }
}
