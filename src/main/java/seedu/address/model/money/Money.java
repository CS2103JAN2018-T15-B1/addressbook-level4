package seedu.address.model.money;

import java.text.NumberFormat;
import java.util.*;
import java.io.Serializable;
import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.RoundingMode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.model.money.exceptions.CurrencyUnknownException;
import seedu.address.model.money.exceptions.MismatchedCurrencyException;
import seedu.address.model.money.exceptions.ObjectNotMoneyException;

//@@author YingxuH
/**
 * Represent an amount of money in any currency.
 *
 * This class assumes decimal currency, without funky divisions
 * like 1/5 and so on. Money objects are immutable.
 * Most operations involving more than one Money object will throw a
 * MismatchedCurrencyException if the currencies don't match.
 *
 */
public class Money implements Comparable<Money>, Serializable {

    public static final String MONEY_VALIDATION_REGEX_WITHOUT_CURRENCY = "\\d+(\\.\\d+)?";
    public static final String MONEY_VALIDATION_REGEX_WITH_UNKNOWN_PREFIX = "(\\p{Alpha}+|\\p{Sc})\\s*\\d+(\\.\\d+)?";
    public static final String MONEY_PREFIX = "(\\p{Alpha}+|\\p{Sc})\\s*";
    public static final String MONEY_DIGITS = "\\s*\\d+(\\.\\d+)?";

    public static final String MESSAGE_MONEY_CONSTRAINTS =
            String.format("price should only contains currency sy/mbol(optional) and digits," +
                    " and it cannot be negative");
    public static final String MESSAGE_MONEY_SYMBOL_CONSTRAINTS =
            String.format("currency code should be limited ISO 4277 code");

    /**
     * The money amount.
     * Never null.
     */
    private BigDecimal fAmount;

    /**
     * The currency of the money, such as US Dollars or Euros.
     * Never null.
     */
    private final Currency fCurrency;

    /**
     * The rounding style to be used.
     */
    private final RoundingMode fRounding;

    public static BigDecimal DEFAULT_AMOUNT = new BigDecimal(0.00);

    /**
     * The default currency to be used if no currency is passed to the constructor.
     * To be initialized by the static init().
     */
    public static Currency DEFAULT_CURRENCY = Currency.getInstance("SGD");

    /**
     * The default rounding style to be used if no currency is passed to the constructor.
     */
    public static RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    /**
     * String representation for Money class.
     */
    public final String repMoney;

    private int fHashCode;
    private static final int HASH_SEED = 23;
    private static final int HASH_FACTOR = 37;

    /**
     * Full constructor.
     *
     * @param aAmount is required, can be positive or negative. The number of
     * decimals in the amount cannot exceed the maximum number of
     * decimals for the given Currency.
     * @param aCurrency
     * @param aRoundingStyle is required, must match a rounding style used by
     * BigDecimal.
     */
    public Money(BigDecimal aAmount, Currency aCurrency, RoundingMode aRoundingStyle){
        checkNotNull(aAmount, aCurrency, aRoundingStyle);
        fAmount = aAmount;
        fCurrency = aCurrency;
        fRounding = aRoundingStyle;
        repMoney = fCurrency.getSymbol() + " " + fAmount.toPlainString();
    }

    /**
     * Constructor taking only the money amount.
     * @param aAmount is required, can be positive or negative.
     */
    public Money(BigDecimal aAmount){
        this(aAmount, DEFAULT_CURRENCY, DEFAULT_ROUNDING);
    }

    /**
     * Constructor taking the money amount and currency.
     *
     * The rounding style takes a default value.
     * @param aAmount is required, can be positive or negative.
     * @param aCurrency is required.
     */
    public Money(BigDecimal aAmount, Currency aCurrency){
        this(aAmount, aCurrency, DEFAULT_ROUNDING);
    }

    /**
     * Constructor taking the money amount and the rounding mode.
     * @param aAmount is required, can be positive or negative.
     */
    public Money(BigDecimal aAmount, RoundingMode aRoundingStyle){
        this(aAmount, DEFAULT_CURRENCY, aRoundingStyle);
    }

    /**
     * empty constructor
     */
    public Money() {
        this(DEFAULT_AMOUNT, DEFAULT_CURRENCY, DEFAULT_ROUNDING);
    }
    /** Return the amount passed to the constructor. */
    public BigDecimal getAmount() { return fAmount; }

