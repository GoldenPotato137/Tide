package cn.goldenpotato.tide.Water;

import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkLocation
{
    public World world;
    public int x, z;

    public ChunkLocation(World world, int x, int z)
    {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public ChunkLocation(Chunk chunk)
    {
        this.world = chunk.getWorld();
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    public Chunk GetChunk()
    {
        return world.getChunkAt(x, z);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ChunkLocation)
        {
            ChunkLocation loc = (ChunkLocation) obj;
            return loc.world == world && loc.x == x && loc.z == z;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return world.getName().hashCode() * 114 + x * 514 + z * 1919810;
    }
}
