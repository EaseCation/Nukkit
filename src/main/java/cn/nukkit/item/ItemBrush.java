package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemBrush extends ItemTool {
    public ItemBrush() {
        this(0, 1);
    }

    public ItemBrush(Integer meta) {
        this(meta, 1);
    }

    public ItemBrush(Integer meta, int count) {
        super(BRUSH, meta, count, "Brush");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_BRUSH;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        if (player.isAdventure()) {
            return false;
        }

        if (target.isAir()) {
            return true;
        }

        int time = level.getServer().getTick();
        if (player.brushCooldown >= time) {
            return true;
        }
        player.brushCooldown = time + 10;

        level.addLevelSoundEvent(target.blockCenter(), LevelSoundEventPacket.SOUND_BRUSH, target.getFullId());
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }

    @Override
    public boolean canRelease() {
        return true;
    }

    @Override
    public int getUseDuration() {
        return 200;
    }
}
