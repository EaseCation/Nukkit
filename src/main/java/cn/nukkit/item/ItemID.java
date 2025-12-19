package cn.nukkit.item;

import cn.nukkit.block.BlockID;

// 弃用的名称用于旧版本, 不要移除
public interface ItemID extends BlockID, ItemRuntimeID {
    int GLOW_STICK = 166;

    int IRON_SHOVEL = 256;
    int IRON_PICKAXE = 257;
    int IRON_AXE = 258;
    int FLINT_AND_STEEL = 259;
    int APPLE = 260;
    int BOW = 261;
    int ARROW = 262;
    int COAL = 263;
    int DIAMOND = 264;
    int IRON_INGOT = 265;
    int GOLD_INGOT = 266;
    int IRON_SWORD = 267;
    int WOODEN_SWORD = 268;
    int WOODEN_SHOVEL = 269;
    int WOODEN_PICKAXE = 270;
    int WOODEN_AXE = 271;
    int STONE_SWORD = 272;
    int STONE_SHOVEL = 273;
    int STONE_PICKAXE = 274;
    int STONE_AXE = 275;
    int DIAMOND_SWORD = 276;
    int DIAMOND_SHOVEL = 277;
    int DIAMOND_PICKAXE = 278;
    int DIAMOND_AXE = 279;
    int STICK = 280;
    int BOWL = 281;
    int MUSHROOM_STEW = 282;
    int GOLDEN_SWORD = 283;
    int GOLDEN_SHOVEL = 284;
    int GOLDEN_PICKAXE = 285;
    int GOLDEN_AXE = 286;
    int STRING = 287;
    int FEATHER = 288;
    int GUNPOWDER = 289;
    int WOODEN_HOE = 290;
    int STONE_HOE = 291;
    int IRON_HOE = 292;
    int DIAMOND_HOE = 293;
    int GOLDEN_HOE = 294;
    int WHEAT_SEEDS = 295;
    int WHEAT = 296;
    int BREAD = 297;
    int LEATHER_HELMET = 298;
    int LEATHER_CHESTPLATE = 299;
    int LEATHER_LEGGINGS = 300;
    int LEATHER_BOOTS = 301;
    int CHAINMAIL_HELMET = 302;
    int CHAINMAIL_CHESTPLATE = 303;
    int CHAINMAIL_LEGGINGS = 304;
    int CHAINMAIL_BOOTS = 305;
    int IRON_HELMET = 306;
    int IRON_CHESTPLATE = 307;
    int IRON_LEGGINGS = 308;
    int IRON_BOOTS = 309;
    int DIAMOND_HELMET = 310;
    int DIAMOND_CHESTPLATE = 311;
    int DIAMOND_LEGGINGS = 312;
    int DIAMOND_BOOTS = 313;
    int GOLDEN_HELMET = 314;
    int GOLDEN_CHESTPLATE = 315;
    int GOLDEN_LEGGINGS = 316;
    int GOLDEN_BOOTS = 317;
    int FLINT = 318;
    int PORKCHOP = 319;
    int COOKED_PORKCHOP = 320;
    int PAINTING = 321;
    int GOLDEN_APPLE = 322;
    int OAK_SIGN = 323;
    /**
     * @deprecated use {@link #OAK_SIGN} instead
     */
    @Deprecated
    int SIGN = 323;
    int WOODEN_DOOR = 324;
    int BUCKET = 325;

    int MINECART = 328;
    int SADDLE = 329;
    int IRON_DOOR = 330;
    int REDSTONE = 331;
    int SNOWBALL = 332;
    int OAK_BOAT = 333;
    /**
     * @deprecated use {@link #OAK_BOAT} instead (flattened)
     */
    @Deprecated
    int BOAT = 333;
    int LEATHER = 334;
    int KELP = 335;
    int BRICK = 336;
    int CLAY_BALL = 337;
    int SUGAR_CANE = 338;
    /**
     * @deprecated use {@link #SUGAR_CANE} instead
     */
    @Deprecated
    int REEDS = 338;
    int PAPER = 339;
    int BOOK = 340;
    int SLIME_BALL = 341;
    int CHEST_MINECART = 342;

