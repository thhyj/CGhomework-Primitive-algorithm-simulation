package demo;

public class Point {
    public int x, y;//, ver;
   // static int tot = 0;

    public Point(int x, int y) {
        this.x = x;
        this.y =y;
      //  ver = ++tot;
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
   /* @Override
    public boolean equals(Object obj) {
        return equal((Point) obj);
    }*/
}
