package edu.sumdu.group5.exception;

/**
 * The Class ClientException.
 */
public class ClientException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new client exception.
     *
     * @param message the message
     */
    public ClientException(String message) {
        super(message);
    }

    /**
    * Instantiates a new client exception.
    *
    * @param e the e
    */
    public ClientException(Exception e) {
        super(e);
    }
    
    /**
     * Instantiates a new client exception.
     *
     * @param e the e
     */
    public ClientException(String str, Exception e) {
        super(str,e);
    }
}
