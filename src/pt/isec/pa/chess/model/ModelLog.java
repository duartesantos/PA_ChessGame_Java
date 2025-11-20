package pt.isec.pa.chess.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that manages logging of messages within the model layer.
 *
 * Supports notifying listeners whenever a new log message is added or logs are cleared.
 * Useful for debugging, diagnostics, or providing runtime feedback in UI components.
 */
public class ModelLog {
    private static final ModelLog instance = new ModelLog();
    private final List<String> logs;
    private final PropertyChangeSupport pcs;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the log list and property change support.
     */
    private ModelLog() {
        logs = new ArrayList<>();
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Returns the singleton instance of the ModelLog class.
     *
     * @return the unique ModelLog instance
     */
    public static ModelLog getInstance() {
        return instance;
    }

    /**
     * Adds a new message to the log and notifies listeners.
     *
     * @param msg the log message to add
     */
    public void add(String msg) {
        logs.add(msg);
        pcs.firePropertyChange(null, null, msg);
    }

    /**
     * Retrieves all log messages as a new list.
     *
     * @return a list of all logged messages
     */
    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    /**
     * Clears all log messages and notifies listeners with an empty list.
     */
    public void clear() {
        logs.clear();
        pcs.firePropertyChange(null, null, new ArrayList<>(logs));
    }

    /**
     * Adds a listener that will be notified whenever a property change occurs.
     *
     * @param listener the  PropertyChangeListener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
