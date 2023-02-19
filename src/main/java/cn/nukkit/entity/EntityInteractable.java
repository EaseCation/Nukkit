package cn.nukkit.entity;

import cn.nukkit.Player;

/**
 * @author Adam Matthew
 */
public interface EntityInteractable {

    String getInteractButtonText(Player player);

    boolean canDoInteraction(Player player);

}
