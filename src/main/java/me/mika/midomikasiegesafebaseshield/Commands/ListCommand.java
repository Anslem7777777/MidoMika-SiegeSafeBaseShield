package me.mika.midomikasiegesafebaseshield.Commands;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ListCommand extends SubCommands{
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List all SSBase";
    }

    @Override
    public String getSyntax() {
        return "/ssbs list";
    }

    @Override
    public void perform(Player player, String[] args) {

        if (args.length > 0){
            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
            Integer areaLimit = config.getInt("player-area-limit");
            Integer playerLitmitLeft = config.getInt(player.getName() + " Count");
            Boolean isNull = true;
            List<String> areaName = new ArrayList<>();

            if (args[0].equalsIgnoreCase("list")){
                for(String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
                    if(mainKey.contains(player.getName())){
                        for(String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)){
                            if (!secondKey.equals("Number-Of-Selected-Location")) {
                                areaName.add(PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo" + ".name"));
                                isNull = false;

                            }
                        }
                    }
                }
                if (isNull){
                    player.sendMessage(net.md_5.bungee.api.ChatColor.of("#FF8000")  + "You have not set any area.");

                }else {
                    player.sendMessage(ChatColor.YELLOW + "Saved Area List ("+ ChatColor.YELLOW + playerLitmitLeft + ChatColor.YELLOW + "/" + ChatColor.YELLOW + areaLimit + ChatColor.YELLOW + "): ");
                    for (int i = 0; i < areaName.size(); i++) {
                        TextComponent areaNameComponent = new TextComponent(String.format(areaName.get(i), areaName.get(i)));
                        areaNameComponent.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        areaNameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ssbs show " + areaNameComponent.getText()));
                        areaNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click me to display your base").color(net.md_5.bungee.api.ChatColor.AQUA).create()));

                        TextComponent numberOfAreaComponent = new TextComponent(String.format(String.valueOf(i + 1), "numberOfArea"));
                        numberOfAreaComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);

                        player.spigot().sendMessage(areaNameComponent);

//                        player.sendMessage(ChatColor.YELLOW + "" + (i + 1)  + ". " + ChatColor.GREEN + areaName.get(i));

                    }
                }

                areaName.clear();

            }
        }

    }

}
