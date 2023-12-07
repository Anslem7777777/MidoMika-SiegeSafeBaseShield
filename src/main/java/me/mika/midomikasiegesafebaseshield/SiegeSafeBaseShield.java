package me.mika.midomikasiegesafebaseshield;

import me.mika.midomikasiegesafebaseshield.Commands.*;
import me.mika.midomikasiegesafebaseshield.Tasks.ParticleTasks;
import me.mika.midomikasiegesafebaseshield.Utils.BaseHealthBar;
import me.mika.midomikasiegesafebaseshield.test.*;
import me.mika.midomikasiegesafebaseshield.Files.ConfigSetup;
import me.mika.midomikasiegesafebaseshield.Listeners.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public final class SiegeSafeBaseShield extends JavaPlugin {
    public BukkitTask repairCoolDownTask;
    public LocalDateTime currentTime;
    private LocalDateTime breakBlockTime;
    private OnBreakSelectedArea onBreakSelectedArea;
    private int repairCoolDown;
    public static Boolean areaUnderAttack = false;
    private static SiegeSafeBaseShield instance;
    public static SiegeSafeBaseShield getInstance() {

        return instance;

    }

    @Override
    public void onEnable() {

        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        instance = this;

        PluginManager pM = getServer().getPluginManager();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        ConfigSetup.setup();
        ConfigSetup.get().options().copyDefaults(true);
        ConfigSetup.save();

        pM.registerEvents(new SelectArea(this), this);
        pM.registerEvents(new OnPlayerItemHeld(this), this);
        pM.registerEvents(new OnBreakSelectedArea(this), this);
        pM.registerEvents(new OnTNTBreakSelectedArea(this), this);
        pM.registerEvents(new OnPlaceBlockInArea(this), this);
        pM.registerEvents(new OnPlayerJoinSetup(this), this);
        pM.registerEvents(new OnPlayerLeave(), this);
        pM.registerEvents(new OnPlacePiston(), this);
        pM.registerEvents(new OnEnteredOthersPlayerBase(), this);

//        getCommand("taskstop").setExecutor(new Task(this));
        getCommand("SSBS").setExecutor(new SSBSCommandsManager());
        getCommand("SSBS").setTabCompleter(new TabCompletion());

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!PlayerSelectedAreaConfig.getKeys(false).contains(p.getName())) {
                PlayerSelectedAreaConfig.set(p.getName() + "." + "Number-Of-Selected-Location" + ".areaInfo" + ".name", "VerifySlot");

            } else {

            }
        }

        try {
            PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
        } catch (IOException error) {
            System.out.println("Save File Error");
        }

        //TODO - Task 对比 config 里的 breakBlockTime 和当下的时间
        repairCoolDownTask = new BukkitRunnable() {
            @Override
            public void run() {
                File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                File playerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
                FileConfiguration playerSelectedAreaConfig = YamlConfiguration.loadConfiguration(playerSelectedAreaFile);

                currentTime = LocalDateTime.now();
                repairCoolDown = config.getInt("repairCoolDown");

                for (String mainKey : playerSelectedAreaConfig.getKeys(false)) {
                    for (String secondKey : playerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                        if (!secondKey.equals("Number-Of-Selected-Location")) {
                            LocalDateTime breakBlockTime = LocalDateTime.parse(playerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo.breakBlockTime"));

                            double totalHardness = playerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo.totalBlockHardness");
                            double repairTotalHardness = playerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo.repairTotalBlockHardness");

                            if (breakBlockTime != null) {
                                Duration duration = Duration.between(breakBlockTime, currentTime);
                                long finalTime = duration.toSeconds();

//                                System.out.println(breakBlockTime);
//                                System.out.println(mainKey);
//                                System.out.println(secondKey);
//                                System.out.println(repairCoolDown);
//
//                                System.out.println("breakBlockTime: " + breakBlockTime);
//                                System.out.println("currentTime: " + currentTime);
//                                System.out.println("final: " + finalTime);

                                if (finalTime > repairCoolDown) {

//                                    System.out.println("totalHardness: " + totalHardness);
//                                    System.out.println("repairTotalHardness: " + repairTotalHardness);

                                    if (totalHardness != repairTotalHardness) {

//                                        System.out.println("repair!!");

                                        playerSelectedAreaConfig.set(mainKey + "." + secondKey + ".areaInfo.totalBlockHardness", repairTotalHardness);
                                        try {
                                            Bukkit.getPlayer(mainKey).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Your area has been repair!"));
                                            Bukkit.getPlayer(mainKey).playSound(Bukkit.getPlayer(mainKey).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.0f);
                                            onBreakSelectedArea.playerAreaGetBreakBlockTime.clear();
                                        }catch (Exception e){

                                        }

                                        for(Player p : onBreakSelectedArea.playerBreakBlockTime.keySet()){
                                            Duration hardnessBarDuration = Duration.between(onBreakSelectedArea.playerBreakBlockTime.get(p), currentTime);
                                            long hardnessBarDurationSecond = hardnessBarDuration.toSeconds();
                                            if (hardnessBarDurationSecond > repairCoolDown) {
                                                // 创建 BaseHealthBar 实例
                                                OnBreakSelectedArea.baseHealthBar = new BaseHealthBar(instance, p);
                                                OnBreakSelectedArea.baseHealthBar.hideBossBar();
                                                OnBreakSelectedArea.playerIsBossBarCreatedMap.put(p.getUniqueId(), false);

                                            }
                                        }

                                        areaUnderAttack = false;

                                        try {
                                            playerSelectedAreaConfig.save(playerSelectedAreaFile);
                                        } catch (IOException error) {
                                            error.printStackTrace();
                                        }

                                    } else {
//                                        System.out.println("Already repair!!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        instance = null;
        for (Player p : Bukkit.getServer().getOnlinePlayers()){
            if (me.mika.midomikasiegesafebaseshield.Listeners.OnBreakSelectedArea.playerIsBossBarCreatedMap.get(p.getUniqueId()) != null){
                // 创建 BaseHealthBar 实例
                OnBreakSelectedArea.baseHealthBar = new BaseHealthBar(instance, p);
                OnBreakSelectedArea.baseHealthBar.hideBossBar();
                OnBreakSelectedArea.playerIsBossBarCreatedMap.put(p.getUniqueId(), false);
            }
        }
//        if (OnBreakSelectedArea.bossBar != null) {
//            OnBreakSelectedArea.bossBar.removeAll();
//        }
//        OnBreakSelectedArea.isBossBarCreated = false;
        SiegeSafeBaseShield.areaUnderAttack = false;

    }

    public static SiegeSafeBaseShield getPlugin(){
        return instance;

    }

    private static String removeBrackets(String input) {
        // 判断字符串是否以 "[" 开头和 "]" 结尾
        if (input.startsWith("[") && input.endsWith("]")) {
            // 使用 substring 去掉开头和结尾的方括号
            return input.substring(1, input.length() - 1);
        } else {
            return input; // 如果不以方括号包围，则返回原始字符串
        }
    }
}

