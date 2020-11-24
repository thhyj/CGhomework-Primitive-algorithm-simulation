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