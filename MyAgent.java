import java.util.Random;

public class MyAgent extends Agent
{
    Random r;

    private static final int BLANK = 0;
    private static final int RED = 1;
    private static final int YELLOW = 2;

    private static final int MINVAL = -1;
    private static final int MAXVAL = 1;
    private static final int MAXDEPTH = 5;

    /**
     * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
     * 
     * @param game The game the agent will be playing.
     * @param iAmRed True if the agent is Red, False if the agent is Yellow.
     */
    public MyAgent(Connect4Game game, boolean iAmRed)
    {
        super(game, iAmRed);
        r = new Random();
    }

    /**
     * The move method is run every time it is this agent's turn in the game. You may assume that
     * when move() is called, the game has at least one open slot for a token, and the game has not
     * already been won.
     * 
     * By the end of the move method, the agent should have placed one token into the game at some
     * point.
     * 
     * After the move() method is called, the game engine will check to make sure the move was
     * valid. A move might be invalid if:
     * - No token was place into the game.
     * - More than one token was placed into the game.
     * - A previous token was removed from the game.
     * - The color of a previous token was changed.
     * - There are empty spaces below where the token was placed.
     * 
     * If an invalid move is made, the game engine will announce it and the game will be ended.
     * 
     */

    /*
     * Strategy:
     * If we can win, move on that column.
     * If the opponent can win, block that column.
     * Otherwise, move randomly.
     */
    public void move() {
        int[] move = evaluateMove(new Connect4Game(myGame), 0);
        System.out.println("Move: " + move[0] + " Score: " + move[1]);
        moveOnColumn(move[0]);
    }

    /*
     * @returns [int]: Integer corresponding to the follow of the slot, as
     *                 defined in the class constants.
     */
    private int getSlotValue(Connect4Slot slot) {
        if (slot.getIsFilled()) {
            if (slot.getIsRed())
                return RED;
            return YELLOW;
        }
        return BLANK;
    }

    private Connect4Slot getSlotAt(Connect4Game game, int colIdx, int slotIdx) {
        return game.getColumn(colIdx).getSlot(slotIdx);
    }

    private boolean isValidMove(Connect4Game game, int colIdx, int slotIdx) {
        return getLowestEmptyIndex(game.getColumn(colIdx)) == slotIdx;
    }

    /*
     * @returns [int]: The index of the slot (0 for s1, 1 for s2, etc.) where
     *                 the winning move is made, or -1 if there is no winning
     *                 move.
     */
    private int oneAway(Connect4Slot s1, Connect4Slot s2, Connect4Slot s3,
                        Connect4Slot s4, int color) {
        int colorCount = 0;
        int blankCount = 0;
        int blankIdx = -1;
        Connect4Slot[] slots = {s1, s2, s3, s4};
        for (int i = 0; i < slots.length; i++) {
            Connect4Slot slot = slots[i];
            if (getSlotValue(slot) == color)
                colorCount += 1;
            else if (getSlotValue(slot) == BLANK) {
                blankIdx = i;
                blankCount += 1;
            }
        }
        if (colorCount == 3 && blankCount == 1)
            return blankIdx;
        return -1;
    }

    private int winningColumn(int color) {
        return winningColumn(myGame, color);
    }

    /*
     * Returns the winning column index for the @color player or -1 if that
     * player can't win.
     * @param color [int]: The color of the player, as defined by the class
     *                     constants (1 for RED, 2 for YELLOW).
     */
    private int winningColumn(Connect4Game game, int color) {
        for (int i = 0; i < game.getColumnCount(); i++) {
            for (int j = 0; j < game.getRowCount(); j++) {
                // Vertical
                if (j + 3 < game.getRowCount()) {
                    if (isValidMove(game, i, j) || isValidMove(game, i, j+1) ||
                        isValidMove(game, i, j+2) || isValidMove(game, i, j+3)) {
                        int winningIdx = oneAway(getSlotAt(game, i, j),
                                                 getSlotAt(game, i, j+1),
                                                 getSlotAt(game, i, j+2),
                                                 getSlotAt(game, i, j+3),
                                                 color);
                        if (winningIdx > -1) {
                            return i;
                        }
                    }
                }
                // Horizontal
                if (i + 3 < game.getColumnCount()) {
                    if (isValidMove(game, i, j) || isValidMove(game, i+1, j) ||
                        isValidMove(game, i+2, j) || isValidMove(game, i+3, j)) {
                        int winningIdx = oneAway(getSlotAt(game, i, j),
                                                 getSlotAt(game, i+1, j),
                                                 getSlotAt(game, i+2, j),
                                                 getSlotAt(game, i+3, j),
                                                 color);
                        if (winningIdx > -1) {
                            return i + winningIdx;
                        }
                    }
                }
                // Diagonals
                if (i + 3 < game.getColumnCount()
                    && j + 3 < game.getRowCount()) {
                    if (isValidMove(game, i, j) || isValidMove(game, i+1, j+1) ||
                        isValidMove(game, i+2, j+2) || isValidMove(game, i+3, j+3)) {
                        int winningIdx = oneAway(getSlotAt(game, i, j),
                                                 getSlotAt(game, i+1, j+1),
                                                 getSlotAt(game, i+2, j+2),
                                                 getSlotAt(game, i+3, j+3),
                                                 color);
                        if (winningIdx > -1) {
                            return i + winningIdx;
                        }
                    }
                }
                if (i > 2 && j + 3 < game.getRowCount()) {
                    if (isValidMove(game, i, j) || isValidMove(game, i-1, j+1) ||
                        isValidMove(game, i-2, j+2) || isValidMove(game, i-3, j+3)) {
                        int winningIdx = oneAway(getSlotAt(game, i, j),
                                                 getSlotAt(game, i-1, j+1),
                                                 getSlotAt(game, i-2, j+2),
                                                 getSlotAt(game, i-3, j+3),
                                                 color);
                        if (winningIdx > -1) {
                            return i - winningIdx;
                        }
                    }
                }
            }
        }
        return -1;
    }

