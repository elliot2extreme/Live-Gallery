package de.imaze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class LiveGalleryService extends WallpaperService {
	private final Handler handler = new Handler();

	@Override
	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}

	private class WallpaperEngine extends Engine {
		private Paint paint = new Paint();
		private File[] files;
		private Bitmap bm;
		private float scale = 1f;
		private static final int IMAGE_SHOW_TIME = 10000;

		public WallpaperEngine() {
			paint.setAntiAlias(true);
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(10f);

			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.TRANSPARENT);

			File dcimDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			File photoDir = new File(dcimDir, "Camera");
			files = photoDir.listFiles();
			// files = new File[] { new File("/mnt/sdcard/Apple Metal.jpg"),
			// new File("/mnt/sdcard/Snow Leopard.jpg"),
			// new File("/mnt/sdcard/Hannah Heartcloud.jpg") };

			draw();
		}

		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};

		private final Runnable fadeOutRunner = new Runnable() {
			@Override
			public void run() {
				fadeOut();
			}
		};

		private final Runnable fadeInRunner = new Runnable() {
			@Override
			public void run() {
				fadeIn();
			}
		};

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder arg0) {
			draw();
		};

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				handler.removeCallbacks(fadeInRunner);
				handler.removeCallbacks(fadeOutRunner);
				searchNewImage();
				draw();
			}

			super.onTouchEvent(event);
		}

		private void searchNewImage() {
			if (files.length > 0) {
				FileInputStream fis = null;
				try {
					Random r = new Random();
					int next = r.nextInt(files.length);

					fis = new FileInputStream(files[next]);
					if (bm != null) {
						bm.recycle();
						bm = null;
					}

					bm = BitmapFactory.decodeStream(fis);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
		}

		private void fadeIn() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {

					Rect drawable_area = new Rect(0, 0, canvas.getWidth(),
							canvas.getHeight());
					canvas.drawColor(Color.TRANSPARENT);
					canvas.scale(scale, scale, canvas.getWidth() * 0.5f, canvas
							.getHeight() * 0.5f);
					canvas.drawBitmap(bm, null, drawable_area, paint);

					scale -= .2f;
					if (scale <= 1) {
						scale = 1f;
						handler.removeCallbacks(fadeInRunner);
						handler.postDelayed(fadeOutRunner, IMAGE_SHOW_TIME);
						return;
					}
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}

			handler.removeCallbacks(fadeInRunner);
			handler.postDelayed(fadeInRunner, 10);
		}

		private void fadeOut() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {

					Rect drawable_area = new Rect(0, 0, canvas.getWidth(),
							canvas.getHeight());
					canvas.drawColor(Color.BLACK);
					canvas.scale(scale, scale, canvas.getWidth() * 0.5f, canvas
							.getHeight() * 0.5f);
					canvas.drawBitmap(bm, null, drawable_area, paint);

					scale += .2f;
					if (scale >= 10) {
						searchNewImage();
						handler.removeCallbacks(fadeOutRunner);
						handler.postDelayed(fadeInRunner, 10);
						return;
					}
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}

			handler.removeCallbacks(fadeOutRunner);
			handler.postDelayed(fadeOutRunner, 10);
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					drawImage(canvas);
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			scale = 1f;
			handler.removeCallbacks(drawRunner);
			handler.postDelayed(fadeOutRunner, IMAGE_SHOW_TIME);
		}

		protected void drawImage(Canvas canvas) {
			if (files.length > 0) {
				FileInputStream fis = null;
				try {
					canvas.drawPaint(paint);
					paint.setColor(Color.WHITE);
					Rect drawable_area = new Rect(0, 0, canvas.getWidth(),
							canvas.getHeight());

					Random r = new Random();
					int next = r.nextInt(files.length);

					fis = new FileInputStream(files[next]);
					if (bm != null) {
						bm.recycle();
						bm = null;
					}

					bm = BitmapFactory.decodeStream(fis);
					canvas.drawBitmap(bm, null, drawable_area, paint);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
		}
	}
}