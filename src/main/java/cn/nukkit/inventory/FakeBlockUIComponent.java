package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.DataPacket;

public abstract class FakeBlockUIComponent extends PlayerUIComponent {
    private final InventoryType type;

    FakeBlockUIComponent(PlayerUIInventory playerUI, InventoryType type, int offset, int size, Position position) {
        super(playerUI, offset, size);
        this.type = type;
        this.holder = new FakeBlockMenu(this, position);
    }

    /**
     * 方块重复交互时复用当前工作站，避免关闭后重新打开同一窗口。
     */
    public static boolean isCurrentWindowAt(Player player, InventoryType type, Position position) {
        if (!(player.getCurrentWindow() instanceof FakeBlockUIComponent current) || current.type != type) {
            return false;
        }
        Position currentPosition = current.getHolder();
        return currentPosition.getLevel() == position.getLevel()
                && currentPosition.getFloorX() == position.getFloorX()
                && currentPosition.getFloorY() == position.getFloorY()
                && currentPosition.getFloorZ() == position.getFloorZ();
    }

    @Override
    public FakeBlockMenu getHolder() {
        return (FakeBlockMenu) this.holder;
    }

    public int getNetworkType() {
        return this.type.getNetworkType();
    }

    @Override
    public boolean open(Player who) {
        InventoryOpenEvent ev = new InventoryOpenEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.onOpen(who);

        return true;
    }

    @Override
    public void close(Player who) {
        InventoryCloseEvent ev = new InventoryCloseEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);

        this.onClose(who);
    }

    @Override
    public DataPacket createContainerOpenPacket(Player player, int windowId) {
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = windowId;
        player.setLastOpenedWindowId(pk.windowId);
        pk.type = this.getNetworkType();
        InventoryHolder holder = this.getHolder();
        if (holder != null) {
            pk.x = (int) ((Vector3) holder).getX();
            pk.y = (int) ((Vector3) holder).getY();
            pk.z = (int) ((Vector3) holder).getZ();
        } else {
            pk.x = pk.y = pk.z = 0;
        }
        return pk;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        if (who.isServerAuthoritativeInventoryEnabled()) {
            return;
        }
        who.dataPacket(createContainerOpenPacket(who, who.getWindowId(this)));

        this.sendContents(who);
    }

    @Override
    public void onClose(Player who) {
        if (!who.isServerAuthoritativeInventoryEnabled()
                && who.getClosingWindowId() != Integer.MAX_VALUE) {
            ContainerClosePacket pk = new ContainerClosePacket();
            pk.windowId = who.getWindowId(this);
            pk.wasServerInitiated = who.getClosingWindowId() != pk.windowId;
            if (pk.wasServerInitiated) {
                pk.windowType = this.getNetworkType();
            }
            who.resetClosingWindowId(pk.windowId);
            who.dataPacket(pk);
        }
        super.onClose(who);
    }
}
