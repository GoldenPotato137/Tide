package cn.goldenpotato.tide.Water;

import cn.goldenpotato.tide.Config.ConfigManager;
import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.Util;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class TideSystem
{
    Map<World, List<TidePoint>> tidePoints = new HashMap<>();
    public List<TideTime> tideTime = new ArrayList<>();

    public void Load()
    {
        File file = new File(Tide.instance.getDataFolder(), "tide.yml");
        if (!file.exists())
            Tide.instance.saveResource("tide.yml", false);
        FileConfiguration reader = YamlConfiguration.loadConfiguration(file);
        if (reader.getConfigurationSection("Tide") == null)
            return;
        Util.Log("Loading tide points, please wait");
        Queue<TidePoint> toInit = new LinkedList<>();
        for (String no : Objects.requireNonNull(reader.getConfigurationSection("Tide")).getKeys(false))
        {
            ConfigurationSection in = reader.getConfigurationSection("Tide." + no);
            if (in == null) continue;
            World world = Bukkit.getWorld(Objects.requireNonNull(in.getString("world", "")));
            if (world == null) continue;
            int x = in.getInt("x", 0), y = in.getInt("y", 0), z = in.getInt("z", 0);
            if (x == 0 && y == 0 && z == 0) continue;
            List<TidePoint> list = tidePoints.computeIfAbsent(world, k -> new ArrayList<>());
            TidePoint tp = new TidePoint(new Location(world, x, y, z), y);
            list.add(tp);
            toInit.add(tp);
        }
        Util.Log(toInit.size() + " tide points loaded");

        new BukkitRunnable()
        {
            int cnt=0;
            @Override
            public void run()
            {
                if(toInit.isEmpty())
                {
                    Util.Log("Tide points Initialized");
                    cancel();
                    return;
                }
                long time = System.currentTimeMillis();
                for(int i=1;i<=200 && !toInit.isEmpty();i++)
                {
                    if(i%50==0 && System.currentTimeMillis()-time>=25)
                        break;
                    toInit.remove().Init();
                }
                if(ConfigManager.config.displayCalcInfo && cnt%5==0)
                    Util.Log(String.format("Tide Points Initializing: %d left",toInit.size()));
                cnt++;
            }
        }.runTaskTimer(Tide.instance, 10,20);
    }

    public void Save()
    {
        File file = new File(Tide.instance.getDataFolder(), "tide.yml");
        if (!file.exists())
            Tide.instance.saveResource("tide.yml", false);
        int i = 0;
        FileConfiguration writer = YamlConfiguration.loadConfiguration(file);
        for (List<TidePoint> locations : tidePoints.values())
            for (TidePoint tidePoint : locations)
            {
                ConfigurationSection out = writer.getConfigurationSection("Tide." + i);
                if (out == null) out = writer.createSection("Tide." + i);
                out.set("world", tidePoint.loc.getWorld().getName());
                out.set("x", tidePoint.loc.getBlockX());
                out.set("y", tidePoint.seaLevel);
                out.set("z", tidePoint.loc.getBlockZ());
                i++;
            }

        try
        {
            writer.save(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void Add(Location location)
    {
        List<TidePoint> list = tidePoints.computeIfAbsent(location.getWorld(), k -> new ArrayList<>());
        location.setY(location.getWorld().getSeaLevel() - 1);
        TidePoint temp = new TidePoint(location, location.getBlockY());
        temp.init = true;
        list.add(temp);
    }

    public void Add(Location location, int seaLevel)
    {
        List<TidePoint> list = tidePoints.computeIfAbsent(location.getWorld(), k -> new ArrayList<>());
        location.setX(location.getBlockX());
        location.setZ(location.getBlockZ());
        list.add(new TidePoint(location, seaLevel).Init());
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void ChangeHeight(int targetHeight, World world)
    {
        int calcRange = ConfigManager.config.calcRange, flowRange = ConfigManager.config.flowRange;
        List<TidePoint> list = tidePoints.get(world);
        for (int i=0;i<list.size();i++)
        {
            TidePoint tidePoint = list.get(i);
            if (tidePoint.init && tidePoint.loc.getNearbyPlayers(calcRange).size() != 0)
            {
                while (tidePoint.loc.getBlockY() != tidePoint.seaLevel + targetHeight)
                {
                    int delta = tidePoint.loc.getBlockY() > tidePoint.seaLevel + targetHeight ? -1 : 1;
                    Location temp = tidePoint.loc.clone();
                    if (delta > 0)
                    {
                        tidePoint.loc.setY(tidePoint.loc.getBlockY() + delta);
                        temp.setY(tidePoint.loc.getBlockY());
                        Tide.waterCalculator.Add(temp, Material.WATER, flowRange + (tidePoint.loc.getBlockY() - tidePoint.seaLevel));
                    }
                    else
                    {
                        Tide.waterCalculator.Add(temp, Material.AIR, flowRange + (tidePoint.loc.getBlockY() - tidePoint.seaLevel));
                        tidePoint.loc.setY(tidePoint.loc.getBlockY() + delta);
                    }
                }
            }
        }
    }

    public void Tick()
    {
        for (World world : tidePoints.keySet())
            for (TideTime time : tideTime)
                if (world.getTime() == time.tick)
                {
                    ChangeHeight(time.level, world);
                    break;
                }
    }

    public static class TidePopulator extends BlockPopulator
    {
        @Override
        public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk source)
        {
            int cntOcean = 0;
            for (int x = 0; x < 16; x++)
                for (int z = 0; z < 16; z++)
                    if (source.getBlock(x, 64, z).getBiome().toString().contains("OCEAN"))
                        cntOcean++;
            if (cntOcean == 0 || cntOcean == 16 * 16) return; //大洋深处或内陆
            Util.Log("Auto generate new tide point at x:" + source.getX() * 16 + " z:" + source.getZ() * 16);
            for (int x = 0; x < 16; x++)
                for (int z = 0; z < 16; z++)
                    if (source.getBlock(x, 64, z).getBiome().toString().contains("OCEAN"))
                    {
                        Tide.tideSystem.Add(new Location(world, source.getX() * 16 + x, world.getSeaLevel() - 1, source.getZ() * 16 + z));
                        return;
                    }
        }
    }

    public void SetAutoWorld()
    {
        StringBuilder result = new StringBuilder("[");
        for (World world : ConfigManager.config.worlds)
        {
            boolean OK = true;
            for (BlockPopulator bp : world.getPopulators())
                if (bp instanceof TidePopulator)
                {
                    OK = false;
                    break;
                }
            if (OK)
                world.getPopulators().add(new TidePopulator());
            result.append(world.getName()).append(" ");
        }
        result.append("]");
        Util.Log("Auto set in worlds:" + result);
    }
}
