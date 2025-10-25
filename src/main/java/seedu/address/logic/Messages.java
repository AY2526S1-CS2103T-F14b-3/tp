package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_INVALID_ASSIGNMENT_IN_PERSON = "Person does not have the assignment";
    public static final String MESSAGE_MARK_PERSON_SUCCESS = "Marked assignment %1$s of %2$s";
    public static final String ALREADY_MARKED = "Assignment is already marked";
    public static final String MESSAGE_UNMARK_PERSON_SUCCESS = "Unmarked assignment %1$s of %2$s";
    public static final String ALREADY_UNMARKED = "Assignment is already unmarked";
    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(StringUtil.toTitleCase(person.getName().fullName))
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Level: ")
                .append(person.getLevel())
                .append("; Classes: ");
        // join ClassGroups with ", " between entries
        String classGroups = person.getClassGroups().stream()
                .map(cg -> StringUtil.toTitleCase(cg.classGroupName))
                .collect(Collectors.joining(", "));
        builder.append(classGroups);

        // Only show assignments if the person has any
        if (!person.getAssignments().isEmpty()) {
            builder.append("; Assignments: ");
            // join Assignments with ", "
            String assignments = person.getAssignments().stream()
                    .map(assignment -> StringUtil.toTitleCase(assignment.toString()))
                    .collect(Collectors.joining(", "));
            builder.append(assignments);
        }

        return builder.toString();
    }

}
