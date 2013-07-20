package exception;

import java.io.PrintWriter;

public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1L;

	/**
	 * Holds the reference to the exception or error that caused this exception
	 * to be thrown.
	 */
	private Throwable cause = null;

	/**
	 * Constructs a new <code>BaseTargetException</code> without specified
	 * detail message.
	 */
	public BaseException() {
		super();
	}

	/**
	 * Constructs a new <code>BaseTargetException</code> with specified detail
	 * message.
	 * 
	 * @param msg
	 *            The error message.
	 */
	public BaseException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a new <code>BaseTargetException</code> with specified nested
	 * <code>Throwable</code>.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public BaseException(Throwable cause) {
		super();
		this.cause = cause;
	}

	/**
	 * Constructs a new <code>BaseTargetException</code> with specified detail
	 * message and nested <code>Throwable</code>.
	 * 
	 * @param msg
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public BaseException(String msg, Throwable cause) {
		super(msg);
		this.cause = cause;
	}

	/**
	 * {@inheritDoc}
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * Returns the detail message string of this throwable. If it was created
	 * with a null message, returns the following: (cause==null ? null :
	 * cause.toString()).
	 * 
	 * @return String message string of the throwable
	 */
	public String getMessage() {
		if (super.getMessage() != null) {
			return super.getMessage();
		} else if (cause != null) {
			return cause.toString();
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void printPartialStackTrace(PrintWriter out) {
		super.printStackTrace(out);
	}
}
