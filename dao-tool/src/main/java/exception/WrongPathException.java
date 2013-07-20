package exception;

public class WrongPathException extends BaseException {

	private static final long serialVersionUID = -1L;

	/**
	 * <p>
	 * Constructs a new <code>WrongPathException</code> without specified detail
	 * message.
	 * </p>
	 */
	public WrongPathException() {
		super();
	}

	/**
	 * <p>
	 * Constructs a new <code>WrongPathException</code> with specified detail
	 * message.
	 * </p>
	 * 
	 * @param msg
	 *            The error message.
	 */
	public WrongPathException(String msg) {
		super(msg);
	}

	/**
	 * <p>
	 * Constructs a new <code>WrongPathException</code> with specified nested
	 * <code>Throwable</code>.
	 * </p>
	 * 
	 * @param cause
	 *            The <code>Exception</code> or <code>Error</code> that caused
	 *            this exception to be thrown.
	 */
	public WrongPathException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constructs a new <code>WrongPathException</code> with specified detail
	 * message and nested <code>Throwable</code>.
	 * </p>
	 * 
	 * @param msg
	 *            The error message.
	 * @param cause
	 *            The <code>Exception</code> or <code>Error</code> that caused
	 *            this exception to be thrown.
	 */
	public WrongPathException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
