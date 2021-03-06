package seedu.address.model.person;

import java.util.Comparator;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
//@@author Sivalavida
/**
 * Represents a Person's age in Retail Analytics.
 * Guarantees: immutable; is valid as declared in {@link #isValidAge(String)}
 */
public class Age implements Comparable<Age>{

    /**
     * The minimum allowed age
     */
    public static int MIN_AGE = 1;

    /**
     * The maximum allowed age
     */
    public static int MAX_AGE = 120;

    public static final String MESSAGE_AGE_CONSTRAINTS =
            String.format("Age must be an integer between %d and %d", MIN_AGE, MAX_AGE);
    public static final String AGE_VALIDATION_REGEX = "\\d{1,3}";
    public final String value;

    /**
     * Constructs a {@code Age}.
     *
     * @param age A valid age of a person.
     */
    public Age(String age) {
        requireNonNull(age);
        checkArgument(isValidAge(age), MESSAGE_AGE_CONSTRAINTS);
        this.value = age;
    }

    /**
     * Returns true if a given string is a valid age of a person
     */
    public static boolean isValidAge(String test) {
        if (test.matches(AGE_VALIDATION_REGEX)){
            int age = Integer.parseInt(test);
            if(age>=MIN_AGE && age<=MAX_AGE){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the int value of the persons Age
     */
    private int getNumericalAge() {
        return Integer.parseInt(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Age // instanceof handles nulls
                && this.value.equals(((Age) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(Age other) {
        return (other.getNumericalAge() - this.getNumericalAge());
    }
}
