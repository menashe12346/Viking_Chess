
import java.util.ArrayList;
import java.util.*;

public class GameLogic implements PlayableLogic{
    private final Piece[][] board;
    public ArrayList<Square> squares;
    private final ConcretePlayer playerOne;
    private final ConcretePlayer playerTwo;
    private Player currentPlayer;

    private King King;

    private int numOfDefenders=12;

    private int numOfAttackers=24;

    private boolean isGameFinished=false;
    public GameLogic() {
        this.board = new Piece[getBoardSize()][getBoardSize()];
        this.squares = new ArrayList<>();
        this.playerOne = new ConcretePlayer(true);
        this.playerTwo = new ConcretePlayer(false);
        this.currentPlayer = playerOne;
        reset();
    }

    @Override
    public boolean move(Position a, Position b) {

        if (isGameFinished) {
            return false;
        }

        //check if the source is empty
        if (board[a.getRow()][a.getCol()]== null) return false;

        //check if the destination is empty
        if(board[b.getRow()][b.getCol()]!= null) return false;

        // Check if there are no pieces in the path
        if (!isPathClear(a, b)) return false;

        Piece piece = board[a.getRow()][a.getCol()];

        //check current Player
        if(piece.getOwner()!=this.currentPlayer) return false;

        if(isValidPosition(b.getRow(),b.getCol()) && (a.getRow()==b.getRow() || a.getCol()==b.getCol())) {

            //check if the user is moving the Pawn to the squares at the edge
            if (((b.getCol() == 0 && b.getRow() == 0) ||
                    (b.getCol() == 0 && b.getRow() == getBoardSize() - 1) ||
                    (b.getCol() == getBoardSize() - 1 && b.getRow() == 0) ||
                    (b.getCol() == getBoardSize() - 1 && b.getRow() == getBoardSize() - 1)) &&
                    board[a.getRow()][a.getCol()] instanceof Pawn) {
                return false;
            }

            if(piece instanceof Pawn){
                board[b.getRow()][b.getCol()] = piece;
                ((Pawn) piece).move(a,b);
                for(int i=0; i<squares.size();i++){
                    if(squares.get(i).position.getRow() == b.getRow() && squares.get(i).position.getCol() == b.getCol()){
                        squares.get(i).addPiece(piece);
                        break;
                    }
                }

                //check for capturing Pawns
                capturePieceRow(b.getRow(), b.getCol(),-1,-2, (Pawn) piece);
                capturePieceRow(b.getRow(), b.getCol(),1,2, (Pawn) piece);

                capturePieceCol(b.getRow(), b.getCol(),-1,-2, (Pawn) piece);
                capturePieceCol(b.getRow(), b.getCol(),1,2, (Pawn) piece);

                captureEdgePiece(0,1,0,2, piece);
                captureEdgePiece(1,0,2,0, piece);
                captureEdgePiece(0,getBoardSize()-2,0,getBoardSize()-3, piece);
                captureEdgePiece(1,getBoardSize() -1,2,getBoardSize()-1, piece);
                captureEdgePiece(getBoardSize()-2,0,getBoardSize()-3,0, piece);
                captureEdgePiece(getBoardSize()-1,1,getBoardSize()-1,2, piece);
                captureEdgePiece(getBoardSize()-2,getBoardSize()-1,getBoardSize()-3,getBoardSize()-1, piece);
                captureEdgePiece(getBoardSize()-1,getBoardSize()-2,getBoardSize()-1,getBoardSize()-3, piece);

                //check if number of attackers are 0 and player1 wins
                if(numOfAttackers==0) {
                    board[a.getRow()][a.getCol()] = null;
                    playerOne.increaseWins();
                    printStatistics(playerOne);
                    isGameFinished=true;
                    return true;
                }

                //check for attacker win
                Position kingPosition = King.KingStepsHistory.get(King.KingStepsHistory.size() - 1);
                int row = kingPosition.getRow();
                int col = kingPosition.getCol();
                if((!isValidPosition(row+1,col) || (board[row+1][col]!=null && board[row+1][col].getOwner()==playerTwo )) &&
                   (!isValidPosition(row-1,col) || (board[row-1][col]!=null && board[row-1][col].getOwner()==playerTwo )) &&
                   (!isValidPosition(row,col+1) || (board[row][col+1]!=null && board[row][col+1].getOwner()==playerTwo )) &&
                   (!isValidPosition(row,col-1) || (board[row][col-1]!=null && board[row][col-1].getOwner()==playerTwo ))
                ) {
                    board[a.getRow()][a.getCol()] = null;
                    playerTwo.increaseWins();
                    printStatistics(playerTwo);
                    isGameFinished=true;
                    return true;
                }

            } else{
                board[b.getRow()][b.getCol()] = piece;
                King = (King) piece;
                ((King) piece).move(a,b);
                for(int i=0; i< squares.size();i++){
                    if(squares.get(i).position.getRow() == b.getRow() && squares.get(i).position.getCol() == b.getCol()){
                        squares.get(i).addPiece(piece);
                        break;
                    }
                }

                //check if the king have reached one of the corners
                if (((b.getCol() == 0 && b.getRow() == 0) ||
                        (b.getCol() == 0 && b.getRow() == getBoardSize() - 1) ||
                        (b.getCol() == getBoardSize() - 1 && b.getRow() == 0) ||
                        (b.getCol() == getBoardSize() - 1 && b.getRow() == getBoardSize() - 1))) {
                    board[a.getRow()][a.getCol()] = null;
                    playerOne.increaseWins();
                    printStatistics(playerOne);
                    isGameFinished=true;
                    return true;
                }
            }

            board[a.getRow()][a.getCol()] = null;

            this.currentPlayer = getOppositePlayer();

            return true;
        }
        return false;
    }

