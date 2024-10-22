package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.IntPositionEntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityHuman extends EntityHumanType {

    public static final int DATA_PLAYER_FLAG_SLEEP = 1;
    public static final int DATA_PLAYER_FLAG_DEAD = 2;

    protected UUID uuid;
    protected byte[] rawUUID;

    @Override
    public float getWidth() {
        if (isSleeping()) {
            return 0.2f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (isSleeping()) {
            return 0.2f;
        }
        if (this.isSwimming() || this.isGliding() || this.isCrawling()) {
            return 0.6f;
        }
        if (this.isSneaking()) {
            if (this instanceof Player player && player.getProtocol() < 594) {
                return 1.65f;  // 旧版本应该还是老的高度
            } else {
                return 1.49f;
            }
        }
        return 1.8f;
    }

    @Override
    public float getEyeHeight() {
        return getHeight() * 0.9f;
    }

    @Override
    protected float getBaseOffset() {
        return 1.8f * 0.9f;
    }

//    @Override
//    public float getRidingOffset() {
//        return getBaseOffset() - 0.4f;
//    }

    protected Skin skin;

    @Override
    public int getNetworkId() {
        return -1;
    }

    public EntityHuman(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public Skin getSkin() {
        return skin;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public byte[] getRawUniqueId() {
        return rawUUID;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public boolean setSwimming(boolean value) {
        if (!super.setSwimming(value)) {
            return false;
        }
        this.recalculateBoundingBox();
        return true;
    }

    @Override
    public boolean setSneaking(boolean value) {
        if (!super.setSneaking(value)) {
            return false;
        }
        this.recalculateBoundingBox();
        return true;
    }

    @Override
    public boolean setGliding(boolean value) {
        if (!super.setGliding(value)) {
            return false;
        }
        this.recalculateBoundingBox();
        return true;
    }

    @Override
    public boolean setCrawling(boolean value) {
        if (!super.setCrawling(value)) {
            return false;
        }
        this.recalculateBoundingBox();
        return true;
    }

    @Override
    protected void initEntity() {
        this.setPlayerFlag(DATA_PLAYER_FLAG_SLEEP, false, false);
        this.setDataFlag(DATA_FLAG_GRAVITY, true, false);

        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0), false);

        if (!(this instanceof Player)) {
            if (this.namedTag.contains("NameTag")) {
                this.setNameTag(this.namedTag.getString("NameTag"));
            }

            if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
                CompoundTag skinTag = this.namedTag.getCompound("Skin");
                if (!skinTag.contains("Transparent")) {
                    skinTag.putBoolean("Transparent", false);
                }
                Skin newSkin = new Skin();
                if (skinTag.contains("ModelId")) {
                    newSkin.setSkinId(skinTag.getString("ModelId"));
                }
                if (skinTag.contains("PlayFabId")) {
                    newSkin.setPlayFabId(skinTag.getString("PlayFabId"));
                }
                if (skinTag.contains("Data")) {
                    byte[] data = skinTag.getByteArray("Data");
                    if (skinTag.contains("SkinImageWidth") && skinTag.contains("SkinImageHeight")) {
                        int width = skinTag.getInt("SkinImageWidth");
                        int height = skinTag.getInt("SkinImageHeight");
                        newSkin.setSkinData(new SerializedImage(width, height, data));
                    } else {
                        newSkin.setSkinData(data);
                    }
                }
                if (skinTag.contains("CapeId")) {
                    newSkin.setCapeId(skinTag.getString("CapeId"));
                }
                if (skinTag.contains("CapeData")) {
                    byte[] data = skinTag.getByteArray("CapeData");
                    if (skinTag.contains("CapeImageWidth") && skinTag.contains("CapeImageHeight")) {
                        int width = skinTag.getInt("CapeImageWidth");
                        int height = skinTag.getInt("CapeImageHeight");
                        newSkin.setCapeData(new SerializedImage(width, height, data));
                    } else {
                        newSkin.setCapeData(data);
                    }
                }
                if (skinTag.contains("GeometryName")) {
                    newSkin.setGeometryName(skinTag.getString("GeometryName"));
                }
                if (skinTag.contains("SkinResourcePatch")) {
                    newSkin.setSkinResourcePatch(new String(skinTag.getByteArray("SkinResourcePatch"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("GeometryData")) {
                    newSkin.setGeometryData(new String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("SkinGeometryDataEngineVersion")) {
                    newSkin.setGeometryDataEngineVersion(skinTag.getString("SkinGeometryDataEngineVersion"));
                }
                if (skinTag.contains("SkinAnimationData")) {
                    newSkin.setAnimationData(new String(skinTag.getByteArray("SkinAnimationData"), StandardCharsets.UTF_8));
                } else if (skinTag.contains("AnimationData")) { // backwards compatible
                    newSkin.setAnimationData(new String(skinTag.getByteArray("AnimationData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("PremiumSkin")) {
                    newSkin.setPremium(skinTag.getBoolean("PremiumSkin"));
                }
                if (skinTag.contains("PersonaSkin")) {
                    newSkin.setPersona(skinTag.getBoolean("PersonaSkin"));
                }
                if (skinTag.contains("CapeOnClassicSkin")) {
                    newSkin.setCapeOnClassic(skinTag.getBoolean("CapeOnClassicSkin"));
                }
                if (skinTag.contains("AnimatedImageData")) {
                    ListTag<CompoundTag> list = skinTag.getList("AnimatedImageData", CompoundTag.class);
                    for (CompoundTag animationTag : list.getAll()) {
                        float frames = animationTag.getFloat("Frames");
                        int type = animationTag.getInt("Type");
                        byte[] image = animationTag.getByteArray("Image");
                        int width = animationTag.getInt("ImageWidth");
                        int height = animationTag.getInt("ImageHeight");
                        int expression = animationTag.getInt("AnimationExpression");
                        newSkin.getAnimations().add(new SkinAnimation(new SerializedImage(width, height, image), type, frames, expression));
                    }
                }
                if (skinTag.contains("ArmSize")) {
                    newSkin.setArmSize(skinTag.getString("ArmSize"));
                }
                if (skinTag.contains("SkinColor")) {
                    newSkin.setSkinColor(skinTag.getString("SkinColor"));
                }
                if (skinTag.contains("PersonaPieces")) {
                    ListTag<CompoundTag> pieces = skinTag.getList("PersonaPieces", CompoundTag.class);
                    for (CompoundTag piece : pieces.getAll()) {
                        newSkin.getPersonaPieces().add(new PersonaPiece(
                                piece.getString("PieceId"),
                                piece.getString("PieceType"),
                                piece.getString("PackId"),
                                piece.getBoolean("IsDefault"),
                                piece.getString("ProductId")
                        ));
                    }
                }
                if (skinTag.contains("PieceTintColors")) {
                    ListTag<CompoundTag> tintColors = skinTag.getList("PieceTintColors", CompoundTag.class);
                    for (CompoundTag tintColor : tintColors.getAll()) {
                        newSkin.getTintColors().add(new PersonaPieceTint(
                                tintColor.getString("PieceType"),
                                tintColor.getList("Colors", StringTag.class).getAll().stream()
                                        .map(stringTag -> stringTag.data).collect(Collectors.toList())
                        ));
                    }
                }
                if (skinTag.contains("IsTrustedSkin")) {
                    newSkin.setTrusted(skinTag.getBoolean("IsTrustedSkin"));
                }
                if (skinTag.contains("OverrideSkin")) {
                    newSkin.setOverridingPlayerAppearance(skinTag.getBoolean("OverrideSkin"));
                }
                this.setSkin(newSkin);
            }

//            this.uuid = Utils.dataToUUID(String.valueOf(this.getId()).getBytes(StandardCharsets.UTF_8), this.getSkin()
//                    .getSkinData().data, this.getNameTag().getBytes(StandardCharsets.UTF_8));
            long id = this.getId();
            this.uuid = new UUID(Hash.xxh64(id), id);
        }

        super.initEntity();
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (skin != null) {
            CompoundTag skinTag = new CompoundTag()
                    .putByteArray("Data", this.getSkin().getSkinData().data)
                    .putInt("SkinImageWidth", this.getSkin().getSkinData().width)
                    .putInt("SkinImageHeight", this.getSkin().getSkinData().height)
                    .putString("ModelId", this.getSkin().getSkinId())
                    .putString("CapeId", this.getSkin().getCapeId())
                    .putByteArray("CapeData", this.getSkin().getCapeData().data)
                    .putInt("CapeImageWidth", this.getSkin().getCapeData().width)
                    .putInt("CapeImageHeight", this.getSkin().getCapeData().height)
                    .putByteArray("SkinResourcePatch", this.getSkin().getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("GeometryData", this.getSkin().getGeometryData().getBytes(StandardCharsets.UTF_8))
                    .putString("SkinGeometryDataEngineVersion", this.getSkin().getGeometryDataEngineVersion())
                    .putByteArray("SkinAnimationData", this.getSkin().getAnimationData().getBytes(StandardCharsets.UTF_8))
                    .putBoolean("PremiumSkin", this.getSkin().isPremium())
                    .putBoolean("PersonaSkin", this.getSkin().isPersona())
                    .putBoolean("CapeOnClassicSkin", this.getSkin().isCapeOnClassic())
                    .putString("ArmSize", this.getSkin().getArmSize())
                    .putString("SkinColor", this.getSkin().getSkinColor())
                    .putBoolean("IsTrustedSkin", this.getSkin().isTrusted())
                    .putBoolean("OverrideSkin", this.getSkin().isOverridingPlayerAppearance());

            List<SkinAnimation> animations = this.getSkin().getAnimations();
            if (!animations.isEmpty()) {
                ListTag<CompoundTag> animationsTag = new ListTag<>("AnimationImageData");
                for (SkinAnimation animation : animations) {
                    animationsTag.add(new CompoundTag()
                            .putFloat("Frames", animation.frames)
                            .putInt("Type", animation.type)
                            .putInt("ImageWidth", animation.image.width)
                            .putInt("ImageHeight", animation.image.height)
                            .putInt("AnimationExpression", animation.expression)
                            .putByteArray("Image", animation.image.data));
                }
                skinTag.putList(animationsTag);
            }

            List<PersonaPiece> personaPieces = this.getSkin().getPersonaPieces();
            if (!personaPieces.isEmpty()) {
                ListTag<CompoundTag> piecesTag = new ListTag<>("PersonaPieces");
                for (PersonaPiece piece : personaPieces) {
                    piecesTag.add(new CompoundTag().putString("PieceId", piece.id)
                            .putString("PieceType", piece.type)
                            .putString("PackId", piece.packId)
                            .putBoolean("IsDefault", piece.isDefault)
                            .putString("ProductId", piece.productId));
                }
            }

            List<PersonaPieceTint> tints = this.getSkin().getTintColors();
            if (!tints.isEmpty()) {
                ListTag<CompoundTag> tintsTag = new ListTag<>("PieceTintColors");
                for (PersonaPieceTint tint : tints) {
                    tintsTag.add(new CompoundTag()
                            .putString("PieceType", tint.pieceType)
                            .putList(new ListTag<>("Colors", tint.colors.stream()
                                    .map(s -> new StringTag("", s)).collect(Collectors.toList()))));
                }
            }

            if (!this.getSkin().getPlayFabId().isEmpty()) {
                skinTag.putString("PlayFabId", this.getSkin().getPlayFabId());
            }

            this.namedTag.putCompound("Skin", skinTag);
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (this != player && this.hasSpawned.putIfAbsent(player.getLoaderId(), player) == null) {
            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            if (!player.sentSkins.contains(this.getUniqueId())) {
                if (this instanceof Player) {
                    String uid = ((Player) this).getLoginChainData().getNetEaseUID();
                    if (uid == null || uid.isEmpty()) uid = ((Player) this).getLoginChainData().getXUID();
                    this.server.updatePlayerListData(this.getUniqueId(), this.getId(), ((Player) this).getDisplayName(), this.skin, uid, new Player[]{player});
                } else {
                    this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getNameTag(), this.skin, new Player[]{player});
                }

                player.sentSkins.add(this.getUniqueId());
            }

            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getNameTag();
            pk.entityUniqueId = this.getId();
            pk.entityRuntimeId = this.getId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = (float) this.motionX;
            pk.speedY = (float) this.motionY;
            pk.speedZ = (float) this.motionZ;
            pk.yaw = (float) this.yaw;
            pk.headYaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.item = this.getInventory().getItemInHand();
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);

            this.armorInventory.sendContents(player);
            this.offhandInventory.sendContents(player);

            if (this.riding != null) {
                SetEntityLinkPacket pkk = new SetEntityLinkPacket();
                pkk.vehicleUniqueId = this.riding.getId();
                pkk.riderUniqueId = this.getId();
                pkk.type = 1;
                pkk.immediate = 1;

                player.dataPacket(pkk);
            }

            // 不再移除皮肤，因为后续可以不再发皮肤，这样可以节省流量
            /*if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getUniqueId(), new Player[]{player});
            }*/
        }
    }

    /*@Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {

            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());

            // 不再移除实体皮肤，因为后续不用再发实体皮肤了
            // if (this instanceof Player) this.server.removePlayerListData(this.getUniqueId(), new Player[]{player});
        }
    }*/

    @Override
    public void close() {
        if (!this.closed) {
            if (inventory != null && (!(this instanceof Player) || ((Player) this).loggedIn)) {
                for (Player viewer : this.inventory.getViewers()) {
                    viewer.removeWindow(this.inventory);
                }
            }

            super.close();
        }
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.sendPosition(new Vector3(x, y, z), yaw, pitch, MovePlayerPacket.MODE_NORMAL, this.getViewers().values().toArray(new Player[0]));
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode, Player[] targets) {
        if (targets == null) {
            return;
        }

        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = this.getId();
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        pk.headYaw = (float) yaw;
        pk.pitch = (float) pitch;
        pk.yaw = (float) yaw;
        pk.mode = mode;
        if (this.riding != null) {
            pk.ridingEid = this.riding.getId();
        }
        pk.setChannel(DataPacket.CHANNEL_PLAYER_MOVING);
        Server.broadcastPacket(targets, pk);
    }

}
