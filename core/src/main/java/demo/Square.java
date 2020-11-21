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

public class Square extends Actor {
    public int coverTimes = 0;
    public boolean isEndpoint = false;
    private DemoStage stage;
    private Sprite sprite;
    private Texture white, black, red;
    private BitmapFont font;

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
        stage = (DemoStage) getStage();
        white = stage.demo.manager.get("white.png");
        black = stage.demo.manager.get("black.png");
        red = stage.demo.manager.get("red.png");
        sprite = new Sprite(white);
        sprite.setX(getX());
        sprite.setY(getY());
        sprite.setSize(getWidth(), getHeight());

        font = new BitmapFont();
        setTouchable(Touchable.enabled);
       /* addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("x = " + x + " y = " + y);
                coverTimes++;
                event.handle();//the Stage will stop trying to handle this event
                return true; //the inputmultiplexer will stop trying to handle this touch
            }
        });*/
        addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("tx = " + tx + " ty = " + ty);
                coverTimes++;
                isEndpoint = true;
                stage.getClicked(tx, ty);
                super.clicked(event, x, y);
            }
        });
       // square = new Texture((FileHandle) ((DemoStage)getStage()).demo.manager.get("white.png"));
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        if(coverTimes == 0) {
            sprite.setRegion(white);
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
   //     batch.draw(square, getX(), getY(), getWidth(), getHeight());
        //font.draw(batch, "x = " + tx + " y = " + ty, getX(), getY());

    }
}
