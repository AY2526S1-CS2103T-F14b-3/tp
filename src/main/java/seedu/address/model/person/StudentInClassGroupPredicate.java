package seedu.address.model.person;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code ClassGroup} matches any of the keywords given.
 */
public class StudentInClassGroupPredicate implements Predicate<Person> {
    private final String classGroup;

    public StudentInClassGroupPredicate(String classGroup) {
        this.classGroup = classGroup;
    }

    @Override
    public boolean test(Person person) {
        return person.getClassGroups().contains(classGroup);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudentInClassGroupPredicate)) {
            return false;
        }

        StudentInClassGroupPredicate otherStudentInClassGroupPredicate = (StudentInClassGroupPredicate) other;
        return classGroup.equals(otherStudentInClassGroupPredicate.classGroup);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("classGroup", classGroup).toString();
    }
}