    int EGG = 344;
    int COMPASS = 345;
    int FISHING_ROD = 346;
    int CLOCK = 347;
    int GLOWSTONE_DUST = 348;
    int COD = 349;
    /**
     * @deprecated use {@link #COD} instead
     */
    @Deprecated
    int FISH = 349;
    int COOKED_COD = 350;
    /**
     * @deprecated use {@link #COOKED_COD} instead
     */
    @Deprecated
    int COOKED_FISH = 350;
    int INK_SAC = 351;
    /**
     * @deprecated use {@link #INK_SAC} instead (flattened)
     */
    @Deprecated
    int DYE = 351;
    int BONE = 352;
    int SUGAR = 353;
    int CAKE = 354;
    int BED = 355;
    int REPEATER = 356;
    int COOKIE = 357;
    int FILLED_MAP = 358;
    /**
     * @deprecated use {@link #FILLED_MAP} instead
     */
    @Deprecated
    int MAP = 358;
    int SHEARS = 359;
    int MELON_SLICE = 360;
    /**
     * @deprecated use {@link #MELON_SLICE} instead
     */
    @Deprecated
    int MELON = 360;
    int PUMPKIN_SEEDS = 361;
    int MELON_SEEDS = 362;
    int BEEF = 363;
    int COOKED_BEEF = 364;
    int CHICKEN = 365;
    int COOKED_CHICKEN = 366;
    int ROTTEN_FLESH = 367;
    int ENDER_PEARL = 368;
    int BLAZE_ROD = 369;
    int GHAST_TEAR = 370;
    int GOLD_NUGGET = 371;
    int NETHER_WART = 372;
    int POTION = 373;
    int GLASS_BOTTLE = 374;
    int SPIDER_EYE = 375;
    int FERMENTED_SPIDER_EYE = 376;
    int BLAZE_POWDER = 377;
    int MAGMA_CREAM = 378;
    int BREWING_STAND = 379;
    int CAULDRON = 380;
    int ENDER_EYE = 381;
    int GLISTERING_MELON_SLICE = 382;
    /**
     * @deprecated use {@link #GLISTERING_MELON_SLICE} instead
     */
    @Deprecated
    int SPECKLED_MELON = 382;
    int SPAWN_EGG = 383;
    int EXPERIENCE_BOTTLE = 384;
    int FIRE_CHARGE = 385;
    /**
     * @deprecated use {@link #FIRE_CHARGE} instead
     */
    @Deprecated
    int FIREBALL = 385;
    int WRITABLE_BOOK = 386;
    int WRITTEN_BOOK = 387;
    int EMERALD = 388;
    int FRAME = 389;
    int FLOWER_POT = 390;
    int CARROT = 391;
    int POTATO = 392;
    int BAKED_POTATO = 393;
    int POISONOUS_POTATO = 394;
    int EMPTY_MAP = 395;
    /**
     * @deprecated use {@link #EMPTY_MAP} instead
     */
    @Deprecated
    int EMPTYMAP = 395;
    int GOLDEN_CARROT = 396;
    int SKULL = 397;
    int CARROT_ON_A_STICK = 398;
    /**
     * @deprecated use {@link #CARROT_ON_A_STICK} instead
     */
    @Deprecated
    int CARROTONASTICK = 398;
    int NETHER_STAR = 399;
    /**
     * @deprecated use {@link #NETHER_STAR} instead
     */
    @Deprecated
    int NETHERSTAR = 399;
    int PUMPKIN_PIE = 400;
    int FIREWORK_ROCKET = 401;
    /**
     * @deprecated use {@link #FIREWORK_ROCKET} instead
     */
    @Deprecated
    int FIREWORKS = 401;
    int FIREWORK_STAR = 402;
    /**
     * @deprecated use {@link #FIREWORK_STAR} instead
     */
    @Deprecated
    int FIREWORKSCHARGE = 402;
    int ENCHANTED_BOOK = 403;
    int COMPARATOR = 404;
    int NETHERBRICK = 405;
    int QUARTZ = 406;
    int TNT_MINECART = 407;
    int HOPPER_MINECART = 408;
    int PRISMARINE_SHARD = 409;
    /**
     * @deprecated use {@link #PRISMARINE_SHARD} instead
     */
    @Deprecated
    int PRISMARINESHARD = 409;
    int HOPPER = 410;
    int RABBIT = 411;
    int COOKED_RABBIT = 412;
    int RABBIT_STEW = 413;
    int RABBIT_FOOT = 414;
    int RABBIT_HIDE = 415;
    int LEATHER_HORSE_ARMOR = 416;
    /**
     * @deprecated use {@link #LEATHER_HORSE_ARMOR} instead
     */
    @Deprecated
    int HORSEARMORLEATHER = 416;
    int IRON_HORSE_ARMOR = 417;
    /**
     * @deprecated use {@link #IRON_HORSE_ARMOR} instead
     */
    @Deprecated
    int HORSEARMORIRON = 417;
    int GOLDEN_HORSE_ARMOR = 418;
    /**
     * @deprecated use {@link #GOLDEN_HORSE_ARMOR} instead
     */
    @Deprecated
    int HORSEARMORGOLD = 418;
    int DIAMOND_HORSE_ARMOR = 419;
    /**
     * @deprecated use {@link #DIAMOND_HORSE_ARMOR} instead
     */
    @Deprecated
    int HORSEARMORDIAMOND = 419;
    int LEAD = 420;
    int NAME_TAG = 421;
    /**
     * @deprecated use {@link #NAME_TAG} instead
     */
    @Deprecated
    int NAMETAG = 421;
    int PRISMARINE_CRYSTALS = 422;
    int MUTTON = 423;
    /**
     * @deprecated use {@link #MUTTON} instead
     */
    @Deprecated
    int MUTTONRAW = 423;
    int COOKED_MUTTON = 424;
    /**
     * @deprecated use {@link #COOKED_MUTTON} instead
     */
    @Deprecated
    int MUTTONCOOKED = 424;
    int ARMOR_STAND = 425;
    int END_CRYSTAL = 426;
    int SPRUCE_DOOR = 427;
    int BIRCH_DOOR = 428;
    int JUNGLE_DOOR = 429;
    int ACACIA_DOOR = 430;
    int DARK_OAK_DOOR = 431;
    int CHORUS_FRUIT = 432;
    int POPPED_CHORUS_FRUIT = 433;
    /**
     * @deprecated use {@link #POPPED_CHORUS_FRUIT} instead
     */
    @Deprecated
    int CHORUS_FRUIT_POPPED = 433;
    int CREEPER_BANNER_PATTERN = 434;
    /**
     * @deprecated use {@link #CREEPER_BANNER_PATTERN} instead (flattened)
     */
    @Deprecated
    int BANNER_PATTERN = 434;

