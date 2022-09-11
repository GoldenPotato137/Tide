package cn.goldenpotato.tide.Water;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ChunkData
{
    /** 四周有多少个chunk加载了*/
    public int loadedCount;

    /** 该chunk上次更新时间*/
    public int lastUpdated;

    /** 该chunk是否是内陆块,是1，不是0，尚未计算-1*/
    public int isInner;

    /**该chunk需要更新的块*/
    public List<Location> toUpdate;

    public ChunkData()
    {
        isInner = -1;
        loadedCount = 0;
        lastUpdated = 0;
        toUpdate = new ArrayList<>();
    }
}

