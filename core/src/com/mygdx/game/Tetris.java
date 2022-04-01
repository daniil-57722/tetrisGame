package com.mygdx.game;


import static com.mygdx.game.Constants.CELL_SIZE;
import static com.mygdx.game.Constants.STAGE_HEIGHT;
import static com.mygdx.game.Constants.STAGE_WIDTH;
import static com.mygdx.game.GameStage.NUM_COLUMNS;
import static com.mygdx.game.GameStage.NUM_ROWS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.io.IOException;

public class Tetris implements Screen {
GameClass game;
  OrthographicCamera camera;
  private boolean isGameGoing = true;
  private long lastRotateMillis;
  private long lastHorizontalMoveMillis;
  private long lastFallMillis;
  private float fallingSpeed;
  private Tetromino currentTetromino;
  private Tetromino nextTetromino;
  private Stage stage;
  private SpriteBatch batch; // отрисовщик
  private ShapeRenderer renderer; //отрисовщик для сложных фигур
  private BitmapFont scoreFont;
  private GameStage gameStage; // окно в котором падают фигуры
  private int score;
  private SoftKey lastPressedSoftKey;
  private static final int STAGE_START_X = 25;
  private static final int STAGE_START_Y = 90;
  private static final int NEXT_TETROIMINO_SIZE = 80;
  private static final int MIN_HORIZONTAL_MOVE_INTERVAL_MILLIS = 50;
  private static final int MIN_FALL_INTERVAL_MILLIS = 50;
  private static final int MIN_ROTATE_INTERVAL_MILLIS = 150;
  private static final int[] SCORES = new int[] {0, 10, 30, 80, 150};
  Music bg;
  Preferences preferences = Gdx.app.getPreferences("MyPrefs");

  Texture backToMenu;
  public Tetris(GameClass game) {
    bg = Gdx.audio.newMusic(Gdx.files.internal("bgmusic.mp3"));
    if (preferences.getBoolean("sound")){
      bg.setVolume(1);}
    else{
      bg.setVolume(0f);
    }
    bg.play();

    this.game = game;

    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);
    this.game = game;
    stage = new Stage(new FitViewport(STAGE_WIDTH, STAGE_HEIGHT));
    Gdx.input.setInputProcessor(stage);
    camera = new OrthographicCamera();
    camera.setToOrtho(false, STAGE_WIDTH, STAGE_HEIGHT);
    batch = new SpriteBatch();

    scoreFont = new BitmapFont();
    scoreFont.setColor(Color.WHITE);
    renderer = new ShapeRenderer();
    fallingSpeed = 4.5f; // blocks per seconds
    gameStage = new GameStage();
    currentTetromino = Tetromino.getInstance();
    nextTetromino = Tetromino.getInstance();

