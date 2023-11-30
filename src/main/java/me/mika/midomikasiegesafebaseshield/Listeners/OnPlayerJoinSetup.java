package me.mika.midomikasiegesafebaseshield.Listeners;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import me.mika.midomikasiegesafebaseshield.Utils.ChangeSelectedBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class OnPlayerJoinSetup implements Listener {
    private LocalDateTime getBreakBlockTime;
    SiegeSafeBaseShield plugin;
    public OnPlayerJoinSetup(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinServerSetup(PlayerJoinEvent e){

        Player p = e.getPlayer();
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
        ItemStack handOnItem = p.getInventory().getItemInMainHand();

        if (handOnItem != null && handOnItem.getType() == Material.STICK) {
//            ChangeSelectedBlock.ChangeBorderToGold(p);
            OnPlayerItemHeld.playerIsGoldBorderBooleanMap.put(p.getUniqueId(), true);

        } else if (String.valueOf(handOnItem) == "null" || handOnItem.getType() != Material.STICK) {
//            ChangeSelectedBlock.ChangeBorderToOri(p);
            OnPlayerItemHeld.playerIsGoldBorderBooleanMap.put(p.getUniqueId(), false);

        }

        if (!PlayerSelectedAreaConfig.getKeys(false).contains(p.getName())){
            PlayerSelectedAreaConfig.set(p.getName() + "." + "Number-Of-Selected-Location" + ".areaInfo" + ".name", "VerifySlot");

        }else {

        }

        try {
            PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
        } catch (IOException error) {
            System.out.println("Save File Error");
        }
    }

}