    int DRAGON_BREATH = 437;
    int SPLASH_POTION = 438;

    int LINGERING_POTION = 441;
    int SPARKLER = 442;
    int COMMAND_BLOCK_MINECART = 443;
    int ELYTRA = 444;
    int SHULKER_SHELL = 445;
    int BANNER = 446;
    int MEDICINE = 447;
    int BALLOON = 448;
    int RAPID_FERTILIZER = 449;
    int TOTEM_OF_UNDYING = 450;
    /**
     * @deprecated use {@link #TOTEM_OF_UNDYING} instead
     */
    @Deprecated
    int TOTEM = 450;
    int BLEACH = 451;
    int IRON_NUGGET = 452;
    int ICE_BOMB = 453;
    int BOARD = 454;
    int TRIDENT = 455;
    int PORTFOLIO = 456;
    int BEETROOT = 457;
    int BEETROOT_SEEDS = 458;
    int BEETROOT_SOUP = 459;
    int SALMON = 460;
    int TROPICAL_FISH = 461;
    /**
     * @deprecated use {@link #TROPICAL_FISH} instead
     */
    @Deprecated
    int CLOWNFISH = 461;
    int PUFFERFISH = 462;
    int COOKED_SALMON = 463;
    int DRIED_KELP = 464;
    int NAUTILUS_SHELL = 465;
    int ENCHANTED_GOLDEN_APPLE = 466;
    /**
     * @deprecated use {@link #ENCHANTED_GOLDEN_APPLE} instead
     */
    @Deprecated
    int APPLEENCHANTED = 466;
    int HEART_OF_THE_SEA = 467;
    int TURTLE_SCUTE = 468;
    /**
     * @deprecated use {@link #TURTLE_SCUTE} instead
     */
    @Deprecated
    int SCUTE = 468;
    /**
     * @deprecated use {@link #TURTLE_SCUTE} instead
     */
    @Deprecated
    int TURTLE_SHELL_PIECE = 468;
    int TURTLE_HELMET = 469;
    int PHANTOM_MEMBRANE = 470;
    int CROSSBOW = 471;
    int SPRUCE_SIGN = 472;
    int BIRCH_SIGN = 473;
    int JUNGLE_SIGN = 474;
    int ACACIA_SIGN = 475;
    int DARK_OAK_SIGN = 476;
    /**
     * @deprecated use {@link #DARK_OAK_SIGN} instead
     */
    @Deprecated
    int DARKOAK_SIGN = 476;
    int SWEET_BERRIES = 477;

