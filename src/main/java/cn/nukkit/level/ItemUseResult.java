package cn.nukkit.level;

import cn.nukkit.item.Item;
import javax.annotation.Nullable;

/**
 * 方块交互是否已处理、可继续空气使用，或已被拒绝。
 */
public record ItemUseResult(Status status, @Nullable Item item) {
    public static final ItemUseResult PASS = new ItemUseResult(Status.PASS, null);
    public static final ItemUseResult FAIL = new ItemUseResult(Status.FAIL, null);

    public enum Status {
        SUCCESS,
        PASS,
        FAIL
    }

    public static ItemUseResult success(Item item) {
        return new ItemUseResult(Status.SUCCESS, item);
    }
}
