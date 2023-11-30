package me.mika.midomikasiegesafebaseshield.Commands;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    SiegeSafeBaseShield plugin;
    SaveCommand saveCommand = new SaveCommand(plugin);
    DeleteCommand deleteCommand = new DeleteCommand();
    ListCommand listCommand = new ListCommand();
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1){
            List<String> arguments = new ArrayList<>();
            arguments.add(saveCommand.getName());
            arguments.add(deleteCommand.getName());
            arguments.add(listCommand.getName());

            return arguments;

        }
        return null;
    }
}
