package pt.isec.pa.chess.modelui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelUI {

    public static final String PROP_LEARNING_MODE = "learningMode";
    public static final String PROP_SOUND = "soundEnabled";
    private boolean soundEnable = true;
    private boolean learningMode = false;
    PropertyChangeSupport pcs;

    public ModelUI(){
        pcs = new PropertyChangeSupport(this);
    }


    public void setLearningMode (boolean value){
        boolean old = this.learningMode;
        this.learningMode = value;
        pcs.firePropertyChange(PROP_LEARNING_MODE,old,value);
    }

    public boolean isLearningMode() {
        return learningMode;
    }

    public boolean isSoundEnable() {
        return soundEnable;
    }

    public void toggleSound () {
        boolean oldValue = this.soundEnable;
        this.soundEnable = !this.soundEnable;
        pcs.firePropertyChange(PROP_SOUND,oldValue,soundEnable);
    }

    public void addPropertyChangeListener (String property, PropertyChangeListener listener){
        pcs.addPropertyChangeListener(property,listener);
    }
}
