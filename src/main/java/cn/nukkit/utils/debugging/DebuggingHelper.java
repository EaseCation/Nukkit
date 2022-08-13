package cn.nukkit.utils.debugging;

import lombok.extern.log4j.Log4j2;

@Log4j2
public final class DebuggingHelper {

    public static void checkCurrentThreadIs(Thread thread) {
        Thread current = Thread.currentThread();
        if (current == thread) {
            return;
        }
        log.warn("different thread: expected {} got {}", thread, current, new Throwable());
    }

    private DebuggingHelper() {
        throw new IllegalStateException();
    }
}
