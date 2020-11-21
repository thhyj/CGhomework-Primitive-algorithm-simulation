package demo;

public class Line {
    public Point a, b;
    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }
    public Line(int x1, int y1, int x2, int y2) {
        a = new Point(x1, y1);
        b = new Point(x2, y2);
    }
}
