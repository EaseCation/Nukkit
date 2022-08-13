package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.stream.Collectors;

public class BlockEntityCommandBlock extends BlockEntitySpawnable {
    // BaseCommandBlock
    public static final String TAG_COMMAND = "Command";
    public static final String TAG_VERSION = "Version";
    public static final String TAG_SUCCESS_COUNT = "SuccessCount";
    public static final String TAG_CUSTOM_NAME = "CustomName";
    public static final String TAG_LAST_OUTPUT = "LastOutput";
    public static final String TAG_LAST_OUTPUT_PARAMS = "LastOutputParams";
    public static final String TAG_TRACK_OUTPUT = "TrackOutput";
    public static final String TAG_LAST_EXECUTION = "LastExecution";
    public static final String TAG_TICK_DELAY = "TickDelay";
    public static final String TAG_EXECUTE_ON_FIRST_TICK = "ExecuteOnFirstTick";
    // CommandBlockActor
    public static final String TAG_POWERED = "powered";
    public static final String TAG_AUTO = "auto";
    public static final String TAG_CONDITION_MET = "conditionMet";
    public static final String TAG_LP_CONDIONAL_MODE = "LPCondionalMode";
    public static final String TAG_LP_REDSTONE_MODE = "LPRedstoneMode";
    public static final String TAG_LP_COMMAND_MODE = "LPCommandMode";
    // BlockData
    public static final String TAG_CONDITIONAL_MODE = "conditionalMode";

    public static final int CURRENT_VERSION = 20;

    protected String command;
    protected int successCount;
    protected String lastOutput;
    protected List<String> lastOutputParams;
    protected boolean trackOutput;
    protected long lastExecution;
    protected int tickDelay;
    protected boolean executingOnFirstTick;

    protected boolean powered;
    protected boolean auto;
    protected boolean conditionMet;
    protected boolean lastOutputCondionalMode;
    protected boolean lastOutputRedstoneMode;
    protected int lastOutputCommandMode;

    protected boolean conditionalMode; //TODO: from block VanillaStates::ConditionalBit

    public BlockEntityCommandBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (this.namedTag.contains(TAG_POWERED)) {
            this.powered = this.namedTag.getBoolean(TAG_POWERED);
        } else {
            this.powered = false;
        }
        if (this.namedTag.contains(TAG_AUTO)) {
            this.auto = this.namedTag.getBoolean(TAG_AUTO);
        } else {
            this.auto = false;
        }
        if (this.namedTag.contains(TAG_CONDITION_MET)) {
            this.conditionMet = this.namedTag.getBoolean(TAG_CONDITION_MET);
        } else {
            this.conditionMet = false;
        }
        if (this.namedTag.contains(TAG_LP_CONDIONAL_MODE)) {
            this.lastOutputCondionalMode = this.namedTag.getBoolean(TAG_LP_CONDIONAL_MODE);
        } else {
            this.lastOutputCondionalMode = true;
        }
        if (this.namedTag.contains(TAG_LP_REDSTONE_MODE)) {
            this.lastOutputRedstoneMode = this.namedTag.getBoolean(TAG_LP_REDSTONE_MODE);
        } else {
            this.lastOutputRedstoneMode = true;
        }
        if (this.namedTag.contains(TAG_LP_COMMAND_MODE)) {
            this.lastOutputCommandMode = this.namedTag.getInt(TAG_LP_COMMAND_MODE);
        } else {
            this.lastOutputCommandMode = 0;
        }

        if (this.namedTag.contains(TAG_COMMAND)) {
            this.command = this.namedTag.getString(TAG_COMMAND);
        } else {
            this.command = "";
        }
        //TODO: "Version" tag upgrade ("CommandJSON")
        if (this.namedTag.contains(TAG_SUCCESS_COUNT)) {
            this.successCount = this.namedTag.getInt(TAG_SUCCESS_COUNT);
        } else {
            this.successCount = 0;
        }
        if (this.namedTag.contains(TAG_LAST_OUTPUT)) {
            this.lastOutput = this.namedTag.getString(TAG_LAST_OUTPUT);
        } else {
            this.lastOutput = "";
        }
        if (this.namedTag.contains(TAG_LAST_OUTPUT_PARAMS)) {
            this.lastOutputParams = this.namedTag.getList(TAG_LAST_OUTPUT_PARAMS, StringTag.class).getAllUnsafe().stream()
                    .map(tag -> tag.data)
                    .collect(Collectors.toList());
        } else {
            this.lastOutputParams = new ObjectArrayList<>();
        }
        if (this.namedTag.contains(TAG_TRACK_OUTPUT)) {
            this.trackOutput = this.namedTag.getBoolean(TAG_TRACK_OUTPUT);
        } else {
            this.trackOutput = true;
        }
        if (this.namedTag.contains(TAG_LAST_EXECUTION)) {
            this.lastExecution = this.namedTag.getLong(TAG_LAST_EXECUTION);
        } else {
            this.lastExecution = 0;
        }
        if (this.namedTag.contains(TAG_TICK_DELAY)) {
            this.tickDelay = this.namedTag.getInt(TAG_TICK_DELAY);
        } else {
            this.tickDelay = 0;
        }
        if (this.namedTag.contains(TAG_EXECUTE_ON_FIRST_TICK)) {
            this.executingOnFirstTick = this.namedTag.getBoolean(TAG_EXECUTE_ON_FIRST_TICK);
        } else {
            this.executingOnFirstTick = false;
        }

