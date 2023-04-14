package cn.nukkit.network;

import cn.nukkit.Server;
import cn.nukkit.network.protocol.BatchPacket.Track;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Zlib;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CompressBatchedTask extends AsyncTask<Void> {

    private final int level;
    private byte[] data;
    private byte[] finalData;
    private final int channel;
    private final List<InetSocketAddress> targets;
    private final Track[] tracks;
    private final boolean zlibRaw;

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets) {
        this(data, targets, null);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, Track[] tracks) {
        this(data, targets, 7, tracks);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, int level) {
        this(data, targets, level, null);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, int level, Track[] tracks) {
        this(data, targets, level, 0, tracks);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, int level, int channel) {
        this(data, targets, level, channel, null);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, int level, int channel, Track[] tracks) {
        this(data, targets, level, channel, tracks, false);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, int level, boolean zlibRaw) {
        this(data, targets, level, null, zlibRaw);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, int level, Track[] tracks, boolean zlibRaw) {
        this(data, targets, level, 0, tracks, zlibRaw);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, int level, int channel, boolean zlibRaw) {
        this(data, targets, level, channel, null, zlibRaw);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, int level, int channel, Track[] tracks, boolean zlibRaw) {
        this.data = data;
        this.targets = targets;
        this.level = level;
        this.channel = channel;
        this.tracks = tracks;
        this.zlibRaw = zlibRaw;
    }

    @Override
    public void onRun() {
        try {
            if (zlibRaw) this.finalData = Network.deflateRaw(data, level);
            else this.finalData = Zlib.deflate(this.data, this.level);
            this.data = null;
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void onCompletion(Server server) {
        server.broadcastPacketsCallback(this.finalData, this.targets, tracks);
    }
}
