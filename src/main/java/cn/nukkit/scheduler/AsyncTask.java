package cn.nukkit.scheduler;

import cn.nukkit.Server;
import lombok.extern.log4j.Log4j2;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Nukkit Project Team
 */
@Log4j2
public abstract class AsyncTask<T> implements Runnable {

    public static final Queue<AsyncTask<?>> FINISHED_LIST = new ConcurrentLinkedQueue<>();

    private volatile T result;
    private volatile int taskId;
    private volatile boolean finished;

    @Override
    public void run() {
        this.result = null;
        this.onRun();
        this.finished = true;
        FINISHED_LIST.offer(this);
    }

    public boolean isFinished() {
        return this.finished;
    }

    public T getResult() {
        return this.result;
    }

    public boolean hasResult() {
        return this.result != null;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public abstract void onRun();

    public void onCompletion(Server server) {

    }

    public void cleanObject() {
        this.result = null;
        this.taskId = 0;
        this.finished = false;
    }

    public static void collectTask() {
        AsyncTask<?> task;
        while ((task = FINISHED_LIST.poll()) != null) {
            try {
                task.onCompletion(Server.getInstance());
            } catch (Exception e) {
                log.fatal("Exception while async task "
                        + task.getTaskId()
                        + " invoking onCompletion", e);
            }
        }
    }

}
