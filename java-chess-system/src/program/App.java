package program;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ArrayList;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class App {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();
		
		while (!chessMatch.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc);
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), chessMatch.possibleMoves(source));
				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc);
				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				if (capturedPiece != null) {
					captured.add(capturedPiece);
				}
				if (chessMatch.getPromoted() != null) {
					System.out.println("Entere a piece for promotion (B/N/R/Q): ");
					String promotion = sc.nextLine();
					chessMatch.replacePromotedPiece(promotion);
				}
			} catch (InputMismatchException | ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}			
		}
		UI.clearScreen();
		UI.printMatch(chessMatch, captured);
	}
	
}