    private void moveOnColumn(Connect4Game game, int colIdx, int color) {
        int lowestEmptySlotIndex = getLowestEmptyIndex(game.getColumn(colIdx));
        if (lowestEmptySlotIndex > -1) {
            Connect4Slot lowestEmptySlot = game.getColumn(colIdx).getSlot(lowestEmptySlotIndex);
            if (color == RED)
                lowestEmptySlot.addRed();
            else
                lowestEmptySlot.addYellow();
        }
    }

    private Connect4Game makeMove(Connect4Game game, int colIdx, int color) {
        Connect4Game newGame = new Connect4Game(game);
        moveOnColumn(newGame, colIdx, color);
        return newGame;
    }

    /*
     * @returns [array]: [int column, int score]
     */
    private int[] evaluateMove(Connect4Game game, int depth) {
        int myColor = iAmRed ? RED : YELLOW;
        int oppColor = iAmRed ? YELLOW : RED;
        int[] move = { -1, 0 };

        if (depth >= MAXDEPTH) {
            move[0] = randomMove();
            return move;
        }
        // My move
        else if (depth % 2 == 0) {
            int colIdx = winningColumn(game, myColor);
            if (colIdx > -1) {
                move[0] = colIdx;
                move[1] = 1;
                return move;
            }
            else {
                move[1] = MINVAL;
                for (int i = 0; i < game.getColumnCount(); i++) {
                    if (!game.getColumn(i).getIsFull()) {
                        int[] score = evaluateMove(makeMove(game, i, myColor),
                                                   depth + 1);
                        if (score[1] >= move[1]) {
                            move[0] = i;
                            move[1] = score[1];
                            if (move[1] == MAXVAL)
                                break;
                        }
                    }
                }
                return move;
            }
        }
        // Opponent's move
        else {
            int colIdx = winningColumn(game, oppColor);
            if (colIdx > -1) {
                move[0] = colIdx;
                move[1] = -1;
                return move;
            }
            else {
                move[1] = MAXVAL;
                for (int i = 0; i < game.getColumnCount(); i++) {
                    if (!game.getColumn(i).getIsFull()) {
                        int[] score = evaluateMove(makeMove(game, i, oppColor),
                                                   depth + 1);
                        if (score[1] <= move[1]) {
                            move[0] = i;
                            move[1] = score[1];
                            if (move[1] == MINVAL)
                                break;
                        }
                    }
                }
                return move;
            }
        }
    }

    /**
     * Drops a token into a particular column so that it will fall to the bottom of the column.
     * If the column is already full, nothing will change.
     * 
     * @param columnNumber The column into which to drop the token.
     */
    public void moveOnColumn(int columnNumber)
    {
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
                                                                                                  // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1)  // if the column is not full
        {
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index
            if (iAmRed) // If the current agent is the Red player...
            {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            }
            else // If the current agent is the Yellow player (not the Red player)...
            {
                lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
            }
        }
    }

    /**
     * Returns the index of the top empty slot in a particular column.
     * 
     * @param column The column to check.
     * @return the index of the top empty slot in a particular column; -1 if the column is already full.
     */
    public int getLowestEmptyIndex(Connect4Column column) {
        int lowestEmptySlot = -1;
        for  (int i = 0; i < column.getRowCount(); i++)
        {
            if (!column.getSlot(i).getIsFilled())
            {
                lowestEmptySlot = i;
            }
        }
        return lowestEmptySlot;
    }

    /**
     * Returns a random valid move. If your agent doesn't know what to do, making a random move
     * can allow the game to go on anyway.
     * 
     * @return a random valid move.
     */
    public int randomMove()
    {
        int i = r.nextInt(myGame.getColumnCount());
        while (getLowestEmptyIndex(myGame.getColumn(i)) == -1)
        {
            i = r.nextInt(myGame.getColumnCount());
        }
        return i;
    }

    /**
     * Returns the column that would allow the agent to win.
     * 
     * You might want your agent to check to see if it has a winning move available to it so that
     * it can go ahead and make that move. Implement this method to return what column would
     * allow the agent to win.
     *
     * @return the column that would allow the agent to win.
     */
    public int iCanWin()
    {
        return 0;
    }

    /**
     * Returns the column that would allow the opponent to win.
     * 
     * You might want your agent to check to see if the opponent would have any winning moves
     * available so your agent can block them. Implement this method to return what column should
     * be blocked to prevent the opponent from winning.
     *
     * @return the column that would allow the opponent to win.
     */
    public int theyCanWin()
    {
        return 0;
    }

    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName()
    {
        return "My Agent";
    }
}
