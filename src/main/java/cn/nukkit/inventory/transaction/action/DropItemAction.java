package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 */
public class DropItemAction extends InventoryAction {

    public DropItemAction(Item source, Item target) {
        super(source, target);
    }

    /**
     * Verifies that the source item of a drop-item action must be air. This is not strictly necessary, just a sanity
     * check.
     */
    @Override
    public boolean isValid(Player source) {
        return this.sourceItem.isNull() && !targetItem.isNull() && targetItem.getCount() <= targetItem.getMaxStackSize();
    }

    @Override
    public boolean onPreExecute(Player source) {
        PlayerDropItemEvent ev = new PlayerDropItemEvent(source, this.targetItem);
        if (source.isSpectator()) {
            ev.setCancelled();
        }
        source.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            source.stopAction();
        }

        return !ev.isCancelled();
    }

    /**
     * Drops the target item in front of the player.
     */
    @Override
    public boolean execute(Player source) {
        return source.dropItem(this.targetItem);
    }

    /**
     * 提交之前只取得掉落所需资源；实体事件必须在库存事务成为终态后触发。
     */
    @Nullable
    public PreparedDrop prepareAuthoritative(Player source) {
        if (!source.spawned || !source.isAlive() || source.isClosed()) {
            return null;
        }
        Level level = source.getLevel();
        Vector3 position = source.add(0, 1.3, 0);
        FullChunk chunk = level.getChunk(position.getChunkX(), position.getChunkZ(), true);
        if (chunk == null || chunk.getProvider() == null) {
            return null;
        }
        Vector3 motion = source.getDirectionVector().multiply(0.4);
        return new PreparedDrop(level, chunk, new Vector3(position.x, position.y, position.z), motion, targetItem.clone());
    }

    @Override
    public void onExecuteSuccess(Player source) {
        if (source.isServerAuthoritativeInventoryEnabled()) {
            source.setDataFlag(Player.DATA_FLAG_ACTION, false);
        }
    }

    @Override
    public void onExecuteFail(Player source) {

    }

    public static final class PreparedDrop {
        private final Level level;
        private final FullChunk chunk;
        private final Vector3 position;
        private final Vector3 motion;
        private final Item item;

        private PreparedDrop(Level level, FullChunk chunk, Vector3 position, Vector3 motion, Item item) {
            this.level = level;
            this.chunk = chunk;
            this.position = position;
            this.motion = motion;
            this.item = item;
        }

        public void spawn() {
            // 插件可在实体事件中接管或关闭掉落，不能据此退还已提交的库存。
            level.dropItem(chunk, position, item, motion, 40);
        }
    }
}
