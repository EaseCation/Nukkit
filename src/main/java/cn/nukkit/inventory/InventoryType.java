package cn.nukkit.inventory;

import cn.nukkit.network.protocol.types.ContainerType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public enum InventoryType {
    CHEST(27, "Chest", ContainerType.CONTAINER),
    ENDER_CHEST(27, "Ender Chest", ContainerType.CONTAINER),
    DOUBLE_CHEST(27 + 27, "Double Chest", ContainerType.CONTAINER),
    PLAYER(40, "Player", ContainerType.INVENTORY), //36 CONTAINER, 4 ARMOR
    FURNACE(3, "Furnace", ContainerType.FURNACE),
    CRAFTING(5, "Crafting", ContainerType.WORKBENCH), //4 CRAFTING slots, 1 RESULT
    WORKBENCH(10, "Crafting", ContainerType.WORKBENCH), //9 CRAFTING slots, 1 RESULT
    BREWING_STAND(5, "Brewing", ContainerType.BREWING_STAND), //1 INPUT, 3 POTION, 1 fuel
    ANVIL(3, "Anvil", ContainerType.ANVIL), //2 INPUT, 1 OUTPUT
    ENCHANT_TABLE(2, "Enchant", ContainerType.ENCHANTMENT), //1 INPUT/OUTPUT, 1 LAPIS
    DISPENSER(9, "Dispenser", ContainerType.DISPENSER),
    DROPPER(9, "Dropper", ContainerType.DROPPER),
    HOPPER(5, "Hopper", ContainerType.HOPPER),
    UI(1, "UI", ContainerType.INVENTORY),
    SHULKER_BOX(27, "Shulker Box", ContainerType.CONTAINER),
    BEACON(1, "Beacon", ContainerType.BEACON),
    OFFHAND(1, "Offhand", ContainerType.INVENTORY),
    MINECART_CHEST(27, "Minecart with Chest", /*ContainerType.MINECART_CHEST*/ContainerType.CONTAINER),
    MINECART_HOPPER(5, "Minecart with Hopper", /*ContainerType.MINECART_HOPPER*/ContainerType.HOPPER),
    LOOM(4, "Loom", ContainerType.LOOM),
    GRINDSTONE(3, "Grindstone", ContainerType.GRINDSTONE),
    BARREL(27, "Barrel", ContainerType.CONTAINER),
    BLAST_FURNACE(3, "Blast Furnace", ContainerType.BLAST_FURNACE),
    SMOKER(3, "Smoker", ContainerType.SMOKER),
    STONECUTTER(2, "Stonecutter", ContainerType.STONECUTTER),
    CARTOGRAPHY(3, "Cartography", ContainerType.CARTOGRAPHY),
    SMITHING_TABLE(3, "Smithing Table", ContainerType.SMITHING_TABLE),
    BOAT_CHEST(27, "Boat with Chest", /*ContainerType.CHEST_BOAT*/ContainerType.CONTAINER),
    HORSE(2, "Horse", ContainerType.HORSE),
    COMMAND_BLOCK(0, "Command Block", ContainerType.COMMAND_BLOCK),
    STRUCTURE_EDITOR(0, "Structure Block", ContainerType.STRUCTURE_EDITOR),
    JIGSAW_EDITOR(0, "Jigsaw Block", ContainerType.JIGSAW_EDITOR),
    COMPOUND_CREATOR(10, "Compound Creator", ContainerType.COMPOUND_CREATOR),
    ELEMENT_CONSTRUCTOR(1, "Element Constructor", ContainerType.ELEMENT_CONSTRUCTOR),
    MATERIAL_REDUCER(9, "Material Reducer", ContainerType.MATERIAL_REDUCER),
    LAB_TABLE(9, "Lab Table", ContainerType.LAB_TABLE),
    ;

    private final int size;
    private final String title;
    private final int typeId;

    InventoryType(int defaultSize, String defaultBlockEntity, int typeId) {
        this.size = defaultSize;
        this.title = defaultBlockEntity;
        this.typeId = typeId;
    }

    public int getDefaultSize() {
        return size;
    }

    public String getDefaultTitle() {
        return title;
    }

    public int getNetworkType() {
        return typeId;
    }
}
