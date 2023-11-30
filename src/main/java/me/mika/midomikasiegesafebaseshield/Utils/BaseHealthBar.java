package me.mika.midomikasiegesafebaseshield.Utils;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseHealthBar {

    private final SiegeSafeBaseShield plugin;
    private final Player p;
    private static Map<Player, BossBar> bossBars = new HashMap<>();

    public BaseHealthBar(SiegeSafeBaseShield plugin, Player p) {
        this.plugin = plugin;
        this.p = p;

    }

    public void showBossBar(String areaOwner) {
        if (bossBars.get(p) == null) {
            BossBar bossBar = Bukkit.createBossBar(ChatColor.RED + "Area Owner: " + ChatColor.GOLD + areaOwner, BarColor.RED, BarStyle.SOLID);
            bossBars.put(p, bossBar);
            bossBars.get(p).setVisible(true);
            bossBars.get(p).addPlayer(p);
        }
    }

    public void hideBossBar() {
        BossBar bossBar = bossBars.get(p);
        if (bossBar != null) {
            bossBars.remove(p);
            bossBar.removePlayer(p);
        }
    }

    public void updateBossBars(Double hardnessValue, Double repairHardnessValue) {
        double bossBarProgress = hardnessValue / repairHardnessValue;
        bossBars.get(p).setProgress(bossBarProgress);

    }

}
