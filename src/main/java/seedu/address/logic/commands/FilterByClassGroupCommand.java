package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASSGROUP;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.StudentInClassGroupPredicate;

/**
 * Finds and lists all persons in address book whose class group matches the specified keyword.
 * Keyword matching is case-insensitive.
 */
public class FilterByClassGroupCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all students who are in ALL of the specified classes "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: " + PREFIX_CLASSGROUP + "CLASS [" + PREFIX_CLASSGROUP + "CLASS]...\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_CLASSGROUP + "Math-1000 " + PREFIX_CLASSGROUP + "Physics-1800 "
            + "(finds students who are in BOTH Math-1000 AND Physics-1800)";

    private static final String PREDICATE_FIELD_NAME = "predicate";

    private final StudentInClassGroupPredicate predicate;

    /**
     * Creates a FilterByClassGroupCommand to filter the person list by the specified {@code predicate}.
     */
    public FilterByClassGroupCommand(StudentInClassGroupPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterByClassGroupCommand)) {
            return false;
        }

        FilterByClassGroupCommand otherFilterByClassGroupCommand = (FilterByClassGroupCommand) other;
        return predicate.equals(otherFilterByClassGroupCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add(PREDICATE_FIELD_NAME, predicate)
                .toString();
    }
}
