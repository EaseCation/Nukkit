package cn.nukkit.utils.bugreport;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "NukkitExceptionHandler", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class ExceptionHandlerAppender extends AbstractAppender {
    private static LogExceptionHandler HANDLER;

    public ExceptionHandlerAppender(String name) {
        super(name, null, null, true, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent event) {
        Throwable throwable = event.getThrown();
        if (throwable == null) {
            return;
        }

        LogExceptionHandler handler = HANDLER;
        if (handler != null) {
            handler.handle(throwable);
        }
    }

    @PluginFactory
    public static ExceptionHandlerAppender createAppender(@PluginAttribute("name") String name) {
        return new ExceptionHandlerAppender(name);
    }

    public static void setHandler(LogExceptionHandler handler) {
        HANDLER = handler;
    }

    @FunctionalInterface
    public interface LogExceptionHandler {
        void handle(Throwable ex);
    }
}
