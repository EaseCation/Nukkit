package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Nukkit Project Team
 */
@ToString
public class AnimatePacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.ANIMATE_PACKET;

    public long eid;
    public Action action;
    public float data;
    public float rowingTime;
    @Nullable
    public SwingSource swingSource;

    @Override
    public void decode() {
        this.action = Action.fromId(this.getVarInt());
        this.eid = getEntityRuntimeId();
        if (this.action == Action.ROW_RIGHT || this.action == Action.ROW_LEFT) {
            this.rowingTime = this.getLFloat();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.action.getId());
        this.putEntityRuntimeId(this.eid);
        if (this.action == Action.ROW_RIGHT || this.action == Action.ROW_LEFT) {
            this.putLFloat(this.rowingTime);
        }
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public enum Action {
        NO_ACTION(0),
        SWING_ARM(1),
        WAKE_UP(3),
        CRITICAL_HIT(4),
        MAGIC_CRITICAL_HIT(5),
        ROW_RIGHT(128),
        ROW_LEFT(129);

        private static final Int2ObjectMap<Action> ID_LOOKUP = new Int2ObjectOpenHashMap<>();

        static {
            ID_LOOKUP.defaultReturnValue(Action.NO_ACTION);

            for (Action value : values()) {
                ID_LOOKUP.put(value.id, value);
            }
        }

        private final int id;

        Action(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Action fromId(int id) {
            return ID_LOOKUP.getOrDefault(id, NO_ACTION);
        }
    }

    public enum SwingSource {
        NONE("none"),
        BUILD("build"),
        MINE("mine"),
        INTERACT("interact"),
        ATTACK("attack"),
        USE_ITEM("useitem"),
        THROW_ITEM("throwitem"),
        DROP_ITEM("dropitem"),
        EVENT("event"),
        ;

        private static final Map<String, SwingSource> BY_NAME = Arrays.stream(values())
                .collect(Collectors.toMap(SwingSource::getName, Function.identity()));

        private final String name;

        SwingSource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public static SwingSource byName(String name) {
            return BY_NAME.get(name);
        }
    }
}
