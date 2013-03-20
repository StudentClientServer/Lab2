package controller;

public class ControllerExcpetion extends Exception {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new model exception.
     *
     * @param cause the cause
     */
    ControllerExcpetion(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     */
    ControllerExcpetion(String message) {
        super(message);
    }

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     * @param cause the cause
     */
    ControllerExcpetion(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new model exception.
     */
    public ControllerExcpetion() {
        super();
    }
}
