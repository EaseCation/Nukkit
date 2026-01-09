package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHanging;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPaintingPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityPainting extends EntityHanging {

    public static final int NETWORK_ID = EntityID.PAINTING;

    public final static Motive[] motives = Motive.values();
    private Motive motive;

    private BlockFace direction;
    private BlockVector3 blockIn;

    public EntityPainting(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public static Motive getMotive(String name) {
        return Motive.BY_NAME.getOrDefault(name, Motive.KEBAB);
    }

    @Override
    public float getWidth() {
        return this.motive.width;
    }

    @Override
    public float getHeight() {
        return this.motive.height;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        if (this.namedTag.contains("Direction")) {
            this.direction = BlockFace.fromHorizontalIndex(this.namedTag.getByte("Direction"));
        } else if (this.namedTag.contains("Dir")) {
            int dir = this.namedTag.getByte("Dir");
            if (dir == 2) {
                dir = 0;
            } else if (dir == 0) {
                dir = 2;
            }
            this.direction = BlockFace.fromHorizontalIndex(dir);
        } else {
            this.direction = BlockFace.NORTH;
        }

        if (this.namedTag.contains("TileX") && this.namedTag.contains("TileY") && this.namedTag.contains("TileZ")) {
            this.blockIn = new BlockVector3(this.namedTag.getInt("TileX"), this.namedTag.getInt("TileY"), this.namedTag.getInt("TileZ"));
        } else {
            this.blockIn = null;
        }

        if (this.namedTag.contains("Motive")) {
            String motif = this.namedTag.getString("Motive");
            this.motive = getMotive(motif);

            this.recalculateBoundingBox(false);

            namedTag.putString("Motif", motif);
            namedTag.remove("Motive");
        } else if (this.namedTag.contains("Motif")) {
            this.motive = getMotive(this.namedTag.getString("Motif"));

            this.recalculateBoundingBox(false);
        } else {
            List<Motive> validMotives = new ObjectArrayList<>(7);
            IntSet validTypes = new IntOpenHashSet(2);
            IntList invalidTypes = new IntArrayList();
            int maxSize = 0;
            for (Motive motive : motives) {
                if (motive.hidden) {
                    continue;
                }

                int type = motive.width << 2 | motive.height;
                if (invalidTypes.contains(type)) {
                    continue;
                }
                if (validTypes.contains(type)) {
                    validMotives.add(motive);
                    continue;
                }

                int size = motive.width * motive.height;
                if (size < maxSize) {
                    invalidTypes.add(type);
                    continue;
                }

                this.motive = motive;
                this.recalculatePosition();

                if (!this.isSurfaceValid()) {
                    invalidTypes.add(type);
                    continue;
                }

                if (size > maxSize) {
                    maxSize = size;

                    invalidTypes.addAll(validTypes);
                    validTypes.clear();
                    validMotives.clear();
                }

                validTypes.add(type);
                validMotives.add(motive);
            }
            if (validMotives.isEmpty()) {
                this.motive = null;
                return;
            }

            this.motive = validMotives.get(ThreadLocalRandom.current().nextInt(validMotives.size()));

            if (this.blockIn != null) {
                this.recalculatePosition();
            } else {
                this.recalculateBoundingBox(false);
            }
        }

        super.initEntity();
    }

    @Override
    public DataPacket createAddEntityPacket() {
        AddPaintingPacket addPainting = new AddPaintingPacket();
        addPainting.entityUniqueId = this.getId();
        addPainting.entityRuntimeId = this.getId();
        addPainting.x = (float) this.x;
        addPainting.y = (float) this.y;
        addPainting.z = (float) this.z;
        addPainting.direction = this.direction.getHorizontalIndex();
        addPainting.title = this.motive.title;
        return addPainting;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("Direction", this.direction.getHorizontalIndex());

        if (this.blockIn != null) {
            this.namedTag.putInt("TileX", this.blockIn.x);
            this.namedTag.putInt("TileY", this.blockIn.y);
            this.namedTag.putInt("TileZ", this.blockIn.z);
        }

        this.namedTag.putString("Motif", this.motive.title);
    }

    @Override
    public BlockFace getDirection() {
        return this.direction;
    }

    private void recalculatePosition() {
        if (this.blockIn == null) {
            return;
        }

        BlockFace ccw = this.direction.rotateYCCW();
        double widthOffset = offset(this.motive.width);
        this.setPosition(this.temporalVector.setComponents(
                this.blockIn.x + 0.5 + widthOffset * ccw.getXOffset() - this.direction.getXOffset() * (0.5 - 1f / 16 * 0.5),
                this.blockIn.y + 0.5 + offset(this.motive.height),
                this.blockIn.z + 0.5 + widthOffset * ccw.getZOffset() - this.direction.getZOffset() * (0.5 - 1f / 16 * 0.5)));
    }

    @Override
    public void recalculateBoundingBox(boolean send) {
        if (this.direction == null) {
            return;
        }

        float sizeX = this.getWidth();
        float sizeY = this.getHeight();
        float sizeZ = this.getWidth();

        if (this.direction.getAxis() == BlockFace.Axis.Z) {
            sizeZ = 1f / 16;
        } else {
            sizeX = 1f / 16;
        }

        sizeX *= 0.5f;
        sizeY *= 0.5f;
        sizeZ *= 0.5f;

        this.boundingBox.setBounds(this.x - sizeX + (1f / 16 * 0.5 * 0.5), this.y - sizeY + (1f / 16 * 0.5 * 0.5), this.z - sizeZ + (1f / 16 * 0.5 * 0.5), this.x + sizeX - (1f / 16 * 0.5 * 0.5), this.y + sizeY - (1f / 16 * 0.5 * 0.5), this.z + sizeZ - (1f / 16 * 0.5 * 0.5));
    }

    @Override
    public boolean isSurfaceValid() {
        int minX = Mth.floor(this.boundingBox.getMinX());
        int minY = Mth.floor(this.boundingBox.getMinY());
        int minZ = Mth.floor(this.boundingBox.getMinZ());
        int maxX = Mth.floor(this.boundingBox.getMaxX());
        int maxY = Mth.floor(this.boundingBox.getMaxY());
        int maxZ = Mth.floor(this.boundingBox.getMaxZ());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = this.level.getBlock(x, y, z, false);
                    int id = block.getId();
                    if (block.isAir() || id == Block.SNOW_LAYER) {
                        continue;
                    }
                    if (block.isFire() || block.isLava() || block.isDiode()) {
                        return false;
                    }
                    if (block.isWallSign() || id == Block.WALL_BANNER || block.isItemFrame() || block.isTorch()) {
                        BlockFace direction = ((Faceable) block).getBlockFace();
                        if (direction == this.direction) {
                            return false;
                        }
                    }
                    if (block.canPassThrough() || block.canBeFlowedInto()) {
                        continue;
                    }
                    if (block.collidesWithBB(this.boundingBox)) {
                        return false;
                    }
                }
            }
        }

        if (this.blockIn == null) {
            return true;
        }

        int width = (int) this.getWidth();
        int height = (int) this.getHeight();
        BlockVector3 attached = this.blockIn.getSide(this.direction.getOpposite());
        BlockFace ccw = this.direction.rotateYCCW();
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int widthOffset = (width - 1) / -2;
                int heightOffset = (height - 1) / -2;
                Block block = this.level.getBlock(attached.getSide(ccw, w + widthOffset).getSide(BlockFace.UP, h + heightOffset).asVector3());
                int id = block.getId();
                if (id == Block.AIR || block.canBeFlowedInto() || id == Block.SNOW_LAYER || this.level.getBlockEntity(block) != null) {
                    return false;
                }
            }
        }

        Entity[] entities = this.level.getNearbyEntities(this.boundingBox, this);
        for (Entity entity : entities) {
            if (entity instanceof EntityHanging) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void dropItem(Entity entity) {
        this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_REMOVED);
        this.level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_DESTROY_BLOCK_NO_SOUND, Block.getFullId(Block.OAK_PLANKS));

        if (!this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            return;
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.isCreative()) {
                return;
            }
        }

        this.level.dropItem(this, Item.get(Item.PAINTING));
    }

    @Override
    public void onPushByPiston(BlockEntityPistonArm piston) {
        if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            this.level.dropItem(this, Item.get(Item.PAINTING));
        }

        this.close();
    }

    public Motive getMotive() {
        return this.motive;
    }

    private static double offset(int length) {
        return (length & 1) == 0 ? 0.5 : 0;
    }

    /**
     * Motif
     */
    public enum Motive {
        KEBAB("Kebab", 1, 1),
        AZTEC("Aztec", 1, 1),
        ALBAN("Alban", 1, 1),
        AZTEC2("Aztec2", 1, 1),
        BOMB("Bomb", 1, 1),
        PLANT("Plant", 1, 1),
        WASTELAND("Wasteland", 1, 1),
//        MEDITATIVE("Meditative", 1, 1), //TODO: 1.21.0
        WANDERER("Wanderer", 1, 2),
        GRAHAM("Graham", 1, 2),
//        PRAIRIE_RIDE("PrairieRide", 1, 2),
        POOL("Pool", 2, 1),
        COURBET("Courbet", 2, 1),
        SUNSET("Sunset", 2, 1),
        SEA("Sea", 2, 1),
        CREEBET("Creebet", 2, 1),
        MATCH("Match", 2, 2),
        BUST("Bust", 2, 2),
        STAGE("Stage", 2, 2),
        VOID("Void", 2, 2),
        SKULL_AND_ROSES("SkullAndRoses", 2, 2),
        WITHER("Wither", 2, 2),
//        BAROQUE("Baroque", 2, 2),
//        HUMBLE("Humble", 2, 2),
        EARTH("Earth", 2, 2, true),
        FIRE("Fire", 2, 2, true),
        WATER("Water", 2, 2, true),
        WIND("Wind", 2, 2, true),
//        BOUQUET("Bouquet", 3, 3),
//        CAVEBIRD("Cavebird", 3, 3),
//        COTAN("Cotan", 3, 3),
//        ENDBOSS("Endboss", 3, 3),
//        FERN("Fern", 3, 3),
//        OWLEMONS("Owlemons", 3, 3),
//        SUNFLOWERS("Sunflowers", 3, 3),
//        TIDES("Tides", 3, 3),
//        BACKYARD("Backyard", 3, 4),
//        POND("Pond", 3, 4),
        FIGHTERS("Fighters", 4, 2),
//        CHAINGING("Chainging", 4, 2),
//        FINDING("Finding", 4, 2),
//        LOWMIST("Lowmist", 4, 2),
//        PASSAGE("Passage", 4, 2),
        SKELETON("Skeleton", 4, 3),
        DONKEY_KONG("DonkeyKong", 4, 3),
        POINTER("Pointer", 4, 4),
        PIG_SCENE("Pigscene", 4, 4),
        BURNING_SKULL("BurningSkull", 4, 4),
//        UNPACKED("Unpacked", 4, 4),
//        ORB("Orb", 4, 4),
        ;

        public final String title;
        public final int width;
        public final int height;
        public final boolean hidden;

        private static final Map<String, Motive> BY_NAME = new Object2ObjectOpenHashMap<>();

        static {
            for (Motive motive : values()) {
                BY_NAME.put(motive.title, motive);
            }
        }

        Motive(String title, int width, int height) {
            this(title, width, height, false);
        }

        Motive(String title, int width, int height, boolean hidden) {
            this.title = title;
            this.width = width;
            this.height = height;
            this.hidden = hidden;
        }
    }
}
