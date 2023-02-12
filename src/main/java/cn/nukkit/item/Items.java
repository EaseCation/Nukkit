package cn.nukkit.item;

import cn.nukkit.GameVersion;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.item.ItemID.*;

public final class Items {

    public static void registerVanillaItems() {
        registerItem(MUSIC_DISC_11, ItemRecord11.class);
        registerItem(MUSIC_DISC_CAT, ItemRecordCat.class);
        registerItem(MUSIC_DISC_13, ItemRecord13.class);
        registerItem(MUSIC_DISC_BLOCKS, ItemRecordBlocks.class);
        registerItem(MUSIC_DISC_CHIRP, ItemRecordChirp.class);
        registerItem(MUSIC_DISC_FAR, ItemRecordFar.class);
        registerItem(MUSIC_DISC_WARD, ItemRecordWard.class);
        registerItem(MUSIC_DISC_MALL, ItemRecordMall.class);
        registerItem(MUSIC_DISC_MELLOHI, ItemRecordMellohi.class);
        registerItem(MUSIC_DISC_STAL, ItemRecordStal.class);
        registerItem(MUSIC_DISC_STRAD, ItemRecordStrad.class);
        registerItem(MUSIC_DISC_WAIT, ItemRecordWait.class);

        registerItem(KELP, ItemKelp.class, V1_4_0);
        registerItem(TRIDENT, ItemTrident.class, V1_4_0);
        registerItem(DRIED_KELP, ItemDriedKelp.class, V1_4_0);
        registerItem(HEART_OF_THE_SEA, ItemHeartOfTheSea.class, V1_4_0);

        registerItem(NAUTILUS_SHELL, ItemNautilusShell.class, V1_5_0);
        registerItem(SCUTE, ItemScute.class, V1_5_0);
        registerItem(TURTLE_HELMET, ItemTurtleShell.class, V1_5_0);

        registerItem(PHANTOM_MEMBRANE, ItemPhantomMembrane.class, V1_6_0);

        registerItem(SPRUCE_SIGN, ItemSignSpruce.class, V1_9_0);
        registerItem(BIRCH_SIGN, ItemSignBirch.class, V1_9_0);
        registerItem(JUNGLE_SIGN, ItemSignJungle.class, V1_9_0);
        registerItem(ACACIA_SIGN, ItemSignAcacia.class, V1_9_0);
        registerItem(DARK_OAK_SIGN, ItemSignDarkOak.class, V1_9_0);

        registerItem(BANNER_PATTERN, ItemBannerPattern.class, V1_10_0);
        registerItem(CROSSBOW, ItemCrossbow.class, V1_10_0);
        registerItem(SHIELD, ItemShield.class, V1_10_0);

        registerItem(SWEET_BERRIES, ItemSweetBerries.class, V1_11_0);
        registerItem(CAMPFIRE, ItemCampfire.class, V1_11_0);

        registerItem(SUSPICIOUS_STEW, ItemSuspiciousStew.class, V1_13_0);

        registerItem(HONEY_BOTTLE, ItemHoneyBottle.class, V1_14_0);
        registerItem(HONEYCOMB, ItemHoneycomb.class, V1_14_0);

        registerItem(LODESTONE_COMPASS, ItemCompassLodestone.class, V1_16_0);
        registerItem(NETHERITE_INGOT, ItemIngotNetherite.class, V1_16_0);
        registerItem(NETHERITE_SWORD, ItemSwordNetherite.class, V1_16_0);
        registerItem(NETHERITE_SHOVEL, ItemShovelNetherite.class, V1_16_0);
        registerItem(NETHERITE_PICKAXE, ItemPickaxeNetherite.class, V1_16_0);
        registerItem(NETHERITE_AXE, ItemAxeNetherite.class, V1_16_0);
        registerItem(NETHERITE_HOE, ItemHoeNetherite.class, V1_16_0);
        registerItem(NETHERITE_HELMET, ItemHelmetNetherite.class, V1_16_0);
        registerItem(NETHERITE_CHESTPLATE, ItemChestplateNetherite.class, V1_16_0);
        registerItem(NETHERITE_LEGGINGS, ItemLeggingsNetherite.class, V1_16_0);
        registerItem(NETHERITE_BOOTS, ItemBootsNetherite.class, V1_16_0);
        registerItem(NETHERITE_SCRAP, ItemScrapNetherite.class, V1_16_0);
        registerItem(CRIMSON_SIGN, ItemSignCrimson.class, V1_16_0);
        registerItem(WARPED_SIGN, ItemSignWarped.class, V1_16_0);
        registerItem(CRIMSON_DOOR, ItemDoorCrimson.class, V1_16_0);
        registerItem(WARPED_DOOR, ItemDoorWarped.class, V1_16_0);
        registerItem(WARPED_FUNGUS_ON_A_STICK, ItemWarpedFungusOnAStick.class, V1_16_0);
        registerItem(CHAIN, ItemChain.class, V1_16_0);
        registerItem(MUSIC_DISC_PIGSTEP, ItemRecordPigstep.class, V1_16_0);
        registerItem(NETHER_SPROUTS, ItemNetherSprouts.class, V1_16_0);
        registerItem(SOUL_CAMPFIRE, ItemCampfireSoul.class, V1_16_0);

        registerItem(GLOW_INK_SAC, ItemGlowInkSac.class, V1_17_0);
        registerItem(RAW_IRON, ItemRawIron.class, V1_17_0);
        registerItem(RAW_GOLD, ItemRawGold.class, V1_17_0);
        registerItem(RAW_COPPER, ItemRawCopper.class, V1_17_0);
        registerItem(AMETHYST_SHARD, ItemAmethystShard.class, V1_17_0);
        registerItem(SPYGLASS, ItemSpyglass.class, V1_17_0);
        registerItem(GLOW_FRAME, ItemItemFrameGlow.class, V1_17_0);
        // copper_ingot
        // glow_berries

        registerItem(MUSIC_DISC_OTHERSIDE, ItemRecordOtherside.class, V1_18_0);

        registerItem(GOAT_HORN, ItemGoatHorn.class, V1_19_0);
        registerItem(MUSIC_DISC_5, ItemRecord5.class, V1_19_0);
        registerItem(DISC_FRAGMENT_5, ItemDiscFragment5.class, V1_19_0);
        registerItem(RECOVERY_COMPASS, ItemCompassRecovery.class, V1_19_0);
        registerItem(ECHO_SHARD, ItemEchoShard.class, V1_19_0);
        registerItem(MANGROVE_DOOR, ItemDoorMangrove.class, V1_19_0);
        registerItem(MANGROVE_SIGN, ItemSignMangrove.class, V1_19_0);
        // chest_boat

    }

    private static Class<? extends Item> registerItem(int id, Class<? extends Item> clazz) {
        Item.list[id] = clazz;
        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Item> registerItem(int id, Class<? extends Item> clazz, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerItem(id, clazz);
    }

    public static Item air() {
        return Item.get(AIR, 0, 0);
    }

    private Items() {
        throw new IllegalStateException();
    }
}
