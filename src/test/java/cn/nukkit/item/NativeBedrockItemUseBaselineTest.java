package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.PlayerFood;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.inventory.ArmorInventory;
import cn.nukkit.inventory.ItemUseHand;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.item.food.Food;
import cn.nukkit.item.food.FoodInBowl;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.potion.PotionID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NativeBedrockItemUseBaselineTest {

    @BeforeAll
    static void initializeItems() {
        Block.init();
        Item.init();
        Effect.init();
        Potion.init();
    }

    @Test
    void keepsNativeBowArrowLookupOrder() {
        Player player = mock(Player.class);
        PlayerOffhandInventory offhandInventory = mock(PlayerOffhandInventory.class);
        PlayerInventory mainInventory = mock(PlayerInventory.class);
        when(player.getOffhandInventory()).thenReturn(offhandInventory);
        when(player.getInventory()).thenReturn(mainInventory);
        when(offhandInventory.contains(any(Item.class))).thenReturn(false);
        when(mainInventory.contains(any(Item.class))).thenReturn(true);

        assertTrue(new ItemBow().onClickAir(player, null));
        verify(offhandInventory).contains(any(Item.class));
        verify(mainInventory).contains(any(Item.class));
    }

    @Test
    void keepsNativePotionContainerInTheMainInventory() {
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        ItemPotion potion = new ItemPotion(PotionID.WATER, 1);
        when(player.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(player.isSurvivalLike()).thenReturn(true);
        when(player.isOffhandItemInteraction()).thenReturn(false);
        when(player.getInventory()).thenReturn(inventory);

        assertTrue(potion.onUse(player, potion.getUseDuration()));
        verify(inventory).setItemInHand(potion);
        verify(inventory).addItem(ArgumentMatchers.<Item>argThat(
                item -> item.getId() == Item.GLASS_BOTTLE));
        verify(inventory, never()).addItemOrDrop(any(Item.class));
    }

    @Test
    void keepsNativeFoodContainerDeliveryInTheOriginalEatingPath() {
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        PlayerFood playerFood = mock(PlayerFood.class);
        FoodInBowl food = new FoodInBowl(6, 7.2f);
        when(player.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(player.getFoodData()).thenReturn(playerFood);
        when(player.getInventory()).thenReturn(inventory);
        when(player.isSurvivalLike()).thenReturn(true);
        when(player.isOffhandItemInteraction()).thenReturn(false);

        assertTrue(food.eatenBy(player));
        verify(playerFood).addFoodLevel(food);
        verify(inventory).addItem(ArgumentMatchers.<Item>argThat(
                item -> item.getId() == Item.BOWL));
    }

    @Test
    void leavesOffhandFoodContainerDeliveryToTheOffhandCaller() {
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        PlayerFood playerFood = mock(PlayerFood.class);
        FoodInBowl food = new FoodInBowl(6, 7.2f);
        when(player.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(player.getFoodData()).thenReturn(playerFood);
        when(player.getInventory()).thenReturn(inventory);
        when(player.isSurvivalLike()).thenReturn(true);
        when(player.isOffhandItemInteraction()).thenReturn(true);

        Food.Consumption consumption = food.consume(player);

        assertTrue(consumption.eaten());
        assertEquals(Item.BOWL, consumption.containerItem().getId());
        verify(inventory, never()).addItem(any(Item.class));
    }

    @Test
    void keepsNativeArmorReplacementOnTheMainHandPath() {
        Player player = mock(Player.class);
        ArmorInventory armorInventory = mock(ArmorInventory.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        Item oldHelmet = new ItemShield();
        ItemHelmetLeather helmet = new ItemHelmetLeather();
        when(player.getArmorInventory()).thenReturn(armorInventory);
        when(player.getInventory()).thenReturn(inventory);
        when(armorInventory.getHelmet()).thenReturn(oldHelmet);
        when(armorInventory.setHelmet(helmet)).thenReturn(true);

        helmet.onClickAir(player, null);
        verify(inventory).setItemInHand(oldHelmet);
    }

    @Test
    void keepsNativeTridentReturnBoundToTheSelectedHotbarSlot() {
        assertEquals(7, ItemTrident.resolveFavoredSlot(ItemUseHand.MAIN_HAND, 7));
        assertEquals(EntityThrownTrident.FAVORED_SLOT_OFFHAND,
                ItemTrident.resolveFavoredSlot(ItemUseHand.OFF_HAND, 7));
    }
}
