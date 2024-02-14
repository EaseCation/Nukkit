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
import cn.nukkit.level.HeightRange;
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
public class CloneCommand extends VanillaCommand {

    public CloneCommand(String name) {
        super(name, "%commands.clone.description", "%nukkit.command.clone.usage");
        this.setPermission("nukkit.command.clone");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("begin", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("maskMode", true, new CommandEnum("MaskMode", Stream.of(MaskMode.values())
                        .filter(mode -> mode != MaskMode.FILTERED)
                        .map(mode -> mode.toString().toLowerCase())
                        .collect(Collectors.toSet()))),
                CommandParameter.newEnum("cloneMode", true, new CommandEnum("CloneMode", CloneMode.values())),
        });
        this.commandParameters.put("filtered", new CommandParameter[]{
                CommandParameter.newType("begin", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("maskMode", new CommandEnum("MaskModeFiltered", "filtered")),
                CommandParameter.newEnum("cloneMode", new CommandEnum("CloneMode", CloneMode.values())),
                CommandParameter.newEnum("block", CommandEnum.ENUM_BLOCK)
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
            Position begin = parser.parsePosition().floor();
            Position end = parser.parsePosition().floor();
            Position destination = parser.parsePosition().floor();
            MaskMode maskMode = MaskMode.REPLACE;
            CloneMode cloneMode = CloneMode.NORMAL;
            Block filter = Blocks.air();

            if (parser.hasNext()) {
                maskMode = parser.parseEnum(MaskMode.class);
                if (parser.hasNext()) {
                    cloneMode = parser.parseEnum(CloneMode.class);
                    if (parser.hasNext()) {
                        filter = parser.parseBlock();
                    }
                }
            }

            AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
            int size = Mth.floor((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

            if (size > 16 * 16 * 256 * 8) {
                sender.sendMessage(String.format(TextFormat.RED + "Too many blocks in the specified area (%1$d > %2$d)", size, 16 * 16 * 256 * 8));
                return true;
            }

            Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
            AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));
            Level level = begin.getLevel();
            HeightRange heightRange = level.getHeightRange();

            if (blocksAABB.getMinY() < heightRange.getMinY() || blocksAABB.getMaxY() > heightRange.getMaxY() || destinationAABB.getMinY() < heightRange.getMinY() || destinationAABB.getMaxY() > heightRange.getMaxY()) {
                sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                return true;
            }
            if (blocksAABB.intersectsWith(destinationAABB) && cloneMode != CloneMode.FORCE) {
                sender.sendMessage(TextFormat.RED + "Source and destination can not overlap");
                return true;
            }

            for (int sourceChunkX = Mth.floor(blocksAABB.getMinX()) >> 4, destinationChunkX = Mth.floor(destinationAABB.getMinX()) >> 4; sourceChunkX <= Mth.floor(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
                for (int sourceChunkZ = Mth.floor(blocksAABB.getMinZ()) >> 4, destinationChunkZ = Mth.floor(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= Mth.floor(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                    if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                        sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                        return true;
                    }
                    if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                        sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                        return true;
                    }
                }
            }

            List<Block> blocks = level.getBlocks(blocksAABB);
            List<Block> extraBlocks = level.getExtraBlocks(blocksAABB);
            List<Block> destinationBlocks = level.getBlocks(destinationAABB);
            int count = 0;

            boolean move = cloneMode == CloneMode.MOVE;
            switch (maskMode) {
                case REPLACE:
                    for (int i = 0; i < blocks.size(); i++) {
                        Block block = blocks.get(i);
                        Block extraBlock = extraBlocks.get(i);
                        Block destinationBlock = destinationBlocks.get(i);

                        level.setExtraBlock(destinationBlock, extraBlock.clone(), true, false);
                        level.setBlock(destinationBlock, block.clone(), true, false);
                        ++count;

                        if (move) {
                            level.setExtraBlock(block, Blocks.air(), true, false);
                            level.setBlock(block, Blocks.air(), true, false);
                        }
                    }

                    break;
                case MASKED:
                    for (int i = 0; i < blocks.size(); i++) {
                        Block block = blocks.get(i);
                        Block extraBlock = extraBlocks.get(i);
                        Block destinationBlock = destinationBlocks.get(i);

                        if (block.getId() != Block.AIR) {
                            level.setExtraBlock(destinationBlock, extraBlock.clone(), true, false);
                            level.setBlock(destinationBlock, block.clone(), true, false);
                            ++count;

                            if (move) {
                                level.setExtraBlock(block, Blocks.air(), true, false);
                                level.setBlock(block, Blocks.air(), true, false);
                            }
                        }
                    }

                    break;
                case FILTERED:
                    for (int i = 0; i < blocks.size(); i++) {
                        Block block = blocks.get(i);
                        Block extraBlock = extraBlocks.get(i);
                        Block destinationBlock = destinationBlocks.get(i);

                        if (block.getId() == filter.getId() && block.getDamage() == filter.getDamage()) {
                            level.setExtraBlock(destinationBlock, extraBlock.clone(), true, false);
                            level.setBlock(destinationBlock, block.clone(), true, false);
                            ++count;

                            if (move) {
                                level.setExtraBlock(block, Blocks.air(), true, false);
                                level.setBlock(block, Blocks.air(), true, false);
                            }
                        }
                    }

                    break;
            }

            if (count == 0) {
                sender.sendMessage(TextFormat.RED + "No blocks cloned");
            } else {
                sender.sendMessage(String.format("%1$d blocks cloned", count));
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }

        return true;
    }

    enum MaskMode {
        REPLACE,
        MASKED,
        FILTERED,
    }

    enum CloneMode {
        NORMAL,
        FORCE,
        MOVE,
    }
}
