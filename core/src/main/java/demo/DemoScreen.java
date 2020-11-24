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
