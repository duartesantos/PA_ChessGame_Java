package pt.isec.pa.chess.ui.res;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundManager {
    private SoundManager() { }

    private static MediaPlayer mp;

    public static boolean play(String filename) {
        try {
            var url = SoundManager.class.getResource("sounds/en/" + filename);
            if (url == null) return false;
            String path = url.toExternalForm();
            Media music = new Media(path);
            stop();
            mp = new MediaPlayer(music);
            mp.setStartTime(Duration.ZERO);
            mp.setStopTime(music.getDuration());
            mp.setAutoPlay(true);

            // mp.setOnPlaying(()-> System.out.println("onPlaying"));
            // mp.setOnReady(()-> System.out.println("onReady"));
            // mp.setOnEndOfMedia(()-> System.out.println("onEndMedia"));
            // mp.setOnStopped(()-> System.out.println("onStopped"));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPlaying() {
        return mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public static void stop() {
        if (mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING)
            mp.stop();
    }

    public static List<String> getSoundList() {
        File soundsDir = new File(SoundManager.class.getResource("sounds/en/").getFile());
        return Arrays.stream(soundsDir.listFiles()).map(x -> x.getName()).toList();
    }

    public static MediaPlayer getMediaPlayer () {
        return mp;
    }

    public static void playMovesSounds (char pieceSymbol, String from, String to, boolean captured, char capturedSymbol, boolean isCheck){

        List <String> sounds = new ArrayList<>();

        //quando movemos uma peça
        sounds.add(getPieceName(pieceSymbol).toLowerCase() + ".mp3");

        //para buscarmos a origem dessa peça
        sounds.add(Character.toLowerCase(from.charAt(0)) + ".mp3");
        sounds.add(from.charAt(1) + ".mp3");

        //verficamos se a peça capturou outra peça (se sim, adiciona à lista de audio)
        if (captured && capturedSymbol != '\0'){
            sounds.add("captured.mp3");
            sounds.add(getPieceName(capturedSymbol).toLowerCase()+ ".mp3");
        }

        //destino para onde a peça se moveu
        sounds.add(Character.toLowerCase(to.charAt(0)) + ".mp3");
        sounds.add(to.charAt(1) + ".mp3");

        if (isCheck){
            sounds.add("check.mp3");
        }
        playSoundSequence(sounds);
    }


    public static void playSoundSequence (List <String> sounds){
        if (sounds == null || sounds.isEmpty()){
            return;
        }
        String sound = sounds.remove(0);
        if (!SoundManager.play(sound)){
            return;
        }

        SoundManager.getMediaPlayer().setOnEndOfMedia (() ->{
            playSoundSequence(sounds);
        });

    }


    private static String getPieceName (char symbol) {
        return switch (Character.toLowerCase(symbol)) {
            case 'k' -> "king";
            case 'q' -> "queen";
            case 'r' -> "rook";
            case 'b' -> "bishop";
            case 'n' -> "knight";
            case 'p' -> "pawn";
            default -> "unknown";
        };
    }
}
