package it.polimi.ingsw.client.cli;

/**
 * Exception thrown if user choice is not listed
 */
public class InvalidOptionException extends Exception{
    public InvalidOptionException(String message) {
        super(message);
    }
}
