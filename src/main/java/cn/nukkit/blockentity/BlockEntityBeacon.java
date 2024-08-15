package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Items;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * author: Rover656
 */
@Log4j2
public class BlockEntityBeacon extends BlockEntitySpawnable {
    private static final byte[] ALLOWED_EFFECTS = new byte[Effect.UNDEFINED];

    static {
        ALLOWED_EFFECTS[Effect.SPEED] = 1;
        ALLOWED_EFFECTS[Effect.HASTE] = 1;
        ALLOWED_EFFECTS[Effect.RESISTANCE] = 2;
        ALLOWED_EFFECTS[Effect.JUMP_BOOST] = 2;
        ALLOWED_EFFECTS[Effect.STRENGTH] = 3;
        ALLOWED_EFFECTS[Effect.REGENERATION] = 4;
    }

    private static boolean isAllowedEffect(int effectId, boolean primary, int maxLevel) {
        if (effectId < 0 || effectId >= ALLOWED_EFFECTS.length) {
            return false;
        }
        byte level = ALLOWED_EFFECTS[effectId];
        if (level <= 0 || level > maxLevel) {
            return false;
        }
        return !primary || level < POWER_LEVEL_MAX;
    }

    public BlockEntityBeacon(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("Levels")) {
            namedTag.putInt("Levels", 0); // Nukkit only
        }

        if (!namedTag.contains("primary")) {
            if (namedTag.contains("Primary")) {
                namedTag.putInt("primary", namedTag.getInt("Primary"));
                namedTag.remove("Primary");
            } else {
                namedTag.putInt("primary", 0);
            }
        }

        if (!namedTag.contains("secondary")) {
            if (namedTag.contains("Secondary")) {
                namedTag.putInt("secondary", namedTag.getInt("Secondary"));
                namedTag.remove("Secondary");
            } else {
                namedTag.putInt("secondary", 0);
            }
        }

        scheduleUpdate();

