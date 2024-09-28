package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.MovementEffectType;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class ItemFirework extends Item {

    public ItemFirework() {
        this(0);
    }

    public ItemFirework(Integer meta) {
        this(meta, 1);
    }

    public ItemFirework(Integer meta, int count) {
        super(FIREWORK_ROCKET, meta, count, "Fireworks");
        initNamedTag();
    }

    public void initNamedTag() {
        if (!hasCompoundTag() || !this.getNamedTag().contains("Fireworks")) {
            CompoundTag tag = getNamedTag();
            if (tag == null) {
                tag = new CompoundTag();

                Random rand = ThreadLocalRandom.current();
                CompoundTag ex = new CompoundTag()
                        .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.BLACK.getDyeData()})
                        .putByteArray("FireworkFade", new byte[]{})
                        .putBoolean("FireworkFlicker", false)
                        .putBoolean("FireworkTrail", false)
                        .putByte("FireworkType", FireworkExplosion.ExplosionType.CREEPER_SHAPED.ordinal());

                tag.putCompound("Fireworks", new CompoundTag("Fireworks")
                        .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                        .putByte("Flight", 1)
                        .putInt("LifeTime", 20 + rand.nextInt(12))
                );

                this.setNamedTag(tag);
            }
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
//        if (player.isAdventure()) {
//            return false;
//        }

        if (block.canPassThrough() || block.canBeFlowedInto()) {
            this.spawnFirework(level, block.blockCenter());

            if (!player.isCreative()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getArmorInventory().getChestplate() instanceof ItemElytra && player.isGliding()) {
            Vector3 dir = Vector3.directionFromRotation(player.pitch, player.yaw);

            EntityFirework firework = this.spawnFirework(player.getLevel(), player, dir, player);

            int lifetime = firework.getLifeTime();
            if (lifetime > 0) {
                player.sendMovementEffect(player.getId(), MovementEffectType.GLIDE_BOOST, lifetime);
            }

            player.setMotion(dir.multiply(2));

            if (!player.isCreative()) {
                this.count--;
            }

            return true;
        }

        return false;
    }

    public void setLifeTime(int ticks) {
        initNamedTag();
        this.getNamedTag().getCompound("Fireworks").putInt("LifeTime", ticks);
    }

    public int getLifeTime() {
        initNamedTag();
        if (!this.getNamedTag().getCompound("Fireworks").exist("LifeTime")) {
            Random rand = ThreadLocalRandom.current();
            this.getNamedTag().getCompound("Fireworks").putInt("LifeTime", 20 + rand.nextInt(12));
        }

        return this.getNamedTag().getCompound("Fireworks").getInt("LifeTime");
    }

    public void addExplosion(FireworkExplosion explosion) {
        List<DyeColor> colors = explosion.getColors();
        List<DyeColor> fades = explosion.getFades();

        if (colors.isEmpty()) {
            return;
        }
        byte[] clrs = new byte[colors.size()];
        for (int i = 0; i < clrs.length; i++) {
            clrs[i] = (byte) colors.get(i).getDyeData();
        }

        byte[] fds = new byte[fades.size()];
        for (int i = 0; i < fds.length; i++) {
            fds[i] = (byte) fades.get(i).getDyeData();
        }

        ListTag<CompoundTag> explosions = this.getNamedTag().getCompound("Fireworks").getList("Explosions", CompoundTag.class);
        CompoundTag tag = new CompoundTag()
                .putByteArray("FireworkColor", clrs)
                .putByteArray("FireworkFade", fds)
                .putBoolean("FireworkFlicker", explosion.flicker)
                .putBoolean("FireworkTrail", explosion.trail)
                .putByte("FireworkType", explosion.type.ordinal());

        explosions.add(tag);
    }

    public void clearExplosions() {
        this.getNamedTag().getCompound("Fireworks").putList(new ListTag<CompoundTag>("Explosions"));
    }

    public EntityFirework spawnFirework(Level level, Vector3 pos) {
        return spawnFirework(level, pos, null);
    }

    public EntityFirework spawnFirework(Level level, Vector3 pos, @Nullable Vector3 motion) {
        return spawnFirework(level, pos, motion, null);
    }

    public EntityFirework spawnFirework(Level level, Vector3 pos, @Nullable Vector3 motion, @Nullable Entity attached) {
        boolean projectile = motion != null;
        CompoundTag nbt = Entity.getDefaultNBT(pos, motion, projectile ? (float) motion.yRotFromDirection() : 0, projectile ? (float) motion.xRotFromDirection() : 0)
                .putCompound("FireworkItem", NBTIO.putItemHelper(this));

        EntityFirework entity = new EntityFirework(level.getChunk(pos.getChunkX(), pos.getChunkZ()), nbt, projectile, attached);
        entity.spawnToAll();
        return entity;
    }

    public static class FireworkExplosion {

        private final List<DyeColor> colors = new ArrayList<>();
        private final List<DyeColor> fades = new ArrayList<>();
        private boolean flicker = false;
        private boolean trail = false;
        private ExplosionType type = ExplosionType.CREEPER_SHAPED;

        public List<DyeColor> getColors() {
            return this.colors;
        }

        public List<DyeColor> getFades() {
            return this.fades;
        }

        public boolean hasFlicker() {
            return this.flicker;
        }

        public boolean hasTrail() {
            return this.trail;
        }

        public ExplosionType getType() {
            return this.type;
        }

        public FireworkExplosion setFlicker(boolean flicker) {
            this.flicker = flicker;
            return this;
        }

        public FireworkExplosion setTrail(boolean trail) {
            this.trail = trail;
            return this;
        }

        public FireworkExplosion type(ExplosionType type) {
            this.type = type;
            return this;
        }

        public FireworkExplosion addColor(DyeColor color) {
            colors.add(color);
            return this;
        }

        public FireworkExplosion addFade(DyeColor fade) {
            fades.add(fade);
            return this;
        }

        public enum ExplosionType {
            SMALL_BALL,
            LARGE_BALL,
            STAR_SHAPED,
            CREEPER_SHAPED,
            BURST;

            private static final ExplosionType[] VALUES = values();

            public static ExplosionType[] getValues() {
                return VALUES;
            }
        }
    }
}
