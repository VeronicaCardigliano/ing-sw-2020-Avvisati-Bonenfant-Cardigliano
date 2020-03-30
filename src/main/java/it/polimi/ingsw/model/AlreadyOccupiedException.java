package it.polimi.ingsw.model;

public class AlreadyOccupiedException extends Exception {
    public AlreadyOccupiedException() { super("This position is already occupied"); }
    public AlreadyOccupiedException(String message) { super(message); }
    public AlreadyOccupiedException(String message, Throwable cause) { super(message, cause); }
    public AlreadyOccupiedException(Throwable cause) { super(cause); }
}
