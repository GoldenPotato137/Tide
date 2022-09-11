package cn.goldenpotato.tide.Water;

import cn.goldenpotato.tide.Config.ConfigManager;
import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.JsonUtil;
import cn.goldenpotato.tide.Util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class TideSystem
{
    private static final Map<ChunkLocation,ChunkData> _chunkData = new HashMap<>();
    Map<World, List<TidePoint>> tidePoints = new HashMap<>();
    public List<TideTime> tideTime = new ArrayList<>();

    public static int _seaLevel;

    public static void Load(World world)
    {
        Util.Log(ChatColor.GREEN + "Loading tide points for " + world.getName());
        File worldFolder = new File(Tide.instance.getDataFolder(), world.getName());
        JsonArray array = JsonUtil.LoadJsonArray("inner-chunks.json", worldFolder);
        for (JsonElement object : array)
        {
            int chunkX = object.getAsJsonObject().get("x").getAsInt();
            int chunkZ = object.getAsJsonObject().get("z").getAsInt();
            GetChunkData(world,chunkX,chunkZ).isInner = 1;
        }
        array = JsonUtil.LoadJsonArray("water-chunks.json", worldFolder);
        for (JsonElement object : array)
        {
            int chunkX = object.getAsJsonObject().get("x").getAsInt();
            int chunkZ = object.getAsJsonObject().get("z").getAsInt();
            GetChunkData(world,chunkX,chunkZ).isInner = 0;
        }
    }

    public void Save()
    {
        Util.Log(ChatColor.GREEN + "Saving chunk data");
        Map<String, JsonArray[]> output = new HashMap<>();
        for (ChunkLocation chunk : _chunkData.keySet())
        {
            JsonArray[] out = output.get(chunk.world.getName());
            if (out == null)
            {
                out = new JsonArray[2];
                out[0] = new JsonArray();
                out[1] = new JsonArray();
                output.put(chunk.world.getName(), out);
            }
            JsonObject tmp = new JsonObject();
            tmp.addProperty("x", chunk.x);
            tmp.addProperty("z", chunk.z);
//            out[GetChunkData(chunk.world,chunk.x,chunk.z).isInner==1 ? 1 : 0].add(tmp);
        }

//        for (String worldStr : output.keySet())
//        {
//            JsonUtil.SaveJson("inner-chunks.json", new File(Tide.instance.getDataFolder(), worldStr), output.get(worldStr)[1]);
//            JsonUtil.SaveJson("water-chunks.json", new File(Tide.instance.getDataFolder(), worldStr), output.get(worldStr)[0]);
//        }
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
        for (int i = 0; i < list.size(); i++)
        {
            TidePoint tidePoint = list.get(i);
            if (tidePoint.init)
            {
                while (tidePoint.loc.getBlockY() != tidePoint.seaLevel + targetHeight)
                {
                    int delta = tidePoint.loc.getBlockY() > tidePoint.seaLevel + targetHeight ? -1 : 1;
                    Location temp = tidePoint.loc.clone();
                    if (delta > 0)
                    {
                        tidePoint.loc.setY(tidePoint.loc.getBlockY() + delta);
                        temp.setY(tidePoint.loc.getBlockY());
//                        Tide.waterCalculator.Add(temp, Material.WATER, flowRange + (tidePoint.loc.getBlockY() - tidePoint.seaLevel));
                    }
                    else
                    {
//                        Tide.waterCalculator.Add(temp, Material.AIR, flowRange + (tidePoint.loc.getBlockY() - tidePoint.seaLevel));
                        tidePoint.loc.setY(tidePoint.loc.getBlockY() + delta);
                    }
                }
            }
        }
    }

    /**
     * 获取指定chunk的chunk数据
     */
    public static ChunkData GetChunkData(World world,int x,int z)
    {
        ChunkData ans = _chunkData.get(new ChunkLocation(world,x,z));
        if(ans == null)
        {
            ans = new ChunkData();
            _chunkData.put(new ChunkLocation(world,x,z),ans);
        }
        return ans;
    }

    /**
     * 获取指定chunk的chunk数据
     */
    public static ChunkData GetChunkData(ChunkLocation chunkLocation)
    {
        return GetChunkData(chunkLocation.world,chunkLocation.x,chunkLocation.z);
    }

    public static ChunkData GetChunkData(Chunk chunk)
    {
        return GetChunkData(chunk.getWorld(),chunk.getX(),chunk.getZ());
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
//        StringBuilder result = new StringBuilder("[");
//        for (World world : ConfigManager.config.worlds)
//        {
//            boolean OK = true;
//            for (BlockPopulator bp : world.getPopulators())
//                if (bp instanceof TidePopulator)
//                {
//                    OK = false;
//                    break;
//                }
//            if (OK)
//                world.getPopulators().add(new TidePopulator());
//            result.append(world.getName()).append(" ");
//        }
//        result.append("]");
//        Util.Log("Auto set in worlds:" + result);
    }

    /**
     * 获取当前海平面高度
     * @return 当前海平面高度
     */
    public static int SeaLevel()
    {
        return _seaLevel;
    }

    /**
     * 获取上一次更新海平面的时间
     */
    public static int LastUpdate()
    {
        return _seaLevel+114514;
    }
}
