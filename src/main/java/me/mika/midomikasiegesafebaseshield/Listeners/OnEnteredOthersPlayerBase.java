package me.mika.midomikasiegesafebaseshield.Listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OnEnteredOthersPlayerBase implements Listener {
    private Set<String> selectedAreaOriBlocks;
    private static Map<UUID, LocalDateTime> playerTime = new HashMap<>();
    private static Map<UUID, Boolean> playerIsEnteredOthersBase = new HashMap<>();
//    private static Map<UUID, List<UUID>> playerBaseAccess = new HashMap<>();

    @EventHandler
    public void onEnterOthersBasePlayerBase(PlayerMoveEvent e){
        Player p = e.getPlayer();
        LocalDateTime currentTime = LocalDateTime.now();
        Block blockUnderPlayer = p.getLocation().subtract(0,0,0).getBlock();
        Block blockUnderPlayer2 = p.getLocation().subtract(0,1,0).getBlock();
        Block blockUnderPlayer3 = p.getLocation().add(0,1,0).getBlock();
        String blockUnderPlayerLocString = blockUnderPlayer.getWorld().getName() + ";" + blockUnderPlayer.getX() + ";" + blockUnderPlayer.getY() + ";" + blockUnderPlayer.getZ();
        String blockUnderPlayerLocString2 = blockUnderPlayer2.getWorld().getName() + ";" + blockUnderPlayer2.getX() + ";" + blockUnderPlayer2.getY() + ";" + blockUnderPlayer2.getZ();
        String blockUnderPlayerLocString3 = blockUnderPlayer3.getWorld().getName() + ";" + blockUnderPlayer3.getX() + ";" + blockUnderPlayer3.getY() + ";" + blockUnderPlayer3.getZ();
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
            if (!mainKey.equals(p.getName())){
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)){
                    if (!secondKey.equals("Number-Of-Selected-Location")) {
                        selectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + ".areaInfo" + ".selectedAreaOriBlocks").getKeys(false);

                    }
                }
            }
        }

        if (selectedAreaOriBlocks.contains(blockUnderPlayerLocString) || selectedAreaOriBlocks.contains(blockUnderPlayerLocString2) || selectedAreaOriBlocks.contains(blockUnderPlayerLocString3)) {
            if (playerIsEnteredOthersBase.get(p.getUniqueId()) == null || playerIsEnteredOthersBase.get(p.getUniqueId()) == false) {
                playerIsEnteredOthersBase.put(p.getUniqueId(), true);
//                p.sendMessage(ChatColor.of(new Color(255, 165, 0, 10)) + "Caution: You Are Entering Other Players' Territory!");
                p.sendTitle(" ", ChatColor.of(new Color(255, 165, 0, 10)) + "Caution: Entering Other Players' Territory!", 20, 0, 20);

            }
        }else {
            if (playerIsEnteredOthersBase.get(p.getUniqueId()) != null && playerIsEnteredOthersBase.get(p.getUniqueId()) == true){
                playerIsEnteredOthersBase.put(p.getUniqueId(), false);
//                p.sendMessage(ChatColor.of(new Color(255, 165, 0, 10)) + "You Have Left Other Players' Territory");
                p.sendTitle(" ", ChatColor.of(new Color(255, 165, 0, 10)) + "Left Other Players' Territory", 20, 0, 20);

            }
        }
    }
}
