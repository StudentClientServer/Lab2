package model;


public class ServerException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new model exception.
     *
     * @param cause the cause
     */
    ServerException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     */
    ServerException(String message) {
        super(message);
    }

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     * @param cause the cause
     */
    ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new model exception.
     */
    public ServerException() {
        super();
    }

	public ServerException(Exception e) {
        super(e);
    }
}
