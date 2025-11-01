package cn.nukkit;

import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.item.food.Food;
import cn.nukkit.level.GameRule;
import cn.nukkit.potion.Effect;

/**
 * Created by funcraft on 2015/11/11.
 */
public class PlayerFood {
    private static final int MAX_FOOD_LEVEL = 20;

    private int foodLevel;
    private float foodSaturationLevel;
    private float exhaustionLevel;
    private int foodTickTimer;

    private final Player player;

    public PlayerFood(Player player, int foodLevel, float foodSaturationLevel) {
        this.player = player;
        this.foodLevel = foodLevel;
        this.foodSaturationLevel = foodSaturationLevel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getLevel() {
        return this.foodLevel;
    }

    public int getMaxLevel() {
        return MAX_FOOD_LEVEL;
    }

    public void setLevel(int foodLevel) {
        this.setLevel(foodLevel, -1);
    }

    public void setLevel(int foodLevel, float saturationLevel) {
        if (foodLevel > 20) {
            foodLevel = 20;
        }

        if (foodLevel < 0) {
            foodLevel = 0;
        }

        if (foodLevel <= 6 && !(this.getLevel() <= 6)) {
            if (this.getPlayer().isSprinting()) {
                this.getPlayer().setSprinting(false);
            }
        }

        PlayerFoodLevelChangeEvent ev = new PlayerFoodLevelChangeEvent(this.getPlayer(), foodLevel, saturationLevel);
        this.getPlayer().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            this.sendFoodLevel();
            return;
        }
        int foodLevel0 = ev.getFoodLevel();
        float fsl = ev.getFoodSaturationLevel();
        if (fsl != -1) {
            if (fsl > foodLevel) fsl = foodLevel;
            if (foodSaturationLevel != fsl) {
                this.foodSaturationLevel = fsl;
                this.getPlayer().setAttribute(Attribute.getAttribute(Attribute.PLAYER_SATURATION).setValue(foodSaturationLevel));
            }
        }
        if (this.foodLevel == foodLevel0) {
            return;
        }
        this.foodLevel = foodLevel0;
        this.sendFoodLevel();
    }

    public float getFoodSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public void setFoodSaturationLevel(float fsl) {
        if (fsl > this.getLevel()) fsl = this.getLevel();
        if (fsl < 0) fsl = 0;
        PlayerFoodLevelChangeEvent ev = new PlayerFoodLevelChangeEvent(this.getPlayer(), this.getLevel(), fsl);
        this.getPlayer().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        fsl = ev.getFoodSaturationLevel();
        if (foodSaturationLevel == fsl) {
            return;
        }
        this.foodSaturationLevel = fsl;
        if (!player.spawned) {
            return;
        }
        this.getPlayer().setAttribute(Attribute.getAttribute(Attribute.PLAYER_SATURATION).setValue(foodSaturationLevel));
    }

    public float getExhaustionLevel() {
        return this.exhaustionLevel;
    }

    public void useHunger() {
        this.useHunger(1);
    }

    public void useHunger(int amount) {
        float sfl = this.getFoodSaturationLevel();
        int foodLevel = this.getLevel();
        if (sfl > 0) {
            float newSfl = sfl - amount;
            if (newSfl < 0) newSfl = 0;
            this.setFoodSaturationLevel(newSfl);
        } else {
            this.setLevel(foodLevel - amount);
        }
    }

    public void addFoodLevel(Food food) {
        this.addFoodLevel(food.getRestoreFood(), food.getRestoreSaturation());
    }

    public void addFoodLevel(int foodLevel, float fsl) {
        this.setLevel(this.getLevel() + foodLevel, this.getFoodSaturationLevel() + fsl);
    }

    public void reset() {
        this.foodLevel = 20;
        this.foodSaturationLevel = 5;
        this.exhaustionLevel = 0;
        this.foodTickTimer = 0;
        this.sendAll();
    }

    public void sendFoodLevel() {
        if (this.getPlayer().spawned) {
            this.getPlayer().setAttribute(Attribute.getAttribute(Attribute.PLAYER_HUNGER).setValue(foodLevel));
        }
    }

    public void sendAll() {
        if (!player.spawned) {
            return;
        }
        player.setAttribute(
                Attribute.getAttribute(Attribute.PLAYER_HUNGER).setValue(foodLevel),
                Attribute.getAttribute(Attribute.PLAYER_SATURATION).setValue(foodSaturationLevel),
                Attribute.getAttribute(Attribute.PLAYER_EXHAUSTION).setValue(exhaustionLevel)
        );
    }

    public void update(int tickDiff) {
        if (!this.getPlayer().isFoodEnabled()) return;
        if (this.getPlayer().isAlive()) {
            int diff = player.level.getDifficulty();
            if (this.getLevel() > 17) {
                this.foodTickTimer += tickDiff;
                if (this.foodTickTimer >= 80) {
                    if (player.level.gameRules.getBoolean(GameRule.NATURAL_REGENERATION) && this.getPlayer().getHealth() < this.getPlayer().getMaxHealth()) {
                        EntityRegainHealthEvent ev = new EntityRegainHealthEvent(this.getPlayer(), 1, EntityRegainHealthEvent.CAUSE_EATING);
                        this.getPlayer().heal(ev);

                        this.updateFoodExpLevel(3);
                    }
                    this.foodTickTimer = 0;
                }
            } else if (this.getLevel() <= 0) {
                this.foodTickTimer += tickDiff;
                if (this.foodTickTimer >= 80) {
                    EntityDamageEvent ev = new EntityDamageEvent(this.getPlayer(), DamageCause.HUNGER, 1);
                    float now = this.getPlayer().getHealth();
                    if (diff == 1) {
                        if (now > 10) this.getPlayer().attack(ev);
                    } else if (diff == 2) {
                        if (now > 1) this.getPlayer().attack(ev);
                    } else {
                        this.getPlayer().attack(ev);
                    }
                    this.foodTickTimer = 0;
                }
            } else {
                this.foodTickTimer = 0;
            }

            Effect hunger = this.getPlayer().getEffect(Effect.HUNGER);
            if (hunger != null) {
                this.updateFoodExpLevel(0.005f * (hunger.getAmplifier() + 1));
            }
        }
    }

    /**
     * add exhaustion
     */
    public void updateFoodExpLevel(float use) {
        if (!this.getPlayer().isFoodEnabled()) return;
        if (player.level.getDifficulty() == 0) return;
        if (this.getPlayer().hasEffect(Effect.SATURATION)) return;
        float lastExhaustionLevel = exhaustionLevel;
        this.exhaustionLevel += use;
        if (this.exhaustionLevel > 4) {
            this.useHunger(1);
            this.exhaustionLevel = 0;
        }
        if (lastExhaustionLevel == exhaustionLevel) {
            return;
        }
        if (!player.spawned) {
            return;
        }
        this.getPlayer().setAttribute(Attribute.getAttribute(Attribute.PLAYER_EXHAUSTION).setValue(exhaustionLevel));
    }

    /**
     * @deprecated use {@link #setLevel(int)} instead
     * @param foodLevel level
     **/
    @Deprecated
    public void setFoodLevel(int foodLevel) {
        setLevel(foodLevel);
    }

    /**
     * @deprecated use {@link #setLevel(int, float)} instead
     * @param foodLevel level
     * @param saturationLevel saturation
     **/
    @Deprecated
    public void setFoodLevel(int foodLevel, float saturationLevel) {
        setLevel(foodLevel, saturationLevel);
    }
}
