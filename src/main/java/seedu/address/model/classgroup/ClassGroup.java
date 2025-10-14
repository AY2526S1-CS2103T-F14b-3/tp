package seedu.address.model.classgroup;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.tag.Tag;

import seedu.address.model.person.Person;

/**
 * Represents a group of students with a specific class schedule.
 */
public class ClassGroup {
    private final Set<Person> students;
    private final TimeSlot timeSlot;

    //edit these 2 classes
    /** Subject taught to this class group. */
    private final String subject;
    /** List of assignment names for this class group. */
    private final ArrayList<String> assignmentList;
    //

    /**
     * Constructs a {@code ClassGroup} with a specific {@code TimeSlot}.
     * Constructs a {@code ClassGroup} with a specific {@code Set<Person> students}.
     */
    public ClassGroup(TimeSlot timeSlot, Set<Person> students, String subject, ArrayList<String> assignmentList) {
        requireAllNonNull(timeSlot, students, subject, assignmentList);
        this.students = students;
        this.timeSlot = timeSlot;
        this.subject = subject;
        this.assignmentList = assignmentList;
    }

    public Set<Person> getStudents() {
        return students;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public ArrayList<String> getAssignmentList() {
        return this.assignmentList;
    }

    public String getSubject() {
        return this.subject;
    }
}

