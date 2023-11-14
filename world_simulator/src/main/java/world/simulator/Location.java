package world.simulator;

public class Location
{
    public Location(int x, int y)
    {
        X = x;
        Y = y;
    }
    private int X;
    private int Y;
    public int GetX() { return X; }
    public int GetY() { return Y; }
    public void SetX(int x) { X = x; }
    public void SetY(int y) { Y = y; }
}
