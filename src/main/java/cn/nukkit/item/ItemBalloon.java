package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.DyeColor;

public class ItemBalloon extends Item {
    public ItemBalloon() {
        this(0, 1);
    }

    public ItemBalloon(Integer meta) {
        this(meta, 1);
    }

    public ItemBalloon(Integer meta, int count) {
        super(BALLOON, meta, count, DyeColor.getByWoolData(meta != null ? meta : 0).getName() + " Balloon");
    }

    @Override
    public String getDescriptionId() {
        return "item.balloon." + getColor().getDescriptionName() + ".name";
    }

    @Override
    public boolean isStackedByData() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        if (!block.isWall() && !block.isFence()) {
            return false;
        }
        //TODO: create balloon entity
        return true;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }

    public DyeColor getColor() {
        return DyeColor.getByWoolData(getDamage());
    }
}
