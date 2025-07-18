package cn.nukkit.level.generator.task;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.scheduler.AsyncTask;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class LightPopulationTask extends AsyncTask<Void> {

    public final int levelId;
    public BaseFullChunk chunk;

    public LightPopulationTask(Level level, BaseFullChunk chunk) {
        this.levelId = level.getId();
        this.chunk = chunk;
    }

    @Override
    public void onRun() {
        BaseFullChunk chunk = this.chunk.clone();
        if (chunk == null) {
            return;
        }

        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();

        this.chunk = chunk.clone();
    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);

        if (level != null) {
            BaseFullChunk chunk = this.chunk.clone();
            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}
