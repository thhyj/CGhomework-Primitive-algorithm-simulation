package demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import sun.jvm.hotspot.runtime.posix.POSIXSignals;

import java.util.LinkedList;
import java.util.Stack;

public class DemoStage extends Stage {
    Demo demo;
    LinkedList<Line> lineLinkedList;
    Stack<Point> pointStack;
    LinkedList<Point> pointLinkedList;
    LinkedList<Line> visitLinkedList;
    public float touchedX, touchedY;
    public Point touchPoint;
    public Square[][] square;
    private int clickedStatus = 0;
    public Point lastPoint = new Point(-1, -1), firstPoint = new Point(-1, -1);
    public Line lastLine;
    List list;


    ClearButton clearButton;
    ScanLineButton scanLineButton;
  //  Sprite white, black, red;
    public DemoStage(Demo demo) {
        this.demo = demo;
    }
    public void init() {

        lineLinkedList = new LinkedList<Line>();
        pointLinkedList = new LinkedList<Point>();
        visitLinkedList = new LinkedList<Line>();
        pointStack = new Stack<Point>();
        square = new Square[40][30];
        for(int i = 0; i < 40; ++i) {
            for(int j = 0; j < 30; ++j) {
                square[i][j] = new Square(i * 16, j * 16, demo.squareSize, demo.squareSize);
                addActor(square[i][j]);
                square[i][j].init();
            }
        }

        clearButton = new ClearButton(demo, 0, 480, 64, 20);
        scanLineButton = new ScanLineButton(demo, 64, 480, 64, 20);
        addActor(clearButton);
        addActor(scanLineButton);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(demo.scanLine) {
            if(lastPoint.equal(firstPoint)) {

                for(int j = 0; j < 30; ++j) {
                    visitLinkedList.clear();
                    pointLinkedList.clear();
                    lastLine = new Line(-1,0,0,0);
                    for(int i = 0; i < 40; ++i) {
                        for(Line line : square[i][j].lineLinkedList) {
                            System.out.println("i = " + i + " j = " + j  +" line.a.x = " + line.a.x + " line.a.y = " + line.a.y
                                    + " line.b.x = " + line.b.x + " line.b.y = " + line.b.y );
                            if(!visitLinkedList.contains(line) && line.a.y != line.b.y) {

                                if(lastLine.a.x != -1 && square[i][j].isEndpoint && square[i][j].lineLinkedList.size() < 3) {
                                 //   Point l1 = lastLine.b.minus(lastLine.a);
                                  //  Point l2 = line.b.minus(line.a);
                                 //   System.out.println("l1.x = " + l1.x + " l1.y = " + l1.y + " l2.x = " + l2.x + " l2.y = " + l2.y);
                                    if((lastLine.a.y > j || lastLine.b.y > j)&& (line.b.y > j || line.a.y > j)) {
                                        if(!pointLinkedList.contains(new Point(i, j))) {
                                            System.out.println("Point: i = " + i + " j = " + j);
                                            pointLinkedList.add(new Point(i, j));
                                        }
                                    } else {
                                        if((lastLine.a.y <= j && lastLine.b.y <= j)&& (line.b.y <= j && line.a.y <= j)) {
                                            System.out.println("lastline.a.x = " + lastLine.a.x+ " lastLine.a.y = " + lastLine.a.y
                                                    + " lastline.b.x = " + lastLine.b.x+ " lastLine.b.y = " + lastLine.b.y +
                                                    " line.a.x = " +
                                                    line.a.x + " line.a.y = " + line.a.y
                                                    + " line.b.x = " + line.b.x + " line.b.y = " + line.b.y );
                                            pointLinkedList.removeLast();
                                        }
                                    }
                                } else {
                                    if(!pointLinkedList.contains(new Point(i, j))) {
                                        System.out.println("Point: i = " + i + " j = " + j);
                                        pointLinkedList.add(new Point(i, j));
                                    }
                                }

                                visitLinkedList.add(line);
                                lastLine = line;
                            }
                        }
                        /*if(visitLinkedList.size() % 2 == 1) {
                            square[i][j].colored = true;
                        }*/
                       /* try {
                            Thread.currentThread().sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    }
                    boolean draw = false;
                    int start = 0;
                    for(Point point:pointLinkedList) {
                        if(start == 0)
                            start = point.x;
                        else {
                            System.out.println("start = " + start + " end = " + point.x);
                            for(int i = start; i <= point.x; ++i) {
                                square[i][j].colored = true;
                            }
                            start = 0;
                        }
                    }
                }
            }
            demo.scanLine = false;
        }
    }
    protected void getClicked(int x, int y) {
        if(pointStack.empty()) {
            pointStack.push(new Point(x, y));
           // clickedStatus++;
        } else {
            drawLine(pointStack.pop(), new Point(x, y));
            pointStack.push(new Point(x, y));
         //   clickedStatus = 1;
        }
    }

    private void drawLine(Point a, Point b) {
        Line temp = new Line(a, b);
        lineLinkedList.add(temp);
        square[a.x][a.y].addLine(temp);
        square[b.x][b.y].addLine(temp);
        bresenham(temp);
    }

    private void bresenham(Line line) {
        Point a = new Point(line.a.x, line.a.y);
        Point b = new Point(line.b.x, line.b.y);
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
                square[x][y].addLine(line);
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
                square[x][y].addLine(line);
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
                square[x][y].addLine(line);
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
                square[x][y].addLine(line);
            }
        }
    }
}
