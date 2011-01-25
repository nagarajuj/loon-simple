package org.loon.framework.android.game.action.sprite;

import org.loon.framework.android.game.action.map.shapes.RectBox;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.utils.GraphicsUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * Copyright 2008 - 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class SpriteRotateSheet {

	private final int width, height, halfWidth, halfHeight;

	private int number, bitmapWidth, bitmapHeight;

	private boolean isCircle;

	private Bitmap sheetRotationImages;

	public SpriteRotateSheet(String fileName, int number, boolean c) {
		this(GraphicsUtils.loadImage(fileName), number, c);
	}

	public SpriteRotateSheet(LImage img, int number, boolean c) {
		this(img.getBitmap(), number, c);
	}

	public SpriteRotateSheet(Bitmap img, int number, boolean c) {
		this.isCircle = c;
		this.number = number;
		this.width = img.getWidth();
		this.height = img.getHeight();
		if (!suited(width, height)) {
			throw new RuntimeException("size not allowed :" + width + ","
					+ height);
		}
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.bitmapWidth = width;
		this.bitmapHeight = height;
		LImage tmp = null;
		LGraphics g = null;
		if (c) {
			tmp = LImage.createImage(width * number + width, height,
					Config.ARGB_4444);
			g = tmp.getLGraphics();
			int x;
			for (int i = 0; i < number; i++) {
				x = i * width;
				float degrees = i * 360 / number;
				g.save();
				g.rotateCanvas(degrees, x + width / 2, height / 2);
				g.setFilterBitmap(true);
				g.drawBitmap(img, x, 0);
				g.restore();
			}
		} else {
			RectBox[] lazyRotates = new RectBox[360];
			for (int i = 0; i < number; i++) {
				int index = i * 360 / number;
				RectBox rect = getNewBounds(0, 0, width, height, index);
				lazyRotates[i] = rect;
				bitmapWidth = Math.max(bitmapWidth, rect.width);
				bitmapHeight = Math.max(bitmapHeight, rect.height);
			}
			tmp = LImage.createImage(bitmapWidth * number + bitmapWidth,
					bitmapHeight, Config.ARGB_4444);
			g = tmp.getLGraphics();
			int x = 0;
			for (int i = 0; i < number; i++) {
				float degrees = i * 360 / number;
				RectBox rect = lazyRotates[i];
				x = (i * (bitmapWidth));
				g.save();
				g.rotateCanvas(degrees, x + rect.width / 2, rect.height / 2);
				g.setFilterBitmap(true);
				g.drawBitmap(img, x + (rect.width - width) / 2,
						(rect.height - height) / 2);
				g.restore();
			}
			lazyRotates = null;
		}
		if (g != null) {
			g.dispose();
			g = null;
		}
		sheetRotationImages = tmp.getBitmap();
		tmp = null;
	}

	public static RectBox getNewBounds(int x, int y, int width, int height,
			double rotate) {
		double rotation = Math.toRadians(rotate);
		double angSin = Math.sin(rotation);
		double angCos = Math.cos(rotation);
		int newW = (int) Math.floor((width * Math.abs(angCos))
				+ (height * Math.abs(angSin)));
		int newH = (int) Math.floor((height * Math.abs(angCos))
				+ (width * Math.abs(angSin)));
		int centerX = (int) (x + (width / 2));
		int centerY = (int) (y + (height / 2));
		int newX = (int) (centerX - (newW / 2));
		int newY = (int) (centerY - (newH / 2));
		return new RectBox(newX, newY, newW, newH);
	}

	public static boolean suited(int w, int h) {
		return (w == h || (w > 16 && w < 48 && h > 16 && h < 48))
				&& (w <= 128 && h <= 128);
	}

	public void draw2(LGraphics g, int x, int y, double rotation) {
		if (sheetRotationImages != null) {
			if (rotation >= 360) {
				if (rotation < 720) {
					rotation -= 360;
				} else {
					rotation %= 360;
				}
			} else if (rotation < 0) {
				if (rotation >= -360) {
					rotation += 360;
				} else {
					rotation = 360 + rotation % 360;
				}
			}
			int spriteIndex = (int) (rotation * number / 360);
			g.drawBitmap(sheetRotationImages, x, y, x + bitmapWidth, y
					+ bitmapHeight, spriteIndex * bitmapWidth, 0, (spriteIndex
					* bitmapWidth + bitmapWidth), bitmapHeight);
		}
	}

	public void draw(LGraphics g, int x, int y, double rotation) {
		if (sheetRotationImages != null) {
			synchronized (sheetRotationImages) {
				while (rotation < 0) {
					rotation += 360;
				}
				while (rotation > 360) {
					rotation -= 360;
				}
				int spriteIndex = (int) (rotation * number / 360);
				if (isCircle) {
					x = (int) x - halfWidth;
					y = (int) y - halfHeight;
				} else {
					double rotate = Math.toRadians(rotation);
					double sinA = Math.sin(rotate);
					double cosA = Math.cos(rotate);
					x = (int) (x - (halfWidth - (halfWidth * cosA - halfHeight
							* sinA)));
					y = (int) (y - (halfHeight - (halfHeight * cosA + halfWidth
							* sinA)));
				}
				g
						.drawBitmap(sheetRotationImages, x, y, x + bitmapWidth,
								y + bitmapHeight, spriteIndex * bitmapWidth, 0,
								(spriteIndex * bitmapWidth + bitmapWidth),
								bitmapHeight);
			}
		}
	}

	public void dispose() {
		if (sheetRotationImages != null) {
			synchronized (sheetRotationImages) {
				sheetRotationImages.recycle();
				sheetRotationImages = null;
			}
		}
	}

	public Bitmap getSheetImage() {
		return sheetRotationImages;
	}

	public int getHeight() {
		return height;
	}

	public int getNumber() {
		return number;
	}

	public int getWidth() {
		return width;
	}

}
