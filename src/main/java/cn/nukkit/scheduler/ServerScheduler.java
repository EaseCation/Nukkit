package cn.nukkit.scheduler;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.log4j.Log4j2;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nukkit Project Team
 */
@Log4j2
public class ServerScheduler {

    private final ThreadPoolExecutor asyncPool;

    private final Queue<TaskHandler> pending;
    private final Queue<TaskHandler> queue;
    private final Map<Integer, TaskHandler> taskMap;
    private final AtomicInteger currentTaskId;

    private volatile int currentTick;

    public ServerScheduler() {
        this(0);
    }

    public ServerScheduler(int corePoolSize) {
        this(corePoolSize, Integer.MAX_VALUE);
    }

    public ServerScheduler(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, 60);
    }

    public ServerScheduler(int corePoolSize, int maximumPoolSize, long keepAliveSeconds) {
        this.pending = new ConcurrentLinkedQueue<>();
        this.currentTaskId = new AtomicInteger(2);
        this.queue = new PriorityQueue<>(Comparator.comparingInt(TaskHandler::getNextRunTick).thenComparingInt(TaskHandler::getTaskId));
        this.taskMap = new ConcurrentHashMap<>();
        this.asyncPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveSeconds, TimeUnit.SECONDS,
                corePoolSize == maximumPoolSize ? new LinkedBlockingQueue<>() : new SynchronousQueue<>(), new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("Nukkit Asynchronous Task Handler #%d")
                .setUncaughtExceptionHandler((thread, ex) -> log.fatal("Exception in asynchronous thread", ex))
                .build());
    }

    public ThreadPoolExecutor getAsyncPool() {
        return asyncPool;
    }

    public TaskHandler scheduleTask(Task task) {
        return addTask(task, 0, 0, false);
    }

    /**
     * @deprecated Use {@link #scheduleTask(Plugin, Runnable)}
     */
    @Deprecated
    public TaskHandler scheduleTask(Runnable task) {
        return addTask(null, task, 0, 0, false);
    }

    public TaskHandler scheduleTask(Plugin plugin, Runnable task) {
        return addTask(plugin, task, 0, 0, false);
    }

    /**
     * @deprecated Use {@link #scheduleTask(Plugin, Runnable, boolean)}
     */
    @Deprecated
    public TaskHandler scheduleTask(Runnable task, boolean asynchronous) {
        return addTask(null, task, 0, 0, asynchronous);
    }

    public TaskHandler scheduleTask(Plugin plugin, Runnable task, boolean asynchronous) {
        return addTask(plugin, task, 0, 0, asynchronous);
    }

    public TaskHandler scheduleTask(Plugin plugin, Runnable task, boolean asynchronous, boolean virtual) {
        return addTask(plugin, task, 0, 0, asynchronous, virtual);
    }

    /**
     * @deprecated Use {@link #scheduleAsyncTask(Plugin, AsyncTask)}
     */
    @Deprecated
    public TaskHandler scheduleAsyncTask(AsyncTask task) {
        return addTask(null, task, 0, 0, true, task.isVirtual());
    }

    public TaskHandler scheduleAsyncTask(Plugin plugin, AsyncTask task) {
        return addTask(plugin, task, 0, 0, true, task.isVirtual());
    }

    public TaskHandler scheduleDelayedTask(Task task, int delay) {
        return this.addTask(task, delay, 0, false);
    }

    public TaskHandler scheduleDelayedTask(Task task, int delay, boolean asynchronous) {
        return this.addTask(task, delay, 0, asynchronous);
    }

    public TaskHandler scheduleDelayedTask(Task task, int delay, boolean asynchronous, boolean virtual) {
        return this.addTask(task, delay, 0, asynchronous, virtual);
    }

    /**
     * @deprecated Use {@link #scheduleDelayedTask(Plugin, Runnable, int)}
     */
    @Deprecated
    public TaskHandler scheduleDelayedTask(Runnable task, int delay) {
        return addTask(null, task, delay, 0, false);
    }

    public TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay) {
        return addTask(plugin, task, delay, 0, false);
    }

    /**
     * @deprecated Use {@link #scheduleDelayedTask(Plugin, Runnable, int, boolean)}
     */
    @Deprecated
    public TaskHandler scheduleDelayedTask(Runnable task, int delay, boolean asynchronous) {
        return addTask(null, task, delay, 0, asynchronous);
    }

    public TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay, boolean asynchronous) {
        return addTask(plugin, task, delay, 0, asynchronous);
    }

    public TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay, boolean asynchronous, boolean virtual) {
        return addTask(plugin, task, delay, 0, asynchronous, virtual);
    }

    /**
     * @deprecated Use {@link #scheduleRepeatingTask(Plugin, Runnable, int)}
     */
    @Deprecated
    public TaskHandler scheduleRepeatingTask(Runnable task, int period) {
        return addTask(null, task, 0, period, false);
    }

    public TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period) {
        return addTask(plugin, task, 0, period, false);
    }

    /**
     * @deprecated Use {@link #scheduleRepeatingTask(Plugin, Runnable, int, boolean)}
     */
    @Deprecated
    public TaskHandler scheduleRepeatingTask(Runnable task, int period, boolean asynchronous) {
        return addTask(null, task, 0, period, asynchronous);
    }

    public TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period, boolean asynchronous) {
        return addTask(plugin, task, 0, period, asynchronous);
    }

    public TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period, boolean asynchronous, boolean virtual) {
        return addTask(plugin, task, 0, period, asynchronous, virtual);
    }

    public TaskHandler scheduleRepeatingTask(Task task, int period) {
        return addTask(task, 0, period, false);
    }

    public TaskHandler scheduleRepeatingTask(Task task, int period, boolean asynchronous) {
        return addTask(task, 0, period, asynchronous);
    }

    public TaskHandler scheduleRepeatingTask(Task task, int period, boolean asynchronous, boolean virtual) {
        return addTask(task, 0, period, asynchronous, virtual);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Task task, int delay, int period) {
        return addTask(task, delay, period, false);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Task task, int delay, int period, boolean asynchronous) {
        return addTask(task, delay, period, asynchronous);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Task task, int delay, int period, boolean asynchronous, boolean virtual) {
        return addTask(task, delay, period, asynchronous, virtual);
    }

    /**
     * @deprecated Use {@link #scheduleDelayedRepeatingTask(Plugin, Runnable, int, int)}
     */
    @Deprecated
    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period) {
        return addTask(null, task, delay, period, false);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period) {
        return addTask(plugin, task, delay, period, false);
    }

    /**
     * @deprecated Use {@link #scheduleDelayedRepeatingTask(Plugin, Runnable, int, int, boolean)}
     */
    @Deprecated
    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period, boolean asynchronous) {
        return addTask(null, task, delay, period, asynchronous);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous) {
        return addTask(plugin, task, delay, period, asynchronous);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous, boolean virtual) {
        return addTask(plugin, task, delay, period, asynchronous, virtual);
    }

    public void cancelTask(int taskId) {
        TaskHandler taskHandler = taskMap.remove(taskId);
        if (taskHandler != null) {
            try {
                taskHandler.cancel();
            } catch (RuntimeException ex) {
                log.fatal("Exception while invoking onCancel", ex);
            }
        }
    }

    public void cancelTask(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("Plugin cannot be null!");
        }
        for (Map.Entry<Integer, TaskHandler> entry : taskMap.entrySet()) {
            TaskHandler taskHandler = entry.getValue();
            // TODO: Remove the "taskHandler.getPlugin() == null" check
            // It is only there for backwards compatibility!
            if (taskHandler.getPlugin() == null || plugin.equals(taskHandler.getPlugin())) {
                try {
                    taskHandler.cancel(); /* It will remove from task map automatic in next main heartbeat. */
                } catch (RuntimeException ex) {
                   log.fatal("Exception while invoking onCancel", ex);
                }
            }
        }
    }

    public void cancelAllTasks() {
        for (Map.Entry<Integer, TaskHandler> entry : this.taskMap.entrySet()) {
            try {
                entry.getValue().cancel();
            } catch (RuntimeException ex) {
                log.fatal("Exception while invoking onCancel", ex);
            }
        }
        this.taskMap.clear();
        this.queue.clear();
        this.currentTaskId.set(0);
    }

    public boolean isQueued(int taskId) {
        return this.taskMap.containsKey(taskId);
    }

    private TaskHandler addTask(Task task, int delay, int period, boolean asynchronous) {
        return addTask(task instanceof PluginTask ? ((PluginTask) task).getOwner() : null, task, delay, period, asynchronous, false);
    }

    private TaskHandler addTask(Task task, int delay, int period, boolean asynchronous, boolean virtual) {
        return addTask(task instanceof PluginTask ? ((PluginTask) task).getOwner() : null, task, delay, period, asynchronous, virtual);
    }

    private TaskHandler addTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous) {
        return addTask(plugin, task, delay, period, asynchronous, false);
    }

    private TaskHandler addTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous, boolean virtual) {
        if (plugin != null && plugin.isDisabled()) {
            throw new PluginException("Plugin '" + plugin.getName() + "' attempted to register a task while disabled.");
        }
        if (delay < 0 || period < 0) {
            throw new PluginException("Attempted to register a task with negative delay or period.");
        }

        TaskHandler taskHandler = new TaskHandler(plugin, task, nextTaskId(), asynchronous, virtual);
        taskHandler.setDelay(delay);
        taskHandler.setPeriod(period);
        taskHandler.setNextRunTick(taskHandler.isDelayed() ? currentTick + taskHandler.getDelay() : currentTick);

        if (task instanceof Task) {
            ((Task) task).setHandler(taskHandler);
        }

        pending.offer(taskHandler);
        taskMap.put(taskHandler.getTaskId(), taskHandler);

        return taskHandler;
    }

    public void mainThreadHeartbeat(int currentTick) {
        this.currentTick = currentTick;
        // Accepts pending.
        TaskHandler pendingTask;
        while ((pendingTask = pending.poll()) != null) {
            queue.offer(pendingTask);
        }
        // Main heart beat.
        while (isReady(currentTick)) {
            TaskHandler taskHandler = queue.poll();
            if (taskHandler.isCancelled()) {
                taskMap.remove(taskHandler.getTaskId());
                continue;
            } else if (taskHandler.isAsynchronous()) {
                if (taskHandler.isVirtual()) {
                    Thread.ofVirtual()
                            .name("Nukkit Asynchronous (Virtual) Task Handler #", 0)
                            .uncaughtExceptionHandler((thread, ex) -> log.fatal("Exception in asynchronous (virtual) task", ex))
                            .start(taskHandler.getTask());
                } else {
                    asyncPool.execute(() -> {
                        try {
                            taskHandler.getTask().run();
                        } catch (Throwable e) {
                            log.fatal("Exception in asynchronous task", e);
                        }
                    });
                }
            } else {
                try {
                    taskHandler.run(currentTick);
                } catch (Throwable e) {
                    log.fatal("Could not execute taskHandler " + taskHandler.getTaskId() + ": ", e);
                }
            }
            if (taskHandler.isRepeating()) {
                taskHandler.setNextRunTick(currentTick + taskHandler.getPeriod());
                pending.offer(taskHandler);
            } else {
                try {
                    TaskHandler handler = taskMap.remove(taskHandler.getTaskId());
                    if (handler != null) {
                        handler.cancel();
                    }
                } catch (RuntimeException ex) {
                    log.fatal("Exception while invoking onCancel", ex);
                }
            }
        }
        AsyncTask.collectTask();
    }

    private boolean isReady(int currentTick) {
        TaskHandler taskHandler = this.queue.peek();
        return taskHandler != null && taskHandler.getNextRunTick() <= currentTick;
    }

    private int nextTaskId() {
        return currentTaskId.incrementAndGet();
    }

}
