package demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import sun.jvm.hotspot.runtime.posix.POSIXSignals;

import java.util.LinkedList;
import java.util.Stack;

public class DemoStage extends Stage {
    Demo demo;
    LinkedList<Line> lineLinkedList;
    Stack<Point> pointStack;
    LinkedList<Point> pointLinkedList;
    public float touchedX, touchedY;
    public Point touchPoint;
    public Square[][] square;
    private int clickedStatus = 0;
    protected void getClicked(int x, int y) {
        if(clickedStatus == 0) {
            pointStack.push(new Point(x, y));
            clickedStatus++;
        } else {
            drawLine(pointStack.pop(), new Point(x, y));
            pointStack.push(new Point(x, y));
            clickedStatus = 1;
        }
    }

    private void drawLine(Point a, Point b) {
        lineLinkedList.add(new Line(a, b));
        bresenham(a, b);
    }

    private void bresenham(Point a, Point b) {
        if(a.x > b.x) {
            Point temp = a;
            a = b;
            b = temp;
        }
        int dy = b.y - a.y;
        int dx = b.x - a.x;
        int p;
        float m = (float)dy / (float)dx;
        System.out.println("m = " + m);

        if(m <= 1 && m >= 0) {
            int d2y = 2 * dy;
            int d2y_2x = 2*dy - 2*dx;
            p = d2y - dx;
            for(int x = a.x + 1, y = a.y; x < b.x; ++x) {
                if(p < 0) {
                    p = p + d2y;
                } else {
                    y = y + 1;
                    p = p + d2y_2x;
                }
                square[x][y].coverTimes++;
            }
        } else if (m >= 1) {
            p = 2 * dx - dy;
            for(int y = a.y + 1, x = a.x; y < b.y; ++y) {
                if(p < 0) {
                    p = p + dx * 2;
                } else {
                    x = x + 1;
                    p = p + dx * 2 - dy * 2;
                }
                square[x][y].coverTimes++;
            }
        }
        else if (m < 0 && m >= -1){
            dy = -dy;
            p = dy * 2 - dx;
            for(int x = a.x + 1, y = a.y; x < b.x; ++x) {
                if(p < 0) {
                    p = p + dy * 2;
                } else {
                    y = y - 1;
                    p = p + dy * 2 - dx * 2;
                }
                square[x][y].coverTimes++;
            }
        } else if(m < -1) {
            dy = -dy;
            p = 2 * dx - dy;
            for(int y = a.y - 1, x = a.x; y > b.y; --y) {
                if(p < 0) {
                    p = p + dx * 2;
                } else {
                    x = x + 1;
                    p = p + dx * 2 - dy * 2;
                }
                square[x][y].coverTimes++;
            }
        }
    }

    public DemoStage(Demo demo) {
        this.demo = demo;
    }
    public void init() {
        lineLinkedList = new LinkedList<Line>();
        pointLinkedList = new LinkedList<Point>();
        pointStack = new Stack<Point>();
        square = new Square[40][30];
        for(int i = 0; i < 40; ++i) {
            for(int j = 0; j < 30; ++j) {
                square[i][j] = new Square(i * 16, j * 16, demo.squareSize, demo.squareSize);
                addActor(square[i][j]);
                square[i][j].init();
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
      /*  touchedX = Gdx.input.getX();
        touchedY = Gdx.input.getY();
        if(Gdx.input.isTouched()) {
            System.out.println("x = " + touchedX);
            System.out.println("y = " + touchedY);
        }*/
    }
}
