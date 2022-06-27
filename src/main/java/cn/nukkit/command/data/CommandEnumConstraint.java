package cn.nukkit.command.data;

import java.util.EnumSet;
import java.util.Set;

/**
 * CommandEnumConstraint is sent in the AvailableCommandsPacket to limit what values of
 * an enum may be used taking in account things such as whether cheats are enabled.
 */
public class CommandEnumConstraint {
    /**
     * The option in an enum that the constraints should be applied to.
     */
    public String option;

    /**
     * The name of the enum of which the option above should be constrained.
     */
    public CommandEnum enumData;

    /**
     * List of constraints that should be applied to the enum option.
     */
    public Set<CommandEnumConstraintType> constraints = EnumSet.noneOf(CommandEnumConstraintType.class);
}
