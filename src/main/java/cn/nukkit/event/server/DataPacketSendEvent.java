package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.protocol.DataPacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DataPacketSendEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final DataPacket packet;
    private final Player player;

    private DataPacket finalPacket;
    @Nullable
    private List<DataPacket> replacedPackets;

    public DataPacketSendEvent(Player player, DataPacket packet) {
        this.packet = packet;
        this.player = player;
        finalPacket = packet;
    }

    public Player getPlayer() {
        return player;
    }

    public DataPacket getPacket() {
        return packet;
    }

    public void setPacket(DataPacket packet) {
        Objects.requireNonNull(packet, "packet");
        if (finalPacket == packet) {
            return;
        }
        if (replacedPackets == null) {
            replacedPackets = new ArrayList<>(1);
        }
        replacedPackets.add(finalPacket);
        finalPacket = packet;
    }

    public DataPacket getFinalPacket() {
        return finalPacket;
    }

    @Nullable
    public List<DataPacket> getReplacedPackets() {
        return replacedPackets;
    }
}
