package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.assignment.Assignment;

/**
 * Jackson-friendly version of {@link Assignment}.
 */
class JsonAdaptedAssignment {

    private final String assignmentName;

    /**
     * Constructs a {@code JsonAdaptedAssignment} with the given {@code assignmentName}.
     */
    @JsonCreator
    public JsonAdaptedAssignment(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    /**
     * Converts a given {@code Assignment} into this class for Jackson use.
     */
    public JsonAdaptedAssignment(Assignment source) {
        assignmentName = source.assignmentName;
    }

    @JsonValue
    public String getTagName() {
        return assignmentName;
    }

    /**
     * Converts this Jackson-friendly adapted tag object into the model's {@code Tag} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted tag.
     */
    public Assignment toModelType() throws IllegalValueException {
        if (!Assignment.isValidAssignmentName(assignmentName)) {
            throw new IllegalValueException(Assignment.MESSAGE_CONSTRAINTS);
        }
        return new Assignment(assignmentName);
    }

}