    int CAMERA = 498;
    int COMPOUND = 499;
    int MUSIC_DISC_13 = 500;
    /**
     * @deprecated use {@link #MUSIC_DISC_13} instead
     */
    @Deprecated
    int RECORD_13 = 500;
    int MUSIC_DISC_CAT = 501;
    /**
     * @deprecated use {@link #MUSIC_DISC_CAT} instead
     */
    @Deprecated
    int RECORD_CAT = 501;
    int MUSIC_DISC_BLOCKS = 502;
    /**
     * @deprecated use {@link #MUSIC_DISC_BLOCKS} instead
     */
    @Deprecated
    int RECORD_BLOCKS = 502;
    int MUSIC_DISC_CHIRP = 503;
    /**
     * @deprecated use {@link #MUSIC_DISC_CHIRP} instead
     */
    @Deprecated
    int RECORD_CHIRP = 503;
    int MUSIC_DISC_FAR = 504;
    /**
     * @deprecated use {@link #MUSIC_DISC_FAR} instead
     */
    @Deprecated
    int RECORD_FAR = 504;
    int MUSIC_DISC_MALL = 505;
    /**
     * @deprecated use {@link #MUSIC_DISC_MALL} instead
     */
    @Deprecated
    int RECORD_MALL = 505;
    int MUSIC_DISC_MELLOHI = 506;
    /**
     * @deprecated use {@link #MUSIC_DISC_MELLOHI} instead
     */
    @Deprecated
    int RECORD_MELLOHI = 506;
    int MUSIC_DISC_STAL = 507;
    /**
     * @deprecated use {@link #MUSIC_DISC_STAL} instead
     */
    @Deprecated
    int RECORD_STAL = 507;
    int MUSIC_DISC_STRAD = 508;
    /**
     * @deprecated use {@link #MUSIC_DISC_STRAD} instead
     */
    @Deprecated
    int RECORD_STRAD = 508;
    int MUSIC_DISC_WARD = 509;
    /**
     * @deprecated use {@link #MUSIC_DISC_WARD} instead
     */
    @Deprecated
    int RECORD_WARD = 509;
    int MUSIC_DISC_11 = 510;
    /**
     * @deprecated use {@link #MUSIC_DISC_11} instead
     */
    @Deprecated
    int RECORD_11 = 510;
    int MUSIC_DISC_WAIT = 511;
    /**
     * @deprecated use {@link #MUSIC_DISC_WAIT} instead
     */
    @Deprecated
    int RECORD_WAIT = 511;

    int SHIELD = 513;

    int AXOLOTL_BUCKET = 517;
    int GLOW_INK_SAC = 518;

