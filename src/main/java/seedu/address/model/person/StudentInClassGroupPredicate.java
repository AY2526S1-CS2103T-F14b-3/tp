package seedu.address.model.person;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code ClassGroup} matches the specified class group keyword.
 * Keyword matching is case-insensitive.
 */
public class StudentInClassGroupPredicate implements Predicate<Person> {
    private final java.util.Set<String> classGroups;

    /**
     * Constructs a {@code StudentInClassGroupPredicate} with the specified class group keywords.
     *
     * @param classGroups The set of class group keywords to match against.
     */
    public StudentInClassGroupPredicate(java.util.Set<String> classGroups) {
        this.classGroups = classGroups;
    }

    /**
     * Tests whether the given person belongs to any of the class groups specified by this predicate.
     * The comparison is case-insensitive.
     *
     * @param person The person to test.
     * @return true if the person belongs to any of the specified class groups, false otherwise.
     */
    @Override
    public boolean test(Person person) {
        return classGroups.stream()
                .allMatch(groupToMatch -> person.getClassGroups().stream()
                        .map(classGroup -> classGroup.getClassGroupName())
                        .anyMatch(name -> name.equalsIgnoreCase(groupToMatch)));
    }

    /**
     * Returns true if both predicates have the same class group keyword.
     *
     * @param other The other object to compare with.
     * @return true if both predicates are equal, false otherwise.
     */
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
        return classGroups.equals(otherStudentInClassGroupPredicate.classGroups);
    }

    /**
     * Returns a string representation of this predicate.
     *
     * @return A string containing the class group keywords.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).add("classGroups", classGroups).toString();
    }
}
