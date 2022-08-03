package cn.goldenpotato.tide.Listener;

import cn.goldenpotato.tide.Tide;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TickListener implements Listener
{
    @EventHandler
    public void OnTickEnd(ServerTickEndEvent e)
    {
        Tide.tideSystem.Tick();
    }
}
