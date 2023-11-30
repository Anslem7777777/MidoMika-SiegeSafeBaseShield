package me.mika.midomikasiegesafebaseshield.Files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigSetup {

    private static File file;
    private static FileConfiguration PlayerSelectedAreaConfig;

    //Finds or generates the custom config file
    public static void setup(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException e){

            }
        }
        PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get(){

        return PlayerSelectedAreaConfig;

    }

    public static void save(){
        try {
            PlayerSelectedAreaConfig.save(file);
        }catch (IOException e){
            System.out.println("Save File Error");
        }
    }

    public static void reload(){

        PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(file);

    }
}