    private boolean captureEdgePiece(int i_1,int j_1,int i_2,int j_2, Piece piece){
        if(board[i_1][j_1]!=null && board[i_1][j_1].getOwner()==getOppositePlayer() && board[i_2][j_2]!=null && board[i_2][j_2].getOwner()==currentPlayer){
            board[i_1][j_1] = null;
            ((Pawn) piece).increaseKill();
            if(isSecondPlayerTurn()){
                numOfDefenders--;
            } else {
                numOfAttackers--;
            }
            return true;
        }
        return false;
    }

    private void printStatistics(ConcretePlayer winner){
        printStepsHistory(winner);
        printAsterisks();
        System.out.println();
        printNumOfKills(winner);
        printAsterisks();
        System.out.println();
        printNumOfSquares(winner);
        printAsterisks();
        System.out.println();
        printNumOfPiecesOnSquare(winner);
    }

    private void printAsterisks(){
        for(int i=0;i<75;i++){
            System.out.print('*');
        }
    }

    private void printStepsHistory(ConcretePlayer winner){

        ArrayList<Piece> Defenders = new ArrayList<>();
        ArrayList<Piece> Attackers = new ArrayList<>();

        for (int i=0; i<board.length;i++){
            for (int j=0;j<board[i].length;j++){
                if(board[i][j]!=null) {
                    if (board[i][j] instanceof Pawn) {
                        if(((Pawn) board[i][j]).name.charAt(0)=='D') {
                            Defenders.add(board[i][j]);
                        }else{
                            Attackers.add(board[i][j]);
                        }
                    } else {
                            Defenders.add(board[i][j]);
                    }
                }
            }
        }

        // Create an instance of ComparatorChess
        ComparatorChess comparator = new ComparatorChess(ComparatorChess.ComparisonType.StepsHistory, winner);

        // Sort the ArrayList of pieces using the comparator
        Collections.sort(Defenders, comparator);
        Collections.sort(Attackers, comparator);

        if(winner==playerOne){
            for (Piece piece : Defenders) {
                if (piece instanceof Pawn) {
                    ((Pawn) piece).printStepsHistory();
                } else {
                    ((King) piece).printStepsHistory();
                }
            }

            for (Piece piece : Attackers) {
                ((Pawn) piece).printStepsHistory();
            }

        } else{
            for (Piece piece : Attackers) {
                ((Pawn) piece).printStepsHistory();
            }

            for (Piece piece : Defenders) {
                if (piece instanceof Pawn) {
                    ((Pawn) piece).printStepsHistory();
                } else {
                    ((King) piece).printStepsHistory();
                }
            }
        }

    }

