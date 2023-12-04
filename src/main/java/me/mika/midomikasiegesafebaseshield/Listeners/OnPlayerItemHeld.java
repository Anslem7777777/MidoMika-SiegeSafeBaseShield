package me.mika.midomikasiegesafebaseshield.Listeners;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import me.mika.midomikasiegesafebaseshield.Tasks.ParticleTasks;
import me.mika.midomikasiegesafebaseshield.Utils.ChangeSelectedBlock;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnPlayerItemHeld implements Listener {
    public static Map<UUID, Boolean> playerIsGoldBorderBooleanMap = new HashMap<>();
    private boolean isGoldBorder = false;
    public static Player onHotbarChangePlayer;
    SiegeSafeBaseShield plugin;
    public OnPlayerItemHeld(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onHotbarChange(PlayerItemHeldEvent e) {

        onHotbarChangePlayer = e.getPlayer();
        UUID playerUUID = onHotbarChangePlayer.getUniqueId();

        World world = onHotbarChangePlayer.getWorld();
        ItemStack handOnItem = onHotbarChangePlayer.getInventory().getItem(e.getNewSlot());

        if (handOnItem != null && handOnItem.getType() == Material.STICK) {
//            ChangeSelectedBlock.ChangeBorderToGold(onHotbarChangePlayer);
            isGoldBorder = true;

        } else if (String.valueOf(handOnItem) == "null" || handOnItem.getType() != Material.STICK) {
//            ChangeSelectedBlock.ChangeBorderToOri(onHotbarChangePlayer);
            isGoldBorder = false;

        }

        playerIsGoldBorderBooleanMap.put(playerUUID, isGoldBorder);

        Player player = OnPlayerItemHeld.onHotbarChangePlayer;
        if (isGoldBorder == true) {
            ParticleTasks particleTask = new ParticleTasks(plugin, player);
            particleTask.start();

        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        ItemStack handOnItem = p.getInventory().getItemInMainHand();

        if (String.valueOf(handOnItem) == "null" || handOnItem.getType() != Material.STICK) {
//            ChangeSelectedBlock.ChangeBorderToOri(p);
            isGoldBorder = false;

        }

    }

    @EventHandler
    public void onTakeItem(PlayerPickupItemEvent e){
        Player p = e.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ItemStack handOnItem = p.getInventory().getItemInMainHand();

            if (handOnItem != null && handOnItem.getType() == Material.STICK) {
//                ChangeSelectedBlock.ChangeBorderToGold(p);
                isGoldBorder = true;

            }
        }, 4);// 1秒 = 20 ticks，这里是 0.2 秒的延迟，4 ticks
    }

    //setter
    public void setIsGoldBorder(boolean value) {
        this.isGoldBorder = value;
    }

    //getter
    public boolean getIsGoldBorder() {
        return isGoldBorder;
    }
}

