package pt.isec.pa.chess.model.command;

public interface ICommand {
    boolean execute();
    boolean undo();
}