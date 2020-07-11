package org.financer.shared.exceptions;

/**
 * Abstract root exception class for all exception within the financer project.
 *
 * @author Raphael Müßeler
 */
public abstract class FinancerException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;

    public FinancerException(String message, Throwable cause) {
        super(message, cause);
        this.messageKey = null;
        this.args = new Object[]{};
    }

    /**
     * Instantiates the exception with message key and argument for that message key. The message key and the args are
     * used to translate the message via i18n.
     *
     * @param messageKey message key
     * @param args       arguments for message translation
     */
    public FinancerException(String message, String messageKey, Object... args) {
        super(String.format(message, args));
        this.messageKey = messageKey;
        this.args = args;
    }

    /**
     * Returns the message key that is used to translate the message. Each derivative of this class has it's own default
     * message key, but it can be changed when constructing the exception.
     *
     * @return message key
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Returns the arguments for this message key.
     *
     * @return arguments
     */
    public Object[] getArguments() {
        return args;
    }
}
