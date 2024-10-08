package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class PlayerEnderChestInventory extends BaseInventory {

    public PlayerEnderChestInventory(EntityHumanType player) {
        super(player, InventoryType.ENDER_CHEST);
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        if (who != this.getHolder()) {
            return;
        }

        super.onOpen(who);

        ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
        containerOpenPacket.windowId = who.getWindowId(this);
        containerOpenPacket.type = this.getType().getNetworkType();
        Vector3 chest = who.getViewingEnderChest();
        if (chest != null) {
            containerOpenPacket.x = (int) chest.getX();
            containerOpenPacket.y = (int) chest.getY();
            containerOpenPacket.z = (int) chest.getZ();
        } else {
            containerOpenPacket.x = containerOpenPacket.y = containerOpenPacket.z = 0;
        }
        who.dataPacket(containerOpenPacket);

        this.sendContents(who);

        if (chest instanceof BlockEntityEnderChest enderChest && enderChest.getViewers().size() == 1) {
            BlockEventPacket blockEventPacket = new BlockEventPacket();
            blockEventPacket.x = (int) chest.getX();
            blockEventPacket.y = (int) chest.getY();
            blockEventPacket.z = (int) chest.getZ();
            blockEventPacket.eventType = 1;
            blockEventPacket.eventData = 1;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().blockCenter(), LevelSoundEventPacket.SOUND_ENDERCHEST_OPEN);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket containerClosePacket = new ContainerClosePacket();
        containerClosePacket.windowId = who.getWindowId(this);
        containerClosePacket.wasServerInitiated = who.getClosingWindowId() != containerClosePacket.windowId;
        if (containerClosePacket.wasServerInitiated) {
            containerClosePacket.windowType = getType().getNetworkType();
        }
        who.resetClosingWindowId(containerClosePacket.windowId);
        who.dataPacket(containerClosePacket);

        super.onClose(who);

        Vector3 chest = who.getViewingEnderChest();
        if (chest instanceof BlockEntityEnderChest enderChest && enderChest.getViewers().size() == 1) {
            BlockEventPacket blockEventPacket = new BlockEventPacket();
            blockEventPacket.x = (int) chest.getX();
            blockEventPacket.y = (int) chest.getY();
            blockEventPacket.z = (int) chest.getZ();
            blockEventPacket.eventType = 1;
            blockEventPacket.eventData = 0;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.getHolder().blockCenter(), LevelSoundEventPacket.SOUND_ENDERCHEST_CLOSED);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
            }
        }
        who.setViewingEnderChest(null);

        super.onClose(who);
    }
}
