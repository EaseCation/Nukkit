package cn.nukkit.utils;

import cn.nukkit.Player;

public interface PlayerViolationListener {
    PlayerViolationListener NOPE = new PlayerViolationListener() {
    };

    default void onCommandRequest(Player player, int length) {
    }

    default void onChatTooFast(Player player, int length) {
    }

    default void onChatTooLong(Player player, int length) {
    }
}
