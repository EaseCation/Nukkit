package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerInteractEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Block blockTouched;

    protected final Vector3 touchVector;

    protected final BlockFace blockFace;

    protected final Item item;

    protected final Action action;

    protected long unkownEntityId = -1;

    public PlayerInteractEvent(Player player, Item item, Vector3 block, BlockFace face) {
        this(player, item, block, face, Action.RIGHT_CLICK_BLOCK);
    }

    public PlayerInteractEvent(Player player, Item item, Vector3 block, @Nullable BlockFace face, Action action) {
        if (block instanceof Block) {
            this.blockTouched = (Block) block;
            this.touchVector = new Vector3(0, 0, 0);
        } else {
            this.touchVector = block;
            this.blockTouched = Block.get(Block.AIR, 0, new Position(0, 0, 0, player.level));
        }
        if (item == null) {
            item = Items.air();
        }

        this.player = player;
        this.item = item;
        this.blockFace = face;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public Item getItem() {
        return item;
    }

    public Block getBlock() {
        return blockTouched;
    }

    public Vector3 getTouchVector() {
        return touchVector;
    }

    /**
     * 方块交互面. 右击空气时为 {@code null}.
     */
    @Nullable
    public BlockFace getFace() {
        return blockFace;
    }

    public PlayerInteractEvent setUnkownEntityId(long unkownEntityId) {
        this.unkownEntityId = unkownEntityId;
        return this;
    }

    public long getUnkownEntityId() {
        return unkownEntityId;
    }

    public enum Action {
        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_AIR,
        RIGHT_CLICK_AIR,
        CLICK_UNKNOWN_ENTITY,
        PHYSICAL
    }
}
