package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentID;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.lang.reflect.Field;

/**
 * Created by Pub4Game on 23.01.2016.
 */
public class EnchantCommand extends VanillaCommand {

    private static final Object2ByteMap<String> NAME_TO_ID = new Object2ByteOpenHashMap<>();

    static {
        NAME_TO_ID.defaultReturnValue((byte) -1);
        try {
            for (Field field : EnchantmentID.class.getDeclaredFields()) {
                NAME_TO_ID.put(field.getName().substring(3).toLowerCase(), (byte) field.getInt(null));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public EnchantCommand(String name) {
        super(name, "%nukkit.command.enchant.description", "%commands.enchant.usage");
        this.setPermission("nukkit.command.enchant");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("enchantmentId", CommandParamType.INT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
        this.commandParameters.put("byName", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("enchantmentName", CommandEnum.ENUM_ENCHANT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        Player player = sender.getServer().getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }
        int enchantId = NAME_TO_ID.getByte(args[1].toLowerCase());
        int enchantLevel;
        try {
            if (enchantId == -1) {
                enchantId = Integer.parseInt(args[1]);
            }
            enchantLevel = args.length == 3 ? Integer.parseInt(args[2]) : 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        Enchantment enchantment = Enchantment.getEnchantment(enchantId);
        if (enchantment == null) {
            sender.sendMessage(new TranslationContainer("commands.enchant.notFound", String.valueOf(enchantId)));
            return true;
        }
        enchantment.setLevel(enchantLevel);
        Item item = player.getInventory().getItemInHand();
        if (item.getId() <= 0) {
            sender.sendMessage(new TranslationContainer("commands.enchant.noItem"));
            return true;
        }
        item.addEnchantment(enchantment);
        player.getInventory().setItemInHand(item);
        Command.broadcastCommandMessage(sender, new TranslationContainer("%commands.enchant.success"));
        return true;
    }
}
