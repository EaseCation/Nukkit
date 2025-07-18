package cn.nukkit.level.generator.task;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;
import cn.nukkit.scheduler.AsyncTask;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class GenerationTask extends AsyncTask<Void> {

    private final Level level;
    public boolean state;
    private BaseFullChunk chunk;

    public GenerationTask(Level level, BaseFullChunk chunk) {
        this.state = true;
        this.chunk = chunk;
        this.level = level;
    }

    @Override
    public void onRun() {
        this.state = false;
        Generator generator = level.getGenerator();
        if (generator == null) {
            Server.getInstance().getLogger().debug(level.getFolderName() + "/GenerationTask: generator == null");
            return;
        }

        SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();

        if (manager == null) {
            Server.getInstance().getLogger().debug(level.getFolderName() + "/GenerationTask: manager == null");
            return;
        }

        manager.cleanChunks(level.getSeed());
        synchronized (manager) {
            try {
                BaseFullChunk chunk = this.chunk;

                if (chunk == null) {
                    return;
                }

                synchronized (chunk) {
                    if (!chunk.isGenerated()) {
                        if (level.getProvider() == null) {
                            this.state = false;
                            Server.getInstance().getLogger().debug(level.getFolderName() + "/GenerationTask: provider == null");
                            return;
                        }

                        manager.setChunk(chunk.getX(), chunk.getZ(), chunk);
                        generator.generateChunk(chunk.getX(), chunk.getZ());
                        chunk = manager.getChunk(chunk.getX(), chunk.getZ());
                        chunk.setGenerated();
                    }
                }
                this.chunk = chunk;
                this.state = true;
            } finally {
                manager.cleanChunks(level.getSeed());
            }
        }
    }

    @Override
    public void onCompletion(Server server) {
        if (level != null) {
            if (!this.state) {
                return;
            }

            BaseFullChunk chunk = this.chunk;

            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}
