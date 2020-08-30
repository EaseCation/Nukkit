package cn.nukkit.network.protocol;

import cn.nukkit.level.GameRules;

import java.util.Arrays;

/**
 * Created on 15-10-13.
 */
public class StartGamePacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.START_GAME_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public int playerGamemode;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float pitch;
    public int seed;
    public byte dimension;
    public int generator = 1;
    public int worldGamemode;
    public int difficulty;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public boolean hasAchievementsDisabled = true;
    public int dayCycleStopTime = -1; //-1 = not stopped, any positive value = stopped at that time
    public boolean eduMode = false;
    public float rainLevel;
    public float lightningLevel;
    public boolean multiplayerGame = true;
    public boolean broadcastToLAN = true;
    public boolean broadcastToXboxLive = true;
    public boolean commandsEnabled;
    public boolean isTexturePacksRequired = false;
    public GameRules gameRules = null;
    public boolean bonusChest = false;
    public boolean trustPlayers = false;
    public int permissionLevel = 1;
    public int gamePublish = 4;
    public String levelId = ""; //base64 string, usually the same as world folder name in vanilla
    public String worldName;
    public String premiumWorldTemplateId = "";
    public boolean unknown = false;
    public long currentTick;

    public int enchantmentSeed;

    @Override
    public void decode() {
        this.entityUniqueId = this.getEntityUniqueId();
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.playerGamemode = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.playerGamemode);
        this.putVector3f(this.x, this.y, this.z);
        this.putLFloat(this.yaw);
        this.putLFloat(this.pitch);
        this.putVarInt(this.seed);
        this.putVarInt(this.dimension);
        this.putVarInt(this.generator);
        this.putVarInt(this.worldGamemode);
        this.putVarInt(this.difficulty);
        this.putBlockVector3(this.spawnX, this.spawnY, this.spawnZ);
        this.putBoolean(this.hasAchievementsDisabled);
        this.putVarInt(this.dayCycleStopTime);
        this.putBoolean(this.eduMode);
        this.putLFloat(this.rainLevel);
        this.putLFloat(this.lightningLevel);
        this.putBoolean(this.multiplayerGame);
        this.putBoolean(this.broadcastToLAN);
        this.putBoolean(this.broadcastToXboxLive);
        this.putBoolean(this.commandsEnabled);
        this.putBoolean(this.isTexturePacksRequired);
        this.putGameRules(this.gameRules);
        this.putBoolean(this.bonusChest);
        this.putBoolean(this.trustPlayers);
        this.putVarInt(this.permissionLevel);
        this.putVarInt(this.gamePublish);
        this.putString(this.levelId);
        this.putString(this.worldName);
        this.putString(this.premiumWorldTemplateId);
        this.putBoolean(this.unknown);
        this.putLLong(this.currentTick);
        this.putVarInt(this.enchantmentSeed);
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static void main(String[] args) {
        byte[] data = toBytes("0b0000fdffffff9f100302edb39c41e47a62427adba5c080d5af411935dec280eafada0a000402001c390a01010000000000000000000001010001001412636f6d6d616e64626c6f636b6f757470757401000f646f6461796c696768746379636c6501000d646f656e7469747964726f707301010a646f666972657469636b010109646f6d6f626c6f6f7401010d646f6d6f62737061776e696e6701010b646f74696c6564726f707301010e646f776561746865726379636c6501010e64726f776e696e6764616d61676501010a66616c6c64616d61676501010a6669726564616d61676501010d6b656570696e76656e746f727901000b6d6f626772696566696e6701010370767001010f73686f77636f6f7264696e617465730100136e61747572616c726567656e65726174696f6e01010b746e746578706c6f64657301011373656e64636f6d6d616e64666565646261636b0101146578706572696d656e74616c67616d65706c61790100156d6178636f6d6d616e64636861696e6c656e67746802feff070000000206040000000006010000002439376562313163362d633462352d343636302d613363662d6433353764613762393531320fe8999ae7a9bae5a4a7e4b9b1e696970000ccd81c0000000000ac80fda2020060006834000023000000fe2b00000000ffffffff0f000060002835000024000000fe0a00000160002836000025000000fe3c00000060002837000026000000fe3b00000160008838000027000000fe37000060001f010001000000bfffffff600a8039000028000000fe4800001412636f6d6d616e64626c6f636b6f757470757401000f646f6461796c696768746379636c6501000d646f656e7469747964726f707301010a646f666972657469636b010109646f6d6f626c6f6f7401010d646f6d6f62737061776e696e6701010b646f74696c6564726f707301010e646f776561746865726379636c6501010e64726f776e696e6764616d61676501010a66616c6c64616d61676501010a6669726564616d61676501010d6b656570696e76656e746f727901000b6d6f626772696566696e6701010370767001010f73686f77636f6f7264696e617465730100136e61747572616c726567656e65726174696f6e01010b746e746578706c6f64657301011373656e64636f6d6d616e64666565646261636b0101146578706572696d656e74616c67616d65706c61790100156d6178636f6d6d616e64636861696e6c656e67746802feff07");
        System.out.println(Arrays.toString(data));
        StartGamePacket pk = new StartGamePacket();
        pk.setBuffer(data, 3);
        pk.decode();
        System.out.println(pk.toString());
    }

}
