package cn.goldenpotato.tide.Listener;

import cn.goldenpotato.tide.Tide;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;

public class WaterListener implements Listener
{
    public static boolean doWaterFlow = true;

    @EventHandler
    public void onWaterSpread(BlockFromToEvent e)
    {
        if(!doWaterFlow)
            e.setCancelled(true);
    }

    @EventHandler
    public void onWaterChange(FluidLevelChangeEvent e)
    {
        if(!doWaterFlow)
            e.setCancelled(true);
    }
}
