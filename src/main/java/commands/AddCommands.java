package commands;

import java.util.Arrays;
import java.util.List;

public enum AddCommands {
    ADD("add"),
    ADD_IF_MAX("add_if_max"),
    UPDATE_ID("update_id"),
    REMOVE_LOWER("remove_lower"),
    REMOVE_GREATER("remove_greater");
    private String name;
    AddCommands(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public static List<String> getNames() {
        return Arrays.stream(AddCommands.values()).map(AddCommands::getName).toList();
    }
}
