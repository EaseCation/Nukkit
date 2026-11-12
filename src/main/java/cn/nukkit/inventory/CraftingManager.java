package cn.nukkit.inventory;

import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.inventory.recipe.CraftingGridMatcher;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Hash;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class CraftingManager {

//    public static BatchPacket packet = null;
//    public static BatchPacket packetRaw;

    protected static int RECIPE_COUNT = 1;

    private final Int2ObjectMap<List<CraftingRecipe>> craftingGridRecipes = new Int2ObjectOpenHashMap<>();

    public final Collection<Recipe> recipes = new ArrayDeque<>();

    protected final List<Recipe> networkRecipes = Utils.make(() -> {
        List<Recipe> list = new ArrayList<>();
        list.add(null);
        return list;
    });

    protected final Long2ObjectMap<List<ShapelessRecipe>> shapelessBasedRecipes = new Long2ObjectOpenHashMap<>();
//    protected final Long2ObjectMap<List<ShapelessRecipe>> wildcardShapelessBasedRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<List<ShapelessRecipe>> shapelessRecipes = new Long2ObjectOpenHashMap<>();
//    protected final Long2ObjectMap<List<ShapelessRecipe>> wildcardShapelessRecipes = new Long2ObjectOpenHashMap<>();
    protected final Map<RecipeTag, Long2ObjectMap<List<ShapelessRecipe>>> taggedShapelessRecipes = new EnumMap<>(RecipeTag.class);
//    protected final Map<RecipeTag, Long2ObjectMap<List<ShapelessRecipe>>> taggedWildcardShapelessRecipes = new EnumMap<>(RecipeTag.class);
    protected final Long2ObjectMap<List<ShapelessUserDataRecipe>> shapelessUserDataRecipes = new Long2ObjectOpenHashMap<>();
//    protected final Long2ObjectMap<List<ShapelessUserDataRecipe>> wildcardShapelessUserDataRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<List<ShapelessChemistryRecipe>> shapelessChemistryRecipes = new Long2ObjectOpenHashMap<>();
//    protected final Long2ObjectMap<List<ShapelessChemistryRecipe>> wildcardShapelessChemistryRecipes = new Long2ObjectOpenHashMap<>();

    protected final Long2ObjectMap<List<ShapedRecipe>> shapedBasedRecipes = new Long2ObjectOpenHashMap<>();
//    protected final Long2ObjectMap<List<ShapedRecipe>> wildcardShapedBasedRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<List<ShapedRecipe>> shapedRecipes = new Long2ObjectOpenHashMap<>();
//    protected final Long2ObjectMap<List<ShapedRecipe>> wildcardShapedRecipes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<List<ShapedChemistryRecipe>> shapedChemistryRecipes = new Long2ObjectOpenHashMap<>();
//    protected final Long2ObjectMap<List<ShapedChemistryRecipe>> wildcardShapedChemistryRecipes = new Long2ObjectOpenHashMap<>();

    protected final Long2ObjectMap<SmithingTransformRecipe> smithingRecipes = new Long2ObjectOpenHashMap<>();
    protected final Map<RecipeTag, List<SmithingTransformRecipe>> taggedSmithingRecipes = new EnumMap<>(RecipeTag.class);

    protected final List<SmithingTrimRecipe> smithingTrimRecipes = new ArrayList<>();
    protected final Map<RecipeTag, List<SmithingTrimRecipe>> taggedSmithingTrimRecipes = new EnumMap<>(RecipeTag.class);

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

    protected final Map<String, List<Item>> tags = new HashMap<>();

    public CraftingManager() {
        initialize();
    }

    protected void initialize() {
/*
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
*/
    }
/*
    @SuppressWarnings("unchecked")
    private void loadRecipes(Config config) {
        List<Map<?, ?>> recipes = config.getMapList("recipes");
        log.info("Loading recipes...");
        for (Map<?, ?> recipe : recipes) {
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
                                this.registerRecipe(new ShapelessRecipe(recipeId, recipeId, priority, Item.fromJson(first), sorted, tag));
                                break;
                            case 5:
                                this.registerRecipe(new ShapelessUserDataRecipe(recipeId, recipeId, priority, Item.fromJson(first), sorted, tag));
                                break;
                            case 6:
                                this.registerRecipe(new ShapelessChemistryRecipe(recipeId, recipeId, priority, Item.fromJson(first), sorted, tag));
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
                                this.registerRecipe(new ShapedRecipe(recipeId, recipeId, priority, Item.fromJson(first), shape, ingredients, extraResults, tag));
                                break;
                            case 7:
                                this.registerRecipe(new ShapedChemistryRecipe(recipeId, recipeId, priority, Item.fromJson(first), shape, ingredients, extraResults, tag));
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
                        this.registerRecipe(MultiRecipe.create(UUID.fromString((String) recipe.get("uuid"))));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("Exception during registering recipe", e);
            }
        }

        // Load brewing recipes
        List<Map<?, ?>> potionMixes = config.getMapList("potionMixes");

        for (Map<?, ?> potionMix : potionMixes) {
            int fromPotionId = ((Number) potionMix.get("fromPotionId")).intValue(); // gson returns doubles...
            int ingredient = ((Number) potionMix.get("ingredient")).intValue();
            int toPotionId = ((Number) potionMix.get("toPotionId")).intValue();

            registerBrewingRecipe(new BrewingRecipe(Item.get(ItemID.POTION, fromPotionId), Item.get(ingredient), Item.get(ItemID.POTION, toPotionId)));
        }

        List<Map<?, ?>> containerMixes = config.getMapList("containerMixes");

        for (Map<?, ?> containerMix : containerMixes) {
            int fromItemId = ((Number) containerMix.get("fromItemId")).intValue();
            int ingredient = ((Number) containerMix.get("ingredient")).intValue();
            int toItemId = ((Number) containerMix.get("toItemId")).intValue();

            registerContainerRecipe(new ContainerRecipe(Item.get(fromItemId), Item.get(ingredient), Item.get(toItemId)));
        }
    }
*//*
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

        for (SmithingTransformRecipe recipe : this.smithingRecipes.values()) {
            pk.addSmithingTransformRecipe(recipe);
        }

        for (SmithingTrimRecipe recipe : this.smithingTrimRecipes.values()) {
            pk.addSmithingTrimRecipe(recipe);
        }

        pk.tryEncode();

        packet = pk.compress(Compressor.ZLIB, Deflater.BEST_COMPRESSION);
        packetRaw = pk.compress(Compressor.ZLIB_RAW, Deflater.BEST_COMPRESSION);

        Track[] tracks = new Track[]{new Track(pk.pid(), pk.getCount())};
        packet.tracks = tracks;
        packetRaw.tracks = tracks;
    }
*/
    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    public Long2ObjectMap<SmithingTransformRecipe> getSmithingRecipes() {
        return smithingRecipes;
    }

    public List<SmithingTransformRecipe> getSmithingRecipes(RecipeTag tag) {
        return taggedSmithingRecipes.getOrDefault(tag, Collections.emptyList());
    }

    public List<SmithingTrimRecipe> getSmithingTrimRecipes() {
        return smithingTrimRecipes;
    }

    public Long2ObjectMap<FurnaceRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }

    public Long2ObjectMap<FurnaceRecipe> getFurnaceRecipes(RecipeTag tag) {
        Long2ObjectMap<FurnaceRecipe> recipes = taggedFurnaceRecipes.get(tag);
        return recipes == null ? Long2ObjectMaps.emptyMap() : recipes;
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
        if (recipe == null) recipe = recipes.get(Item.toHash(input.getId(), 0) | 1L);
        return recipe;
    }

    protected static long getMultiItemHash(Collection<Item> items) {
        if (items.isEmpty()) {
            return Hash.xxh64Void();
        }

        BinaryStream stream = new BinaryStream(items.size() * 8);
        for (Item item : items) {
            stream.putLong(item.asHashWithCount());
        }
        return Hash.xxh64(stream.getBufferUnsafe(), 0, stream.getCount());
    }

    protected static long getMultiItemHashWithoutCount(Item... items) {
        if (items.length == 0) {
            return Hash.xxh64Void();
        }

        BinaryStream stream = new BinaryStream(items.length * 8);
        for (Item item : items) {
            stream.putLong(item.asHash());
        }
        return Hash.xxh64(stream.getBufferUnsafe(), 0, stream.getCount());
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        Item input = recipe.getInput();
        this.furnaceRecipes.put(input.asHash(), recipe);
        // 物品散列低七位原本为空，用独立标记区分通配原料与明确的 aux=0。
        long hash = input.hasMeta() ? input.asHash() : Item.toHash(input.getId(), 0) | 1L;

        RecipeTag tag = recipe.getTag();
        taggedFurnaceRecipes.computeIfAbsent(tag, k -> new Long2ObjectOpenHashMap<>())
                .put(hash, recipe);
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        registerGridRecipe(recipe, recipe.getIngredientsAggregate().size());
//        long inputHash = getMultiItemHash(recipe.getIngredientList());
        long outputHash = recipe.getResult().asHash();
        shapedBasedRecipes.computeIfAbsent(outputHash, k -> new ArrayList<>())
                .add(recipe);

        switch (recipe.getType()) {
            case SHAPED:
                shapedRecipes.computeIfAbsent(outputHash, k -> new ArrayList<>())
                        .add(recipe);
                break;
            case SHAPED_CHEMISTRY:
                shapedChemistryRecipes.computeIfAbsent(outputHash, k -> new ArrayList<>())
                        .add((ShapedChemistryRecipe) recipe);
                break;
        }
    }

    public void registerRecipe(Recipe recipe) {
        if (recipe instanceof MultiRecipe) {
            recipe.registerToCraftingManager(this);
            return;
        }
        if (recipe instanceof CraftingRecipe) {
//            UUID id = Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()));
            int runtimeId = ++RECIPE_COUNT;
            UUID id = new UUID(Hash.xxh64(runtimeId), runtimeId);
            ((CraftingRecipe) recipe).setId(id);
            this.recipes.add(recipe);
        } else if (recipe instanceof FurnaceRecipe furnace) {
//            UUID id = Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()));
            int runtimeId = ++RECIPE_COUNT;
            UUID id = new UUID(Hash.xxh64(runtimeId), runtimeId);
            furnace.setId(id);
        }

        recipe.registerToCraftingManager(this);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        registerGridRecipe(recipe, recipe.getIngredientCount());
//        List<Item> list = recipe.getIngredientList();
//        list.sort(recipeComparator);

//        long inputHash = getMultiItemHash(list);
        long outputHash = recipe.getResult().asHash();
        shapelessBasedRecipes.computeIfAbsent(outputHash, k -> new ArrayList<>())
                .add(recipe);

        switch (recipe.getType()) {
            case SHAPELESS:
                shapelessRecipes.computeIfAbsent(outputHash, k -> new ArrayList<>())
                        .add(recipe);
                break;
            case SHAPELESS_USER_DATA:
                shapelessUserDataRecipes.computeIfAbsent(outputHash, k -> new ArrayList<>())
                        .add((ShapelessUserDataRecipe) recipe);
                break;
            case SHAPELESS_CHEMISTRY:
                shapelessChemistryRecipes.computeIfAbsent(outputHash, k -> new ArrayList<>())
                        .add((ShapelessChemistryRecipe) recipe);
                break;
        }

        RecipeTag tag = recipe.getTag();
        taggedShapelessRecipes.computeIfAbsent(tag, k -> new Long2ObjectOpenHashMap<>())
                .computeIfAbsent(outputHash, k -> new ArrayList<>())
                .add(recipe);
    }

    public void registerSmithingTransformRecipe(SmithingTransformRecipe recipe) {
        long hash = recipe.getResult().asHash();
        this.smithingRecipes.put(hash, recipe);

        RecipeTag tag = recipe.getTag();
        taggedSmithingRecipes.computeIfAbsent(tag, k -> new ArrayList<>())
                .add(recipe);
    }

    public void registerSmithingTrimRecipe(SmithingTrimRecipe recipe) {
        smithingTrimRecipes.add(recipe);
        taggedSmithingTrimRecipes.computeIfAbsent(recipe.getTag(), k -> new ArrayList<>())
                .add(recipe);
    }

    protected static long getContainerHash(int ingredientId, int containerId) {
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
        if (potion.isPotion()) {
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

    @Deprecated
    public CraftingRecipe matchRecipe(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList, RecipeTag tag) {
        long outputHash = primaryOutput.asHash();
//        long inputHash = -1;
//        boolean tried = false;

        if (tag == RecipeTag.CRAFTING_TABLE) {
            List<ShapedRecipe> recipeMap = shapedBasedRecipes.get(outputHash);
            if (recipeMap != null) {
//                inputList.sort(recipeComparator);
//                inputHash = getMultiItemHash(inputList);
//                tried = true;

//                ShapedRecipe recipe = recipeMap.get(inputHash);
//                if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
//                    return recipe;
//                }

                for (ShapedRecipe shapedRecipe : recipeMap) {
                    if (shapedRecipe.getTag() == tag && (shapedRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapedRecipe, inputList, primaryOutput, extraOutputList))) {
                        return shapedRecipe;
                    }
                }
            }
        }

        Long2ObjectMap<List<ShapelessRecipe>> shapelessRecipes = taggedShapelessRecipes.get(tag);
        if (shapelessRecipes != null) {
            List<ShapelessRecipe> recipes = shapelessRecipes.get(outputHash);
            if (recipes != null) {
//                if (!tried) {
//                    inputList.sort(recipeComparator);
//                    inputHash = getMultiItemHash(inputList);
//                }

//                ShapelessRecipe recipe = recipes.get(inputHash);
//                if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))
//                        && (!(recipe instanceof ShapelessUserDataRecipe userDataRecipe) || userDataRecipe.matchUserData(primaryOutput, inputList))) {
//                    return recipe;
//                }

                for (ShapelessRecipe shapelessRecipe : recipes) {
                    if ((shapelessRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapelessRecipe, inputList, primaryOutput, extraOutputList))
                            && (!(shapelessRecipe instanceof ShapelessUserDataRecipe userDataRecipe) || userDataRecipe.matchUserData(primaryOutput, inputList))) {
                        return shapelessRecipe;
                    }
                }
            }
        }

        for (MultiRecipe recipe : multiRecipes.values()) {
            if (recipe.supportsLegacyMatching() && recipe.getTag() == tag
                    && recipe.getResultUnsafe().asHash() == outputHash
                    && recipe.match(inputList, primaryOutput, extraOutputList, 1)) {
                return recipe;
            }
        }
        return null;
    }

    @Nullable
    public CraftingRecipe matchRecipeByOutputs(Item primaryOutput, Predicate<CraftingRecipe> predicate) {
        long outputHash = primaryOutput.asHash();

        List<ShapedRecipe> shapedRecipes = shapedBasedRecipes.get(outputHash);
        if (shapedRecipes != null) {
            for (ShapedRecipe recipe : shapedRecipes) {
                if (predicate.test(recipe)) {
                    return recipe;
                }
            }
        }

        List<ShapelessRecipe> shapelessRecipes = shapelessBasedRecipes.get(outputHash);
        if (shapelessRecipes != null) {
            for (ShapelessRecipe recipe : shapelessRecipes) {
                if (predicate.test(recipe)) {
                    return recipe;
                }
            }
        }

        for (MultiRecipe recipe : multiRecipes.values()) {
            if (recipe.isSupported() && recipe.getResultUnsafe().asHash() == outputHash && predicate.test(recipe)) {
                return recipe;
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

    @Nullable
    public CraftingGridMatch matchCraftingGrid(Item[] grid, int disabledSlots) {
        if (grid.length != 9) {
            return null;
        }
        int occupied = 0;
        for (Item item : grid) {
            if (item == null) {
                return null;
            }
            if (!item.isNull()) {
                occupied++;
            }
        }
        List<CraftingRecipe> candidates = craftingGridRecipes.get(occupied);
        if (candidates != null) {
            for (CraftingRecipe recipe : candidates) {
                CraftingGridMatch match = CraftingGridMatcher.match(recipe, grid, disabledSlots);
                if (match != null) {
                    return match;
                }
            }
        }
        List<CraftingRecipe> variableCandidates = craftingGridRecipes.get(0);
        if (occupied > 0 && variableCandidates != null) {
            for (CraftingRecipe recipe : variableCandidates) {
                CraftingGridMatch match = CraftingGridMatcher.match(recipe, grid, disabledSlots);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    private void registerGridRecipe(CraftingRecipe recipe, int slots) {
        if (recipe.getTag() == RecipeTag.CRAFTING_TABLE && slots >= 0 && slots <= 9
                && recipe.getType() != RecipeType.SHAPED_CHEMISTRY && recipe.getType() != RecipeType.SHAPELESS_CHEMISTRY) {
            craftingGridRecipes.computeIfAbsent(slots, ignored -> new ArrayList<>()).add(recipe);
        }
    }

    public void registerMultiRecipe(MultiRecipe recipe) {
        MultiRecipe previous = this.multiRecipes.put(recipe.getId(), recipe);
        if (previous != null) {
            for (List<CraftingRecipe> candidates : craftingGridRecipes.values()) {
                candidates.removeIf(candidate -> candidate instanceof MultiRecipe multi && multi.getId().equals(recipe.getId()));
            }
            for (int index = 1; index < networkRecipes.size(); index++) {
                if (networkRecipes.get(index) instanceof MultiRecipe multi && multi.getId().equals(recipe.getId())) {
                    networkRecipes.set(index, null);
                }
            }
        }
        if (recipe.isSupported()) {
            registerGridRecipe(recipe, recipe.getGridIngredientCount());
        }
    }

    @Nullable
    public MultiRecipe getMultiRecipe(UUID id) {
        return multiRecipes.get(id);
    }

    public void registerMaterialReducerRecipe(MaterialReducerRecipe recipe) {
        this.materialReducerRecipes.put(recipe.getInput().asHash(), recipe);
    }

    @Nullable
    public SmithingTransformRecipe matchSmithingRecipe(Item template, Item input, Item addition, RecipeTag tag) {
        List<SmithingTransformRecipe> recipes = taggedSmithingRecipes.get(tag);
        if (recipes != null) {
            for (SmithingTransformRecipe recipe : recipes) {
                if (matchesSmithingIngredients(recipe, template, input, addition)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    @Nullable
    public SmithingTrimRecipe matchSmithingTrimRecipe(Item template, Item input, Item addition, RecipeTag tag) {
        List<SmithingTrimRecipe> recipes = taggedSmithingTrimRecipes.get(tag);
        if (recipes != null) {
            for (SmithingTrimRecipe recipe : recipes) {
                if (matchesSmithingIngredients(recipe, template, input, addition)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    private static boolean matchesSmithingIngredients(SmithingRecipe recipe, Item template, Item input, Item addition) {
        return matchesSmithingIngredient(recipe.getTemplate(), template)
                && matchesSmithingIngredient(recipe.getBase(), input)
                && matchesSmithingIngredient(recipe.getIngredient(), addition);
    }

    private static boolean matchesSmithingIngredient(RecipeIngredient ingredient, Item item) {
        if (ingredient.getCount() == 0) {
            return item.isNull();
        }
        return ingredient.getCount() > 0 && item.getCount() >= ingredient.getCount() && ingredient.accepts(item);
    }

    public int registerNetworkRecipe(Recipe recipe) {
        int networkId = networkRecipes.size();
        recipe.setNetworkId(networkId);
        networkRecipes.add(recipe);
        return networkId;
    }

    public int getNetworkRecipeCount() {
        return networkRecipes.size();
    }

    @Nullable
    public Recipe getRecipe(int networkId) {
        if (networkId < 0 || networkId >= networkRecipes.size()) {
            return null;
        }
        return networkRecipes.get(networkId);
    }
}
