package cn.goldenpotato.tide;

import cn.goldenpotato.tide.Command.CommandManager;
import cn.goldenpotato.tide.Config.ConfigManager;
import cn.goldenpotato.tide.Config.MessageManager;
import cn.goldenpotato.tide.Listener.ChunkListener;
import cn.goldenpotato.tide.Listener.WaterListener;
import cn.goldenpotato.tide.Metrics.Metrics;
import cn.goldenpotato.tide.Water.TideSystem;
import cn.goldenpotato.tide.Water.WaterCalculator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Tide extends JavaPlugin
{
    public static Tide instance;

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        instance = this;

        //Load config
        Load();

        //Register events
        Bukkit.getPluginManager().registerEvents(new WaterListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkListener(),this);

        //Register commands
        CommandManager.Init();
        Objects.requireNonNull(Bukkit.getPluginCommand("tide")).setExecutor(new CommandManager());

        WaterCalculator.Init();
        TideSystem.Init();

        //Metrics
        int pluginId = 15943;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("locale", () -> ConfigManager.config.language));
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        WaterCalculator.Stop();
        TideSystem.Stop();
        Save();
    }

    public static void Load()
    {
        ConfigManager.LoadConfig();
        MessageManager.LoadMessage();
    }

    public static void Save()
    {
        TideSystem.Save();
        ConfigManager.Save();
    }
}
