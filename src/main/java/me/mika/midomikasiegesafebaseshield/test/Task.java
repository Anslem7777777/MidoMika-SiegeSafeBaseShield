package me.mika.midomikasiegesafebaseshield.test;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Task implements CommandExecutor {
    SiegeSafeBaseShield plugin;
    public Task(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (commandSender == player && player.hasPermission("OP")){
                plugin.repairCoolDownTask.cancel();
                player.sendMessage("Task Stop!");

        }

        return true;
    }
}
