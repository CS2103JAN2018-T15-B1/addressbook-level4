package seedu.address.model.money.exceptions;

//@@author YingxuH
/**
 * Signals that the money objects do not have matching currencies.
 */

public class ObjectNotMoneyException extends RuntimeException {
    public ObjectNotMoneyException(String aMessage){
        super(aMessage);
    }
}