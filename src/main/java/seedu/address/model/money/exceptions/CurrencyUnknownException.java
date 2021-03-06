package seedu.address.model.money.exceptions;

//@@author YingxuH
/**
 * Signals that the money objects do not have matching currencies.
 */

public class CurrencyUnknownException extends RuntimeException {
    public CurrencyUnknownException(String aMessage){
        super(aMessage);
    }
}