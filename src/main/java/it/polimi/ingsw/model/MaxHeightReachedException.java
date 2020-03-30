package it.polimi.ingsw.model;

public class MaxHeightReachedException extends Exception {
    public MaxHeightReachedException() { super("Already reached max height"); }
    public MaxHeightReachedException(String message) { super(message); }
    public MaxHeightReachedException(String message, Throwable cause) { super(message, cause); }
    public MaxHeightReachedException(Throwable cause) { super(cause); }
}