        if (this.namedTag.contains(TAG_CONDITIONAL_MODE)) {
            this.conditionalMode = this.namedTag.getBoolean(TAG_CONDITIONAL_MODE);
        } else {
            this.conditionalMode = false;
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean(TAG_POWERED, this.powered);
        this.namedTag.putBoolean(TAG_AUTO, this.auto);
        this.namedTag.putBoolean(TAG_CONDITION_MET, this.conditionMet);
        this.namedTag.putBoolean(TAG_LP_CONDIONAL_MODE, this.lastOutputCondionalMode);
        this.namedTag.putBoolean(TAG_LP_REDSTONE_MODE, this.lastOutputRedstoneMode);
        this.namedTag.putInt(TAG_LP_COMMAND_MODE, this.lastOutputCommandMode);

        this.namedTag.putString(TAG_COMMAND, this.command);
        this.namedTag.putInt(TAG_VERSION, CURRENT_VERSION);
        this.namedTag.putInt(TAG_SUCCESS_COUNT, this.successCount);
        this.namedTag.putString(TAG_LAST_OUTPUT, this.lastOutput);
        this.namedTag.putList(new ListTag<>(TAG_LAST_OUTPUT_PARAMS, this.lastOutputParams.stream()
                .map(param -> new StringTag("", param))
                .collect(Collectors.toList())));
        this.namedTag.putBoolean(TAG_TRACK_OUTPUT, this.trackOutput);
        this.namedTag.putLong(TAG_LAST_EXECUTION, this.lastExecution);
        this.namedTag.putInt(TAG_TICK_DELAY, this.tickDelay);
        this.namedTag.putBoolean(TAG_EXECUTE_ON_FIRST_TICK, this.executingOnFirstTick);

        this.namedTag.putBoolean(TAG_CONDITIONAL_MODE, this.conditionalMode);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.COMMAND_BLOCK || blockId == BlockID.REPEATING_COMMAND_BLOCK || blockId == BlockID.CHAIN_COMMAND_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, COMMAND_BLOCK)
                .putBoolean(TAG_POWERED, this.powered)
                .putBoolean(TAG_AUTO, this.auto)
                .putBoolean(TAG_CONDITION_MET, this.conditionMet)
                .putBoolean(TAG_LP_CONDIONAL_MODE, this.lastOutputCondionalMode)
                .putBoolean(TAG_LP_REDSTONE_MODE, this.lastOutputRedstoneMode)
                .putInt(TAG_LP_COMMAND_MODE, this.lastOutputCommandMode)

                .putString(TAG_COMMAND, this.command)
//                .putInt(TAG_VERSION, CURRENT_VERSION)
                .putInt(TAG_SUCCESS_COUNT, this.successCount)
                .putString(TAG_CUSTOM_NAME, this.getName())
                .putString(TAG_LAST_OUTPUT, this.lastOutput)
                .putList(new ListTag<>(TAG_LAST_OUTPUT_PARAMS, this.lastOutputParams.stream()
                        .map(param -> new StringTag("", param))
                        .collect(Collectors.toList())))
                .putBoolean(TAG_TRACK_OUTPUT, this.trackOutput)
                .putLong(TAG_LAST_EXECUTION, this.lastExecution)
                .putInt(TAG_TICK_DELAY, this.tickDelay)
                .putBoolean(TAG_EXECUTE_ON_FIRST_TICK, this.executingOnFirstTick)

                .putBoolean(TAG_CONDITIONAL_MODE, this.conditionalMode);
    }
}
