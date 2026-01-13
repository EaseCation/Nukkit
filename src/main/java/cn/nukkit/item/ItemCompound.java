package cn.nukkit.item;

public class ItemCompound extends Item {
    public static final int TYPE_SALT = 0;
    public static final int TYPE_SODIUM_OXIDE = 1;
    public static final int TYPE_SODIUM_HYDROXIDE = 2;
    public static final int TYPE_MAGNESIUM_NITRATE = 3;
    public static final int TYPE_IRON_SULFIDE = 4;
    public static final int TYPE_LITHIUM_HYDRIDE = 5;
    public static final int TYPE_SODIUM_HYDRIDE = 6;
    public static final int TYPE_CALCIUM_BROMIDE = 7;
    public static final int TYPE_MAGNESIUM_OXIDE = 8;
    public static final int TYPE_SODIUM_ACETATE = 9;
    public static final int TYPE_LUMINOL = 10;
    public static final int TYPE_CHARCOAL = 11;
    public static final int TYPE_SUGAR = 12;
    public static final int TYPE_ALUMINUM_OXIDE = 13;
    public static final int TYPE_BORON_TRIOXIDE = 14;
    public static final int TYPE_SOAP = 15;
    public static final int TYPE_POLYETHYLENE = 16;
    public static final int TYPE_GARBAGE = 17;
    public static final int TYPE_MAGNESIUM_SALTS = 18;
    public static final int TYPE_SULFATE = 19;
    public static final int TYPE_BARIUM_SULFATE = 20;
    public static final int TYPE_POTASSIUM_CHLORIDE = 21;
    public static final int TYPE_MERCURY_CHLORIDE = 22;
    public static final int TYPE_CERIUM_CHLORIDE = 23;
    public static final int TYPE_TUNGSTEN_CHLORIDE = 24;
    public static final int TYPE_CALCIUM_CHLORIDE = 25;
    public static final int TYPE_WATER = 26;
    public static final int TYPE_GLUE = 27;
    public static final int TYPE_HYPOCHLORITE_ION = 28;
    public static final int TYPE_CRUDE_OIL = 29;
    public static final int TYPE_LATEX = 30;
    public static final int TYPE_POTASSIUM_IODIDE = 31;
    public static final int TYPE_SODIUM_FLUORIDE = 32;
    public static final int TYPE_BENZENE = 33;
    public static final int TYPE_INK = 34;
    public static final int TYPE_HYDROGEN_PEROXIDE = 35;
    public static final int TYPE_AMMONIA = 36;
    public static final int TYPE_SODIUM_HYPOCHLORITE = 37;

    private static final String[] COMPOUND_TYPE_NAMES = {
            "salt",
            "sodiumoxide",
            "sodiumhydroxide",
            "magnesiumnitrate",
            "ironsulfide",
            "lithiumhydride",
            "sodiumhydride",
            "calciumbromide",
            "magnesiumoxide",
            "sodiumacetate",
            "luminol",
            "charcoal",
            "sugar",
            "aluminumoxide",
            "borontrioxide",
            "soap",
            "polyethylene",
            "garbage",
            "magnesiumsalts",
            "sulfate",
            "bariumsulfate",
            "potassiumchloride",
            "mercuricchloride",
            "ceriumchloride",
            "tungstenchloride",
            "calciumchloride",
            "water",
            "glue",
            "hypochlorite",
            "crudeoil",
            "latex",
            "potassiumiodide",
            "sodiumfluoride",
            "benzene",
            "ink",
            "hydrogenperoxide",
            "ammonia",
            "sodiumhypochlorite",
    };

    public ItemCompound() {
        this(0, 1);
    }

    public ItemCompound(Integer meta) {
        this(meta, 1);
    }

    public ItemCompound(Integer meta, int count) {
        super(COMPOUND, meta, count, "Compound");
    }

    @Override
    public String getDescriptionId() {
        int type = getDamage();
        if (type >= 0 && type < COMPOUND_TYPE_NAMES.length) {
            return "item.compound." + COMPOUND_TYPE_NAMES[type] + ".name";
        }
        return "item.compound.salt.name";
    }

    @Override
    public boolean isStackedByData() {
        return true;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
