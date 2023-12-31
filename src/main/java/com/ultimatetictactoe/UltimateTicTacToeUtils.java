package com.ultimatetictactoe;

import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;

public class UltimateTicTacToeUtils
{
	public static Point getGridPointFromWorld(WorldPoint worldPoint, WorldPoint topLeftCornerWorld)
	{
		return new Point(
			worldPoint.getX() - topLeftCornerWorld.getX(),
			topLeftCornerWorld.getY() - worldPoint.getY()
		);
	}

	public static WorldPoint getWorldPointFromGrid(Point point, WorldPoint topLeftCornerWorld)
	{
		return new WorldPoint(
			topLeftCornerWorld.getX() + point.getX(),
			topLeftCornerWorld.getY() - point.getY(),
			topLeftCornerWorld.getPlane()
		);
	}
}
