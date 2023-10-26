package cn.nukkit.item;

public class ItemPotterySherdFriend extends Item {
    public ItemPotterySherdFriend() {
        this(0, 1);
    }

    public ItemPotterySherdFriend(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdFriend(Integer meta, int count) {
        super(FRIEND_POTTERY_SHERD, meta, count, "Friend Pottery Sherd");
    }
}
