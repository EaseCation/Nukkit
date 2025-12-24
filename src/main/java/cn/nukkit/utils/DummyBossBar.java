package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.property.EntityPropertyRegistry;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.BossEventPacket.BossBarColor;
import cn.nukkit.network.protocol.types.EntityLink;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

/**
 * DummyBossBar
 * ===============
 * @author boybook
 * Nukkit Project
 * ===============
 */
public class DummyBossBar {

    private final Player player;
    private final long bossBarId;
    private boolean spawned = false;

    private String text;
    private float length;
    private BossBarColor color;

    private DummyBossBar(Builder builder) {
        this.player = builder.player;
        this.bossBarId = builder.bossBarId;
        this.text = builder.text;
        this.length = builder.length;
        this.color = builder.color;
    }

    public static Builder builder(Player player) {
        return new Builder(player);
    }

    /**
     * Boss bar builder
     */
    public static class Builder {
        private final Player player;
        private final long bossBarId;

        private String text = "";
        private float length = 100;
        private BossBarColor color = null;

        private Builder(Player player) {
            this.player = player;
            this.bossBarId = Entity.entityCount++;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder length(float length) {
            if (length >= 0 && length <= 100) this.length = length;
            return this;
        }

        public Builder color(BossBarColor color) {
            this.color = color;
            return this;
        }

        public DummyBossBar build() {
            return new DummyBossBar(this);
        }
    }

    /**
     * Get boss bar owner
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get boss bar id
     *
     * @return boss bar id
     */
    public long getBossBarId() {
        return bossBarId;
    }

    /**
     * Get boss bar text
     *
     * @return current text
     */
    public String getText() {
        return text;
    }