        super.initBlockEntity();
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BEACON;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, BEACON)
                .putInt("primary", this.namedTag.getInt("primary"))
                .putInt("secondary", this.namedTag.getInt("secondary"));
    }

    @Override
    public boolean onUpdate() {
        if (isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        //Only apply effects every 4 secs
        if (currentTick % 80 != 0) {
            return true;
        }

        int oldPowerLevel = this.getPowerLevel();
        //Get the power level based on the pyramid
        setPowerLevel(calculatePowerLevel());
        int newPowerLevel = this.getPowerLevel();

        //Skip beacons that do not have a pyramid or sky access
        if (newPowerLevel < 1 || !hasSkyAccess()) {
            if (oldPowerLevel > 0) {
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BEACON_DEACTIVATE);
            }
            return true;
        } else if (oldPowerLevel < 1) {
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BEACON_ACTIVATE);
        } else {
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BEACON_AMBIENT);
        }

        //Get all players in game
        Map<Long, Player> players = this.level.getPlayers();

        //Calculate vars for beacon power
        int range = 10 + getPowerLevel() * 10;
        int rangeSq = range * range;
        int duration = 9 + getPowerLevel() * 2;

        for (Map.Entry<Long, Player> entry : players.entrySet()) {
            Player p = entry.getValue();

            //If the player is in range
            if (p.distanceSquared2(this) < rangeSq) {
                Effect e;

                if (isAllowedEffect(getPrimaryPower(), true, getPowerLevel())) {
                    //Apply the primary power
                    e = Effect.getEffect(getPrimaryPower());

                    //Set duration
                    e.setDuration(duration * 20);

                    //If secondary is selected as the primary too, apply 2 amplification
                    if (getSecondaryPower() == getPrimaryPower() && getPowerLevel() == POWER_LEVEL_MAX) {
                        e.setAmplifier(1);
                    }

                    //Transparent particles
                    e.setAmbient(true);

                    //Add the effect
                    p.addEffect(e);
                }

                //If we have a secondary power as regen, apply it
                if (getSecondaryPower() == Effect.REGENERATION && getPowerLevel() == POWER_LEVEL_MAX) {
                    //Get the regen effect
                    e = Effect.getEffect(Effect.REGENERATION);

                    //Set duration
                    e.setDuration(duration * 20);

                    //Transparent particles
                    e.setAmbient(true);

                    //Add effect
                    p.addEffect(e);
                }
            }
        }

        return true;
    }

    private static final int POWER_LEVEL_MAX = 4;

    private boolean hasSkyAccess() {
        int tileX = getFloorX();
        int tileY = getFloorY();
        int tileZ = getFloorZ();

        //Check every block from our y coord to the top of the world
        for (int y = level.getHighestBlockAt(tileX, tileZ); y > tileY; y--) {
            int testBlockId = level.getBlock(tileX, y, tileZ).getId();
            if (!Block.transparent[testBlockId]) {
                //There is no sky access
                return false;
            }
        }

        return true;
    }

    private int calculatePowerLevel() {
        int tileX = getFloorX();
        int tileY = getFloorY();
        int tileZ = getFloorZ();

        //The power level that we're testing for
        for (int powerLevel = 1; powerLevel <= POWER_LEVEL_MAX; powerLevel++) {
            int queryY = tileY - powerLevel; //Layer below the beacon block

            for (int queryX = tileX - powerLevel; queryX <= tileX + powerLevel; queryX++) {
                for (int queryZ = tileZ - powerLevel; queryZ <= tileZ + powerLevel; queryZ++) {

                    int testBlockId = level.getBlock(queryX, queryY, queryZ).getId();
                    if (
                            testBlockId != Block.IRON_BLOCK &&
                                    testBlockId != Block.GOLD_BLOCK &&
                                    testBlockId != Block.EMERALD_BLOCK &&
                                    testBlockId != Block.DIAMOND_BLOCK &&
                                    testBlockId != Block.NETHERITE_BLOCK
                            ) {
                        return powerLevel - 1;
                    }
                }
            }
        }

        return POWER_LEVEL_MAX;
    }

    public int getPowerLevel() {
        return namedTag.getInt("Level");
    }

    public void setPowerLevel(int level) {
        int currentLevel = getPowerLevel();
        if (level != currentLevel) {
            namedTag.putInt("Level", level);
            setDirty();
        }
    }

    public int getPrimaryPower() {
        return namedTag.getInt("primary");
    }

    public void setPrimaryPower(int power) {
        int currentPower = getPrimaryPower();
        if (power != currentPower) {
            namedTag.putInt("primary", power);
            setDirty();
            this.spawnToAll();
        }
    }

    public int getSecondaryPower() {
        return namedTag.getInt("secondary");
    }

    public void setSecondaryPower(int power) {
        int currentPower = getSecondaryPower();
        if (power != currentPower) {
            namedTag.putInt("secondary", power);
            setDirty();
            this.spawnToAll();
        }
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.BEACON)) {
            return false;
        }

        Inventory inv = player.getWindowById(Player.BEACON_WINDOW_ID);
        if (inv instanceof BeaconInventory inventory && inventory.getHolder().equalsPosition(this)) {
            inv.setItem(0, Items.air());
        } else {
            log.debug("{} attempted to set effect but beacon inventory is null", player.getName());
            return false;
        }

        int primary = nbt.getInt("primary");
        if (isAllowedEffect(primary, true, POWER_LEVEL_MAX)) {
            this.setPrimaryPower(primary);
        } else {
            log.debug("{} attempted to set an invalid primary effect to a beacon: {}", player.getName(), primary);
        }

        int secondary = nbt.getInt("secondary");
        if (isAllowedEffect(secondary, false, POWER_LEVEL_MAX)) {
            this.setSecondaryPower(secondary);
        } else {
            log.debug("{} attempted to set an invalid secondary effect to a beacon: {}", player.getName(), secondary);
        }

        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BEACON_POWER);
        return true;
    }
}
