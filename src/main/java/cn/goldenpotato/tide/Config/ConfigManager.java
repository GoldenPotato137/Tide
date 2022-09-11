package cn.goldenpotato.tide.Config;

import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.Util;
import cn.goldenpotato.tide.Water.TideTime;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager
{
    static public Config config;
    static boolean init = false;

    static void Init()
    {
        Tide.instance.saveDefaultConfig();
        config = new Config();
        init = true;
    }

    static public void LoadConfig()
    {
        if (!init) Init();
        Tide.instance.reloadConfig();
        FileConfiguration reader = Tide.instance.getConfig();
        //Language
        config.language = reader.getString("Language", "zh-CN");
        Util.Log("Using locale: " + config.language);

        //AutoGenerate
        config.worlds = reader.getStringList("AutoGenerate");

        //Range
        config.flowRange = reader.getInt("FlowRange", 100);
        config.calcRange = reader.getInt("CalcRange", 300);
        //DisplayCalcInfo
        config.displayCalcInfo = reader.getBoolean("DisplayCalcInfo", true);
        //Tide
        List<TideTime> tideTime = Tide.tideSystem.tideTime;
        ConfigurationSection in = reader.getConfigurationSection("Tide");
        if (in != null)
        {
            for (String sTime : in.getKeys(false))
            {
                int time;
                try
                {
                    time = Integer.parseInt(sTime);
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
                tideTime.add(new TideTime(time, in.getInt(sTime + ".level", 0)));
            }
        }
        Util.Log(tideTime.size() + " tidal hour loaded");
    }

    static public void Save()
    {
        FileConfiguration writer = Tide.instance.getConfig();
        writer.set("AutoGenerate", config.worlds);
        Tide.instance.saveConfig();
    }
}