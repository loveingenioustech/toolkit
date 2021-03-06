package exception;

public class GenerateException extends BaseException {

	private static final long serialVersionUID = -1L;

	/**
	 * <p>
	 * Constructs a new <code>GenerateException</code> without specified detail
	 * message.
	 * </p>
	 */
	public GenerateException() {
		super();
	}

	/**
	 * <p>
	 * Constructs a new <code>GenerateException</code> with specified detail
	 * message.
	 * </p>
	 * 
	 * @param msg
	 *            The error message.
	 */
	public GenerateException(String msg) {
		super(msg);
	}

	/**
	 * <p>
	 * Constructs a new <code>GenerateException</code> with specified nested
	 * <code>Throwable</code>.
	 * </p>
	 * 
	 * @param cause
	 *            The <code>Exception</code> or <code>Error</code> that caused
	 *            this exception to be thrown.
	 */
	public GenerateException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Constructs a new <code>GenerateException</code> with specified detail
	 * message and nested <code>Throwable</code>.
	 * </p>
	 * 
	 * @param msg
	 *            The error message.
	 * @param cause
	 *            The <code>Exception</code> or <code>Error</code> that caused
	 *            this exception to be thrown.
	 */
	public GenerateException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
