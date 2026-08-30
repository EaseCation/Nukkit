package cn.nukkit.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Main logger
 *
 * @author MagicDroidX
 * Nukkit
 */
//We need to keep this class for backwards compatibility
public class MainLogger implements org.slf4j.Logger, Logger {

    private static final MainLogger INSTANCE = new MainLogger();

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(MainLogger.class);

    public static MainLogger getLogger() {
        return INSTANCE;
    }

    private MainLogger() {
    }

    @Override
    public String getName() {
        return this.logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String message) {
        this.logger.trace(message);
    }

    @Override
    public void trace(String format, Object argument) {
        this.logger.trace(format, argument);
    }

    @Override
    public void trace(String format, Object firstArgument, Object secondArgument) {
        this.logger.trace(format, firstArgument, secondArgument);
    }

    @Override
    public void trace(String format, Object... arguments) {
        this.logger.trace(format, arguments);
    }

    @Override
    public void trace(String message, Throwable throwable) {
        this.logger.trace(message, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return this.logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String message) {
        this.logger.trace(marker, message);
    }

    @Override
    public void trace(Marker marker, String format, Object argument) {
        this.logger.trace(marker, format, argument);
    }

    @Override
    public void trace(Marker marker, String format, Object firstArgument, Object secondArgument) {
        this.logger.trace(marker, format, firstArgument, secondArgument);
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        this.logger.trace(marker, format, arguments);
    }

    @Override
    public void trace(Marker marker, String message, Throwable throwable) {
        this.logger.trace(marker, message, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String message) {
        this.logger.debug(message);
    }

    @Override
    public void debug(String format, Object argument) {
        this.logger.debug(format, argument);
    }

    @Override
    public void debug(String format, Object firstArgument, Object secondArgument) {
        this.logger.debug(format, firstArgument, secondArgument);
    }

    @Override
    public void debug(String format, Object... arguments) {
        this.logger.debug(format, arguments);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        this.logger.debug(message, throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return this.logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String message) {
        this.logger.debug(marker, message);
    }

    @Override
    public void debug(Marker marker, String format, Object argument) {
        this.logger.debug(marker, format, argument);
    }

    @Override
    public void debug(Marker marker, String format, Object firstArgument, Object secondArgument) {
        this.logger.debug(marker, format, firstArgument, secondArgument);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        this.logger.debug(marker, format, arguments);
    }

    @Override
    public void debug(Marker marker, String message, Throwable throwable) {
        this.logger.debug(marker, message, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void info(String format, Object argument) {
        this.logger.info(format, argument);
    }

    @Override
    public void info(String format, Object firstArgument, Object secondArgument) {
        this.logger.info(format, firstArgument, secondArgument);
    }

    @Override
    public void info(String format, Object... arguments) {
        this.logger.info(format, arguments);
    }

    @Override
    public void info(String message, Throwable throwable) {
        this.logger.info(message, throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return this.logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String message) {
        this.logger.info(marker, message);
    }

    @Override
    public void info(Marker marker, String format, Object argument) {
        this.logger.info(marker, format, argument);
    }

    @Override
    public void info(Marker marker, String format, Object firstArgument, Object secondArgument) {
        this.logger.info(marker, format, firstArgument, secondArgument);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        this.logger.info(marker, format, arguments);
    }

    @Override
    public void info(Marker marker, String message, Throwable throwable) {
        this.logger.info(marker, message, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public void warn(String message) {
        this.logger.warn(message);
    }

    @Override
    public void warn(String format, Object argument) {
        this.logger.warn(format, argument);
    }

    @Override
    public void warn(String format, Object firstArgument, Object secondArgument) {
        this.logger.warn(format, firstArgument, secondArgument);
    }

    @Override
    public void warn(String format, Object... arguments) {
        this.logger.warn(format, arguments);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        this.logger.warn(message, throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return this.logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String message) {
        this.logger.warn(marker, message);
    }

    @Override
    public void warn(Marker marker, String format, Object argument) {
        this.logger.warn(marker, format, argument);
    }

    @Override
    public void warn(Marker marker, String format, Object firstArgument, Object secondArgument) {
        this.logger.warn(marker, format, firstArgument, secondArgument);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        this.logger.warn(marker, format, arguments);
    }

    @Override
    public void warn(Marker marker, String message, Throwable throwable) {
        this.logger.warn(marker, message, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public void error(String message) {
        this.logger.error(message);
    }

    @Override
    public void error(String format, Object argument) {
        this.logger.error(format, argument);
    }

    @Override
    public void error(String format, Object firstArgument, Object secondArgument) {
        this.logger.error(format, firstArgument, secondArgument);
    }

    @Override
    public void error(String format, Object... arguments) {
        this.logger.error(format, arguments);
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.logger.error(message, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return this.logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String message) {
        this.logger.error(marker, message);
    }

    @Override
    public void error(Marker marker, String format, Object argument) {
        this.logger.error(marker, format, argument);
    }

    @Override
    public void error(Marker marker, String format, Object firstArgument, Object secondArgument) {
        this.logger.error(marker, format, firstArgument, secondArgument);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        this.logger.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String message, Throwable throwable) {
        this.logger.error(marker, message, throwable);
    }

    @Override
    public void emergency(String message) {
        this.logger.error(message);
    }

    @Override
    public void alert(String message) {
        this.logger.warn(message);
    }

    @Override
    public void critical(String message) {
        this.logger.error(message);
    }

    @Override
    public void warning(String message) {
        this.logger.warn(message);
    }

    @Override
    public void notice(String message) {
        this.logger.warn(message);
    }

    @Override
    public void log(LogLevel level, String message) {
        level.log(this, message);
    }

    public void logException(Throwable throwable) {
        this.logger.error("Throwing", throwable);
    }

    @Override
    public void emergency(String message, Throwable throwable) {
        this.logger.error(message, throwable);
    }

    @Override
    public void alert(String message, Throwable throwable) {
        this.logger.warn(message, throwable);
    }

    @Override
    public void critical(String message, Throwable throwable) {
        this.logger.error(message, throwable);
    }

    @Override
    public void warning(String message, Throwable throwable) {
        this.logger.warn(message, throwable);
    }

    @Override
    public void notice(String message, Throwable throwable) {
        this.logger.warn(message, throwable);
    }

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        level.log(this, message, throwable);
    }
}
