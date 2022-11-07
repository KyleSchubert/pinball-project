package com.pinball.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	Texture testImage;
	private Rectangle testRectangle;
	private Pinball pinball;
    //private final Vector2 gravityVector = new Vector2(0, -300);
	
	@Override
	public void create () { // this is where assets are usually loaded, apparently
		batch = new SpriteBatch();
		testImage = new Texture("pepe.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 750, 900);

		testRectangle = new Rectangle();
		testRectangle.x = 300;
		testRectangle.y = 500;
		testRectangle.width = 30;
		testRectangle.height = 30;

		pinball = new Pinball(10,230, 700,  15);
        //pinball.addAccelerationField(gravityVector, "gravity");
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update(); // should happen once per frame apparently

		batch.setProjectionMatrix(camera.combined); // the guide said to put this
		batch.begin();
		batch.draw(testImage, testRectangle.x, testRectangle.y);
		//batch.draw(pinball.getTexture(), pinball.getX(), pinball.getY());
		batch.end();

		testRectangle.x += 1;
		testRectangle.y -= 1;

		//pinball.advance(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		testImage.dispose();
	}
}
