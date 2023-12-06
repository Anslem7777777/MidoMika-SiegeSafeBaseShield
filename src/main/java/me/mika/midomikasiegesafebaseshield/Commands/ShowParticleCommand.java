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


public class ShowParticleCommand extends SubCommands{
    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "show SSBase particle";
    }

    @Override
    public String getSyntax() {
        return "/ssbs show";
    }

    @Override
    public void perform(Player player, String[] args) {

        if (args.length > 0){
            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

            player.sendMessage(args);

        }
    }
}