    /** Return the currency passed to the constructor, or the default currency. */
    public Currency getCurrency() { return fCurrency; }

    /** Return the rounding style passed to the constructor, or the default rounding style. */
    public RoundingMode getRoundingStyle() { return fRounding; }

    /**
     * Returns true if a given string is a valid Money.
     */
    public static boolean isValidMoney(String test) {
        return isValidMoneyWithoutCurrency(test) || isValidMoneyWithUnknownPrefix(test);
    }

    /**
     * Returns true if a given string is a valid Money with currency symbol code.
     */
    public static boolean isValidMoneyWithUnknownPrefix(String test) {
        return test.matches(MONEY_VALIDATION_REGEX_WITH_UNKNOWN_PREFIX);
    }

    /**
     * Return the currency that the symbol represents if the symbol is valid, otherwise returns the default
     * currency
     *
     * @param symbol
     * @return
     */
    public static Currency parseCurrency(String symbol) {
        for (Currency currency: Currency.getAvailableCurrencies()) {
            String code = currency.getSymbol();
            if (symbol.equals(code)) {
                return currency;
            }
        }
        throw new CurrencyUnknownException("unknown currency: " + symbol +"\n"+ MESSAGE_MONEY_SYMBOL_CONSTRAINTS);
    }


    /**
     * Returns true if a given string is a valid Money without currency symbol.
     */
    public static boolean isValidMoneyWithoutCurrency(String test) {
        return test.matches(MONEY_VALIDATION_REGEX_WITHOUT_CURRENCY);
    }

    /**
     * Parses a {@code String price} into a {@code price}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code price} is invalid.
     */
    public static Money parsePrice(String price) throws IllegalValueException {
        return ParserUtil.parsePrice(price);
    }

    /**
     * Return true only if aThat Money has the same currency
     * as this Money. For the public use.
     * Assume the aThat is also a money object
     */
    public boolean isSameCurrencyAs(Money aThat){
        boolean result = false;
        if ( aThat != null ) {
            result = this.fCurrency.equals(aThat.fCurrency);
        }
        return result;
    }

    /** Return true only if the amount is positive. */
    public boolean isPlus(){
        return fAmount.compareTo(ZERO) > 0;
    }

    public boolean isMinus(){
        return fAmount.compareTo(ZERO) <  0;
    }

    public boolean isZero(){
        return fAmount.compareTo(ZERO) ==  0;
    }

    /**
     * Add aThat Money to this Money.
     * Currencies must match.
     */
    public Money plus(Object aThat){
        checkObjectIsMoney(aThat);
        Money that = (Money)aThat;
        checkCurrenciesMatch(that);
        return new Money(fAmount.add(that.fAmount), fCurrency, fRounding);
    }

    /**
     * Subtract aThat Money from this Money.
     * Currencies must match.
     */
    public Money minus(Object aThat){
        checkObjectIsMoney(aThat);
        Money that = (Money)aThat;
        checkCurrenciesMatch(that);
        return new Money(fAmount.subtract(that.fAmount), fCurrency, fRounding);
    }

    /**
     * Sum a collection of Money objects.
     * Currencies must match.
     *
     * @param aMoneys collection of Money objects, all of the same currency.
     * If the collection is empty, then a zero value is returned.
     */
    public static Money sum(Collection<Money> aMoneys){
        Money sum = new Money(ZERO);
        for(Money money : aMoneys){
            sum = sum.plus(money);
        }
        return sum;
    }

    /**
     * Equals (insensitive to scale).
     *
     * Return true only if the amounts are equal.
     * Currencies must match. This method is not synonymous with the equals method.
     */
    public boolean eq(Object aThat){
        checkObjectIsMoney(aThat);
        Money that = (Money)aThat;
        checkCurrenciesMatch(that);
        return compareAmount(that) == 0;
    }

    public boolean gt(Object aThat){
        checkObjectIsMoney(aThat);
        Money that = (Money)aThat;
        checkCurrenciesMatch(that);
        return compareAmount(that) > 0;
    }

    public boolean gteq(Object aThat){
        checkObjectIsMoney(aThat);
        Money that = (Money)aThat;
        checkCurrenciesMatch(that);
        return compareAmount(that) >= 0;
    }

    public boolean lt(Object aThat){
        checkObjectIsMoney(aThat);
        Money that = (Money)aThat;
        checkCurrenciesMatch(that);
        return compareAmount(that) < 0;
    }