    gameStage.setPosition(STAGE_START_X, STAGE_START_Y);
    stage.addActor(gameStage);
    Group controlGroup = new Group();
    controlGroup.setPosition(60, 5);
    Texture leftArrowTexture = new Texture(Gdx.files.internal("arrow_left.png"));
    Image leftArrow = new Image(leftArrowTexture);
    Texture rightArrowTexture = new Texture(Gdx.files.internal("arrow_right.png"));
    Image rightArrow = new Image(rightArrowTexture);
    Texture circleTexture = new Texture(Gdx.files.internal("circle.png"));
    Image circle = new Image(circleTexture);
    Texture downArrowTexture = new Texture(Gdx.files.internal("arrow_down.png"));
    Image downArrow = new Image(downArrowTexture);
    backToMenu = new Texture("backToMenu.png");
    Image backBtn = new Image(backToMenu);
    registerSoftKeyPressEvent(backBtn, SoftKey.ESC);
    registerSoftKeyPressEvent(leftArrow, SoftKey.LEFT);
    registerSoftKeyPressEvent(rightArrow, SoftKey.RIGHT);
    registerSoftKeyPressEvent(circle, SoftKey.ROTATE);
    registerSoftKeyPressEvent(downArrow, SoftKey.DOWN);
    leftArrow.scaleBy(1.1f);
    rightArrow.scaleBy(1.1f);
    circle.scaleBy(1.1f);
    downArrow.scaleBy(1.1f);
    leftArrow.setPosition(0, 0);
    downArrow.setPosition(100, 0);
    rightArrow.setPosition(200, 0);
    circle.setPosition(300, 0);
    backBtn.setPosition(300,90);
    controlGroup.addActor(rightArrow);
    controlGroup.addActor(leftArrow);
    controlGroup.addActor(circle);
    controlGroup.addActor(downArrow);
    controlGroup.addActor(backBtn);
    stage.addActor(controlGroup);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {

    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    if (!isGameGoing) {
      try {
        game.setScreen(new GameOverScreen(game, score));
      } catch (IOException e) {
        e.printStackTrace();
      }
      isGameGoing = true;
      gameStage.reset();
      score = 0;
      return;
    }

    if (TimeUtils.millis() - lastFallMillis > (1 / fallingSpeed) * 1000) {
      lastFallMillis = TimeUtils.millis();
      currentTetromino.fall();
    } else if (isKeyPressed(Input.Keys.SPACE) && TimeUtils.millis() - lastRotateMillis > MIN_ROTATE_INTERVAL_MILLIS) {
      currentTetromino.rotate(gameStage);
      lastRotateMillis = TimeUtils.millis();
    }

    if (gameStage.isOnGround(currentTetromino.getBlocks())) {
      int numDeletedRows = gameStage.setBlocks(currentTetromino.getBlocks());
      score += SCORES[numDeletedRows];
      currentTetromino = nextTetromino;
      currentTetromino.initPosition();
      nextTetromino = Tetromino.getInstance();
      if (gameStage.isOnGround(currentTetromino.getBlocks())) {
        isGameGoing = false;
        return;
      }
    } else if (isKeyPressed(Input.Keys.LEFT) && TimeUtils.millis() - lastHorizontalMoveMillis > MIN_HORIZONTAL_MOVE_INTERVAL_MILLIS) {
      currentTetromino.moveToLeft(gameStage);
      lastHorizontalMoveMillis = TimeUtils.millis();
    } else if (isKeyPressed(Input.Keys.RIGHT) && TimeUtils.millis() - lastHorizontalMoveMillis > MIN_HORIZONTAL_MOVE_INTERVAL_MILLIS) {
      currentTetromino.moveToRight(gameStage);
      lastHorizontalMoveMillis = TimeUtils.millis();
    } else if (isKeyPressed(Input.Keys.DOWN) && TimeUtils.millis() - lastFallMillis > MIN_FALL_INTERVAL_MILLIS) {
      lastFallMillis = TimeUtils.millis();
      currentTetromino.fall();
    } else if (isKeyPressed(Input.Keys.ESCAPE)) {
      game.setScreen(new MainMenuScreen(game));
      bg.stop();
      gameStage.reset();
      score = 0;
    }

    camera.update();

    renderStage();
  }

  @Override
  public void resize(int width, int height) {

  }
  public static void renderBlock(ShapeRenderer renderer, int column, int row) {
    renderer.rect(STAGE_START_X + column * CELL_SIZE, STAGE_START_Y + row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
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
    stage.dispose();
    batch.dispose();
    renderer.dispose();
    scoreFont.dispose();
  }

  private boolean isKeyPressed(int hardKey) {
    if (Gdx.input.isKeyPressed(hardKey)) {
      return true;
    }
    switch (hardKey) {
      case Input.Keys.RIGHT:
        return lastPressedSoftKey == SoftKey.RIGHT;
      case Input.Keys.LEFT:
        return lastPressedSoftKey == SoftKey.LEFT;
      case Input.Keys.DOWN:
        return lastPressedSoftKey == SoftKey.DOWN;
      case Input.Keys.SPACE:
        return lastPressedSoftKey == SoftKey.ROTATE;
      case Input.Keys.ESCAPE:
        return lastPressedSoftKey == SoftKey.ESC;
    }
    return false;
  }

  private void renderStage() {
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();

    renderer.setProjectionMatrix(camera.combined);
    renderer.begin(ShapeRenderer.ShapeType.Line);
    int nextTetriminoBoxX = CELL_SIZE * NUM_COLUMNS + 2 * STAGE_START_X;
    int nextTetriminoBoxY = STAGE_START_Y + CELL_SIZE * NUM_ROWS - NEXT_TETROIMINO_SIZE;
    renderer.rect(nextTetriminoBoxX - 1, nextTetriminoBoxY - 1, NEXT_TETROIMINO_SIZE + 2, NEXT_TETROIMINO_SIZE + 2);
    renderer.end();

    renderer.begin(ShapeRenderer.ShapeType.Filled);
    currentTetromino.render(renderer);
    nextTetromino.render(renderer, nextTetriminoBoxX, nextTetriminoBoxY, NEXT_TETROIMINO_SIZE / 4);
    renderer.end();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    scoreFont.draw(batch, String.format("Score: %d", score), nextTetriminoBoxX, nextTetriminoBoxY - 30);
    batch.end();
  }

  private void registerSoftKeyPressEvent(Actor softKey, final SoftKey key) {
    softKey.addListener(new InputListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        lastPressedSoftKey = key;
        return true;
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        lastPressedSoftKey = null;
      }
    });
  }

  private enum SoftKey {
    DOWN, LEFT, RIGHT, ROTATE, ESC;
  }
}
