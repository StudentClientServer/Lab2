package exceptions;

public class ServerException extends Exception {

	  /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new model exception.
     *
     * @param cause the cause
     */
    public ServerException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new model exception.
     */
    public ServerException() {
        super();
    }
	
	/**
     * Instantiates a new model exception.
     */
    public ServerException(Exception e) {
        super(e);
    }
}
