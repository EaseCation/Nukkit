package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.PayItemAction;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;

//TODO: BeaconEffectChangeEvent
public class BeaconTransaction extends InventoryTransaction {
    private final int primaryEffect;
    private final int secondaryEffect;
    private final Position position;
    private BlockEntityBeacon beacon;

    public BeaconTransaction(Player source, int primaryEffect, int secondaryEffect, Position position) {
        super(source);
        this.primaryEffect = primaryEffect;
        this.secondaryEffect = secondaryEffect;
        this.position = position;
    }

    @Override
    public boolean canExecute() {
        if (!validateAuthoritativeContext() || !super.canExecute()) {
            return false;
        }
        int payments = 0;
        for (InventoryAction action : actions) {
            if (action instanceof PayItemAction) {
                payments++;
            }
        }
        return payments == 1;
    }

    @Override
    protected boolean validateAuthoritativeContext() {
        if (!source.isServerAuthoritativeInventoryEnabled() || source.isSpectator()
                || !(source.getCurrentWindow() instanceof BeaconInventory inventory)
                || inventory.getHolder().getLevel() != position.getLevel()
                || !inventory.getHolder().equalsPosition(position)) {
            return false;
        }
        Block block = position.getLevelBlock();
        if (!block.is(Block.BEACON)) {
            return false;
        }
        BlockEntity blockEntity = block.level.getBlockEntity(block);
        if (!(blockEntity instanceof BlockEntityBeacon currentBeacon) || beacon != null && beacon != currentBeacon) {
            return false;
        }
        beacon = currentBeacon;
        int powerLevel = beacon.getPowerLevel();
        return BlockEntityBeacon.isAllowedEffect(primaryEffect, true, powerLevel)
                && (secondaryEffect == 0 || powerLevel == BlockEntityBeacon.POWER_LEVEL_MAX
                && (secondaryEffect == primaryEffect || secondaryEffect == Effect.REGENERATION));
    }

    @Override
    protected void onAuthoritativeCommit() {
        beacon.setPrimaryPower(primaryEffect);
        beacon.setSecondaryPower(secondaryEffect);
        beacon.level.addLevelSoundEvent(beacon, LevelSoundEventPacket.SOUND_BEACON_POWER);
    }
}