    private void printNumOfKills(ConcretePlayer winner){

        ArrayList<Piece> Pieces = new ArrayList<>();

        for (int i=0; i<board.length;i++){
            for (int j=0;j<board[i].length;j++){
                if(board[i][j]!=null && board[i][j] instanceof Pawn) {
                    Pieces.add(board[i][j]);
                }
            }
        }

        // Create an instance of ComparatorChess
        ComparatorChess comparator = new ComparatorChess(ComparatorChess.ComparisonType.NumOfKills, winner);

        // Sort the ArrayList of pieces using the comparator
        Collections.sort(Pieces, comparator);

        for (Piece piece : Pieces) {
            if (piece instanceof Pawn) {
                ((Pawn) piece).printKills();
            }
        }
    }

    private void printNumOfSquares(ConcretePlayer winner){

        ArrayList<Piece> Pieces = new ArrayList<>();

        for (int i=0; i<board.length;i++){
            for (int j=0;j<board[i].length;j++){
                if(board[i][j]!=null) {
                    Pieces.add(board[i][j]);
                }
            }
        }

        // Create an instance of ComparatorChess
        ComparatorChess comparator = new ComparatorChess(ComparatorChess.ComparisonType.Squares, winner);

        // Sort the ArrayList of pieces using the comparator
        Collections.sort(Pieces, comparator);

        for (Piece piece : Pieces) {
            if (piece instanceof Pawn) {
                ((Pawn) piece).printDistance();
            } else{
                ((King) piece).printDistance();
            }
        }
    }

    private void printNumOfPiecesOnSquare(ConcretePlayer winner) {

        // Create an instance of ComparatorChess
        ComparatorSquares comparator = new ComparatorSquares(winner);

        // Sort the ArrayList of pieces using the comparator
        Collections.sort(squares, comparator);

        boolean atLeastOneSquare=false;
        for (Square square : squares) {
            if(square.pieces.size()>1) {
                atLeastOneSquare=true;
                System.out.println("(" + square.position.getRow() + ", " + square.position.getCol() + ")" + square.pieces.size() + " pieces");
            }
        }

        if(atLeastOneSquare){
            printAsterisks();
        }
        System.out.println();
    }
    private Player getOppositePlayer() {
        if (this.currentPlayer == playerOne) return playerTwo;
        return playerOne;
    }

    private void capturePieceRow(int row, int col, int i, int j, Pawn pawn) {
        Player OppositePlayer = getOppositePlayer();
    if ((row + i) < getBoardSize() && (row + i) >= 0 && (row + j) < getBoardSize() && (row + j)>=0) {
        if (board[row + i][col] != null && board[row + j][col] != null && board[row + i][col] instanceof Pawn && board[row + j][col] instanceof Pawn) {
            if (board[row + i][col].getOwner() == OppositePlayer && board[row + j][col].getOwner() == this.currentPlayer) {
                board[row + i][col] = null;
                pawn.increaseKill();
                if(isSecondPlayerTurn()){
                    numOfDefenders--;
                } else {
                    numOfAttackers--;
                }
            }
        }
    } else if ((row + i) == getBoardSize() - 1 && board[row + i][col] != null && board[row + i][col] instanceof Pawn && board[row + i][col].getOwner() == OppositePlayer) {
        board[row + i][col] = null;
        pawn.increaseKill();
        if(isSecondPlayerTurn()){
            numOfDefenders--;
        } else {
            numOfAttackers--;

        }
    }else if ((row + i) == 0 && board[row + i][col] != null && board[row + i][col] instanceof Pawn && board[row + i][col].getOwner() == OppositePlayer) {
        board[row + i][col] = null;
        pawn.increaseKill();
        if(isSecondPlayerTurn()){
            numOfDefenders--;
        } else {
            numOfAttackers--;
        }
    }
    }

