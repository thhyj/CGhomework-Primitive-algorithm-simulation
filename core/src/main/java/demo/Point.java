package demo;

public class Point {
    public int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y =y;

    }
    public boolean equal(Point rhs) {
        return x == rhs.x && y == rhs.y;
    }
    public Point minus(Point rhs) {
        return new  Point(x - rhs.x, y - rhs.y);
    }
    public int dotProduct(Point rhs) {
        return x * rhs.x + y * rhs.y;
    }

}