    public boolean lteq(Object aThat){
        checkObjectIsMoney(aThat);
        Money that = (Money)aThat;
        checkCurrenciesMatch(that);
        return compareAmount(that) <= 0;
    }

    /**
     * Multiply this Money by an integral factor.
     *
     * The scale of the returned Money is equal to the scale of 'this'
     * Money.
     */
    public Money times(int aFactor){
        BigDecimal factor = new BigDecimal(aFactor);
        BigDecimal newAmount = fAmount.multiply(factor);
        return new Money(newAmount, fCurrency, fRounding);
    }

    /**
     * Multiply this Money by an non-integral factor (having a decimal point).
     */
    public Money times(double aFactor){
        BigDecimal newAmount = fAmount.multiply(asBigDecimal(aFactor));
        newAmount = newAmount.setScale(getNumDecimalsForCurrency(), fRounding);
        return  new Money(newAmount, fCurrency, fRounding);
    }

    /**
     * Returns
     * getAmount().getPlainString() + space + getCurrency().getSymbol().
     *
     * The return value uses the default locale/currency, and will not
     * always be suitable for display to an end user.
     */
    public String toString(){ return repMoney; }

    /**
     * This equal is sensitive to scale.
     *
     * For example, 10 is not equal to 10.00
     * The eq method, on the other hand, is not
     * sensitive to scale.
     */
    public boolean equals(Object aThat){
        if (this == aThat) return true;
        if (! (aThat instanceof Money) ) return false;
        Money that = (Money)aThat;
        //the object fields are never null :
        boolean result = (this.fAmount.equals(that.fAmount) );
        result = result && (this.fCurrency.equals(that.fCurrency) );
        result = result && (this.fRounding == that.fRounding);
        return result;
    }

    public int hashCode(){
        if ( fHashCode == 0 ) {
            fHashCode = HASH_SEED;
            fHashCode = HASH_FACTOR * fHashCode + fAmount.hashCode();
            fHashCode = HASH_FACTOR * fHashCode + fCurrency.hashCode();
            fHashCode = HASH_FACTOR * fHashCode + fRounding.hashCode();
        }
        return fHashCode;
    }

    /**
     * Compare by amount, then currency and rounding method.
     * @param aThat
     * @return
     */
    public int compareTo(Money aThat) {
        final int EQUAL = 0;

        if ( this == aThat ) return EQUAL;

        //the object fields are never null
        int comparison = this.fAmount.compareTo(aThat.fAmount);
        if ( comparison != EQUAL ) return comparison;

        comparison = this.fCurrency.getCurrencyCode().compareTo(
                aThat.fCurrency.getCurrencyCode()
        );
        if ( comparison != EQUAL ) return comparison;


        comparison = this.fRounding.compareTo(aThat.fRounding);
        if ( comparison != EQUAL ) return comparison;

        return EQUAL;
    }

    private void checkNotNull(BigDecimal aAmount, Currency aCurrency, RoundingMode aRoundingStyle){
        if( aAmount == null ) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if( aCurrency == null ) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if( aRoundingStyle == null) {
            throw new IllegalArgumentException("rounding style cannot be null");
        }
        if ( aAmount.scale() > aCurrency.getDefaultFractionDigits() ) {
            throw new IllegalArgumentException(
                    "Number of decimals is " + aAmount.scale() + ", but currency only takes " +
                            aCurrency.getDefaultFractionDigits() + " decimals."
            );
        }
    }

    private int getNumDecimalsForCurrency(){
        return fCurrency.getDefaultFractionDigits();
    }

    /**
     * throw new exception if the other Monday is not the same currency.
     * @param aThat
     */
    private void checkCurrenciesMatch(Money aThat){
        if (! this.fCurrency.equals(aThat.getCurrency())) {
            throw new MismatchedCurrencyException(
                    aThat.getCurrency() + " doesn't match the expected currency : " + fCurrency
            );
        }
    }

    private void checkObjectIsMoney(Object aThat) {
        if (! (aThat instanceof Money) ) {
            throw new ObjectNotMoneyException(
                    aThat.getClass() + " doesn't match with Money class"
            );
        }
    }

    /** Ignores scale: 0 same as 0.00 */
    private int compareAmount(Money aThat){
        return this.fAmount.compareTo(aThat.fAmount);
    }

    private BigDecimal asBigDecimal(double aDouble){
        String asString = Double.toString(aDouble);
        return new BigDecimal(asString);
    }
} 