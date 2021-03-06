package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.*;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.order.Order;
import seedu.address.model.order.exceptions.DuplicateOrderException;

//@@author qinghao1
/**
 * Adds an order to the address book
 */
public class AddOrderCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "addorder";

    public static final String MESSAGE_USAGE = COMMAND_WORD

            + ": Creates new order given a person's email, and at least one (Product ID, Quantity, Price)\n"
            + "Parameters: "
            + PREFIX_EMAIL + "EMAIL (Must be an existing person) "
            + PREFIX_SUBORDER + "Product ID, Quantity, Price\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_EMAIL + "john@example.com "
            + PREFIX_SUBORDER + "1 5 $3.00 "
            + PREFIX_SUBORDER + "2 4 $2.50 "
            + PREFIX_SUBORDER + "3 1 $100 ";

    public static final String MESSAGE_SUCCESS = "New order added.";
    public static final String MESSAGE_DUPLICATE_ORDER = "This order already exists in the app.";
    public static final String MESSAGE_INVALID_ORDER = "The order is invalid. Check that the person and products exist.";

    private final Order toAdd;

    /**
     * Creates an {@code AddOrderCommand} to add the specified {@code Order}
     */
    public AddOrderCommand(Order order) {
        requireNonNull(order);
        toAdd = order;
    }

    /**
     * Checks that the {@code AddOrderCommand} is valid (i.e. {@code Order} to be created is valid)
     */
    public boolean isValid() {
        ReadOnlyAddressBook ab = this.model.getAddressBook();
        return toAdd.isValid(ab.getPersonList(), ab.getProductList());
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        //Check that order is valid
        if (!isValid()) {
            //If order invalid, decrement order counter because it was incremented during creation
            Order.decrementOrderCounter();
            throw new CommandException(MESSAGE_INVALID_ORDER);
        }
        try {
            model.addOrder(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (DuplicateOrderException e) {
            throw new CommandException(MESSAGE_DUPLICATE_ORDER);
        }

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddOrderCommand // instanceof handles nulls
                && toAdd.equals(((AddOrderCommand) other).toAdd));
    }
}
