package cn.goldenpotato.tide.Listener;

import org.bukkit.block.data.Waterlogged;
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
        else if(e.getBlock().getBlockData() instanceof Waterlogged)
            e.setCancelled(true);
        else
        {
            switch (e.getBlock().getType())
            {
                case KELP_PLANT:
                case KELP:
                case TALL_SEAGRASS:
                case SEAGRASS:
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWaterChange(FluidLevelChangeEvent e)
    {
        if(!doWaterFlow)
            e.setCancelled(true);
    }
}
