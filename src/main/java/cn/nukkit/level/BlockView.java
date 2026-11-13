package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;

import java.util.NoSuchElementException;

/**
 * 观察者方块视图：区块数据与方块更新下发给持有该视图的玩家前，逐个方块改写成另一种方块。
 * 只改变客户端看到的方块，服务端世界数据、碰撞与交互仍按原方块处理。
 * <p>
 * 实现必须满足以下约定：
 * <ul>
 * <li>{@link #remap(int)} 是只依赖输入的纯函数，与坐标、时间和玩家无关；同一输入始终返回同一结果。</li>
 * <li>实例不可变且线程安全：区块序列化在异步线程中调用 {@link #remap(int)}。</li>
 * <li>返回的方块应在所有可用协议版本中都有网络 ID，可用 {@link #isSupportedTarget(int)} 检查。
 * 个别版本不支持时，该版本的玩家会看到原方块。</li>
 * <li>不替换带方块实体的方块，也不替换成带方块实体的方块，可用 {@link #isSupportedSource(int)} 与
 * {@link #isSupportedTarget(int)} 检查。方块实体数据不经过视图，始终按服务端原样下发，
 * 只有两端都不涉及方块实体，客户端才不会收到与其方块不匹配的方块实体数据。</li>
 * <li>区块缓存按视图实例区分：同一种视图应由所有玩家共享同一个实例，
 * 为每名玩家单独创建实例会让每名玩家都单独序列化一份区块。</li>
 * </ul>
 * 默认实现见 {@link MappedBlockView}，通过 {@link #builder(String)} 构建。
 */
public interface BlockView {

    /**
     * @param fullId 完整方块 ID（id << {@link Block#BLOCK_META_BITS} | meta）
     * @return 替换后的完整方块 ID；不替换时原样返回
     */
    int remap(int fullId);

    /**
     * 用于日志与调试的名称
     */
    String getName();

    static MappedBlockView.Builder builder(String name) {
        return MappedBlockView.builder(name);
    }

    /**
     * 检查方块能否被替换：方块已注册且不带方块实体。
     * 只应在构建视图时调用，不要在序列化等热点路径中使用。
     *
     * @param blockId 方块 ID（不含数据值）
     */
    static boolean isSupportedSource(int blockId) {
        if (blockId < 0 || blockId >= Block.BLOCK_ID_COUNT || Blocks.getBlockNameById(blockId) == null) {
            return false;
        }
        return Block.get(blockId).getBlockEntityType() == 0;
    }

    /**
     * 检查方块能否作为替换目标：方块已注册、数据值合法、不带方块实体，
     * 且在所有可用协议版本的方块调色板中都有网络 ID。
     * 只应在构建视图时调用，不要在序列化等热点路径中使用。
     */
    static boolean isSupportedTarget(int fullId) {
        int id = fullId >> Block.BLOCK_META_BITS;
        int meta = fullId & Block.BLOCK_META_MASK;
        if (id < 0 || id >= Block.BLOCK_ID_COUNT || Blocks.getBlockNameById(id) == null) {
            return false;
        }
        Block block = Block.get(id);
        if (!block.isValidMeta(meta)) {
            return false;
        }
        if (Block.get(id, meta).getBlockEntityType() != 0) {
            return false;
        }
        for (StaticVersion version : StaticVersion.getAvailableVersions()) {
            GlobalBlockPaletteInterface palette = GlobalBlockPalette.getStaticBlockPalette(version);
            if (palette == null) {
                continue;
            }
            try {
                palette.getOrCreateRuntimeId0(id, meta);
            } catch (NoSuchElementException e) {
                // 调色板没有提供不抛异常的查询，构建期逐个探测即可
                return false;
            }
        }
        return true;
    }
}
