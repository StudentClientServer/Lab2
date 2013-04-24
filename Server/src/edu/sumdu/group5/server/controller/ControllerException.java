package edu.sumdu.group5.server.controller;


public class ControllerException extends Exception {
   
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new model exception.
     *
     * @param cause the cause
     */
    public ControllerException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     */
    public ControllerException(String message) {
        super(message);
    }

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new model exception.
     */
    public ControllerException() {
        super();
    }
}
