package chess;

import java.io.InvalidObjectException;
import java.lang.IllegalStateException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

    private Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;
    private boolean checkMate;
    private ChessPiece onPassantVulnerable;
    private ChessPiece promoted;
    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }

        return mat;
    }

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckMate() {
        return checkMate;
    }

    public ChessPiece getOnPassantVulnerable() {
        return onPassantVulnerable;
    }

    public ChessPiece getPromoted() {
        return promoted;
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece piece : pieces) {
            if (piece instanceof King) {
                return (ChessPiece)piece;
            }
        }
        throw new IllegalStateException("The " + color + " king is not on the board.");
    }

    public boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece piece : opponentPieces) {
            boolean[][] mat = piece.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false;
        }
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[i].length; j++) {
                    if (mat[i][j]) {
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece captured = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, captured);
                        if (!testCheck) {
                            return false;
                        }
                    } 
                }
            }
        }
        return true;
    }

    public boolean[][] possibleMoves(ChessPosition chessPosition) {
        Position position = chessPosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }
    
    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
        validateTargerPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check!");
        }
        // Promoted
        promoted = null;
        if (board.piece(target) instanceof Pawn) {
            ChessPiece pawn = (ChessPiece)board.piece(target);
            if (pawn.getColor() == Color.WHITE && target.getRow() == 0 || pawn.getColor() == Color.BLACK && target.getRow() == 7) {
                promoted = pawn;
                promoted = replacePromotedPiece("Q");
            }
        }
        check = (testCheck(opponent(currentPlayer))) ? true : false;
        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {
            nextTurn();
        }
        // En Passant
        if (board.piece(target) instanceof Pawn && target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2) {
            onPassantVulnerable = (ChessPiece)board.piece(target);
        }
		return (ChessPiece)capturedPiece;
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(source);
        p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }
        // castling
        if (p instanceof King) {
            // kingside
            if (target.getColumn() == source.getColumn() + 2) {
                Position sourceKingRook = new Position(target.getRow(), target.getColumn() + 1);
                Position targetKingRook = new Position(target.getRow(), target.getColumn() - 1);
                ChessPiece kingRook = (ChessPiece)board.removePiece(sourceKingRook);
                kingRook.increaseMoveCount();
                board.placePiece(kingRook, targetKingRook);
            }
            // queenside
            else if (target.getColumn() == source.getColumn() - 2) {
                Position sourceQueenRook = new Position(target.getRow(), target.getColumn() - 2);
                Position targetQueenRook = new Position(target.getRow(), target.getColumn() + 1);
                ChessPiece queenRook = (ChessPiece)board.removePiece(sourceQueenRook);
                queenRook.increaseMoveCount();
                board.placePiece(queenRook, targetQueenRook);
            }
        }
        // en passant
        if (p instanceof Pawn) {
            if (target.getColumn() != source.getColumn() && capturedPiece == null) {
                Position capturedPosition = new Position(target.getRow(), target.getColumn());
                if (p.getColor() == Color.WHITE) {
                    capturedPosition.setRow(capturedPosition.getRow() + 1);
                } else {
                    capturedPosition.setRow(capturedPosition.getRow() - 1);
                }
                capturedPiece = board.removePiece(capturedPosition);
                piecesOnTheBoard.remove(capturedPiece);
                capturedPieces.add(capturedPiece);
            }
        }
		return capturedPiece;
	}

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece)board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);

        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
        // castling
        if (p instanceof King) {
            // kingside
            if (target.getColumn() == source.getColumn() + 2) {
                Position sourceKingRook = new Position(target.getRow(), target.getColumn() + 1);
                Position targetKingRook = new Position(target.getRow(), target.getColumn() - 1);
                ChessPiece kingRook = (ChessPiece)board.removePiece(targetKingRook);
                kingRook.decreaseMoveCount();
                board.placePiece(kingRook, sourceKingRook);
            }
            // queenside
            else if (target.getColumn() == source.getColumn() - 2) {
                Position sourceQueenRook = new Position(target.getRow(), target.getColumn() - 2);
                Position targetQueenRook = new Position(target.getRow(), target.getColumn() + 1);
                ChessPiece queenRook = (ChessPiece)board.removePiece(targetQueenRook);
                queenRook.decreaseMoveCount();
                board.placePiece(queenRook, sourceQueenRook);
            }
        }
        // en passant
        if (p instanceof Pawn) {
            if (target.getColumn() != source.getColumn() && capturedPiece == null) {
                ChessPiece capturedPawn = (ChessPiece)board.removePiece(target);
                Position capturedPosition = new Position(target.getRow(), target.getColumn());
                if (p.getColor() == Color.WHITE) {
                    capturedPosition.setRow(3);
                } else {
                    capturedPosition.setRow(4);
                }
                board.placePiece(capturedPawn, capturedPosition);
            }
        }
    }
	
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
        if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
            throw new ChessException("The chosen piece is not yours!");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("This piece has no moves avaliable!");
        }
	}

    private void validateTargerPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    public ChessPiece replacePromotedPiece(String type) {
        if (promoted == null) {
            throw new IllegalStateException("Does not have an promoted piece");
        }
        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);
        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);
        return newPiece;
    }

    private ChessPiece newPiece(String type, Color color) {
        switch (type) {
            case "Q": return new Queen(board, color);
            case "R": return new Rook(board, color);
            case "N": return new Knight(board, color);
            case "B": return new Bishop(board, color);
            default: throw new InvalidParameterException("Invalid type! The type have to be Q, R, N or B");
        }
    }

    private void initialSetup() {
        // White pieces
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));
        // Black Pieces
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
    }
    
}
