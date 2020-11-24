# CGhomework-Primitive-algorithm-simulation 
## 项目介绍  
使用java语言的libgdx框架,采用Stage-Actor模式编写,将屏幕分成40*30的像素块进行模拟,
实现了Bresenham画线算法，扫描线填充算法，四连接以及八连接的种子填充算法。 
## 程序逻辑 
Stage-Actor的运行逻辑为，首先在**Screen**的Render中调用**Stage**的**act**和**draw**,
从而每一帧会运行当前活跃的**Stage**及其下属**Actor**的**act**和**draw**函数  

<span id ="back"></span>
程序的入口为[**Lwjgl3Launcher.java**](#jump1)
其生成$640 * 500$大小的窗口，并调用[**Demo.java**](#jump2)  
**Demo**中设置[**DemoScreen**](#jump3)  为当前**Screen**  
**DemoScreen**中添加[**DemoStage**](#jump4)    
所有主体逻辑均在**DemoStage**及其**Actor**中实现  
首先实现了[**Point**](#jump5)   和 [**Line**](#jump6)  类以便于程序编写  
[**Square**](#jump7)  类继承**Actor**,设置了方块的基本属性和方法,之后被添加到**DemoStage**中,与**DemoStage**共同完成了程序的算法部分 
其它[**Actor**](#jump8)  类都只与用户交互有关，而与算法逻辑无关，便忽略不介绍。  
<details>
<summary>程序代码</summary>  

## 程序代码  
### <span id="jump1">Lwjgl3Launcher.java </span>  [back](#back)
```java 
package demo.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import demo.Demo;

/** Launches the desktop (LWJGL3) application.
 * */
public class Lwjgl3Launcher {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {
		return new Lwjgl3Application(new Demo(), getDefaultConfiguration());
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("DEMO");
		configuration.setWindowedMode(640, 500);
		configuration.setWindowIcon("Sanae.jpg");
		return configuration;
	}
}

```  
### <span id="jump2">Demo.java   </span> [back](#back)
```java
package demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Demo extends Game {
    public int squareSize = 16;
    public AssetManager manager;
    public DemoScreen demoScreen;
    public boolean scanLine = false;
    public boolean seedFill = false;
    public boolean eightConnect = false;
    private void loadResources() {
        manager = new AssetManager();
        manager.load("black.png", Texture.class);
        manager.load("white.png", Texture.class);
        manager.load("red.png", Texture.class);
        manager.load("yellow.png", Texture.class);

        manager.finishLoading();
    }
    @Override
    public void create() {
      //  super.create();
        loadResources();
        demoScreen = new DemoScreen(this);
        demoScreen.init();
        setScreen(demoScreen);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.graphics.setWindowedMode(width, height);
        super.resize(width, height);
    }
}  
```  
### <span id="jump3">DemoScreen.java   </span>[back](#back)
```java
package demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DemoScreen extends ScreenAdapter {
    public Demo demo;
    public DemoStage demoStage;
    public SpriteBatch batch;
    public BitmapFont font;
    public Texture texture;
    public DemoScreen(Demo demo) {
        this.demo = demo;
    }
    public void init() {
        font = new BitmapFont();
        batch= new SpriteBatch();
        demoStage = new DemoStage(demo);
        demoStage.init();
        Gdx.input.setInputProcessor(demoStage);
    }

    /**
     * render函数，每一帧都会运行，
     * 这里面会调用舞台的act
     * @param delta 距离上一帧的时间
     */
    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1.0f,1.0f,1.0f,1.0f);
        batch.begin();
        demoStage.act();
        demoStage.draw();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
      //  System.out.println("width = " + width + " height = " + height);
        demoStage.getViewport().update(width, height);
        demoStage.getCamera().update();
        super.resize(width, height);
    }
}

```  
### <span id="jump4">DemoStage.java   </span>[back](#back)

```java
package demo;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.viewport.StretchViewport;


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
        super(new StretchViewport(640, 500));
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
                                    //    System.out.println("out : Point(" + i + "," + j + ")");
                                    }
                                } else {
                                    if((lastLine.a.y <= j && lastLine.b.y <= j)&& (line.b.y <= j && line.a.y <= j)
                                            && (!pointLinkedList.isEmpty()) &&
                                            (pointLinkedList.getLast().equal( new Point(i, j)))) {
                                        pointLinkedList.removeLast();
                                     /*   System.out.println("lastline.a.y = " + lastLine.a.y +
                                                " lastline.b.y = " + lastLine.b.y +
                                                " lastline.a.x = " + lastLine.a.x +
                                                " lastline.b.x = " + lastLine.b.x +
                                                " line.a.y = " + line.a.y +
                                                " line.b.y = " + line.b.y);
                                        System.out.println("remove : Point(" + i + "," + j + ")");*/
                                    }
                                }
                            } else {
                                if(!pointLinkedList.contains(new Point(i, j))) {
                                    pointLinkedList.add(new Point(i, j));
                                //    System.out.println("out : Point(" + i + "," + j + ")");

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
                     //       System.out.println("st = " + start + " end = " + point.x);
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

```  
### <span id="jump5">Point.java     </span>[back](#back)


```java
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

```
### <span id="jump6">Line.java  </span>[back](#back)
```java
package demo;

public class Line {
    public Point a, b;
    static int tot = 0;
    int ver;
    public Line(Point a, Point b) {
        ver = ++tot;
        this.a = a;
        this.b = b;
    }
    public Line(int x1, int y1, int x2, int y2) {
        ver = ++tot;
        a = new Point(x1, y1);
        b = new Point(x2, y2);
    }
}

```  
### <span id="jump7">Square.java   </span>[back](#back)
 
```java
package demo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.LinkedList;
import java.util.Set;

/**
 * 方块类，每一个像素块作为一个actor,在demostage上act
 */
public class Square extends Actor {
    public int coverTimes = 0;
    public boolean isEndpoint = false;
    private DemoStage stage;
    private Sprite sprite;
    private Texture white, black, red, yellow;
    private BitmapFont font;
    public boolean colored = false;
    public boolean visited = false;
    public LinkedList<Line> lineLinkedList;
    public int tx, ty;
    public Square(float x, float y, float width, float height) {
        this.tx = (int)x /16;
        this.ty = (int)y / 16;
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }

    public void init() {
        lineLinkedList = new LinkedList<Line>();
        stage = (DemoStage) getStage();
        white = stage.demo.manager.get("white.png");
        black = stage.demo.manager.get("black.png");
        red = stage.demo.manager.get("red.png");
        yellow = stage.demo.manager.get("yellow.png");
        sprite = new Sprite(white);
        sprite.setX(getX());
        sprite.setY(getY());
        sprite.setSize(getWidth(), getHeight());

        font = new BitmapFont();
        setTouchable(Touchable.enabled);

        addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               // System.out.println("tx = " + tx + " ty = " + ty);
               // coverTimes++;
               // isEndpoint = true;
                stage.getClicked(tx, ty);
                stage.lastPoint = new Point(tx, ty);
                if(stage.firstPoint.equal(new Point(-1, -1)) ) {
                    stage.firstPoint = new Point(tx, ty);
                }
                super.clicked(event, x, y);
            }
        });
       // square = new Texture((FileHandle) ((DemoStage)getStage()).demo.manager.get("white.png"));
    }

    public void addLine(Line line) {
        lineLinkedList.add(line);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(coverTimes == 0) {
            if(!colored) {
                sprite.setRegion(white);
            } else {
                sprite.setRegion(yellow);
            }
        } else {
            if(!isEndpoint) {
                sprite.setRegion(black);
            } else {
                sprite.setRegion(red);
            }
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        sprite.draw(batch);


    }
}

``` 
### <span id="jump8">ClearButton.java    </span>[back](#back)

```java
package demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ClearButton extends Actor {
    BitmapFont font;
    Demo demo;
    Sprite red, black, white;
    boolean clickedStatus = false;
    ClearButton(Demo demo, int x, int y, int width, int height) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        this.demo = demo;
        font = new BitmapFont();
        red = new Sprite((Texture) demo.manager.get("red.png"));
        white = new Sprite((Texture) demo.manager.get("white.png"));
        black = new Sprite((Texture) demo.manager.get("black.png"));
        red.setSize(width, height);
        white.setSize(width, height);
        black.setSize(width, height);
        addListener( new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                clickedStatus = !clickedStatus;
                for(int i = 0; i < 40; ++i) {
                    for(int j = 0; j < 30; ++j) {
                        demo.demoScreen.demoStage.square[i][j].coverTimes = 0;
                        demo.demoScreen.demoStage.square[i][j].isEndpoint = false;
                        demo.demoScreen.demoStage.square[i][j].colored = false;
                        demo.demoScreen.demoStage.square[i][j].lineLinkedList.clear();
                    }
                }
                demo.demoScreen.demoStage.lastPoint = new Point(-1, -1);
                demo.demoScreen.demoStage.firstPoint = new Point(-1, -1);
                demo.demoScreen.demoStage.pointStack.clear();
                demo.demoScreen.demoStage.lineLinkedList.clear();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                clickedStatus = !clickedStatus;
                super.touchUp(event, x, y, pointer, button);
            }

        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(clickedStatus == false) {
            white.setX(getX());
            white.setY(getY());
            white.draw(batch);
        } else {
            red.setX(getX());
            red.setY(getY());
            red.draw(batch);

        }
        font.setColor(Color.BLACK);
        font.draw(batch, "clear", getX() + 16, getY() + getHeight());
        super.draw(batch, parentAlpha);
    }
}

```  

ClearColorButton.java  
```java
package demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ClearColorButton extends Actor {
    BitmapFont font;
    Demo demo;
    Sprite red, black, white;
    boolean clickedStatus = false;
    ClearColorButton(Demo demo, int x, int y, int width, int height) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        this.demo = demo;
        font = new BitmapFont();
        red = new Sprite((Texture) demo.manager.get("red.png"));
        white = new Sprite((Texture) demo.manager.get("white.png"));
        black = new Sprite((Texture) demo.manager.get("black.png"));
        red.setSize(width, height);
        white.setSize(width, height);
        black.setSize(width, height);
        addListener( new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                clickedStatus = !clickedStatus;
                for(int i = 0; i < 40; ++i) {
                    for(int j = 0; j < 30; ++j) {

                        demo.demoScreen.demoStage.square[i][j].colored = false;
                        demo.demoScreen.demoStage.square[i][j].visited = false;
                    }
                }
                demo.demoScreen.demoStage.lastPoint = new Point(-1, -1);
                demo.demoScreen.demoStage.firstPoint = new Point(-1, -1);
                demo.demoScreen.demoStage.pointStack.clear();
                demo.demoScreen.demoStage.lineLinkedList.clear();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                clickedStatus = !clickedStatus;
                super.touchUp(event, x, y, pointer, button);
            }

        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(clickedStatus == false) {
            white.setX(getX());
            white.setY(getY());
            white.draw(batch);
        } else {
            red.setX(getX());
            red.setY(getY());
            red.draw(batch);

        }
        font.setColor(Color.BLACK);
        font.draw(batch, "clearcolor", getX() + 16, getY() + getHeight());
        super.draw(batch, parentAlpha);
    }
}

```  
ScanLineButton.java  
```java
package demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ScanLineButton extends Actor {
    BitmapFont font;
    Demo demo;
    Sprite red, black, white;
    boolean clickedStatus = false;
    ScanLineButton(Demo demo, int x, int y, int width, int height) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        this.demo = demo;
        font = new BitmapFont();
        red = new Sprite((Texture) demo.manager.get("red.png"));
        white = new Sprite((Texture) demo.manager.get("white.png"));
        black = new Sprite((Texture) demo.manager.get("black.png"));
        red.setSize(width, height);
        white.setSize(width, height);
        black.setSize(width, height);
        addListener( new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                clickedStatus = !clickedStatus;
                demo.scanLine = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                clickedStatus = !clickedStatus;
                super.touchUp(event, x, y, pointer, button);
            }

        });
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(clickedStatus == false) {
            white.setX(getX());
            white.setY(getY());
            white.draw(batch);
        } else {
            red.setX(getX());
            red.setY(getY());
            red.draw(batch);

        }
        font.setColor(Color.BLACK);
        font.draw(batch, "ScanLine", getX() + 16, getY() + getHeight());
        super.draw(batch, parentAlpha);
    }
}

```  
SeedFillButton.java  
```java
package demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SeedFillButton extends Actor {
    BitmapFont font;
    Demo demo;
    Sprite red, black, white;
    boolean clickedStatus = false;
    SeedFillButton(Demo demo, int x, int y, int width, int height) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        this.demo = demo;
        font = new BitmapFont();
        red = new Sprite((Texture) demo.manager.get("red.png"));
        white = new Sprite((Texture) demo.manager.get("white.png"));
        black = new Sprite((Texture) demo.manager.get("black.png"));
        red.setSize(width, height);
        white.setSize(width, height);
        black.setSize(width, height);
        addListener( new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                clickedStatus = !clickedStatus;
                demo.seedFill = !demo.seedFill;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
              //  clickedStatus = !clickedStatus;
                super.touchUp(event, x, y, pointer, button);
            }

        });
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(clickedStatus == false) {
            white.setX(getX());
            white.setY(getY());
            white.draw(batch);
        } else {
            red.setX(getX());
            red.setY(getY());
            red.draw(batch);

        }
        font.setColor(Color.BLACK);
        font.draw(batch, "SeedFill", getX() + 16, getY() + getHeight());
        super.draw(batch, parentAlpha);
    }
}

```  
SelectConnectButton
```java
package demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SelectConnectButton extends Actor {
        BitmapFont font;
        Demo demo;
        Sprite red, black, white;
        boolean clickedStatus = false;
        SelectConnectButton(Demo demo, int x, int y, int width, int height) {
            setX(x);
            setY(y);
            setWidth(width);
            setHeight(height);
            this.demo = demo;
            font = new BitmapFont();
            red = new Sprite((Texture) demo.manager.get("red.png"));
            white = new Sprite((Texture) demo.manager.get("white.png"));
            black = new Sprite((Texture) demo.manager.get("black.png"));
            red.setSize(width, height);
            white.setSize(width, height);
            black.setSize(width, height);
            addListener( new ClickListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    clickedStatus = !clickedStatus;
                    demo.eightConnect = !demo.eightConnect;
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                }

            });
        }
        @Override
        public void draw(Batch batch, float parentAlpha) {

            if (!clickedStatus) {
                white.setX(getX());
                white.setY(getY());
                white.draw(batch);
            } else {
                red.setX(getX());
                red.setY(getY());
                red.draw(batch);

            }
            font.setColor(Color.BLACK);
            font.draw(batch, "8Connect", getX() + 16, getY() + getHeight());
            super.draw(batch, parentAlpha);
        }

}

```

</details>