package com.mygdx.game;

import static com.mygdx.game.Constants.STAGE_HEIGHT;
import static com.mygdx.game.Constants.STAGE_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class GameOverScreen implements Screen {
    private int score;
    String line;
    String scores;
    private BitmapFont gameoverFont;
    GameClass game;
    OrthographicCamera camera;
    Preferences preferences;

    public GameOverScreen(GameClass game, int score) throws IOException {
        this.score = score;
        MyTextInputListener textInputListener = new MyTextInputListener();
        Gdx.input.getTextInput(textInputListener, "Name", "", "type here");
        preferences = Gdx.app.getPreferences("MyPrefs");
        String scores = preferences.getString("score")+String.valueOf(score)+"\n";;
        preferences.putString("score", scores);
        preferences.flush();
        this.game = game;}

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 300, 480);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        gameoverFont = new BitmapFont();
        gameoverFont.setColor(Color.BLACK);
        gameoverFont.draw(game.batch, "Game over", 110, 370);
        gameoverFont.draw(game.batch, "your score: " + score, 105, 350);
        game.batch.end();


        if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.setScreen(new Tetris(game));
        }
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
