package com.example.mohammadrezaee.dotnboxes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class GameView extends View {
  private static class ScreenPosition {
    public int x;
    public int y;

    public ScreenPosition(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  private static class Diff {
    public Point point;
    public Float diff;

    public Diff(Point point, float diff) {
      this.point = point;
      this.diff = diff;
    }
  }

  private static class Point {
    public int i;
    public int j;

    public Point(int i, int j) {
      this.i = i;
      this.j = j;
    }
  }

  private static class Box {
    public int i;
    public int j;
    public int playerIndex;

    public Box(int i, int j) {
      this.i = i;
      this.j = j;
    }
  }

  private static class Action {
    public Move move;
    public int playerindex;

    public Action(Move move, int playerindex) {
      this.move = move;
      this.playerindex = playerindex;
    }
  }

  private static class Move {
    public int i1;
    public int j1;
    public int i2;
    public int j2;

    public Move(int i1, int j1, int i2, int j2) {
      this.i1 = i1;
      this.j1 = j1;
      this.i2 = i2;
      this.j2 = j2;
    }
  }
  public State state = new State();
  public Options options = new Options();

  private static class Theme {
    private static final int[] PLAYER_COLORS = new int[]{Color.BLUE, Color.RED};
    private static final int SPACE_BETWEEN_DOTS = 100;
    private static final int DOT_RADIUS = 10;
    private static final int BACKGROUND_COLOR = Color.parseColor("#222222");
  }

  private  class State {
    private  ArrayList<Action> actions = new ArrayList<>();

    private int playerIndex = 1;
    private  int[] playerScors = new int[]{0, 0};
    private  boolean GameOver = false;

    private  ArrayList<Box> boxes = new ArrayList<>();
  }

  private  class Options {
    private  final int TYPE_PLAYER = 0;
    private  final int TYPE_CPU = 1;

    private  int cols;
    private  int rows;

    private  int[] playerType = new int[]{TYPE_PLAYER, TYPE_PLAYER};
    public  boolean highPerformance = Settings.isHighPerformance();
  }


  private Paint dotPaint;
  private Paint scoorBorderPaint;
  private Paint scoorEffectPaint;
  private Paint textPaint;
  private Paint linePaint;
  private Paint boxPaint;

  private int offsetX;
  private int offsetY;

  private int widthBox;
  private int heightBox;
  private int screenWidth;
  private int screenWidthHalf;
  private int screenHeight;

  private float touchX;
  private float touchY;

  private static final int EDGE_LEFT = 0;
  private static final int EDGE_RIGHT = 1;
  private static final int EDGE_TOP = 2;
  private static final int EDGE_BOTTOM = 3;

  ArrayList<Move> availableMoves = new ArrayList<>();

  private static int[] hepticRadius = new int[]{10,20};
  private static boolean isLockForRendering = false;
  private static float drawingAlpha = 0;

  private Bitmap bluePencil;
  private Bitmap redPencil;

  public GameView(Context context) {
    super(context);
    initilize();
  }

  public GameView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initilize();
  }

  public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initilize();
  }

  private void initilizePaints() {
    dotPaint = new Paint();
    dotPaint.setAntiAlias(true);
    dotPaint.setColor(Color.WHITE);
    dotPaint.setStyle(Paint.Style.FILL);

    scoorBorderPaint = new Paint();
    scoorBorderPaint.setAntiAlias(true);
    scoorBorderPaint.setColor(Color.WHITE);
    scoorBorderPaint.setStyle(Paint.Style.STROKE);
    scoorBorderPaint.setStrokeWidth(5);

    scoorEffectPaint = new Paint();
    scoorEffectPaint.setAntiAlias(true);
    scoorEffectPaint.setColor(Color.WHITE);
    scoorEffectPaint.setStyle(Paint.Style.STROKE);
    scoorEffectPaint.setStrokeWidth(5);

    boxPaint = new Paint();
    boxPaint.setAntiAlias(true);
    boxPaint.setColor(Color.WHITE);
    boxPaint.setStyle(Paint.Style.FILL);


    linePaint = new Paint();
    linePaint.setAntiAlias(true);
    linePaint.setColor(Color.BLUE);
    linePaint.setStrokeWidth(10);
    linePaint.setStyle(Paint.Style.FILL);

    textPaint = new Paint();
    textPaint.setAntiAlias(true);
    textPaint.setColor(Color.WHITE);
    textPaint.setStyle(Paint.Style.FILL);
    textPaint.setTextAlign(Paint.Align.CENTER);
    textPaint.setTextSize(20);
  }

  public void initilizeMetrics() {
    widthBox = (options.cols - 1) * Theme.SPACE_BETWEEN_DOTS;
    heightBox = (options.rows - 1) * Theme.SPACE_BETWEEN_DOTS;

    screenWidth = G.dp.widthPixels;
    screenHeight = G.dp.heightPixels;

    screenWidthHalf = screenWidth / 2;

    offsetX = (screenWidth - widthBox) / 2;
    offsetY = (screenHeight - heightBox) / 2;
  }

  public void initilize() {
    if (!options.highPerformance){
      initilizeBitmaps();
      mainLoop();
    }
    initilizePaints();
  }

  private void initilizeBitmaps(){
    bluePencil = BitmapFactory.decodeResource(G.context.getResources(),R.drawable.blue_pencil);
    redPencil = BitmapFactory.decodeResource(G.context.getResources(),R.drawable.red_pencil);
  }

  private void refresh() {
    if (isGameFinished()) {
      state.GameOver = true;
    }
    invalidate();
  }

  public void saveGame() {
    JSONObject save = new JSONObject();
    JSONObject optionsjson = new JSONObject();
    JSONObject statejson = new JSONObject();
    JSONArray actionsjson = new JSONArray();
    try {
      optionsjson.put("cols", options.cols);
      optionsjson.put("rows", options.rows);
      optionsjson.put("oponnentType", getPlayerType(2));
      save.put("options", optionsjson);

      statejson.put("playerIndex", state.playerIndex);
      statejson.put("actons", actionsjson);

      for (Action action : state.actions) {
        JSONObject jsonAction = new JSONObject();
        jsonAction.put("i1", action.move.i1);
        jsonAction.put("j1", action.move.j1);
        jsonAction.put("i2", action.move.i2);
        jsonAction.put("j2", action.move.j2);
        jsonAction.put("playerIndex", action.playerindex);

        actionsjson.put(jsonAction);
      }
      save.put("state", statejson);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    String savedGame = save.toString();

    File file = new File(G.APP_DIR + "/save.dat");

    try {
      file.createNewFile();
      FileOutputStream fOut = new FileOutputStream(file);
      OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
      myOutWriter.append(savedGame);

      myOutWriter.close();

      fOut.flush();
      fOut.close();
    } catch (IOException e) {
      Log.e("Exception", "File write failed: " + e.toString());
    }
  }

  public void loadGame() {
    File file = new File(G.APP_DIR + "/save.dat");
    StringBuilder savedGameBuilder = new StringBuilder();

    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;

      while ((line = br.readLine()) != null) {
        savedGameBuilder.append(line);
        savedGameBuilder.append('\n');
      }
      br.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    String savedGame = savedGameBuilder.toString();
    try {
      JSONObject save = new JSONObject(savedGame);
      JSONObject option = save.getJSONObject("options");

      options.cols = option.getInt("cols");
      options.rows = option.getInt("rows");
      options.playerType[1] = option.getInt("oponnentType");
      //options.playerType[1] = oponnentType;
      resetGame();

      JSONObject statejson = save.getJSONObject("state");
      state.playerIndex = statejson.getInt("playerIndex");

      JSONArray actionsjson = statejson.getJSONArray("actons");
      for (int i = 0; i < actionsjson.length(); i++) {
        JSONObject jsonObject = actionsjson.getJSONObject(i);
        state.actions.add(new Action(
          new Move(jsonObject.getInt("i1"),
            jsonObject.getInt("j1"),
            jsonObject.getInt("i2"),
            jsonObject.getInt("j2")
          ),
          jsonObject.getInt("playerIndex")
        ));
      }
      populateAvailableMoves();
      invalidate();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private boolean isGameFinished() {
    return state.boxes.size() == (options.cols - 1) * (options.rows - 1);
  }

  private boolean isCpuTurn() {
    return getPlayerType(state.playerIndex) == options.TYPE_CPU;
  }

  public void resetGame(){
    resetGame(false);
  }
  public void resetGame(boolean isMultiPlayer) {
    if (isMultiPlayer){
      options.playerType[1] = options.TYPE_PLAYER;
    }else {
      options.playerType[1] = options.TYPE_CPU;
    }

    options.cols = Settings.getCols();
    options.rows = Settings.getRows();

    initilizeMetrics();

    state.playerScors[0] = 0;
    state.playerScors[1] = 0;
    state.playerIndex = 1;
    state.GameOver = false;

    state.actions.clear();
    state.boxes.clear();
    populateAvailableMoves();
    if (isCpuTurn()) {
      playNext();
    }
    refresh();
  }

  private void mainLoop(){
    Thread main = new Thread(new Runnable() {
      @Override
      public void run() {
        long physicLastTime = System.currentTimeMillis();
        long renderLastTime = System.currentTimeMillis();

        while (true){

          long physicElapsedTime = System.currentTimeMillis() - physicLastTime;
          if (physicElapsedTime > 20){
            updatePhysic(physicElapsedTime);
            physicLastTime = System.currentTimeMillis();
          }

          long renderElapsedTime = System.currentTimeMillis() - renderLastTime;
          if (renderElapsedTime > 15){
            renderGame();
            renderLastTime = System.currentTimeMillis();
          }

          try {
            Thread.sleep(5);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
    main.start();
  }

  private void updatePhysic(long elapsed){
    for (int i = 0; i< hepticRadius.length; i++){
      hepticRadius[i] += elapsed * 0.05f;
      if (hepticRadius[i] > 80 ){
        hepticRadius[i] = 50;
      }
    }

    drawingAlpha += elapsed * 0.003f;
    if (drawingAlpha >= 1 ){
      isLockForRendering = false;
      drawingAlpha = 0;
    }

  }
  private void renderGame(){
    postInvalidate();
  }
  private void addToAvailbleMove(int i1,int j1,int i2,int j2){
    boolean isInAction = false;
    for (Action action : state.actions){
      if (action.move.i1 == i1 && action.move.j1 == j1 && action.move.i2 == i2 && action.move.j2 == j2){
        isInAction = true;
        break;
      }
    }
    if (!isInAction){
      availableMoves.add(new Move(i1, j1, i2, j2));
    }
  }
  private void populateAvailableMoves() {
    availableMoves.clear();
    for (int i = 0; i < options.cols - 1; i++) {
      for (int j = 0; j < options.rows; j++) {
        int i1 = i;
        int j1 = j;
        int i2 = i + 1;
        int j2 = j;
        addToAvailbleMove(i1,j1,i2,j2);
      }
    }
    for (int i = 0; i < options.cols; i++) {
      for (int j = 0; j < options.rows - 1; j++) {
        int i1 = i;
        int j1 = j;
        int i2 = i;
        int j2 = j + 1;
        addToAvailbleMove(i1,j1,i2,j2);
      }
    }
  }

  private void drawBackground(Canvas canvas) {
    canvas.drawColor(Theme.BACKGROUND_COLOR);
  }

  private void drawConnectedLines(Canvas canvas) {
    if (!options.highPerformance && isLockForRendering){
      for (int i=0; i< state.actions.size() - 1; i++){
        Action action = state.actions.get(i);
        drawLine(canvas,action);
      }
      drawAnimatedLastAction(canvas);
    }else {
      for (Action line : state.actions) {
        drawLine(canvas, line);
      }
    }

  }

  private ScreenPosition[] getLastLinePosition(){
    Action action = state.actions.get(state.actions.size() - 1);
    ScreenPosition p1 = getPointPosition(action.move.i1, action.move.j1);
    ScreenPosition p2 = getPointPosition(action.move.i2, action.move.j2);
    linePaint.setColor(getPlayerColor(action.playerindex));

    ScreenPosition[] ouput = new ScreenPosition[2];

    if (action.move.i1 == action.move.i2){
      //vertical
      int position = (int)(p1.y - Theme.SPACE_BETWEEN_DOTS * drawingAlpha);
      ouput[0] = new ScreenPosition(p1.x,p1.y);
      ouput[1] = new ScreenPosition(p2.x,position);
      return ouput;
    }else {
      //horizental
      int position = (int)(p1.x + Theme.SPACE_BETWEEN_DOTS * drawingAlpha);
      ouput[0] = new ScreenPosition(p1.x,p1.y);
      ouput[1] = new ScreenPosition(position,p2.y);
      return ouput;
    }
  }
  private void drawAnimatedLastAction(Canvas canvas){
    ScreenPosition[] positions = getLastLinePosition();
    canvas.drawLine(positions[0].x,positions[0].y,positions[1].x,positions[1].y,linePaint);
  }
  private void drawAnimatedLastActionPencil(Canvas canvas){
    if (!options.highPerformance && isLockForRendering) {
      Action action = state.actions.get(state.actions.size() - 1);
      int playerIndex = action.playerindex;
      ScreenPosition[] positions = getLastLinePosition();
      drawPencil(canvas, playerIndex, positions[1].x, positions[1].y);
    }
  }
  private void drawBox(Canvas canvas) {
    for (Box box : state.boxes) {
      boxPaint.setColor(getPlayerColor(box.playerIndex));
      ScreenPosition boxPos = getPointPosition(box.i, box.j);
      canvas.drawCircle(boxPos.x + (Theme.SPACE_BETWEEN_DOTS / 2), boxPos.y - (Theme.SPACE_BETWEEN_DOTS / 2), 20, boxPaint);
    }
  }

  private void drawDots(Canvas canvas) {
    for (int i = 0; i < options.cols; i++) {
      for (int j = 0; j < options.rows; j++) {
        ScreenPosition point = getPointPosition(i, j);
        canvas.drawCircle(point.x, point.y, Theme.DOT_RADIUS, dotPaint);
      }
    }
  }

  private void drawPlayerScor(Canvas canvas, int playerIndex, int x, int y) {
    boxPaint.setColor(getPlayerColor(playerIndex));
    canvas.drawCircle(x, 100, 40, boxPaint);
    canvas.drawText("" + getPlayerScor(playerIndex), x, y + 10, textPaint);
    canvas.drawText("PLAYER " + playerIndex, x, y + 70, textPaint);

    if (playerIndex == state.playerIndex) {
      if (!options.highPerformance){
        drawPlayerIndexEffect(canvas,x,y);
      }else {
        scoorBorderPaint.setColor(Color.WHITE);
        canvas.drawCircle(x, 100, 45, scoorBorderPaint);
      }
    }else{
      scoorBorderPaint.setColor(Color.parseColor("#404141"));
      canvas.drawCircle(x, 100, 45, scoorBorderPaint);
    }
  }

  private void drawScors(Canvas canvas) {
    drawPlayerScor(canvas, 1, screenWidthHalf - 100, 100);
    drawPlayerScor(canvas, 2, screenWidthHalf + 100, 100);
  }

  private void drawFinishMessage(Canvas canvas) {
    if (state.GameOver) {
      if (getPlayerScor(1) > getPlayerScor(2)) {
        canvas.drawText("PLAYER 1 WON", screenWidthHalf, 200, textPaint);
      } else {
        canvas.drawText("PLAYER 2 WON", screenWidthHalf, 200, textPaint);
      }
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    drawBackground(canvas);
    drawConnectedLines(canvas);
    drawBox(canvas);
    drawDots(canvas);
    drawScors(canvas);
    drawAnimatedLastActionPencil(canvas);
    drawFinishMessage(canvas);
  }



  private void drawPencil(Canvas canvas,int playerIndex,int x,int y){
    float scale = 0.25f;

    Bitmap bitmap;
    if (playerIndex == 1){
      bitmap = bluePencil;
    }else {
      bitmap = redPencil;
    }

    x -= (int) (bitmap.getWidth() * scale);

    Rect srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
    RectF desRect = new RectF(x,y, x + bitmap.getWidth() * scale,y + bitmap.getHeight() * scale );

    canvas.drawBitmap(bitmap,srcRect,desRect,dotPaint);
  }
  private void drawPlayerIndexEffect(Canvas canvas,int x,int y){
    for (int i = 0; i< hepticRadius.length; i++) {
      float alpha = 2.5f * (100 - hepticRadius[i] - 10);
      scoorEffectPaint.setAlpha((int) alpha);
      canvas.drawCircle(x, y, hepticRadius[i], scoorEffectPaint);
    }
  }
  private int getPlayerColor(int playerindex) {
    return Theme.PLAYER_COLORS[playerindex - 1];
  }

  private int getPlayerScor(int playerIndex) {
    return state.playerScors[playerIndex - 1];
  }

  private int getPlayerType(int playerIndex) {
    return options.playerType[playerIndex - 1];
  }

  private void increasePlayerScor(int playerIndex) {
    state.playerScors[playerIndex - 1]++;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (state.GameOver) {
      return true;
    }
    if (isCpuTurn()) {
      return true;
    }
    if (isLockForRendering){
      return true;
    }

    touchX = event.getX();
    touchY = event.getY();
    ArrayList<Diff> diffs = getDiffsByOrder();

    Diff diff1 = diffs.get(0);
    Diff diff2 = diffs.get(1);

    if (!options.highPerformance){
      isLockForRendering = true;
      drawingAlpha = 0;
    }
    connectLine(diff1.point, diff2.point);
    return super.onTouchEvent(event);
  }

  private ArrayList<Diff> getDiffsByOrder() {
    ArrayList<Diff> diffs = new ArrayList<>();
    for (int i = 0; i < options.cols; i++) {
      for (int j = 0; j < options.rows; j++) {
        ScreenPosition position = getPointPosition(i, j);
        float diff = computeDiff(touchX, touchY, position.x, position.y);
        diffs.add(new Diff(new Point(i, j), diff));
      }
    }

    Collections.sort(diffs, new Comparator<Diff>() {
      @Override
      public int compare(Diff o1, Diff o2) {
        return o1.diff.compareTo(o2.diff);
      }
    });
    return diffs;
  }

  private boolean connectLine(Point point1, Point point2) {

    Point firstPoint;
    Point secondPoint;

    Box box1 = null;
    Box box2 = null;
    if (point1.i == point2.i) {
      //vertical
      if (point1.j < point2.j) {
        firstPoint = point1;
        secondPoint = point2;
      } else {
        firstPoint = point2;
        secondPoint = point1;
      }

      if (firstPoint.i < options.cols - 1) {
        box1 = new Box(firstPoint.i, firstPoint.j);
      }

      if (firstPoint.i - 1 >= 0) {
        box2 = new Box(firstPoint.i - 1, firstPoint.j);
      }
    } else {
      //horizental
      if (point1.i < point2.i) {
        firstPoint = point1;
        secondPoint = point2;
      } else {
        firstPoint = point2;
        secondPoint = point1;
      }

      if (firstPoint.j < options.rows - 1) {
        box1 = new Box(firstPoint.i, firstPoint.j);
      }
      if (firstPoint.j - 1 >= 0) {
        box2 = new Box(firstPoint.i, firstPoint.j - 1);
      }
    }

    //if this line is already connected
    for (Action line : state.actions) {
      if (line.move.i1 == firstPoint.i && line.move.j1 == firstPoint.j && line.move.i2 == secondPoint.i && line.move.j2 == secondPoint.j) {
        return false;
      }
    }

    Action line = new Action(new Move(firstPoint.i, firstPoint.j, secondPoint.i, secondPoint.j), state.playerIndex);
    state.actions.add(line);

    if (!options.highPerformance){
      isLockForRendering = true;
      drawingAlpha = 0;
    }

    for (int index = availableMoves.size() - 1; index >= 0; index--) {
      Move move = availableMoves.get(index);
      if (move.i1 == line.move.i1 && move.j1 == line.move.j1 && move.i2 == line.move.i2 && move.j2 == line.move.j2) {
        availableMoves.remove(move);
      }
    }

    boolean wonBox1 = false;
    if (box1 != null) {
      wonBox1 = wonBox1 || checkBox(box1);
    }
    if (box2 != null) {
      if (wonBox1) {
        checkBox(box2);
      }
      wonBox1 = wonBox1 || checkBox(box2);
    }
    if (!wonBox1) {
      switchSide();
      return true;
    }

    playNext();
    return true;
  }

  private void switchSide() {
    if (state.playerIndex == 1){
      state.playerIndex = 2;
    }else {
      state.playerIndex = 1;
    }
    playNext();
  }

  private void playNext() {
    if (isCpuTurn()) {
      G.handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          if (!options.highPerformance){
            isLockForRendering = true;
            drawingAlpha = 0;
          }
          ai();
          refresh();
        }
      }, 100);
    }
  }

  private int getRandom(int min, int max) {
    return (int) Math.floor(Math.random() * (max - min + 1)) + min;
  }

  private boolean fill3SidesBoxes() {
    for (int i = 0; i <= options.cols - 2; i++) {
      for (int j = 0; j <= options.rows - 2; j++) {
        int sides = 0;
        int freeSide = -1;
        if (hasBottom(i, j)) {
          sides++;
        } else {
          freeSide = EDGE_BOTTOM;
        }
        if (hasTop(i, j)) {
          sides++;
        } else {
          freeSide = EDGE_TOP;
        }
        if (hasRight(i, j)) {
          sides++;
        } else {
          freeSide = EDGE_RIGHT;
        }
        if (hasLeft(i, j)) {
          sides++;
        } else {
          freeSide = EDGE_LEFT;
        }

        if (sides == 3) {
          switch (freeSide) {
            case EDGE_BOTTOM:
              connectBottom(i, j);
              return true;
            case EDGE_TOP:
              connectTop(i, j);
              return true;
            case EDGE_RIGHT:
              connectRight(i, j);
              return true;
            case EDGE_LEFT:
              connectLeft(i, j);
              return true;
          }
        }
      }
    }
    return false;
  }

  private boolean makeRandomMove() {
    int moveIndex = getRandom(0, availableMoves.size() - 1);
    Move move = availableMoves.get(moveIndex);
    connectLine(new Point(move.i1, move.j1), new Point(move.i2, move.j2));

    return true;
  }

  private boolean makeRandomSafeMove(ArrayList<Move> unsafeMoves) {
    ArrayList<Move> safeMoves = new ArrayList<>();

    for (Move move : availableMoves) {
      boolean isSafeMove = true;
      for (Move testMove : unsafeMoves) {
        if (testMove.i1 == move.i1 && testMove.i2 == move.i2 && testMove.j1 == move.j1 && testMove.j2 == move.j2) {
          isSafeMove = false;
          break;
        }
      }

      if (isSafeMove) {
        safeMoves.add(move);
      }
    }

    if (safeMoves.size() == 0) {
      return false;
    }

    int moveIndex = getRandom(0, safeMoves.size() - 1);
    Move move = safeMoves.get(moveIndex);

    connectLine(new Point(move.i1, move.j1), new Point(move.i2, move.j2));
    return true;
  }

  private ArrayList<Move> detectUnsafeMoves() {
    ArrayList<Move> unsafeMoves = new ArrayList<>();

    for (int i = 0; i <= options.cols - 2; i++) {
      for (int j = 0; j <= options.rows - 2; j++) {
        ArrayList<Integer> freeSides = new ArrayList<>();

        if (hasLeft(i, j)) {
          freeSides.add(EDGE_LEFT);
        }

        if (hasRight(i, j)) {
          freeSides.add(EDGE_RIGHT);
        }

        if (hasTop(i, j)) {
          freeSides.add(EDGE_TOP);
        }

        if (hasBottom(i, j)) {
          freeSides.add(EDGE_BOTTOM);
        }

        if (freeSides.size() == 2) {
          Log.i("LOG", "Sides = 2");
          if (freeSides.contains(EDGE_LEFT) && freeSides.contains(EDGE_RIGHT)) {
            //top, bottom
            unsafeMoves.add(new Move(i, j + 1, i + 1, j + 1));
            unsafeMoves.add(new Move(i, j, i + 1, j));
          }

          if (freeSides.contains(EDGE_LEFT) && freeSides.contains(EDGE_TOP)) {
            //right, bottom
            unsafeMoves.add(new Move(i + 1, j, i + 1, j + 1));
            unsafeMoves.add(new Move(i, j, i + 1, j));
          }

          if (freeSides.contains(EDGE_LEFT) && freeSides.contains(EDGE_BOTTOM)) {
            //right, top
            unsafeMoves.add(new Move(i + 1, j, i + 1, j + 1));
            unsafeMoves.add(new Move(i, j + 1, i + 1, j + 1));
          }

          if (freeSides.contains(EDGE_RIGHT) && freeSides.contains(EDGE_TOP)) {
            //left, bottom.
            unsafeMoves.add(new Move(i, j, i, j + 1));
            unsafeMoves.add(new Move(i, j, i + 1, j));
          }

          if (freeSides.contains(EDGE_RIGHT) && freeSides.contains(EDGE_BOTTOM)) {
            //left, top
            unsafeMoves.add(new Move(i, j, i, j + 1));
            unsafeMoves.add(new Move(i, j + 1, i + 1, j + 1));
          }

          if (freeSides.contains(EDGE_TOP) && freeSides.contains(EDGE_BOTTOM)) {
            //left, right
            unsafeMoves.add(new Move(i, j, i, j + 1));
            unsafeMoves.add(new Move(i + 1, j, i + 1, j + 1));
          }
        }
      }
    }

    return unsafeMoves;
  }

  private void ai() {
    if (isGameFinished()) {
      return;
    }

    if (fill3SidesBoxes()) {
      return;
    }

    ArrayList<Move> unsafeMoves = detectUnsafeMoves();

    if (makeRandomSafeMove(unsafeMoves)) {
      return;
    }

    makeRandomMove();

  }

  private boolean connectLeft(int i, int j) {
    return connectLine(new Point(i, j), new Point(i, j + 1));
  }

  private boolean connectRight(int i, int j) {
    return connectLine(new Point(i + 1, j), new Point(i + 1, j + 1));
  }

  private boolean connectTop(int i, int j) {
    return connectLine(new Point(i, j + 1), new Point(i + 1, j + 1));
  }

  private boolean connectBottom(int i, int j) {
    return connectLine(new Point(i, j), new Point(i + 1, j));
  }

  private boolean hasRight(int i, int j) {
    for (Action line : state.actions) {
      if (line.move.i1 == i + 1 && line.move.j1 == j && line.move.i2 == i + 1 && line.move.j2 == j + 1) {
        return true;
      }
    }
    return false;
  }

  private boolean hasLeft(int i, int j) {
    for (Action line : state.actions) {
      if (line.move.i1 == i && line.move.j1 == j && line.move.i2 == i && line.move.j2 == j + 1) {
        return true;
      }
    }
    return false;
  }

  private boolean hasTop(int i, int j) {
    for (Action line : state.actions) {
      if (line.move.i1 == i && line.move.j1 == j + 1 && line.move.i2 == i + 1 && line.move.j2 == j + 1) {
        return true;
      }
    }
    return false;
  }

  private boolean hasBottom(int i, int j) {
    for (Action line : state.actions) {
      if (line.move.i1 == i && line.move.j1 == j && line.move.i2 == i + 1 && line.move.j2 == j) {
        return true;
      }
    }
    return false;
  }

  public boolean checkBox(Box box) {
    int i = box.i;
    int j = box.j;

    boolean hasLeft = hasLeft(i, j);
    boolean hasRight = hasRight(i, j);
    boolean hasTop = hasBottom(i, j);
    boolean hasBottom = hasTop(i, j);

    boolean isFullConnected = hasBottom && hasTop && hasRight && hasLeft;
    if (isFullConnected) {
      box.playerIndex = state.playerIndex;
      state.boxes.add(box);
      increasePlayerScor(box.playerIndex);
      return true;
    }
    return false;
  }

  public float computeDiff(float x1, float y1, float x2, float y2) {
    return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  private boolean drawLine(Canvas canvas, Action line) {
    ScreenPosition p1 = getPointPosition(line.move.i1, line.move.j1);
    ScreenPosition p2 = getPointPosition(line.move.i2, line.move.j2);
    linePaint.setColor(getPlayerColor(line.playerindex));
    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint);
    return true;
  }

  private ScreenPosition getPointPosition(int i, int j) {
    int x = (i * Theme.SPACE_BETWEEN_DOTS) + offsetX;
    int y = ((options.rows - 1 - j) * Theme.SPACE_BETWEEN_DOTS) + offsetY;

    return new ScreenPosition(x, y);
  }
}
