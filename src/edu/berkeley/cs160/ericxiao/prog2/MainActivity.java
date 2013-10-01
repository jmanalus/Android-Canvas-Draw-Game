package edu.berkeley.cs160.ericxiao.prog2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	//Parts of this were taken from helpful posts on Stackoverflow as well as google API docs
	DrawArea mCustomView;
	OnTouchListener touchListener;
	int color = Color.BLACK;
	boolean erase = false;
	float width = 20f;
	private Button userNextButton;
	DrawArea userDrawingArea;
	LinearLayout layout;
	Menu userMenu;
	ArrayList<DrawArea> allDrawings;
	int counter = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layout = (LinearLayout) findViewById(R.id.ll);
		userDrawingArea = new DrawArea(this);
		layout.addView(userDrawingArea);
		counter = 3;
		addListenerOnButton();
		allDrawings = new ArrayList<DrawArea>();

	}

	public class DrawArea extends View { 
		private SparseArray<penStroke> userActiveStrokes; 
		private List<penStroke> userStrokes;
		public DrawArea(Context context) {
			super(context);
			userStrokes = new ArrayList<penStroke>();
			userActiveStrokes = new SparseArray<penStroke>();
		}
		
        @Override //android layout view
		public void onDraw(Canvas canvas) {
			if (userStrokes != null) {
				for (penStroke stroke : userStrokes) {
					if (stroke != null) {
						Path path = stroke.getPath();
						Paint painter = stroke.getPaint();
						if ((path != null) && (painter != null)) {
							canvas.drawPath(path, painter);
						}
						invalidate();
					}
				}
			}
		}
        //Help from stockoverflow
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			final int action = event.getActionMasked();
			final int pointerCount = event.getPointerCount();

			switch (action) {
			case MotionEvent.ACTION_DOWN: {
				pointDown((int) event.getX(), (int) event.getY(), event.getPointerId(0));
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				for (int pc = 0; pc < pointerCount; pc++) {
					pointMove((int) event.getX(pc), (int) event.getY(pc), event.getPointerId(pc));
				}
				break;
			}
			case MotionEvent.ACTION_POINTER_DOWN: {
				for (int pc = 0; pc < pointerCount; pc++) {
					pointDown((int) event.getX(pc), (int) event.getY(pc), event.getPointerId(pc));
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				break;
			}
			case MotionEvent.ACTION_POINTER_UP: {
				break;
			}
			}
			invalidate();
			return true;
		}
		
		public class penStroke {
		    private Path userStrokePath;
		    private Paint userStrokePaint;
		    public penStroke (Paint paint) {
		        userStrokePaint = paint;
		    }
		    public Path getPath() {
		        return userStrokePath;
		    }
		    public Paint getPaint() {
		        return userStrokePaint;
		    }
			public void addPoint(Point point) {
		        if (userStrokePath == null) {
		            userStrokePath = new Path();
		            userStrokePath.moveTo(point.x, point.y);
		        } else {
		            userStrokePath.lineTo(point.x, point.y);
		        }
		    }
		}
		
		private void pointDown(int xAxis, int yAxis, int strokeId) {
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			if (erase) {
				paint.setColor(Color.WHITE);
			} else {
				paint.setColor(color);
			}
			paint.setStrokeWidth(width);
			Point point = new Point(xAxis, yAxis);
			penStroke stroke = new penStroke(paint);
			stroke.addPoint(point);
			userActiveStrokes.put(strokeId, stroke);
			userStrokes.add(stroke);
		}

		private void pointMove(int xAxis, int yAxis, int strokeId) {
			penStroke stroke = userActiveStrokes.get(strokeId);
			if (stroke != null) {
				Point point = new Point(xAxis, yAxis);
				stroke.addPoint(point);
			}
		}
	}
	
	public void addListenerOnButton() {
		userNextButton = (Button) findViewById(R.id.userButton);
		userNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allDrawings.add(userDrawingArea);
				layout.removeView(userDrawingArea);
				userDrawingArea = new DrawArea(MainActivity.this);
				layout.addView(userDrawingArea);
				if (((Button) findViewById(R.id.userButton)).getText().equals(
						"Draw Again?")) {
					((Button) findViewById(R.id.userButton)).setText("Next");
					counter = 4;
					for (int i = 0; i < allDrawings.size(); i++) {
						layout.removeView(allDrawings.get(i));
					}
					allDrawings = new ArrayList<DrawArea>();
					userMenu.findItem(R.id.black).setChecked(true);
				}
				counter = counter - 1;
				if (counter == 1) {
					((Button) findViewById(R.id.userButton)).setText("Finish");
				} else if (counter == 0) {
					displayFinish();
				}
			}
		});
	}
	
	//Taken from stackoverflow to save each drawing and to combine.
	public void displayFinish() {
		layout.removeAllViews();
		layout = (LinearLayout) findViewById(R.id.ll);
		userNextButton.setText("Draw Again?");
		layout.addView(userNextButton);
		LinearLayout.LayoutParams layoutParams0 = (LinearLayout.LayoutParams) allDrawings.get(0).getLayoutParams(); //page1
		layoutParams0.leftMargin = -300;
		layoutParams0.topMargin = -300;
		allDrawings.get(0).setLayoutParams(layoutParams0);
		allDrawings.get(0).invalidate();
		LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) allDrawings.get(1).getLayoutParams(); //page2
		layoutParams1.leftMargin = -700;
		layoutParams1.topMargin = -300;
		allDrawings.get(1).setLayoutParams(layoutParams1);
		allDrawings.get(1).invalidate();	
		LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) allDrawings.get(2).getLayoutParams(); //page3
		layoutParams2.leftMargin = -400;
		layoutParams2.topMargin = -300;
		allDrawings.get(2).setLayoutParams(layoutParams2);
		allDrawings.get(2).invalidate();
		for (int i = 0; i < allDrawings.size(); i++) {
			allDrawings.get(i).setScaleX(0.5f);
			allDrawings.get(i).setScaleY(0.5f);
			layout.addView(allDrawings.get(i));
		}
	}
	
	public boolean onCreateOptionsMenu(Menu userMenu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, userMenu);
		this.userMenu = userMenu;
		return true;
	}
	
    //Colors,Draw pen and Eraser
	public boolean onOptionsItemSelected(MenuItem item) {
		userMenu.findItem(item.getItemId()).setChecked(true);
		switch (item.getItemId()) {
		case R.id.draw:
			Toast.makeText(this, "You have chosen to " + getResources().getString(R.string.draw) + ".",
					Toast.LENGTH_SHORT).show();
			erase = false;
			return true;
		case R.id.erase:
			Toast.makeText(this, "You have chosen to " + getResources().getString(R.string.erase) + ".",
					Toast.LENGTH_SHORT).show();
			erase = true;
			return true;
		case R.id.black:
			Toast.makeText(this, "You have chosen " + getResources().getString(R.string.black) + ".",
					Toast.LENGTH_SHORT).show();
			color = Color.BLACK;
			return true;
		case R.id.blue:
			Toast.makeText(this, "You have chosen " + getResources().getString(R.string.blue) + ".",
					Toast.LENGTH_SHORT).show();
			color = Color.BLUE;
			return true;
		case R.id.yellow:
			Toast.makeText(this, "You have chosen " + getResources().getString(R.string.yellow) + ".",
					Toast.LENGTH_SHORT).show();
			color = Color.YELLOW;
			return true;
		case R.id.red:
			Toast.makeText(this, "You have chosen " + getResources().getString(R.string.red) + ".", 
					Toast.LENGTH_SHORT).show();
			color = Color.RED;
			return true;
		case R.id.orange:
			Toast.makeText(this, "You have chosen " + getResources().getString(R.string.orange) + ".",
					Toast.LENGTH_SHORT).show();
			color = Color.rgb(255, 127, 0);
			return true;

		case R.id.green:
			Toast.makeText(this, "You have chosen " + getResources().getString(R.string.green) + ".",
					Toast.LENGTH_SHORT).show();
			color = Color.GREEN;
			return true;

		case R.id.purple:
			Toast.makeText(this, "You have chosen " + getResources().getString(R.string.purple) + ".",
					Toast.LENGTH_SHORT).show();
			color = Color.rgb(106,90,205);
			return true;
		case R.id.magenta:
			Toast.makeText(this, "You have chosen " + getResources().getString(R.string.magenta) + ".",
					Toast.LENGTH_SHORT).show();
			color = Color.rgb(230, 26, 203);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
