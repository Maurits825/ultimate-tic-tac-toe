package com.ultimatetictactoe;

import static com.ultimatetictactoe.UltimateTicTacToeConstant.GRID_OFFSET;
import static com.ultimatetictactoe.UltimateTicTacToeConstant.GRID_SIZE;
import static com.ultimatetictactoe.UltimateTicTacToeConstant.SMALL_GRID_SIZE;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;

public class UltimateTicTacToeModel
{
	enum State
	{
		IDLE,
		PLAYER1_WIN,
		PLAYER2_WIN,
		DRAW,
		PLAYER1_MOVE,
		PLAYER2_MOVE,
	}

	//TODO enums??, or better way
	private final int EMPTY_VALUE = 0;
	private final int PLAYER1_VALUE = 1;
	private final int PLAYER1_WIN_VALUE = (int) Math.pow(PLAYER1_VALUE, SMALL_GRID_SIZE);
	private final int PLAYER2_VALUE = 2;
	private final int PLAYER2_WIN_VALUE = (int) Math.pow(PLAYER2_VALUE, SMALL_GRID_SIZE);
	private final int DRAW_VALUE = 3;

	private int[][] smallBoard;
	private int[][] bigBoard;

	@Getter
	private State currentState = State.IDLE;
	private Point currentGrid;

	private WorldPoint topLeftCornerWorld;

	public void initialize(WorldPoint playerWorldPosition)
	{
		topLeftCornerWorld = playerWorldPosition.dx(-GRID_OFFSET + 1).dy(GRID_OFFSET - 1);

		smallBoard = new int[GRID_SIZE][GRID_SIZE];
		bigBoard = new int[SMALL_GRID_SIZE][SMALL_GRID_SIZE];

		currentState = State.PLAYER1_MOVE;
		currentGrid = null; //first move can be played anywhere
	}

	public void reset()
	{
		currentState = State.IDLE;
		currentGrid = null;
	}

	public boolean playerOneMove(WorldPoint worldLocation)
	{
		return move(State.PLAYER1_MOVE, UltimateTicTacToeUtils.getGridPointFromWorld(worldLocation, topLeftCornerWorld));
	}

	public boolean playerOneMove(Point point)
	{
		return move(State.PLAYER1_MOVE, point);
	}

	public boolean playerTwoMove(WorldPoint worldLocation)
	{
		return move(State.PLAYER2_MOVE, UltimateTicTacToeUtils.getGridPointFromWorld(worldLocation, topLeftCornerWorld));
	}

	public boolean playerTwoMove(Point point)
	{
		return move(State.PLAYER2_MOVE, point);
	}

	public List<Point> getValidTiles()
	{
		List<Point> validPoints = new ArrayList<>();
		for (int x = 0; x < GRID_SIZE; x++)
		{
			for (int y = 0; y < GRID_SIZE; y++)
			{
				Point point = new Point(x, y);
				if (isValidMove(currentState, point))
				{
					validPoints.add(point);
				}
			}
		}

		return validPoints;
	}

	public List<Point> getPlayerOneWins()
	{
		return getPlayerWins(PLAYER1_VALUE);
	}

	public List<Point> getPlayerTwoWins()
	{
		return getPlayerWins(PLAYER2_VALUE);
	}

	private List<Point> getPlayerWins(int playerValue)
	{
		List<Point> gridWinPoints = new ArrayList<>();
		for (int x = 0; x < SMALL_GRID_SIZE; x++)
		{
			for (int y = 0; y < SMALL_GRID_SIZE; y++)
			{
				Point point = new Point(x, y);
				if (bigBoard[x][y] == playerValue)
				{
					gridWinPoints.add(point);
				}
			}
		}

		return gridWinPoints;
	}

	private boolean move(State playerMove, Point point)
	{
		if (!isValidMove(playerMove, point))
		{
			return false;
		}

		//play the move
		if (currentState == State.PLAYER1_MOVE)
		{
			smallBoard[point.getX()][point.getY()] = PLAYER1_VALUE;
			currentState = State.PLAYER2_MOVE;
		}
		else
		{
			smallBoard[point.getX()][point.getY()] = PLAYER2_VALUE;
			currentState = State.PLAYER1_MOVE;
		}

		updateBigBoard();
		updateCurrentGrid(point);

		int win = getBoardStatus(bigBoard, 0, 0);
		if (win == PLAYER1_VALUE)
		{
			currentState = State.PLAYER1_WIN;
		}
		else if (win == PLAYER2_VALUE)
		{
			currentState = State.PLAYER2_WIN;
		}
		else if (win == DRAW_VALUE)
		{
			currentState = State.DRAW;
		}

		return true;
	}

