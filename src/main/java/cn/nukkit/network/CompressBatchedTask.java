package cn.nukkit.network;

import cn.nukkit.Server;
import cn.nukkit.network.protocol.BatchPacket.Track;
import cn.nukkit.scheduler.AsyncTask;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CompressBatchedTask extends AsyncTask<byte[]> {

    private final Compressor compressor;
    private final int level;
    private byte[] data;
    private final List<InetSocketAddress> targets;
    private final Track[] tracks;

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, Compressor compressor) {
        this(data, targets, compressor, Server.getInstance().networkCompressionLevel);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, Compressor compressor, int level) {
        this(data, targets, compressor, level, null);
    }

    public CompressBatchedTask(byte[] data, List<InetSocketAddress> targets, Compressor compressor, int level, Track[] tracks) {
        this.data = data;
        this.targets = targets;
        this.compressor = compressor;
        this.level = level;
        this.tracks = tracks;
    }

    @Override
    public void onRun() {
        byte[] data = this.data;
        if (data == null) {
            return;
        }
        try {
            setResult(compressor.compress(data, level));
            this.data = null;
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void onCompletion(Server server) {
        byte[] data = getResult();
        if (data == null) {
            return;
        }
        server.broadcastPacketsCallback(data, this.targets, tracks);
    }
}
