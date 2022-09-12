package cn.goldenpotato.tide.Water;

import cn.goldenpotato.tide.Config.ConfigManager;
import cn.goldenpotato.tide.Listener.WaterListener;
import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.Util;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Queue;

public class WaterCalculator
{
    private static boolean[][][] _vis;
    private static boolean _firstUpdate;
    private static final Queue<Chunk> _updateQueue = new LinkedList<>();
    private static BukkitTask _task;

    public static void Init()
    {
        _task = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                long startTime = System.currentTimeMillis();
                WaterListener.doWaterFlow = _updateQueue.isEmpty();
                while (!_updateQueue.isEmpty())
                {
                    if (System.currentTimeMillis() - startTime > ConfigManager.config.maxTimeConsume)
                    {
//                        Util.Log("WaterCalculator: Time out" + (System.currentTimeMillis() - startTime)); //Debug用
                        return;
                    }
                    Chunk chunk = _updateQueue.poll();
                    UpdateChunk(chunk);
                }
            }
        }.runTaskTimer(Tide.instance, 20, 1);
    }

    public static void Stop()
    {
        if (_task != null && !_task.isCancelled())
            _task.cancel();
    }

    private static boolean Check(int x, int z)
    {
        return x >= 0 && x < 16 && z >= 0 && z < 16;
    }

    private static boolean IsAirBlock(Material type)
    {
        return type == Material.AIR || type == Material.GRASS || type == Material.TALL_GRASS;
    }

    private static void Calc(int tx, int ty, int tz, Chunk chunk)
    {
        Queue<Vector> queue = new LinkedList<>();
        queue.add(new Vector(tx, ty, tz));
        while (!queue.isEmpty())
        {
            Vector pos = queue.poll();
            int x = pos.getBlockX(), y = pos.getBlockY(), z = pos.getBlockZ();
            _vis[x][y][z] = true;
            Block block = chunk.getBlock(x, y, z);
            World world = chunk.getWorld();

            boolean tmpFirstUpdate = _firstUpdate;
            boolean flag = false;
            if (!_firstUpdate)
            {
                if (y >= world.getSeaLevel() + TideSystem.SeaLevel(world) && block.getType() != Material.WATER) flag=true;
                if (y < world.getSeaLevel() + TideSystem.SeaLevel(world) && block.getType() != Material.AIR) flag=true;
            }
            else if (block.getType() != Material.WATER && !IsAirBlock(block.getType()))
                flag=true;
            if (y == 0) flag=true;

            if ((tx == 0 || tx == 15 || tz == 0 || tz == 15) && (block.getType() == Material.WATER || block.getType() == Material.AIR))
                block.setType(y >= world.getSeaLevel() + TideSystem.SeaLevel(world) ? Material.AIR : Material.WATER);
            if(flag) continue;

            block.setType(y >= world.getSeaLevel() + TideSystem.SeaLevel(world) ? Material.AIR : Material.WATER);
            _firstUpdate = false;
            int[] dx = {-1, 1, 0, 0, 0, 0}, dy = {0, 0, -1, 1, 0, 0}, dz = {0, 0, 0, 0, -1, 1};
            for (int i = 0; i < 6; i++)
                if (Check(x + dx[i], z + dz[i]))
                {
                    if (!_vis[x + dx[i]][y + dy[i]][z + dz[i]])
                        queue.add(new Vector(x + dx[i], y + dy[i], z + dz[i]));
                }
                else if (dy[i] == 0 && !tmpFirstUpdate) //防止直接回头更新源头区块
                {
//                Util.Log(ChatColor.LIGHT_PURPLE+"test");
                    int tmpX = x + dx[i] == -1 ? -1 : (x + dx[i] == 16 ? 1 : 0);
                    int tmpZ = z + dz[i] == -1 ? -1 : (z + dz[i] == 16 ? 1 : 0);
                    if (Math.abs(tmpX) == 1 && Math.abs(tmpZ) == 1) continue;
                    ChunkData nearbyChunk = TideSystem.GetChunkData(world, chunk.getX() + tmpX, chunk.getZ() + tmpZ);
                    nearbyChunk.toUpdate.add(ToChunkPosition(new Location(world, x + dx[i], y + dy[i], z + dz[i])));
                }
        }
    }

    private static Location ToChunkPosition(Location pos)
    {
        Location result = pos.clone();
        result.setX((result.getBlockX() % 16 + 16) % 16);
        result.setZ((result.getBlockZ() % 16 + 16) % 16);
        return result;
    }

    /**
     * 将该chunk加入更新队列
     *
     * @param chunk 要更新的chunk
     */
    public static void AddUpdate(Chunk chunk)
    {
        _updateQueue.add(chunk);
    }

    private static void UpdateChunk(Chunk chunk)
    {
        ChunkData data = TideSystem.GetChunkData(chunk);
        if (data.loadedCount != 4) return; //如果周围有未加载的chunk，不更新
        if (data.isInner == 1 && data.toUpdate.size() == 0) return; //内陆块需要由相邻块更新

        World world = chunk.getWorld();
        _vis = new boolean[16][world.getMaxHeight()][16];
        boolean flag = false;
        for (Location loc : data.toUpdate)
        {
//            Util.Log(ChatColor.AQUA + "Updating " + loc.toString());
            if (!_vis[loc.getBlockX()][loc.getBlockY()][loc.getBlockZ()])
            {
                _firstUpdate = true;
                Calc(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), chunk);
                flag = true;
            }
        }

        boolean needUpdateNearby = false;
        if (data.isInner == 1) //内陆区块
        {
//            chunk.getBlock(0, 70, 0).setType(Material.ORANGE_WOOL); //Debug用
            needUpdateNearby = true;
            if (flag && ConfigManager.config.debug)
                Util.Log(ChatColor.YELLOW + "内陆区块更新" + chunk.getX() + " " + chunk.getZ());
        }
        else if (data.seaLevel != TideSystem.SeaLevel(world))//水源区块
        {
            if(ConfigManager.config.debug)
                Util.Log(ChatColor.GREEN + "正在计算chunk: " + chunk.getX() + " " + chunk.getZ());
//            chunk.getBlock(0, 70, 0).setType(Material.BLUE_WOOL); //Debug用
            for (int dx = 0; dx < 16; dx++)
                for (int dz = 0; dz < 16; dz++)
                    if (!_vis[dx][world.getSeaLevel() + TideSystem.SeaLevel(world)][dz] && (IsNatureWater(chunk.getBlock(dx, world.getSeaLevel(), dz).getBiome())))
                    {
                        _firstUpdate = true;
                        Calc(dx, world.getSeaLevel() + TideSystem.SeaLevel(world), dz, chunk);
                    }
            needUpdateNearby = true;
        }

        data.seaLevel = TideSystem.SeaLevel(world);
        data.toUpdate.clear();

        //更新邻接区块
        if (needUpdateNearby)
        {
            int[] dxChunk = {-1, 1, 0, 0}, dzChunk = {0, 0, -1, 1};
            for (int i = 0; i < 4; i++)
                if (world.isChunkLoaded(chunk.getX() + dxChunk[i], chunk.getZ() + dzChunk[i]))
                    AddUpdate(world.getChunkAt(chunk.getX() + dxChunk[i], chunk.getZ() + dzChunk[i]));
        }
    }

    private static boolean IsNatureWater(Biome biome)
    {
        if (biome.name().contains("OCEAN")) return true;
        else return biome == Biome.RIVER;
    }

    /**
     * 检查该chunk是否为内陆chunk
     *
     * @param chunk 要检查的chunk
     * @return 是否为内陆chunk
     */
    public static boolean IsInnerChunk(Chunk chunk)
    {
        for (int dx = 0; dx < 16; dx++)
            for (int dz = 0; dz < 16; dz++)
                if (IsNatureWater(chunk.getBlock(dx, chunk.getWorld().getSeaLevel(), dz).getBiome()))
                    return false;
        return true;
    }
}
