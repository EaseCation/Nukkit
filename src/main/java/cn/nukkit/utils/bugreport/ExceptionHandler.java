package cn.nukkit.utils.bugreport;

import lombok.extern.log4j.Log4j2;

/**
 * Project nukkit
 */
@Log4j2
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static void registerExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        handle(thread, throwable);
    }

    public void handle(Thread thread, Throwable throwable) {
//        throwable.printStackTrace();
        log.throwing(throwable);

        try {
            new BugReportGenerator(throwable).start();
        } catch (Exception exception) {
            // Fail Safe
        }
    }

}
