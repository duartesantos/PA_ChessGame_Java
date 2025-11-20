package pt.isec.pa.chess.model.command;
import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.pieces.Pawn;

public class MoveCommand extends AbstractCommand {


    private final String from;      // Ex: "e2"
    private final String to;        // Ex: "e4"

    private Piece movedPiece;       // A peça que foi movida (ex: peão)
    private Piece capturedPiece;    // A peça capturada (se houver)
    private boolean movedPieceOriginalHasMoved;
    private boolean capturedPieceOriginalHasMoved;
    private boolean rookOriginalHasMoved;
    private boolean originalWhiteToMove;   // Quem era o jogador antes da jogada
    private String originalEnPassant;      // Qual era a casa válida para en passant


    // Jogadas especiais
    private boolean wasPromotion = false;
    private Piece originalPawn = null;

    private boolean wasEnPassant = false;
    private String enPassantCapturePos = null;

    private boolean wasCastling = false;
    private Piece rookPiece = null;
    private char rookFromCol, rookToCol;
    private int rookRow;

    public MoveCommand(ChessGame receiver, String from, String to) {
        super(receiver);
        this.from = from;
        this.to = to;
    }


    @Override
    public boolean execute() {

        originalWhiteToMove = receiver.isWhiteToMove();
        originalEnPassant = receiver.getEnPassantTarget();

        movedPiece = receiver.getBoard().getPiece(from.charAt(0), Character.getNumericValue(from.charAt(1)));
        capturedPiece = receiver.getBoard().getPiece(to.charAt(0), Character.getNumericValue(to.charAt(1)));
        // Detectar se foi roque
        if (movedPiece != null && movedPiece.getSymbol() == 'K' || movedPiece.getSymbol() == 'k') {
            if (Math.abs(from.charAt(0) - to.charAt(0)) == 2) {
                wasCastling = true;
                rookRow = from.charAt(1) - '0';
                if (to.charAt(0) == 'g') {
                    // roque pequeno
                    rookFromCol = 'h';
                    rookToCol = 'f';
                } else {
                    // roque grande
                    rookFromCol = 'a';
                    rookToCol = 'd';
                }
                //rookPiece = board.getPiece(rookToCol, rookRow);
                rookPiece = receiver.getPieceAt(rookFromCol, rookRow);

            }
        }

        // Detectar en passant
        if (movedPiece instanceof Pawn && to.equals(receiver.getEnPassantTarget())) {
            wasEnPassant = true;
            enPassantCapturePos = "" + to.charAt(0) + from.charAt(1); // onde estava o peão capturado
            capturedPiece = receiver.getPieceAt(to.charAt(0), Character.getNumericValue(from.charAt(1)));
        }

        // Guardar o peão original antes da promoção
        if (movedPiece instanceof Pawn) {
            int rowTo = Character.getNumericValue(to.charAt(1));
            if ((movedPiece.getIsWhite() && rowTo == 8) || (!movedPiece.getIsWhite() && rowTo == 1)) {
                wasPromotion = true;
                originalPawn = movedPiece;
            }
        }

        movedPieceOriginalHasMoved = movedPiece.getHasMoved();
        if (capturedPiece != null)
            capturedPieceOriginalHasMoved = capturedPiece.getHasMoved();

        movedPieceOriginalHasMoved = movedPiece.getHasMoved();

        if (wasCastling && rookPiece != null)
            rookOriginalHasMoved = rookPiece.getHasMoved();

        return receiver.move(from, to);
    }

    @Override
    public boolean undo() {
        if (movedPiece == null)
            return false;

        char colFrom = from.charAt(0);
        int rowFrom = Character.getNumericValue(from.charAt(1));
        char colTo = to.charAt(0);
        int rowTo = Character.getNumericValue(to.charAt(1));

// 1. Remover a peça que foi movida da casa de destino
        receiver.removePieceAt(colTo, rowTo);

// 2. Restaurar a peça movida à posição original
        if (wasPromotion && originalPawn != null) {
            receiver.setPieceAt(originalPawn, colFrom, rowFrom);
        } else {
            receiver.setPieceAt(movedPiece, colFrom, rowFrom);
        }

// 3. Restaurar a peça capturada, se houve
        if (capturedPiece != null) {
            if (wasEnPassant && enPassantCapturePos != null) {
                char capCol = enPassantCapturePos.charAt(0);
                int capRow = Character.getNumericValue(enPassantCapturePos.charAt(1));
                receiver.setPieceAt(capturedPiece, capCol, capRow);
            } else {
                receiver.setPieceAt(capturedPiece, colTo, rowTo);
            }
        }

// 4. Desfazer o roque, se foi feito
        if (wasCastling && rookPiece != null) {
            receiver.removePieceAt(rookToCol, rookRow);
            receiver.setPieceAt(rookPiece, rookFromCol, rookRow);
        }

// 5. Restaurar o estado do jogo
        receiver.setWhiteToMove(originalWhiteToMove);
        receiver.setEnPassantTarget(originalEnPassant);
        receiver.updateGameState();

        movedPiece.setHasMoved(movedPieceOriginalHasMoved);
        if (capturedPiece != null)
            capturedPiece.setHasMoved(capturedPieceOriginalHasMoved);

        if (wasCastling && rookPiece != null)
            rookPiece.setHasMoved(rookOriginalHasMoved);

        return true;

    }





}
