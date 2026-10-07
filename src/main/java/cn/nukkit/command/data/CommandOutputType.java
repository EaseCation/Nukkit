package cn.nukkit.command.data;

public enum CommandOutputType {
    NONE("none"),
    LAST_OUTPUT("lastoutput"),
    SILENT("silent"),
    ALL_OUTPUT("alloutput"),
    DATA_SET("dataset"),
    ;

    private final String name;

    CommandOutputType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
