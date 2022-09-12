package cn.goldenpotato.tide.Util;

public class Vector2
{
    public int x,z;
    public Vector2(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Vector2)
        {
            Vector2 v = (Vector2) obj;
            return v.x == x && v.z == z;
        }
        return false;
    }
}
