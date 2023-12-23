package cn.nukkit.utils;

import cn.nukkit.Player;

public interface PlayerViolationListener {
    PlayerViolationListener NOPE = new PlayerViolationListener() {
    };

    default void onCommandRequest(Player player) {
    }

    default void onChatTooFast(Player player) {
    }

    default void onChatTooLong(Player player) {
    }
}
