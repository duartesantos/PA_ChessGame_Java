package pt.isec.pa.chess.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.GameState;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameTest {
    ChessGame chessGame;

    @BeforeEach
    public void setUpChessGame() {
        chessGame = new ChessGame("White","Black");
    }

    @Test
    public void isWhiteInitialPlayer () {
        assertTrue(chessGame.isWhiteToMove());
    }

    @Test
    public void testValidMove () {
        boolean moved = chessGame.move("e2","e4");
        assertTrue(moved);
    }

    @Test
    public void testInvalidMove () {
        boolean moved = chessGame.move("e2","e5");
        assertFalse(moved);
    }

    @Test
    public void testPromotePawn () {
        chessGame.importGame("WHITE, Pa7");
        boolean moved = chessGame.move("a7","a8");
        assertTrue(moved);
        assertTrue(chessGame.isWaitingForPromotion());
    }

    @Test
    public void testGameExport () {
        chessGame.move("c2","c4");
        String export = chessGame.exportGame();

        ChessGame newGame = new ChessGame();
        newGame.importGame(export);
        assertFalse(chessGame.isWhiteToMove());
    }

    @Test
    public void testIsCheck() {
        chessGame.importGame("WHITE,ke1,ra1,ra8,ke8,Qh5");
        chessGame.move("h5","e8");
        assertEquals(GameState.CHECK,chessGame.getGameState());
    }
}