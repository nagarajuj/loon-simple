package org.loon.framework.javase.game.action.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.loon.framework.javase.game.action.map.shapes.Vector2D;


/**
 * Copyright 2008 - 2009
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
 * @email ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Field2D implements Config {

	final static private Map directions = new HashMap(8);

	final static private Map directionValues = new HashMap(8);

	private static Vector2D vector2D;

	static {

		directions.put(new Vector2D(0, 0), new Integer(Config.EMPTY));
		directions.put(new Vector2D(1, -1), new Integer(Config.UP));
		directions.put(new Vector2D(-1, -1), new Integer(Config.LEFT));
		directions.put(new Vector2D(1, 1), new Integer(Config.RIGHT));
		directions.put(new Vector2D(-1, 1), new Integer(Config.DOWN));
		directions.put(new Vector2D(0, -1), new Integer(Config.TUP));
		directions.put(new Vector2D(-1, 0), new Integer(Config.TLEFT));
		directions.put(new Vector2D(1, 0), new Integer(Config.TRIGHT));
		directions.put(new Vector2D(0, 1), new Integer(Config.TDOWN));

		directionValues.put(new Integer(Config.EMPTY), new Vector2D(0, 0));
		directionValues.put(new Integer(Config.UP), new Vector2D(1, -1));
		directionValues.put(new Integer(Config.LEFT), new Vector2D(-1, -1));
		directionValues.put(new Integer(Config.RIGHT), new Vector2D(1, 1));
		directionValues.put(new Integer(Config.DOWN), new Vector2D(-1, 1));
		directionValues.put(new Integer(Config.TUP), new Vector2D(0, -1));
		directionValues.put(new Integer(Config.TLEFT), new Vector2D(-1, 0));
		directionValues.put(new Integer(Config.TRIGHT), new Vector2D(1, 0));
		directionValues.put(new Integer(Config.TDOWN), new Vector2D(0, 1));

	}

	private int[][] data;

	private ArrayList result;

	private int tileWidth, tileHeight;

	private int width, height;

	public Field2D(String fileName, int w, int h) {
		try {
			set(TileMapConfig.loadAthwartArray(fileName), w, h);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public Field2D(int[][] data) {
		this(data, 0, 0);
	}

	public Field2D(int[][] data, int w, int h) {
		this.set(data, w, h);
	}

	public void set(int[][] data, int w, int h) {
		this.setMap(data);
		this.setTileWidth(w);
		this.setTileHeight(h);
		this.width = data[0].length;
		this.height = data.length;
	}

	public boolean checkArea(int x, int y) {
		return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
	}

	public int getDrawWidth() {
		return width * tileWidth;
	}

	public int getDrawHeight() {
		return height * tileHeight;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int tilesToWidthPixels(int tiles) {
		return tiles * tileWidth;
	}

	public int tilesToHeightPixels(int tiles) {
		return tiles * tileHeight;
	}

	public int pixelsToTilesWidth(int x) {
		return x / tileWidth;
	}

	public int pixelsToTilesHeight(int y) {
		return y / tileHeight;
	}

	public int getType(int x, int y) {
		try {
			return data[x][y];
		} catch (Exception e) {
			return -1;
		}
	}

	public int[][] getMap() {
		return data;
	}

	public void setMap(int[][] data) {
		this.data = data;
	}

	public boolean isHit(Vector2D point) {
		if (get(data, point) != -1) {
			return true;
		}
		return false;
	}

	public boolean isHit(int px, int py) {
		if (get(data, px, py) != -1) {
			return true;
		}
		return false;
	}

	public static int getDirection(int x, int y) {
		if (vector2D == null) {
			vector2D = new Vector2D(x, y);
		} else {
			vector2D.set(x, y);
		}
		return ((Integer) directions.get(vector2D)).intValue();
	}

	public static Vector2D getDirection(int type) {
		return (Vector2D) directionValues.get(new Integer(type));
	}

	private static void insertArrays(int[][] arrays, int index, int px, int py) {
		arrays[index][0] = px;
		arrays[index][1] = py;
	}

	public int[][] neighbors(int px, int py, boolean flag) {
		int[][] pos = new int[8][2];
		insertArrays(pos, 0, px, py - 1);
		insertArrays(pos, 0, px + 1, py);
		insertArrays(pos, 0, px, py + 1);
		insertArrays(pos, 0, px - 1, py);
		if (flag) {
			insertArrays(pos, 0, px - 1, py - 1);
			insertArrays(pos, 0, px + 1, py - 1);
			insertArrays(pos, 0, px + 1, py + 1);
			insertArrays(pos, 0, px - 1, py + 1);
		}
		return pos;
	}

	public ArrayList neighbors(Vector2D pos, boolean flag) {
		if (result == null) {
			result = new ArrayList(8);
		} else {
			result.clear();
		}
		int x = pos.x();
		int y = pos.y();
		result.add(new Vector2D(x, y - 1));
		result.add(new Vector2D(x + 1, y));
		result.add(new Vector2D(x, y + 1));
		result.add(new Vector2D(x - 1, y));
		if (flag) {
			result.add(new Vector2D(x - 1, y - 1));
			result.add(new Vector2D(x + 1, y - 1));
			result.add(new Vector2D(x + 1, y + 1));
			result.add(new Vector2D(x - 1, y + 1));
		}
		return result;
	}

	public int score(Vector2D goal, Vector2D point) {
		return Math.abs(point.x() - goal.x()) + Math.abs(point.y() - goal.y());
	}

	public int score(int x, int y, int px, int py) {
		return Math.abs(px - x) + Math.abs(py - y);
	}

	private int get(int[][] data, int px, int py) {
		try {
			if (px < width && py < height) {
				return data[py][px];
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private int get(int[][] data, Vector2D point) {
		try {
			if (point.x() < width && point.y() < height) {
				return data[point.y()][point.x()];
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

}