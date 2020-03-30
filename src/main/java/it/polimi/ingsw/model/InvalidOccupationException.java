package it.polimi.ingsw.model;

public class InvalidOccupationException extends Exception{
    public InvalidOccupationException() { super("Cannot occupy a Cell with a doom"); }
    public InvalidOccupationException(String message) { super(message); }
    public InvalidOccupationException(String message, Throwable cause) { super(message, cause); }
    public InvalidOccupationException(Throwable cause) { super(cause); }
}
