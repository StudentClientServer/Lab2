package client;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientException.
 */
public class ClientException extends Exception {

	/** The error code. */
	private ErrorCode errorCode;
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new client exception.
	 *
	 * @param errorCode the error code
	 * @param message the message
	 */
	public ClientException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new client exception.
	 *
	 * @param errorCode the error code
	 * @param message the message
	 * @param cause the cause
	 */
	public ClientException(ErrorCode errorCode, String message,
			Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

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
}