    private void capturePieceCol(int row, int col, int i, int j, Pawn pawn) {
        Player OppositePlayer = getOppositePlayer();

        if((col + i)<getBoardSize() && (col + i)>=0 && (col+j)<getBoardSize() && (col+j)>=0) {
            if (board[row][col + i] != null && board[row][col + j] != null && board[row][col + i] instanceof Pawn && board[row][col + j] instanceof Pawn) {
                if (board[row][col + i].getOwner() == OppositePlayer && board[row][col + j].getOwner() == this.currentPlayer) {
                    board[row][col + i] = null;
                    pawn.increaseKill();
                    if(isSecondPlayerTurn()){
                        numOfDefenders--;
                    } else {
                        numOfAttackers--;
                    }
                }
            }
        }else if((col+i)==getBoardSize()-1 && board[row][col+i] != null && board[row][col + i] instanceof Pawn && board[row][col+i].getOwner() == OppositePlayer){
            board[row][col+i] = null;
            pawn.increaseKill();
            if(isSecondPlayerTurn()){
                numOfDefenders--;
            } else {
                numOfAttackers--;
            }
        }else if((col+i)==0 && board[row][col+i] != null && board[row][col + i] instanceof Pawn && board[row][col+i].getOwner() == OppositePlayer){
            board[row][col+i] = null;
            pawn.increaseKill();
            if(isSecondPlayerTurn()){
                numOfDefenders--;
            } else {
                numOfAttackers--;
            }
        }
    }

    private boolean isPathClear(Position a, Position b) {
        int row1 = a.getRow();
        int col1 = a.getCol();
        int row2 = b.getRow();
        int col2 = b.getCol();

        // Check if there are no pieces in the horizontal path
        if (row1 == row2) {
            int start = Math.min(col1, col2) + 1;
            int end = Math.max(col1, col2);
            for (int col = start; col < end; col++) {
                if (board[row1][col] != null) {
                    return false;
                }
            }
        }
        // Check if there are no pieces in the vertical path
        else if (col1 == col2) {
            int start = Math.min(row1, row2) + 1;
            int end = Math.max(row1, row2);
            for (int row = start; row < end; row++) {
                if (board[row][col1] != null) {
                    return false;
                }
            }
        }
        else {
            // Invalid move (not horizontal or vertical)
            return false;
        }

        return true;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < getBoardSize() && col >= 0 && col < getBoardSize();
    }

    @Override
    public Piece getPieceAtPosition(Position position) {
        return board[position.getRow()][position.getCol()];
    }

    @Override
    public Player getFirstPlayer() {
        return playerOne;
    }

    @Override
    public Player getSecondPlayer() {
        return playerTwo;
    }

    @Override
    public boolean isGameFinished() {
        return isGameFinished;
    }

    @Override
    public boolean isSecondPlayerTurn() {
        return currentPlayer==playerTwo;
    }

