package cn.goldenpotato.tide.Listener;

import cn.goldenpotato.tide.Tide;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;

public class WaterListener implements Listener
{
    @EventHandler
    public void onWaterSpread(BlockFromToEvent e)
    {
        if(!Tide.doWaterFlow)
            e.setCancelled(true);
        if(e.getBlock().getBiome().toString().contains("OCEAN"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onWaterChange(FluidLevelChangeEvent e)
    {
        if(!Tide.doWaterFlow)
            e.setCancelled(true);
    }
}
