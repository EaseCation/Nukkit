package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockPumpkin extends BlockSolid implements Faceable {
    public BlockPumpkin() {
        this(0);
    }

    public BlockPumpkin(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pumpkin";
    }

    @Override
    public int getId() {
        return PUMPKIN;
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE | BlockToolType.SWORD;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x3);
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!item.isShears()) {
            return false;
        }

        if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
            item.pop();
            player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
        }

        level.setBlock(this, get(CARVED_PUMPKIN, getDamage()), true);

        if (level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
            level.dropItem(blockCenter(), Item.get(Item.PUMPKIN_SEEDS));
        }

        level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_PUMPKIN_CARVE);
        return true;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }
}
