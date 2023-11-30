package me.mika.midomikasiegesafebaseshield.Listeners;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import me.mika.midomikasiegesafebaseshield.Utils.BaseHealthBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

public class OnPlayerLeave implements Listener {
    private OnBreakSelectedArea onBreakSelectedArea;
    private BaseHealthBar baseHealthBar;

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        baseHealthBar = new BaseHealthBar(SiegeSafeBaseShield.getPlugin(), p);
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
            if (mainKey.equals(p.getName())) {
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                    if (!secondKey.equals("Number-Of-Selected-Location")) {
                        for (String blocksReplaceBySelectedBlocksLocation : PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks").getKeys(true)) {
                            Material blocksReplaceBySelectedBlocksType = Material.valueOf(PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks" + "." + blocksReplaceBySelectedBlocksLocation));
                            String[] splitLocationParts = blocksReplaceBySelectedBlocksLocation.split(";");
                            World world = Bukkit.getWorld(splitLocationParts[0]);
                            int x = Integer.parseInt(splitLocationParts[1]);
                            int y = Integer.parseInt(splitLocationParts[2]);
                            int z = Integer.parseInt(splitLocationParts[3]);
                            Location location = new Location(world, x, y, z);
                            Block block = location.getBlock();
                            block.setType(blocksReplaceBySelectedBlocksType);

                        }
                    }
                }
            }
        }

        if (OnBreakSelectedArea.playerIsBossBarCreatedMap.get(p.getUniqueId()) != null && OnBreakSelectedArea.baseHealthBar != null) {
            OnBreakSelectedArea.baseHealthBar.hideBossBar();
            OnBreakSelectedArea.playerIsBossBarCreatedMap.put(p.getUniqueId(), false);

        }
//        if (onBreakSelectedArea.bossBar != null) {
//            onBreakSelectedArea.bossBar.removeAll();
//            onBreakSelectedArea.isBossBarCreated = false;
//        }

    }

}