    int RAW_IRON = 520;
    int RAW_GOLD = 521;
    int RAW_COPPER = 522;
    int TADPOLE_BUCKET = 523;

    int BREEZE_ROD = 529;
    int MACE = 530;

    int CAMPFIRE = 720;

    int SUSPICIOUS_STEW = 734;
    int DEBUG_STICK = 735;
    int HONEYCOMB = 736;
    int HONEY_BOTTLE = 737;

    int LODESTONE_COMPASS = 741;
    /**
     * @deprecated use {@link #LODESTONE_COMPASS} instead
     */
    @Deprecated
    int LODESTONECOMPASS = 741;
    int NETHERITE_INGOT = 742;
    int NETHERITE_SWORD = 743;
    int NETHERITE_SHOVEL = 744;
    int NETHERITE_PICKAXE = 745;
    int NETHERITE_AXE = 746;
    int NETHERITE_HOE = 747;
    int NETHERITE_HELMET = 748;
    int NETHERITE_CHESTPLATE = 749;
    int NETHERITE_LEGGINGS = 750;
    int NETHERITE_BOOTS = 751;
    int NETHERITE_SCRAP = 752;
    int CRIMSON_SIGN = 753;
    int WARPED_SIGN = 754;
    int CRIMSON_DOOR = 755;
    int WARPED_DOOR = 756;

    int WARPED_FUNGUS_ON_A_STICK = 757;
    int CHAIN = 758;
    int MUSIC_DISC_PIGSTEP = 759;
    /**
     * @deprecated use {@link #MUSIC_DISC_PIGSTEP} instead
     */
    @Deprecated
    int RECORD_PIGSTEP = 759;
    int NETHER_SPROUTS = 760;
    int GOAT_HORN = 761;

    int POWDER_SNOW_BUCKET = 770;
    int AMETHYST_SHARD = 771;
    int SPYGLASS = 772;
    int MUSIC_DISC_OTHERSIDE = 773;
    /**
     * @deprecated use {@link #MUSIC_DISC_OTHERSIDE} instead
     */
    @Deprecated
    int RECORD_OTHERSIDE = 773;

    int MUSIC_DISC_5 = 776;
    /**
     * @deprecated use {@link #MUSIC_DISC_5} instead
     */
    @Deprecated
    int RECORD_5 = 776;
    int DISC_FRAGMENT_5 = 777;
    int RECOVERY_COMPASS = 778;
    int ECHO_SHARD = 779;
    int MUSIC_DISC_RELIC = 780;
    /**
     * @deprecated use {@link #MUSIC_DISC_RELIC} instead
     */
    @Deprecated
    int RECORD_RELIC = 780;

    int MUSIC_DISC_CREATOR = 783;
    /**
     * @deprecated use {@link #MUSIC_DISC_CREATOR} instead
     */
    @Deprecated
    int RECORD_CREATOR = 783;
    int MUSIC_DISC_CREATOR_MUSIC_BOX = 784;
    /**
     * @deprecated use {@link #MUSIC_DISC_CREATOR_MUSIC_BOX} instead
     */
    @Deprecated
    int RECORD_CREATOR_MUSIC_BOX = 784;
    int MUSIC_DISC_PRECIPICE = 785;
    /**
     * @deprecated use {@link #MUSIC_DISC_PRECIPICE} instead
     */
    @Deprecated
    int RECORD_PRECIPICE = 785;
    int MUSIC_DISC_TEARS = 786;
    /**
     * @deprecated use {@link #MUSIC_DISC_TEARS} instead
     */
    @Deprecated
    int RECORD_TEARS = 786;
    int MUSIC_DISC_LAVA_CHICKEN = 787;
    /**
     * @deprecated use {@link #MUSIC_DISC_LAVA_CHICKEN} instead
     */
    @Deprecated
    int RECORD_LAVA_CHICKEN = 787;

    int SOUL_CAMPFIRE = 801;

    int GLOW_FRAME = 850;

    int MANGROVE_DOOR = 1004;
    int MANGROVE_SIGN = 1005;


    int UNDEFINED_ID = 1006;
}
