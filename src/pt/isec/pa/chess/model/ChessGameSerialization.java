package pt.isec.pa.chess.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ChessGameSerialization {
    private ChessGameSerialization(){}

    public static void save (String fileName,ChessGame game){
        try (ObjectOutputStream oos =new ObjectOutputStream(new FileOutputStream(fileName)))
        {
            oos.writeObject(game);
        }
        catch(Exception e){
            System.err.println("Error Saving Data!");
        }
    }

    public static ChessGame load (String fileName){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))){
            return (ChessGame) ois.readObject();
        }catch (Exception e){
            System.err.println("Error loading data");
        }
        return null;
    }
}
