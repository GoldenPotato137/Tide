package cn.goldenpotato.tide.Config;

import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.Util;
import cn.goldenpotato.tide.Water.TideSystem;
import cn.goldenpotato.tide.Water.TideTime;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

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

        //Worlds
        config.worlds = reader.getStringList("Worlds");
        if(reader.getStringList("AutoGenerate").size()!=0) //兼容旧版本
        {
            config.worlds = reader.getStringList("AutoGenerate");
            reader.set("AutoGenerate", null);
        }

        //CalcTime
        config.maxTimeConsume = reader.getInt("MaxTimeConsume", 10);

        reader.set("DisplayCalcInfo", null); //删除旧版本的配置
        reader.set("CalcRange", null); //删除旧版本的配置
        reader.set("FlowRange", null); //删除旧版本的配置

        //Tide
        List<TideTime> tideTime = TideSystem.tideTime;
        ConfigurationSection in = reader.getConfigurationSection("Tide");
        if (in != null)
        {
            for (String sTime : in.getKeys(false))
            {
                int time;
                try
                {
                    time = Integer.parseInt(sTime);
                } catch (NumberFormatException e)
                {
                    continue;
                }
                tideTime.add(new TideTime(time, in.getInt(sTime + ".level", 0)));
            }
        }
        Util.Log(tideTime.size() + " tidal hour loaded");

        //Debug
        config.debug = reader.getBoolean("Debug", false);

        Tide.instance.saveConfig();
    }

    static public void Save()
    {
        FileConfiguration writer = Tide.instance.getConfig();
        writer.set("Worlds", config.worlds);
        writer.set("MaxTimeConsume", config.maxTimeConsume);
        writer.set("Debug", config.debug);
        Tide.instance.saveConfig();
    }
}