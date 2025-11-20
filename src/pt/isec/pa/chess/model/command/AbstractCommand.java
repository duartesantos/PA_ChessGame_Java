package pt.isec.pa.chess.model.command;

import pt.isec.pa.chess.model.ChessGame;

public abstract class AbstractCommand implements ICommand {
    protected ChessGame receiver;

    protected AbstractCommand(ChessGame receiver) {
        this.receiver = receiver;
    }
}