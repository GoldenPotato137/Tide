package cn.goldenpotato.tide.Water;

import org.bukkit.Location;
import org.bukkit.Material;

public class TidePoint
{
    Location loc;
    int seaLevel;
    boolean init;

    public TidePoint(Location loc, int seaLevel)
    {
        this.loc = loc;
        this.seaLevel = seaLevel;
        this.init = false;
    }

    public TidePoint Init()
    {
        if (init) return null;
        init = true;
        loc.setY(GetCurrentSeaLevel(loc));
        return this;
    }

    private int GetCurrentSeaLevel(Location loc)
    {
        if (loc.getBlock().getType() == Material.WATER)
            for (int y = 1; ; y++)
                if (loc.getBlock().getRelative(0, y, 0).getType() != Material.WATER)
                    return loc.getBlockY() + y - 1;
        for (int y = -1; ; y--)
            if (loc.getBlock().getRelative(0, y, 0).getType() != Material.AIR)
                return loc.getBlockY() + y;
    }
}
