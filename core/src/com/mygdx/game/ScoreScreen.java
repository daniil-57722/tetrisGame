package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ScoreScreen implements Screen {
    GameClass game;
    OrthographicCamera camera;
    Texture btnBackTexture;
    String scores;
    String line;
    Preferences preferences;

    public ScoreScreen(GameClass game) {
        this.game = game;
        btnBackTexture = new Texture("backbtn.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 300, 480);
        preferences = Gdx.app.getPreferences("MyPrefs");

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        String scores = preferences.getString("score");
        String names = preferences.getString("name");
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(btnBackTexture, 50, 100, 200, 60);
        game.font.draw(game.batch, "Scoreboard", 120, 350);
        game.font.draw(game.batch, scores,170, 300);
        game.font.draw(game.batch, names, 20, 300);

        game.batch.end();
        if (Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (touchPos.x>=50&&touchPos.x<=250&&touchPos.y>=100&&touchPos.y<=160){
                game.setScreen(new MainMenuScreen(game));
            }
        }
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
