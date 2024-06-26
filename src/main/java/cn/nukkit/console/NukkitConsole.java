package cn.nukkit.console;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.event.server.ServerInterruptEvent;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NukkitConsole extends SimpleTerminalConsole {
    private final Server server;
    private final BlockingQueue<String> consoleQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean executingCommands = new AtomicBoolean(false);

    public NukkitConsole(Server server) {
        this.server = server;
    }

    @Override
    protected boolean isRunning() {
        return server.isRunning();
    }

    @Override
    protected void runCommand(String command) {
        if (executingCommands.get()) {
            CommandSender sender = server.getConsoleSender();
            if (sender == null) {
                return;
            }
            ServerCommandEvent event = new ServerCommandEvent(sender, command);
            if (server.getPluginManager() != null) {
                server.getPluginManager().callEvent(event);
            }
            if (!event.isCancelled()) {
                Server.getInstance().getScheduler().scheduleTask(null, () -> server.dispatchCommand(event.getSender(), event.getCommand()));
            }
        } else {
            consoleQueue.add(command);
        }
    }

    public String readLine() {
        try {
            return consoleQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void shutdown() {
        ServerInterruptEvent event = new ServerInterruptEvent();
        this.server.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            server.shutdown();
        }
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        builder.completer(new NukkitConsoleCompleter(server));
        builder.appName("Nukkit");
        builder.option(LineReader.Option.HISTORY_BEEP, false);
        builder.option(LineReader.Option.HISTORY_IGNORE_DUPS, true);
        builder.option(LineReader.Option.HISTORY_IGNORE_SPACE, true);
        return super.buildReader(builder);
    }

    public boolean isExecutingCommands() {
        return executingCommands.get();
    }

    public void setExecutingCommands(boolean executingCommands) {
        if (this.executingCommands.compareAndSet(!executingCommands, executingCommands) && executingCommands) {
            consoleQueue.clear();
        }
    }
}
