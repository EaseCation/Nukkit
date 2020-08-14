package cn.nukkit.utils;

import cn.nukkit.Server;
import lombok.extern.log4j.Log4j2;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public final class ServerStatistics implements DynamicMBean {

    private final Server server;
    private final MBeanInfo mBeanInfo;
    private final Map<String, AttributeDescription> attributeDescriptionByName;

    private ServerStatistics(Server server) {
        this.attributeDescriptionByName = Stream.of(
                new AttributeDescription("tickTimes", this::getTickTimes, "Historical tick times (ns)", long[].class),
                new AttributeDescription("averageTickTime", this::getAverageTickTime, "Current average tick time (ms)", Float.TYPE)
                // 这里可以按需加入更多的属性，如每秒tick、玩家数量等
        ).collect(Collectors.toMap(description -> description.name, Function.identity()));
        this.server = server;
        this.mBeanInfo = new MBeanInfo("NukkitServerStatistics", "metrics for Nukkit server",
                this.attributeDescriptionByName.values().stream().map(AttributeDescription::asMBeanAttributeInfo).toArray(MBeanAttributeInfo[]::new),
                null, null, new MBeanNotificationInfo[0]);
    }

    public static void registerJmxMonitoring(Server server) {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(new ServerStatistics(server), new ObjectName("cn.nukkit:type=Server"));
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
            log.warn("Failed to initialise server as JMX bean", e);
        }
    }

    private long[] getTickTimes() {
        return this.server.tickTimes;
    }

    private float getAverageTickTime() {
        return this.server.averageTickTime;
    }

    @Override
    public Object getAttribute(String attribute) {
        AttributeDescription description = this.attributeDescriptionByName.get(attribute);
        return description == null ? null : description.getter.get();
    }

    @Override
    public void setAttribute(Attribute attribute) {

    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return new AttributeList(Arrays.stream(attributes)
                .map(this.attributeDescriptionByName::get)
                .filter(Objects::nonNull)
                .map(description -> new Attribute(description.name, description.getter.get()))
                .collect(Collectors.toList()));
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return new AttributeList();
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return this.mBeanInfo;
    }

    private static final class AttributeDescription {

        private final String name;
        private final Supplier<Object> getter;
        private final String description;
        private final Class<?> type;

        private AttributeDescription(String name, Supplier<Object> getter, String description, Class<?> type) {
            this.name = name;
            this.getter = getter;
            this.description = description;
            this.type = type;
        }

        private MBeanAttributeInfo asMBeanAttributeInfo() {
            return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
        }
    }
}
