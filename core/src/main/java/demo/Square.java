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
