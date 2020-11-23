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
