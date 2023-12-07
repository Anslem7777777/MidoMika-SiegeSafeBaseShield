package me.mika.midomikasiegesafebaseshield.Commands;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    SiegeSafeBaseShield plugin;
    SaveCommand saveCommand = new SaveCommand(plugin);
    DeleteCommand deleteCommand = new DeleteCommand();
    ListCommand listCommand = new ListCommand();
    ShowParticleCommand showParticleCommand = new ShowParticleCommand();
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1){
            List<String> arguments = new ArrayList<>();
            arguments.add(saveCommand.getName());
            arguments.add(deleteCommand.getName());
            arguments.add(listCommand.getName());
            arguments.add(showParticleCommand.getName());

            return arguments;

        } else if (strings.length == 2 && !strings[0].equalsIgnoreCase("list")) {
            Player p = (Player) commandSender;
            List<String> arguments = new ArrayList<>();
            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
                if (mainKey.equals(p.getName())){
                    for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)){
                        if (!secondKey.equalsIgnoreCase("Number-Of-Selected-Location")){
                            String configAreaName = PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + "." + ".areaInfo" + ".name");
                            arguments.add(configAreaName);

                        }
                    }
                }
            }

            return arguments;

        }
        return null;
    }
}
