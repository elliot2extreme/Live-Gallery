package de.imaze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

public class LiveGallery extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new ImageView(this));
	}

	private class ImageView extends View {

		public ImageView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.TRANSPARENT);
			canvas.drawPaint(paint);
			paint.setColor(Color.WHITE);
			Rect drawable_area = new Rect(0, 0, canvas.getWidth(), canvas
					.getHeight());

			File dcimDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			File photoDir = new File(dcimDir, "Camera");
			File[] files = photoDir.listFiles();
			if (files.length > 0) {
				FileInputStream fis = null;
				try {

					Random r = new Random();
					int next = r.nextInt(files.length);

					fis = new FileInputStream(files[next]);
					Bitmap bm = BitmapFactory.decodeStream(fis);
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