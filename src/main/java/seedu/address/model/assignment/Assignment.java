package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidAssignmentName(String)}
 */
public class Assignment {

    public static final String MESSAGE_CONSTRAINTS = "Assigment names should be alphanumeric";
    public static final String VALIDATION_REGEX = "\\p{Alnum}+";

    public final String assignmentName;

    /**
     * Constructs a {@code Assignment}.
     *
     * @param assignmentName A valid assignment name.
     */
    public Assignment(String assignmentName) {
        requireNonNull(assignmentName);
        checkArgument(isValidAssignmentName(assignmentName), MESSAGE_CONSTRAINTS);
        this.assignmentName = assignmentName;
    }

    /**
     * Returns true if a given string is a valid assignment name.
     */
    public static boolean isValidAssignmentName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Assignment)) {
            return false;
        }

        Assignment otherAssignment = (Assignment) other;
        return assignmentName.equals(otherAssignment.assignmentName);
    }

    @Override
    public int hashCode() {
        return assignmentName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + assignmentName + ']';
    }

}
