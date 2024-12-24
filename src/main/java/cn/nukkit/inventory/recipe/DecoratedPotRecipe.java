package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.Items;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.List;

class DecoratedPotRecipe extends SpecialRecipe {
    DecoratedPotRecipe() {
        super(Item.get(ItemBlockID.DECORATED_POT));
    }

    @Override
    public boolean match(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList, RecipeTag tag) {
        if (!extraOutputList.isEmpty()) {
            return false;
        }
        CompoundTag nbt = primaryOutput.getNamedTag();
        if (nbt == null || nbt.size() != 1) {
            return false;
        }
        //  A
        // B C => DBCA
        //  D
        Tag sherds = nbt.get("sherds");
        if (!(sherds instanceof ListTag<?> list) || list.type != Tag.TAG_String || list.size() != 4) {
            return false;
        }
        List<?> stringList = list.parseValue();
        int total = 0;
        for (Item item : inputList) {
            int id = item.getId();
            if (id != Item.BRICK && !item.isPotterySherd()) {
                return false;
            }
            int count = item.getCount();
            total += count;
            if (total > 4) {
                return false;
            }
            String name = Items.getFullNameById(id);
            if (name == null) {
                return false;
            }
            for (int i = 0; i < count; i++) {
                if (!stringList.remove(name)) {
                    return false;
                }
            }
        }
        return stringList.isEmpty();
    }
}
