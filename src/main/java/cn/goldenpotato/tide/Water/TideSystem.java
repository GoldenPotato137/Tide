package cn.goldenpotato.tide.Water;

import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.JsonUtil;
import cn.goldenpotato.tide.Util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TideSystem
{
    private static final Map<ChunkLocation, ChunkData> _chunkData = new HashMap<>();
    private static final Map<World,Integer> _seaLevel = new HashMap<>();  //每个世界的海平面高度（偏移标准海平面的量）
    private static final List<World> worlds = new ArrayList<>();
    public static List<TideTime> tideTime = new ArrayList<>();
    private static BukkitTask _tickTask;

    /**
     * 初始化
     */
    public static void Init()
    {
        _tickTask = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Tick();
            }
        }.runTaskTimer(Tide.instance, 10, 1);
    }

    /**
     * 停止潮汐计算
     */
    public static void Stop()
    {
        if(_tickTask==null || _tickTask.isCancelled()) return;
        _tickTask.cancel();
    }

    /**
     * 加载世界及其chunk数据
     */
    public static void Load(World world)
    {
        if(worlds.contains(world)) return; //已经加载过了

        Util.Log(ChatColor.GREEN + "Loading chunk data for " + world.getName());
        File worldFolder = new File(Tide.instance.getDataFolder(), world.getName());
        JsonArray array = JsonUtil.LoadJsonArray("inner-chunks.json", worldFolder);
        for (JsonElement object : array)
        {
            int chunkX = object.getAsJsonObject().get("x").getAsInt();
            int chunkZ = object.getAsJsonObject().get("z").getAsInt();
            GetChunkData(world, chunkX, chunkZ).isInner = 1;
        }
        array = JsonUtil.LoadJsonArray("water-chunks.json", worldFolder);
        for (JsonElement object : array)
        {
            int chunkX = object.getAsJsonObject().get("x").getAsInt();
            int chunkZ = object.getAsJsonObject().get("z").getAsInt();
            GetChunkData(world, chunkX, chunkZ).isInner = 0;
        }
        worlds.add(world);
        _seaLevel.put(world, 0);
    }

    /**
     * 保存所有世界的chunk数据
     */
    public static void Save()
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
            out[GetChunkData(chunk.world, chunk.x, chunk.z).isInner == 1 ? 1 : 0].add(tmp);
        }

        for (String worldStr : output.keySet())
        {
            JsonUtil.SaveJson("inner-chunks.json", new File(Tide.instance.getDataFolder(), worldStr), output.get(worldStr)[1]);
            JsonUtil.SaveJson("water-chunks.json", new File(Tide.instance.getDataFolder(), worldStr), output.get(worldStr)[0]);
        }
    }

    /***
     * 修改指定世界海平面高度
     * @param targetHeight 目标海平面高度
     * @param world 世界
     */
    public static void ChangeHeight(int targetHeight, World world)
    {
        _seaLevel.put(world, targetHeight);
        for(Chunk chunk : world.getLoadedChunks())
            WaterCalculator.AddUpdate(chunk);
    }

    /**获取指定chunk的chunk数据*/
    public static ChunkData GetChunkData(World world, int x, int z)
    {
        ChunkData ans = _chunkData.get(new ChunkLocation(world, x, z));
        if (ans == null)
        {
            ans = new ChunkData();
            _chunkData.put(new ChunkLocation(world, x, z), ans);
        }
        return ans;
    }

    /**获取指定chunk的chunk数据*/
    public static ChunkData GetChunkData(ChunkLocation chunkLocation)
    {
        return GetChunkData(chunkLocation.world, chunkLocation.x, chunkLocation.z);
    }

    /**获取指定chunk的chunk数据*/
    public static ChunkData GetChunkData(Chunk chunk)
    {
        return GetChunkData(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    private static void Tick()
    {
        for (World world : worlds)
            for (TideTime time : tideTime)
                if (world.getTime() == time.tick)
                {
                    ChangeHeight(time.level, world);
                    break;
                }
    }

    /** 获取当前海平面高度*/
    public static int SeaLevel(World world)
    {
        return _seaLevel.get(world);
    }
}
