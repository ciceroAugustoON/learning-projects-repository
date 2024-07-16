package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    private ChessMatch chessMatch;

    public Pawn(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position p = new Position(0, 0);
        // White Pawns
        if (getColor() == Color.WHITE) {
            p.setValues(position.getRow() - 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setValues(position.getRow() - 2, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getMoveCount() == 0 && mat[p.getRow() + 1][p.getColumn()]) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setValues(position.getRow() - 1, position.getColumn() - 1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setValues(position.getRow() - 1, position.getColumn() + 1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
        } else { // Black Pawns
            p.setValues(position.getRow() + 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setValues(position.getRow() + 2, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getMoveCount() == 0 && mat[p.getRow() - 1][p.getColumn()]) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setValues(position.getRow() + 1, position.getColumn() - 1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setValues(position.getRow() + 1, position.getColumn() + 1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
        }
        // En Passant
        // White
        if (position.getRow() == 3) {
            Position left = new Position(position.getRow(), position.getColumn() - 1);
            if (getBoard().positionExists(left) && getBoard().thereIsAPiece(left) && getBoard().piece(left) == chessMatch.getOnPassantVulnerable()) {
                Position captureMovement = new Position(left.getRow() - 1, left.getColumn());
                if (getBoard().piece(captureMovement) == null) {
                    mat[captureMovement.getRow()][captureMovement.getColumn()] = true;
                }
            }
            Position right = new Position(position.getRow(), position.getColumn() + 1);
            if (getBoard().positionExists(right) && getBoard().thereIsAPiece(right) && getBoard().piece(right) == chessMatch.getOnPassantVulnerable()) {
                Position captureMovement = new Position(right.getRow() - 1, right.getColumn());
                if (getBoard().piece(captureMovement) == null) {
                    mat[captureMovement.getRow()][captureMovement.getColumn()] = true;
                }
            }
        }
        // Black
        else if (position.getRow() == 4) {
            Position left = new Position(position.getRow(), position.getColumn() - 1);
            if (getBoard().positionExists(left) && getBoard().thereIsAPiece(left) && getBoard().piece(left) == chessMatch.getOnPassantVulnerable()) {
                Position captureMovement = new Position(left.getRow() + 1, left.getColumn());
                if (getBoard().piece(captureMovement) == null) {
                    mat[captureMovement.getRow()][captureMovement.getColumn()] = true;
                }
            }
            Position right = new Position(position.getRow(), position.getColumn() + 1);
            if (getBoard().positionExists(right) && getBoard().thereIsAPiece(right) && getBoard().piece(right) == chessMatch.getOnPassantVulnerable()) {
                Position captureMovement = new Position(right.getRow() + 1, right.getColumn());
                if (getBoard().piece(captureMovement) == null) {
                    mat[captureMovement.getRow()][captureMovement.getColumn()] = true;
                }
            }
        }
        return mat;
    }
    @Override
    public String toString() {
        return "P";
    }
}
