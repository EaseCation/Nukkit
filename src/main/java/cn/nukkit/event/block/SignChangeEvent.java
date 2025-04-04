package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SignChangeEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    //TODO
    public static final int FLAG_FRONT_TEXT = 0b1;
    public static final int FLAG_COLOR = 0b10;
    public static final int FLAG_GLOW = 0b100;
    public static final int FLAG_BACK_TEXT = 0b1000;
    public static final int FLAG_WAX = 0b10000;
    public static final int FLAG_TEXT = FLAG_FRONT_TEXT | FLAG_BACK_TEXT;

    private final Player player;
    private final BlockEntitySign blockEntity;

    private final int flags;
    private final String[] lines;
    private final String[] backLines;

    public SignChangeEvent(Block block, Player player, BlockEntitySign blockEntity, int flags, String[] lines, String[] backLines) {
        super(block);
        this.player = player;
        this.blockEntity = blockEntity;
        this.flags = flags;
        this.lines = lines;
        this.backLines = backLines;
    }

    public Player getPlayer() {
        return player;
    }

    public BlockEntitySign getBlockEntity() {
        return blockEntity;
    }

    public int getFlags() {
        return flags;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int index) {
        return this.lines[index];
    }

    public void setLine(int index, String line) {
        this.lines[index] = line;
    }

    public String[] getBackLines() {
        return backLines;
    }

    public String getBackLine(int index) {
        return this.backLines[index];
    }

    public void setBackLine(int index, String line) {
        this.backLines[index] = line;
    }
}
