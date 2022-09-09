package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Mth;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO: async
public class FillCommand extends VanillaCommand {

    public FillCommand(String name) {
        super(name, "%nukkit.command.fill.description", "%nukkit.command.fill.usage");
        this.setPermission("nukkit.command.fill");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("from", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("to", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("block", CommandEnum.ENUM_BLOCK)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newEnum("oldBlockHandling", true, new CommandEnum("FillMode", Stream.of(FillMode.values())
                        .filter(mode -> mode != FillMode.REPLACE)
                        .map(mode -> mode.toString().toLowerCase())
                        .collect(Collectors.toSet()))),
        });
        this.commandParameters.put("replace", new CommandParameter[]{
                CommandParameter.newType("from", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("to", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("block", CommandEnum.ENUM_BLOCK)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newEnum("oldBlockHandling", new CommandEnum("Replace", "replace")),
                CommandParameter.newEnum("replaceBlock", true, CommandEnum.ENUM_BLOCK)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            Position from = parser.parsePosition().floor();
            Position to = parser.parsePosition().floor();
            Block placeBlock = parser.parseBlock();
            FillMode oldBlockHandling = FillMode.REPLACE;
            Block replaceBlock = Blocks.air();

            if (parser.hasNext()) {
                oldBlockHandling = parser.parseEnum(FillMode.class);
                if (parser.hasNext()) {
                    replaceBlock = parser.parseBlock();
                }
            }

            AxisAlignedBB aabb = new SimpleAxisAlignedBB(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));

            if (aabb.getMinY() < 0 || aabb.getMaxY() > 255) {
                sender.sendMessage(TextFormat.RED + "Cannot place blocks outside of the world");
                return true;
            }

            int size = Mth.floor((aabb.getMaxX() - aabb.getMinX() + 1) * (aabb.getMaxY() - aabb.getMinY() + 1) * (aabb.getMaxZ() - aabb.getMinZ() + 1));
            if (size > 16 * 16 * 16 * 8) {
                sender.sendMessage(String.format(TextFormat.RED + "Too many blocks in the specified area (%1$d > %2$d)", size, 16 * 16 * 16 * 8));
                return true;
            }

            Level level = from.getLevel();

            for (int chunkX = Mth.floor(aabb.getMinX()) >> 4; chunkX <= Mth.floor(aabb.getMaxX()) >> 4; chunkX++) {
                for (int chunkZ = Mth.floor(aabb.getMinZ()) >> 4; chunkZ <= Mth.floor(aabb.getMaxZ()) >> 4; chunkZ++) {
                    if (level.getChunkIfLoaded(chunkX, chunkZ) == null) {
                        sender.sendMessage(TextFormat.RED + "Cannot place blocks outside of the world");
                        return true;
                    }
                }
            }

            List<Block> blocks;
            int count = 0;

            switch (oldBlockHandling) {
                case OUTLINE:
                    for (int x = Mth.floor(aabb.getMinX()); x <= Mth.floor(aabb.getMaxX()); x++) {
                        for (int z = Mth.floor(aabb.getMinZ()); z <= Mth.floor(aabb.getMaxZ()); z++) {
                            for (int y = Mth.floor(aabb.getMinY()); y <= Mth.floor(aabb.getMaxY()); y++) {
                                if (x == from.x || x == to.x || z == from.z || z == to.z || y == from.y || y == to.y) {
                                    level.setExtraBlock(x, y, z, Blocks.air(), true ,false);
                                    level.setBlock(x, y, z, placeBlock.clone(), true ,false);
                                    ++count;
                                }
                            }
                        }
                    }

                    break;
                case HOLLOW:
                    for (int x = Mth.floor(aabb.getMinX()); x <= Mth.floor(aabb.getMaxX()); x++) {
                        for (int z = Mth.floor(aabb.getMinZ()); z <= Mth.floor(aabb.getMaxZ()); z++) {
                            for (int y = Mth.floor(aabb.getMinY()); y <= Mth.floor(aabb.getMaxY()); y++) {
                                Block block;

                                if (x == from.x || x == to.x || z == from.z || z == to.z || y == from.y || y == to.y) {
                                    block = placeBlock.clone();
                                } else {
                                    block = Block.get(Block.AIR);
                                }

                                level.setExtraBlock(x, y, z, Blocks.air(), true ,false);
                                level.setBlock(x, y, z, block, true ,false);
                                ++count;
                            }
                        }
                    }

                    break;
                case REPLACE:
                    blocks = level.getBlocks(aabb);

                    for (Block block : blocks) {
                        if (block.getId() == replaceBlock.getId() && block.getDamage() == replaceBlock.getDamage()) {
                            level.setExtraBlock(block, Blocks.air(), true ,false);
                            level.setBlock(block, placeBlock.clone(), true ,false);
                            ++count;
                        }
                    }

                    break;
                case DESTROY:
                    blocks = level.getBlocks(aabb);

                    for (Block block : blocks) {
                        level.useBreakOn(block);
                        level.setExtraBlock(block, Blocks.air(), true ,false);
                        level.setBlock(block, placeBlock.clone(), true ,false);
                        ++count;
                    }

                    break;
                case KEEP:
                    blocks = level.getBlocks(aabb);

                    for (Block block : blocks) {
                        if (block.getId() == Block.AIR) {
                            level.setExtraBlock(block, Blocks.air(), true ,false);
                            level.setBlock(block, placeBlock.clone(), true ,false);
                            ++count;
                        }
                    }

                    break;
            }

            if (count == 0) {
                sender.sendMessage(TextFormat.RED + "0 blocks filled");
            } else {
                sender.sendMessage(String.format("%1$d blocks filled", count));
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }

        return true;
    }

    enum FillMode {
        REPLACE,
        OUTLINE,
        HOLLOW,
        DESTROY,
        KEEP,
    }
}
