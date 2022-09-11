package cn.goldenpotato.tide.Water;

import cn.goldenpotato.tide.Config.ConfigManager;
import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Queue;

public class WaterCalculatorOld
{
    static class ToChange
    {
        Location location, center;
        Material material;
        int radius;

        public ToChange(Location location, Location center, Material material, int radius)
        {
            this.location = location;
            this.center = center;
            this.material = material;
            this.radius = radius;
        }
    }

    Queue<ToChange> queue = new LinkedList<>();
    BukkitRunnable task;

    public void Add(Location location, Material material, int radius)
    {
        Tide.doWaterFlow = false;
        Change(location, location, material, radius);
    }

    private void Change(Location location, Location center, Material material, int radius)
    {
        Material materialToChange = Material.AIR;
        if (material == Material.AIR)
            materialToChange = Material.WATER;
        if (location.getBlock().getType() == materialToChange)
        {
            location.getBlock().setType(material);
            queue.add(new ToChange(location, center, material, radius));
        }
    }

    public double GetPer(ToChange toChange)
    {
        double dis = Math.abs(toChange.center.getBlockX() - toChange.location.getBlockX()) + Math.abs(toChange.center.getBlockZ() - toChange.location.getBlockZ());
        return dis/toChange.radius*100;
    }

    public void StartCalc()
    {
        task = new BukkitRunnable()
        {
            int cnt = 0;

            @Override
            public void run()
            {
                cnt++;
                Tide.doWaterFlow = queue.isEmpty();
                if (queue.size() != 0 && cnt % 10 == 0 && ConfigManager.config.displayCalcInfo)
                    Util.Log(String.format("Calculating water flow : %.2f%%", GetPer(queue.peek())));
//                    Util.Log(String.valueOf(queue.size()));
                long startTime = System.currentTimeMillis();
                for (int i = 1; i <= 10000 && !queue.isEmpty(); i++)
                {
                    //保护TPS
                    if (i % 100 == 0 && System.currentTimeMillis() - startTime >= 25)
                        break;
                    ToChange now = queue.remove();
                    int x = now.location.getBlockX(), y = now.location.getBlockY(), z = now.location.getBlockZ();
                    int[] dx = {0, 0, 1, -1}, dz = {1, -1, 0, 0};
                    for (int j = 0; j < 4; j++)
                        if (Math.abs(now.center.getBlockX() - (x + dx[j])) + Math.abs(now.center.getBlockZ() - (z + dz[j])) <= now.radius)
                            Change(new Location(now.location.getWorld(), x + dx[j], y, z + dz[j]), now.center, now.material, now.radius);
                }
            }
        };
        task.runTaskTimer(Tide.instance, 10, 5);
    }

    public void StopCalc()
    {
        task.cancel();
    }
}