	private boolean isValidMove(State playerMove, Point point)
	{
		if (point.getX() < 0 || point.getX() >= GRID_SIZE)
		{
			return false;
		}

		if (point.getY() < 0 || point.getY() >= GRID_SIZE)
		{
			return false;
		}

		if (currentState != State.PLAYER1_MOVE && currentState != State.PLAYER2_MOVE)
		{
			return false;
		}

		if (playerMove != currentState)
		{
			return false;
		}

		if (smallBoard[point.getX()][point.getY()] != EMPTY_VALUE)
		{
			return false;
		}

		Point gridLocation = new Point(point.getX() / 3, point.getY() / 3);

		if (bigBoard[gridLocation.getX()][gridLocation.getY()] != EMPTY_VALUE)
		{
			return false;
		}

		if (currentGrid == null)
		{
			return true;
		}

		if (!gridLocation.equals(currentGrid))
		{
			return false;
		}

		return true;
	}

	private void updateBigBoard()
	{
		for (int x = 0; x < SMALL_GRID_SIZE; x++)
		{
			for (int y = 0; y < SMALL_GRID_SIZE; y++)
			{
				int startX = x * SMALL_GRID_SIZE;
				int startY = y * SMALL_GRID_SIZE;
				bigBoard[x][y] = getBoardStatus(smallBoard, startX, startY);
			}
		}
	}

	//TODO surely it can be cleaner
	private int getBoardStatus(int[][] board, int startX, int startY)
	{
		boolean isGridFilled = true;

		//check rol & col
		for (int x = 0; x < SMALL_GRID_SIZE; x++)
		{
			int rowProduct = 1;
			int colProduct = 1;
			for (int y = 0; y < SMALL_GRID_SIZE; y++)
			{
				rowProduct *= board[startX + y][startY + x];
				colProduct *= board[startX + x][startY + y];
			}

			int winValue = getProductWin(rowProduct, colProduct);
			if (winValue == PLAYER1_VALUE || winValue == PLAYER2_VALUE)
			{
				return winValue;
			}

			if (winValue == EMPTY_VALUE)
			{
				isGridFilled = false;
			}
		}

		//check diag
		int diag1Product = 1;
		int diag2Product = 1;
		for (int i = 0; i < SMALL_GRID_SIZE; i++)
		{
			diag1Product *= board[startX + i][startY + i];
			diag2Product *= board[startX + SMALL_GRID_SIZE - 1 - i][startY + i];
		}

		int winValue = getProductWin(diag1Product, diag2Product);
		if (winValue == PLAYER1_VALUE || winValue == PLAYER2_VALUE)
		{
			return winValue;
		}

		if (winValue == EMPTY_VALUE)
		{
			isGridFilled = false;
		}

		return isGridFilled ? DRAW_VALUE : EMPTY_VALUE;
	}

	private int getProductWin(int product1, int product2)
	{
		if (product1 == PLAYER1_WIN_VALUE || product2 == PLAYER1_WIN_VALUE)
		{
			return PLAYER1_VALUE;
		}

		if (product1 == PLAYER2_WIN_VALUE || product2 == PLAYER2_WIN_VALUE)
		{
			return PLAYER2_VALUE;
		}

		if (product1 == EMPTY_VALUE || product2 == EMPTY_VALUE)
		{
			return EMPTY_VALUE;
		}

		return DRAW_VALUE;
	}

	private void updateCurrentGrid(Point point)
	{
		if (currentGrid == null)
		{
			currentGrid = new Point(point.getX() / 3, point.getY() / 3);
		}

		currentGrid = new Point(point.getX() - (currentGrid.getX() * 3), point.getY() - (currentGrid.getY() * 3));

		if (bigBoard[currentGrid.getX()][currentGrid.getY()] != EMPTY_VALUE)
		{
			currentGrid = null;
		}
	}
}
