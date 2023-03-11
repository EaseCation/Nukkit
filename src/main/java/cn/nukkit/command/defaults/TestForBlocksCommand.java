package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Mth;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.TextFormat;

import java.util.List;

//TODO: async
public class TestForBlocksCommand extends VanillaCommand {

    public TestForBlocksCommand(String name) {
        super(name, "%nukkit.command.testforblocks.description", "%nukkit.command.testforblocks.usage");
        this.setPermission("nukkit.command.testforblocks");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("begin", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("mode", true, new CommandEnum("TestForBlocksMode", TestForBlocksMode.values())),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            Position begin = parser.parsePosition().floor();
            Position end = parser.parsePosition().floor();
            Position destination = parser.parsePosition().floor();
            TestForBlocksMode mode = TestForBlocksMode.ALL;

            if (parser.hasNext()) {
                mode = parser.parseEnum(TestForBlocksMode.class);
            }

            AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
            int size = Mth.floor((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

            if (size > 16 * 16 * 256 * 8) {
                sender.sendMessage(String.format(TextFormat.RED + "Too many blocks in the specified area (%1$d > %2$d)", size, 16 * 16 * 256 * 8));
                return false;
            }

            Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
            AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));
            Level level = begin.getLevel();

            if (blocksAABB.getMinY() < level.getMinHeight() || blocksAABB.getMaxY() > level.getMaxHeight() || destinationAABB.getMinY() < level.getMinHeight() || destinationAABB.getMaxY() > level.getMaxHeight()) {
                sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                return false;
            }


            for (int sourceChunkX = Mth.floor(blocksAABB.getMinX()) >> 4, destinationChunkX = Mth.floor(destinationAABB.getMinX()) >> 4; sourceChunkX <= Mth.floor(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
                for (int sourceChunkZ = Mth.floor(blocksAABB.getMinZ()) >> 4, destinationChunkZ = Mth.floor(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= Mth.floor(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                    if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                        sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                        return false;
                    }
                    if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                        sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                        return false;
                    }
                }
            }

            List<Block> blocks = level.getBlocks(blocksAABB);
            List<Block> destinationBlocks = level.getBlocks(destinationAABB);
            int count = 0;

            switch (mode) {
                case ALL:
                    for (int i = 0; i < blocks.size(); i++) {
                        Block block = blocks.get(i);
                        Block destinationBlock = destinationBlocks.get(i);

                        if (block.getId() == destinationBlock.getId() && block.getDamage() == destinationBlock.getDamage()) {
                            ++count;
                        } else {
                            sender.sendMessage(TextFormat.RED + "Source and destination are not identical");
                            return false;
                        }
                    }

                    break;
                case MASKED:
                    for (int i = 0; i < blocks.size(); i++) {
                        Block block = blocks.get(i);
                        Block destinationBlock = destinationBlocks.get(i);

                        if (block.getId() == destinationBlock.getId() && block.getDamage() == destinationBlock.getDamage()) {
                            ++count;
                        } else if (block.getId() != Block.AIR) {
                            sender.sendMessage(TextFormat.RED + "Source and destination are not identical");
                            return false;
                        }
                    }

                    break;
            }

            sender.sendMessage(new TranslationContainer("commands.compare.success", count));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }

    enum TestForBlocksMode {
        ALL,
        MASKED,
    }
}