    /**
     * Set the boss bar text and send it to player if changed
     *
     * @param text new text
     */
    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            this.updateBossEntityNameTag();
            this.sendSetBossBarTitle();
        }
    }

    /**
     * Get boss bar length
     *
     * @return length
     */
    public float getLength() {
        return length;
    }

    /**
     * Set boss bar length
     *
     * @param length new length
     */
    public void setLength(float length) {
        if (this.length != length) {
            this.length = length;
            this.sendAttributes();
            this.sendSetBossBarLength();
        }
    }

    /**
     * Set boss bar color. Requires client version 1.18 or newer.
     * @param color the boss bar color
     */
    public void setColor(BossBarColor color) {
        if (this.color != color) {
            this.color = color;
            this.sendSetBossBarTexture();
        }
    }

    /**
     * Get boss bar color
     * @return current color of the boss bar
     */
    public BossBarColor getColor() {
        return this.color;
    }

    public void createBossEntity() {
        AddEntityPacket pkAdd = new AddEntityPacket();
        pkAdd.type = EntityID.CREEPER;
        pkAdd.entityUniqueId = bossBarId;
        pkAdd.entityRuntimeId = bossBarId;
        pkAdd.x = (float) player.x;
        pkAdd.y = player.level.getHeightRange().getMinY() - 10; // Below the bedrock
        pkAdd.z = (float) player.z;
        pkAdd.speedX = 0;
        pkAdd.speedY = 0;
        pkAdd.speedZ = 0;
        pkAdd.metadata = new EntityMetadata()
                // Default Metadata tags
                .putLong(Entity.DATA_FLAGS, 0)
                .putShort(Entity.DATA_AIR, 300)
                .putShort(Entity.DATA_MAX_AIR, 300)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putString(Entity.DATA_NAMETAG, text) // Set the entity name
                .putFloat(Entity.DATA_SCALE, 0.001f) // And make it invisible
                .putFloat(Entity.DATA_BOUNDING_BOX_HEIGHT, 0.01f)
                .putFloat(Entity.DATA_BOUNDING_BOX_WIDTH, 0.01f);
        Pair<Int2IntMap, Int2FloatMap> propertyValues = EntityPropertyRegistry.getProperties(pkAdd.type).getDefaultValues();
        if (propertyValues != null) {
            pkAdd.intProperties = propertyValues.left();
            pkAdd.floatProperties = propertyValues.right();
        }
        pkAdd.links = new EntityLink[]{
                new EntityLink(player.getId(), bossBarId, EntityLink.TYPE_PASSENGER, false, false, 0)
        };
        player.dataPacket(pkAdd);

        this.spawned = true;
    }

    private void sendAttributes() {
        UpdateAttributesPacket pkAttributes = new UpdateAttributesPacket();
        pkAttributes.entityId = bossBarId;
        Attribute attr = Attribute.getAttribute(Attribute.HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length); // Entity health
        pkAttributes.entries = new Attribute[]{attr};
        player.dataPacket(pkAttributes);
    }

    private void sendShowBossBar() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_SHOW;
        pkBoss.title = text;
        pkBoss.healthPercent = this.length / 100;
        pkBoss.color = this.color == null ? BossBarColor.PINK : this.color;
        player.dataPacket(pkBoss);
    }

    private void sendHideBossBar() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_HIDE;
        player.dataPacket(pkBoss);
    }

    private void sendSetBossBarTexture() {
        BossEventPacket pk = new BossEventPacket();
        pk.bossEid = this.bossBarId;
        pk.type = BossEventPacket.TYPE_TEXTURE;
        pk.color = this.color == null ? BossBarColor.PINK : this.color;
        player.dataPacket(pk);
    }

    private void sendSetBossBarTitle() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_TITLE;
        pkBoss.title = text;
        pkBoss.healthPercent = this.length / 100;
        player.dataPacket(pkBoss);
    }

    private void sendSetBossBarLength() {
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = bossBarId;
        pkBoss.type = BossEventPacket.TYPE_HEALTH_PERCENT;
        pkBoss.healthPercent = this.length / 100;
        player.dataPacket(pkBoss);
    }

    /**
     * Don't let the entity go too far from the player, or the BossBar will disappear.
     * Update boss entity's position when teleport and each 5s.
     */
    public void updateBossEntityPosition() {
        if (!spawned) {
            create();
            return;
        }

        MoveEntityPacket pk = new MoveEntityPacket();
        pk.eid = this.bossBarId;
        pk.x = (float) this.player.x;
        pk.y = player.level.getHeightRange().getMinY() - 10;
        pk.z = (float) this.player.z;
        pk.headYaw = 0;
        pk.yaw = 0;
        pk.pitch = 0;
        player.dataPacket(pk);
    }

    private void updateBossEntityNameTag() {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.bossBarId;
        pk.metadata = new EntityMetadata().putString(Entity.DATA_NAMETAG, this.text);
        player.dataPacket(pk);
    }

    private void removeBossEntity() {
        RemoveEntityPacket pkRemove = new RemoveEntityPacket();
        pkRemove.eid = bossBarId;
        player.dataPacket(pkRemove);
        this.spawned = false;
    }

    private void updateEntityLink(byte type) {
        SetEntityLinkPacket packet = new SetEntityLinkPacket();
        packet.vehicleUniqueId = player.getId();
        packet.riderUniqueId = bossBarId;
        packet.type = type;
        player.dataPacket(packet);
    }

    /**
     * Create boss bar entity and send its data
     */
    public void create() {
        createBossEntity();
        sendAttributes();
        updateBossEntityNameTag();
        sendShowBossBar();
        if (color != null) this.sendSetBossBarTexture();
    }

    /**
     * Once the player has teleported, resend Show BossBar
     */
    public void reshow() {
        if (!this.spawned) {
            this.create();
        } else {
            updateBossEntityPosition();
            updateEntityLink(SetEntityLinkPacket.TYPE_PASSENGER);
            sendShowBossBar();
        }
    }

    public void destroy() {
        if (this.spawned) {
            sendHideBossBar();
            updateEntityLink(SetEntityLinkPacket.TYPE_REMOVE);
            removeBossEntity();
        }
    }

}
