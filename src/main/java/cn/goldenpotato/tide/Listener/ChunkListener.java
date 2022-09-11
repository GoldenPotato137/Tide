package cn.goldenpotato.tide.Listener;

import cn.goldenpotato.tide.Config.ConfigManager;
import cn.goldenpotato.tide.Util.Util;
import cn.goldenpotato.tide.Water.ChunkData;
import cn.goldenpotato.tide.Water.ChunkLocation;
import cn.goldenpotato.tide.Water.TideSystem;
import cn.goldenpotato.tide.Water.WaterCalculator;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class ChunkListener implements Listener
{
    @EventHandler
    public void OnChunkLoad(ChunkLoadEvent e)
    {
        if(!ConfigManager.config.worlds.contains(e.getWorld().getName()))
            return;

//        Util.Log("加载chunk: " + e.getChunk().getX() + " " + e.getChunk().getZ());

        //计算该chunk是否是内陆块
        if(TideSystem.GetChunkData(e.getChunk()).isInner==-1)
            TideSystem.GetChunkData(e.getChunk()).isInner = WaterCalculator.IsInnerChunk(e.getChunk())?1:0;

        int[] dx={-1,1,0,0},dz={0,0,-1,1};
        for(int i=0;i<4;i++)
        {
            ChunkLocation nearbyChunkLoc = new ChunkLocation(e.getChunk().getWorld(),e.getChunk().getX()+dx[i],e.getChunk().getZ()+dz[i]);
            ChunkData nearbyChunk = TideSystem.GetChunkData(nearbyChunkLoc);
            nearbyChunk.loadedCount++;
            if(nearbyChunk.loadedCount==4)
                WaterCalculator.AddUpdate(nearbyChunkLoc.GetChunk());
        }
    }

    @EventHandler
    public void OnChunkUnload(ChunkUnloadEvent e)
    {
        if(!ConfigManager.config.worlds.contains(e.getWorld().getName()))
            return;

        Util.Log(ChatColor.RED + "卸载chunk: " + e.getChunk().getX() + " " + e.getChunk().getZ());
        int[] dx={-1,1,0,0},dz={0,0,-1,1};
        for(int i=0;i<4;i++)
        {
            ChunkLocation nearbyChunkLoc = new ChunkLocation(e.getChunk().getWorld(),e.getChunk().getX()+dx[i],e.getChunk().getZ()+dz[i]);
            ChunkData nearbyChunk = TideSystem.GetChunkData(nearbyChunkLoc);
            nearbyChunk.loadedCount--;
        }
    }

    @EventHandler
    public void OnWorldLoad(WorldLoadEvent e)
    {
        if(ConfigManager.config.worlds.contains(e.getWorld().getName()))
            TideSystem.Load(e.getWorld());
    }
}