    @Override
    public void reset() {

        isGameFinished=false;

        numOfDefenders=12;
        numOfAttackers=24;

        for(int i=0;i<getBoardSize();i++){
            for(int j=0;j<getBoardSize();j++){
                board[i][j] = null;
            }
        }

        this.squares = new ArrayList<>();

        this.currentPlayer = playerTwo;

        int center = getBoardSize() / 2;
        board[center][center] = new King(this.playerOne, "K7", new Position(center,center));
        King = (King) board[center][center];

        board[center][center-2] = new Pawn(this.playerOne, "D1", new Position(center,center-2));
        board[center-1][center-1] = new Pawn(this.playerOne, "D2",new Position(center-1,center-1));
        board[center][center-1] = new Pawn(this.playerOne, "D3",new Position(center,center-1));
        board[center+1][center-1] = new Pawn(this.playerOne, "D4", new Position(center+1,center-1));
        board[center-2][center] = new Pawn(this.playerOne, "D5", new Position(center-2,center));
        board[center-1][center] = new Pawn(this.playerOne, "D6", new Position(center-1,center));
        board[center+1][center] = new Pawn(this.playerOne, "D8", new Position(center+1,center));
        board[center+2][center] = new Pawn(this.playerOne, "D9", new Position(center+2,center));
        board[center-1][center+1] = new Pawn(this.playerOne, "D10", new Position(center-1,center+1));
        board[center][center+1] = new Pawn(this.playerOne, "D11", new Position(center,center+1));
        board[center+1][center+1] = new Pawn(this.playerOne, "D12", new Position(center+1,center+1));
        board[center][center+2] = new Pawn(this.playerOne, "D13", new Position(center,center+2));

        this.board[3][0] = new Pawn(this.playerTwo, "A1", new Position(3,0));
        this.board[4][0] = new Pawn(this.playerTwo, "A2", new Position(4,0));
        this.board[5][0] = new Pawn(this.playerTwo, "A3", new Position(5,0));
        this.board[6][0] = new Pawn(this.playerTwo, "A4", new Position(6,0));
        this.board[7][0] = new Pawn(this.playerTwo, "A5", new Position(7,0));


        this.board[getBoardSize()-1][3] = new Pawn(this.playerTwo, "A8", new Position(getBoardSize()-1,3));
        this.board[getBoardSize()-1][4] = new Pawn(this.playerTwo, "A10", new Position(getBoardSize()-1,4));
        this.board[getBoardSize()-1][5] = new Pawn(this.playerTwo, "A14", new Position(getBoardSize()-1,5));
        this.board[getBoardSize()-1][6] = new Pawn(this.playerTwo, "A16", new Position(getBoardSize()-1,6));
        this.board[getBoardSize()-1][7] = new Pawn(this.playerTwo, "A18", new Position(getBoardSize()-1,7));

        this.board[0][3] = new Pawn(this.playerTwo, "A7", new Position(0,3));
        this.board[0][4] = new Pawn(this.playerTwo, "A9", new Position(0,4));
        this.board[0][5] = new Pawn(this.playerTwo, "A11", new Position(0,5));
        this.board[0][6] = new Pawn(this.playerTwo, "A15", new Position(0,6));
        this.board[0][7] = new Pawn(this.playerTwo, "A17", new Position(0,7));

        this.board[3][getBoardSize()-1] = new Pawn(this.playerTwo, "A20", new Position(3,getBoardSize()-1));
        this.board[4][getBoardSize()-1] = new Pawn(this.playerTwo, "A21", new Position(4,getBoardSize()-1));
        this.board[5][getBoardSize()-1] = new Pawn(this.playerTwo, "A22", new Position(5,getBoardSize()-1));
        this.board[6][getBoardSize()-1] = new Pawn(this.playerTwo, "A23", new Position(6,getBoardSize()-1));
        this.board[7][getBoardSize()-1] = new Pawn(this.playerTwo, "A24", new Position(7,getBoardSize()-1));


        this.board[center][1] = new Pawn(this.playerTwo, "A6", new Position(center,1));
        this.board[center][getBoardSize()-2] = new Pawn(this.playerTwo, "A19", new Position(center,getBoardSize()-2));
        this.board[1][center] = new Pawn(this.playerTwo, "A12", new Position(1,center));
        this.board[getBoardSize()-2][center] = new Pawn(this.playerTwo, "A13", new Position(getBoardSize()-2,center));

        for(int i=0;i<getBoardSize();i++){
            for(int j=0;j<getBoardSize();j++){
                squares.add(new Square(i,j));
                if(board[i][j]!=null){
                    squares.get(squares.size()-1).addPiece(board[i][j]);
                }
            }
        }

    }

    @Override
    public void undoLastMove() {

    }

    @Override
    public int getBoardSize() {
        return 11;
    }
}