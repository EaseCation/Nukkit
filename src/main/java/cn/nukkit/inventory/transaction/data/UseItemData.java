package cn.nukkit.inventory.transaction.data;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import lombok.ToString;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 */
@ToString
public class UseItemData implements TransactionData {

    public int actionType;
    /**
     * @since 1.21.20
     */
    public int triggerType;
    public BlockVector3 blockPos;
    @Nullable
    public BlockFace face;
    public int hotbarSlot;
    public Item itemInHand;
    public Vector3 playerPos;
    public Vector3f clickPos;
    /**
     * @since 1.16.0
     */
    @Nullable
    public Block block;
    /**
     * @since 1.21.20
     */
    public boolean clientInteractPrediction;
}
