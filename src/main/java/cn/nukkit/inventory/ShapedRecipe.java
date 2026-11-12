package cn.nukkit.inventory;

import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.inventory.recipe.RecipeMatcher;
import cn.nukkit.inventory.recipe.UnlockingContext;
import cn.nukkit.item.Item;
import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.collection.CharObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ShapedRecipe implements CraftingRecipe {
    @Nullable
    private final String vanillaRecipeId;

    private String recipeId;
    private final Item primaryResult;
    private final List<Item> extraResults = new ObjectArrayList<>();

    private final List<RecipeIngredient> ingredientsAggregate;

    private UUID uuid;

    private final String[] shape;
    private final int priority;

    private final CharObjectMap<RecipeIngredient> ingredients = new CharObjectHashMap<>();

    private final RecipeTag tag;
    private int networkId;

    //TODO: recipe unlocking
    private final UnlockingContext unlockingContext = UnlockingContext.ALWAYS_UNLOCKED;
    private final List<RecipeIngredient> unlockingIngredients = new ObjectArrayList<>();

    private final boolean assumeSymmetry = true; //TODO: 1.20.80+

    public ShapedRecipe(Item primaryResult, String[] shape, Map<Character, RecipeIngredient> ingredients, List<Item> extraResults, RecipeTag tag) {
        this(null, null, 1, primaryResult, shape, ingredients, extraResults, tag);
    }

    /**
     * Constructs a ShapedRecipe instance.
     *
     * @param primaryResult    Primary result of the recipe
     * @param shape<br>        Array of 1, 2, or 3 strings representing the rows of the recipe.
     *                         This accepts an array of 1, 2 or 3 strings. Each string should be of the same length and must be at most 3
     *                         characters long. Each character represents a unique type of ingredient. Spaces are interpreted as air.
     * @param ingredients<br>  Char =&gt; Item map of items to be set into the shape.
     *                         This accepts an array of Items, indexed by character. Every unique character (except space) in the shape
     *                         array MUST have a corresponding item in this list. Space character is automatically treated as air.
     * @param extraResults<br> List of additional result items to leave in the crafting grid afterwards. Used for things like cake recipe
     *                         empty buckets.
     *                         <p>
     *                         Note: Recipes **do not** need to be square. Do NOT add padding for empty rows/columns.
     */
    public ShapedRecipe(@Nullable String vanillaRecipeId, String recipeId, int priority, Item primaryResult, String[] shape, Map<Character, RecipeIngredient> ingredients, List<Item> extraResults, RecipeTag tag) {
        this.vanillaRecipeId = vanillaRecipeId;
        this.recipeId = recipeId;
        this.priority = priority;
        int rowCount = shape.length;
        if (rowCount > 3 || rowCount == 0) {
            throw new IllegalArgumentException("Shaped recipes may only have 1, 2 or 3 rows, not " + rowCount);
        }

        int columnCount = shape[0].length();
        if (columnCount > 3 || columnCount == 0) {
            throw new IllegalArgumentException("Shaped recipes may only have 1, 2 or 3 columns, not " + columnCount);
        }

        for (String row : shape) {
            if (row.length() != columnCount) {
                throw new IllegalArgumentException("Shaped recipe rows must all have the same length (expected " + columnCount + ", got " + row.length() + ")");
            }

            for (int x = 0; x < columnCount; ++x) {
                char c = row.charAt(x);

                if (c != ' ' && !ingredients.containsKey(c)) {
                    throw new IllegalArgumentException("No item specified for symbol '" + c + "'");
                }
            }
        }

        this.primaryResult = primaryResult.clone();
        this.extraResults.addAll(extraResults);

        this.shape = shape;

        for (Map.Entry<Character, RecipeIngredient> entry : ingredients.entrySet()) {
            this.setIngredient(entry.getKey(), entry.getValue());
        }

        this.ingredientsAggregate = new ObjectArrayList<>();
        for (char c : String.join("", this.shape).toCharArray()) {
            if (c == ' ') {
                continue;
            }
            RecipeIngredient ingredient = this.ingredients.get(c);
            /*for (RecipeIngredient existingIngredient : this.ingredientsAggregate) {
                if (existingIngredient.equals(ingredient, ingredient.hasMeta(), ingredient.hasCompoundTag())) {
                    existingIngredient.setCount(existingIngredient.getCount() + ingredient.getCount());
                    ingredient = null;
                    break;
                }
            }*/
            if (ingredient != null) {
                this.ingredientsAggregate.add(ingredient);
            }
        }
//        this.ingredientsAggregate.sort(CraftingManager.recipeComparator);

        this.tag = tag;
    }

    @Nullable
    public String getVanillaRecipeId() {
        return this.vanillaRecipeId;
    }

    public int getWidth() {
        return this.shape[0].length();
    }

    public int getHeight() {
        return this.shape.length;
    }

    @Override
    public Item getResult() {
        return this.primaryResult.clone();
    }

    @Override
    public Item getResultUnsafe() {
        return primaryResult;
    }

    @Override
    public String getRecipeId() {
        return this.recipeId;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void setId(UUID uuid) {
        this.uuid = uuid;

        if (this.recipeId == null) {
            this.recipeId = getId().toString();
        }
    }

    public ShapedRecipe setIngredient(String key, RecipeIngredient item) {
        return this.setIngredient(key.charAt(0), item);
    }

    public ShapedRecipe setIngredient(char key, RecipeIngredient item) {
        if (String.join("", this.shape).indexOf(key) < 0) {
            throw new RuntimeException("Symbol does not appear in the shape: " + key);
        }

        this.ingredients.put(key, item);
        return this;
    }

    public List<RecipeIngredient> getIngredientList() {
        List<RecipeIngredient> items = new ObjectArrayList<>();
        for (int y = 0, y2 = getHeight(); y < y2; ++y) {
            for (int x = 0, x2 = getWidth(); x < x2; ++x) {
                items.add(getIngredient(x, y));
            }
        }
        return items;
    }

    public Map<Integer, Map<Integer, RecipeIngredient>> getIngredientMap() {
        Map<Integer, Map<Integer, RecipeIngredient>> ingredients = new LinkedHashMap<>();

        for (int y = 0, y2 = getHeight(); y < y2; ++y) {
            Map<Integer, RecipeIngredient> m = new LinkedHashMap<>();

            for (int x = 0, x2 = getWidth(); x < x2; ++x) {
                m.put(x, getIngredient(x, y));
            }

            ingredients.put(y, m);
        }

        return ingredients;
    }

    public RecipeIngredient getIngredient(int x, int y) {
        RecipeIngredient item = this.ingredients.get(this.shape[y].charAt(x));

        return item != null ? item : RecipeIngredient.EMPTY;
    }

    public String[] getShape() {
        return shape;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerShapedRecipe(this);
        manager.registerNetworkRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }

    @Override
    public RecipeTag getTag() {
        return tag;
    }

    @Override
    public int getNetworkId() {
        return networkId;
    }

    @Override
    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    @Override
    public List<Item> getExtraResults() {
        return extraResults;
    }

    @Override
    public List<Item> getAllResults() {
        List<Item> list = new ObjectArrayList<>(1 + extraResults.size());
        list.add(primaryResult);
        list.addAll(extraResults);
        return list;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean matchItems(List<Item> inputList, List<Item> extraOutputList, int multiplier) {
        if (!RecipeMatcher.matchIngredients(inputList, getIngredientsAggregate(), multiplier)) {
            return false;
        }

        List<Item> haveOutputs = new ObjectArrayList<>();
        for (Item item : extraOutputList) {
            if (item.isNull())
                continue;
            haveOutputs.add(item.clone());
        }
        haveOutputs.sort(CraftingManager.recipeComparator);
        List<Item> needOutputs = new ObjectArrayList<>();
        if (multiplier != 1) {
            for (Item item : getExtraResults()) {
                if (item.isNull())
                    continue;
                Item itemClone = item.clone();
                itemClone.setCount(itemClone.getCount() * multiplier);
                needOutputs.add(itemClone);
            }
        } else {
            for (Item item : getExtraResults()) {
                if (item.isNull())
                    continue;
                needOutputs.add(item.clone());
            }
        }
        needOutputs.sort(CraftingManager.recipeComparator);

        return this.matchItemList(haveOutputs, needOutputs);
    }

    private boolean matchItemList(List<Item> haveItems, List<Item> needItems) {
        for (Item needItem : new ObjectArrayList<>(needItems)) {
            for (Item haveItem : new ObjectArrayList<>(haveItems)) {
                if (needItem.equals(haveItem, needItem.hasMeta(), needItem.hasCompoundTag())) {
                    int amount = Math.min(haveItem.getCount(), needItem.getCount());
                    needItem.setCount(needItem.getCount() - amount);
                    haveItem.setCount(haveItem.getCount() - amount);
                    if (haveItem.getCount() == 0) {
                        haveItems.remove(haveItem);
                    }
                    if (needItem.getCount() == 0) {
                        needItems.remove(needItem);
                        break;
                    }
                }
            }
        }
        return haveItems.isEmpty() && needItems.isEmpty();
    }

    @Override
    public boolean requiresCraftingTable() {
        return this.getHeight() > 2 || this.getWidth() > 2;
    }

    @Override
    public List<RecipeIngredient> getIngredientsAggregate() {
        return ingredientsAggregate;
    }

    @Override
    public UnlockingContext getUnlockingContext() {
        return unlockingContext;
    }

    @Override
    public List<RecipeIngredient> getUnlockingIngredientList() {
        return unlockingIngredients;
    }

    public boolean isAssumeSymmetry() {
        return assumeSymmetry;
    }

    public static class Entry {
        public final int x;
        public final int y;

        public Entry(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
