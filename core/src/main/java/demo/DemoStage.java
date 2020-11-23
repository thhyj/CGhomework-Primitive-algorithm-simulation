package demo;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * 舞台类,实现了程序的主要功能
 * 屏幕被分为40 * 30的像素块
 */
public class DemoStage extends Stage {
    Demo demo;
    LinkedList<Line> lineLinkedList;
    Stack<Point> pointStack;
    LinkedList<Point> pointLinkedList;
    LinkedList<Line> visitLinkedList;
    ArrayList<Point> fillArray;
    public Point touchPoint;
    public Square[][] square;
    private int clickedStatus = 0;
    public Point lastPoint = new Point(-1, -1), firstPoint = new Point(-1, -1);
    public Line lastLine = new Line(-1,0,0,0);
    List list;
    public boolean filling = false;

    public ClearButton clearButton;
    public ScanLineButton scanLineButton;
    public SeedFillButton seedFillButton;
    public SelectConnectButton selectConnectButton;
    public ClearColorButton clearColorButton;

    /**
     * 构造函数,连接舞台和根节点
     * @param demo 程序根节点
     */
    public DemoStage(Demo demo) {
        this.demo = demo;
    }

    /**
     * 初始化函数,
     * 初始化各种变量，添加actors
     */
    public void init() {
        touchPoint = new Point(-1, -1);
        fillArray = new ArrayList<Point>();
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
        scanLineButton = new ScanLineButton(demo, 64, 480, 128, 20);
        seedFillButton = new SeedFillButton(demo, 192, 480, 128, 20);
        selectConnectButton = new SelectConnectButton(demo, 320, 480, 96, 20);
        clearColorButton = new ClearColorButton(demo, 416, 480, 96, 20);
        addActor(clearButton);
        addActor(scanLineButton);
        addActor(seedFillButton);
        addActor(selectConnectButton);
        addActor(clearColorButton);
    }
    int nowTurn;

    /**
     * 扫描线填充算法
     */
    public void scanLineFill() {
        if(lastPoint.equal(firstPoint)) {

            for(int j = 0; j < 30; ++j) {
                visitLinkedList.clear();
                pointLinkedList.clear();
                lastLine = new Line(-1,0,0,0);
                for(int i = 0; i < 40; ++i) {
                    for(Line line : square[i][j].lineLinkedList) {
                        if(!visitLinkedList.contains(line) && line.a.y != line.b.y) {

                            if(lastLine.a.x != -1 && square[i][j].isEndpoint && square[i][j].lineLinkedList.size() < 3) {
                                if((lastLine.a.y > j || lastLine.b.y > j)&& (line.b.y > j || line.a.y > j)) {
                                    if(!pointLinkedList.contains(new Point(i, j))) {
                                        pointLinkedList.add(new Point(i, j));
                                    }
                                } else {
                                    if((lastLine.a.y <= j && lastLine.b.y <= j)&& (line.b.y <= j && line.a.y <= j)
                                            && (!pointLinkedList.isEmpty())) {
                                        pointLinkedList.removeLast();
                                    }
                                }
                            } else {
                                if(!pointLinkedList.contains(new Point(i, j))) {
                                    pointLinkedList.add(new Point(i, j));
                                }
                            }

                            visitLinkedList.add(line);
                            lastLine = line;
                        }
                    }
                }
                boolean draw = false;
                int start = 0;
                for(Point point:pointLinkedList) {
                    if(start == 0)
                        start = point.x;
                    else {
                        for(int i = start; i <= point.x; ++i) {
                            fillArray.add(new Point(i, j));
                        }
                        start = 0;
                    }
                }
            }
        }
        demo.scanLine = false;
        filling = true;
        nowTurn = 0;
    }

    /**
     * 种子填充算法的递归部分
     * @param x 当前所在的x
     * @param y 当前所在的y
     */
    private void seedFillDFS(int x, int y) {
        if(x < 0 || y < 0 || x >= 40 || y >= 30) return;
        if(square[x][y].visited) return;
        if(square[x][y].coverTimes > 0) return;
        fillArray.add(new Point(x, y));
        square[x][y].visited = true;
        if(demo.eightConnect) {
            seedFillDFS(x + 1, y + 1);
            seedFillDFS(x - 1, y - 1);
            seedFillDFS(x - 1, y + 1);
            seedFillDFS(x + 1, y - 1);
        }
        seedFillDFS(x + 1, y);
        seedFillDFS(x - 1, y);
        seedFillDFS(x, y + 1);
        seedFillDFS(x, y - 1);

    }

    /**
     * 种子填充入口
     */
    public void seedFill() {
        if(!touchPoint.equal(new Point(-1, -1))) {
            seedFillDFS(touchPoint.x, touchPoint.y);
            seedFillButton.clickedStatus = false;
            demo.seedFill = false;
            nowTurn = 0;
            filling = true;
            touchPoint = new Point(-1, -1);
        }
    }

    /**
     * act函数,每一帧舞台都会执行一次这个函数
     * @param delta
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        if(filling) {
            if(nowTurn < fillArray.size()) {
                square[fillArray.get(nowTurn).x][fillArray.get(nowTurn).y].colored = true;
                ++nowTurn;
            } else {
                filling = false;
                fillArray.clear();
            }
            return;
        }
        if(demo.scanLine) {
            scanLineFill();
        } else {
            if(demo.seedFill) {
                seedFill();
            }
        }

    }

    /**
     * 当方块被点击时触发,处理点击事件
     * @param x
     * @param y
     */
    protected void getClicked(int x, int y) {
        if(!demo.seedFill) {
            square[x][y].coverTimes++;
            square[x][y].isEndpoint = true;
            if(pointStack.empty()) {
                pointStack.push(new Point(x, y));
               // clickedStatus++;
            } else {
                drawLine(pointStack.pop(), new Point(x, y));
                pointStack.push(new Point(x, y));
             //   clickedStatus = 1;
            }
        } else {
            touchPoint.x = x;
            touchPoint.y = y;
        }
    }

    /**
     * 画线入口
     * @param a 起点
     * @param b 终点
     */
    private void drawLine(Point a, Point b) {
        Line temp = new Line(a, b);
        lineLinkedList.add(temp);
        square[a.x][a.y].addLine(temp);
        square[b.x][b.y].addLine(temp);
        bresenham(temp);
    }

    /**
     * Bresenham画线算法
     * 8种情况的讨论，
     * 通过视情况交换两点减少为4个if
     * @param line
     */
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
    //    System.out.println("m = " + m);

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
