package com.misday.pg;

public class PGException extends RuntimeException {
	private static final long serialVersionUID = 0;
	private Throwable cause;

	/**
	 * Constructs a PrtlException with an explanatory message.
	 *
	 * @param message
	 *            Detail about the reason for the exception.
	 */
	public PGException(String message) {
		super(message);
	}

	/**
	 * Constructs a new PrtlException with the specified cause.
	 * 
	 * @param cause
	 *            The cause.
	 */
	public PGException(Throwable cause) {
		super(cause.getMessage());
		this.cause = cause;
	}

	/**
	 * Returns the cause of this exception or null if the cause is nonexistent
	 * or unknown.
	 *
	 * @return the cause of this exception or null if the cause is nonexistent
	 *         or unknown.
	 */
	@Override
	public Throwable getCause() {
		return this.cause;
	}

}
