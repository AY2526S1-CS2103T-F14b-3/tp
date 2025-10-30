<frontmatter>
  title: "User Guide"
</frontmatter>

# User Guide

TutorTrack is a **desktop app for managing contacts, optimized for use via the Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, TutorTrack can help you efficiently track students, organise classes and assignments faster than traditional GUI apps.

<!-- Removed legacy AB3 description during migration to TutorTrack -->

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2526S1-CS2103T-F14b-3/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for TutorTrack.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar tutortrack.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 l/2 c/Physics-1800` : Adds a student contact named `John Doe` to TutorTrack.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [c/CLASS] [a/ASSIGNMENT]` can be used as `n/John Doe c/Physics-1800 a/Assignment 1` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[c/CLASS]…​` can be used as ` ` (i.e. 0 times), `c/Physics-1800`, `c/Math-1400 c/Physics-1800` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE`, `p/PHONE n/NAME` is also acceptable.

* Parameters are case-insensitive.<br>
  e.g. `n/NAME` and `N/NAME` are treated the same.

* Parameter values are case-insensitive.<br>
  e.g. `n/John Doe` and `n/john doe` are treated as same names.

* `CLASS` and `ASSIGNMENT` parameter values allows only alphanumeric values, spaces and hyphens.<br>
  e.g. `c/Math-1000`, `c/Computer Science 101`, `a/Project Draft 1` are all acceptable but `c/Math@1000`, `c/Math(1000)` is not.

* `NAME` parameter values allows only alphanumeric values, spaces, hyphens, periods, apostrophes and slashes.<br>
  e.g. `n/John Doe`, `n/Betsy O'Connor`, `n/Mary-Jane Smith Jr.` are all acceptable but `n/John@Doe`, `n/John#1` is not.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

<div markdown="block" class="alert alert-warning">

**:exclamation: Duplicate Checks:**<br>

* Students:
  * Students are considered duplicates if they have the same name (case-insensitive) AND phone number.
  * Students with same name but different phone numbers can be added with a _warning_.
  * Students with same phone number but different names can be added with a _warning_.
* Assignments:
  * Assignments are considered duplicates if the student already has an assignment with the assignment name (case-insensitive) from the same class.
* Classes:
  * Classes are considered duplicates if the student is already enrolled in a class with the same class name (case-insensitive).
</div>

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessageNew.png)

Format: `help`


### Adding a student: `add`

Adds a student to TutorTrack.

Format: `add n/NAME p/PHONE l/LEVEL [c/CLASS]…​`

<div markdown="span" class="alert alert-primary"><span class="fas fa-lightbulb" aria-hidden="true"></span> <strong>Tip:</strong>
A student can have any number of classes and assignments (including 0)
</div>

Examples:
* `add n/John Doe p/98765432 l/2`
* `add n/Betsy Crowe p/1234567 c/Chemistry-1400 l/3 c/Math-1000`

### Deleting a student : `delete`

Deletes the specified student from TutorTrack.

Format: `delete INDEX`

* Deletes the student at the specified `INDEX`.
* The index refers to the index number shown in the displayed student list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd student in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st student in the results of the `find` command.


### Listing all students : `list`

Shows a list of all students in the address book.

Format: `list`

### Editing a student : `edit`

Edits an existing student in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [l/LEVEL]`

* Edits the student at the specified `INDEX`. The index refers to the index number shown in the displayed student list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* Classes and assignments cannot be edited via the `edit` command.

Examples:
*  `edit 1 p/91234567` Edits the phone number of the 1st student to be `91234567`.
*  `edit 2 n/Betsy Crower l/3` Edits the name of the 2nd student to be `Betsy Crower` and the level to `3`.

### Locating students by name: `find`

Finds students whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g `hans` will match `Hans`
* The order of the keywords does not matter. e.g. `Hans Bo` will match `Bo Hans`
* Only the name is searched.
* Only full words will be matched e.g. `Han` will not match `Hans`
* Students matching at least one keyword will be returned (i.e. `OR` search).
  e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find John` returns `john` and `John Doe`
* `find alex john` returns `alex`, `john`<br>
  ![result for 'find alex john'](images/findAlexJohnResult.png)

### Adding assignment(s) to a student: `assign`

Adds one or more assignments to the specified student in TutorTrack.

Format: `assign INDEX c/CLASS a/ASSIGNMENT [a/ASSIGNMENT]...`

* Adds assignment(s) belonging to specified class to the student at the specified `INDEX`.
* The index refers to the index number shown in the displayed student list.
* The index **must be a positive integer** 1, 2, 3, …​
* At least one assignment must be provided.
* Duplicate assignments will not be added.

Examples:
* `list` followed by `assign 1 c/Math-2000 a/MathHW1 a/MathHW2` adds two assignments from class `Math-2000` to the 1st student in the address book.
* `find John` followed by `assign 2 c/History a/ProjectDraft` adds an assignment from class `History` to the 2nd student in the results of the `find` command.

### Deleting assignment(s) from a student: `unassign`

Deletes one or more assignments from the specified student in TutorTrack.

Format: `unassign INDEX c/CLASS a/ASSIGNMENT [a/ASSIGNMENT]...`

* Deletes assignment(s) belonging to specified class from the student at the specified `INDEX`.
* The index refers to the index number shown in the displayed student list.
* The index **must be a positive integer** 1, 2, 3, …​
* At least one assignment must be provided.

Examples:
* `list` followed by `unassign 1 c/Math-2000 a/MathHW1 a/MathHW2` deletes two assignments from class `Math-2000` from the 1st student in the address book.
* `find John` followed by `unassign 2 c/History a/ProjectDraft` deletes an assignment from class `History` from the 2nd student in the results of the `find` command.

### Adding an assignment to all students in a class: `assignall`

Assigns an assignment to all students in the specified class.

Format: `assignall c/CLASS a/ASSIGNMENT`

* Assigns the specified assignment to all students in the specified class.
* Students who already have the specified assignment in the specified class will be skipped.
* If all students in the specified class already have the specified assignment, no changes will be made.

Examples:
* `assignall c/Math-2000 a/MathHW1` assigns the assignment `MathHW1` to all students in the class `Math-2000`.
* `assignall c/Chemistry-1400 a/ProjectDraft` assigns the assignment `ProjectDraft` to all students in the class `Chemistry-1400`.

### Deleting an assignment from all students in a class: `unassignall`

Unassigns an assignment from all students in the specified class.

Format: `unassignall c/CLASS a/ASSIGNMENT`

* Unassigns the specified assignment from all students in the specified class.
* Only students who have the specified assignment in the specified class will be affected.
* If no students in the specified class have the specified assignment, no changes will be made.

Examples:
* `unassignall c/Math-2000 a/MathHW1` unassigns the assignment `MathHW1` from all students in the class `Math-2000`.
* `unassignall c/Chemistry-1400 a/ProjectDraft` unassigns the assignment `ProjectDraft` from all students in the class `Chemistry-1400`.

### Marking an assignment as completed: `mark`

Marks the assignment of student(s) identified by the index number(s) used in the displayed student list and the assignment name.

Format: `mark [INDEX]... [INDEX_RANGE]...  c/CLASS a/ASSIGNMENT`

* Marks the assignment belonging to the specified class as completed for the student(s) at the specified `INDEX`(es) or `INDEX_RANGE`(s).
* The index(es) refers to the index number shown in the displayed student list.
* The index(es) **must be a positive integer** 1, 2, 3, …​
* At least one index or index range must be provided.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
For efficient marking of assignments for a specific class, use the [`filter`](#filtering-students-by-class-filter) command first to display only students in that class, then use `mark` with the filtered list.
</div>

Examples:
* `list` followed by `mark 1 3-5 c/Math-2000 a/MathHW1` marks the assignment `MathHW1` from class `Math-100` as completed for the 1st, 3rd, 4th and 5th students in the student list.
* `find John` followed by `mark 2 c/History a/ProjectDraft` marks the assignment `ProjectDraft` from class `History` as completed for the 2nd student in the results of the `find` command.
* `filter c/Math-2000` followed by `mark 1-10 c/Math-2000 a/MathHW1` marks the assignment for the first 10 students in the Math-2000 class (recommended workflow).

### Unmarking an assignment as not completed: `unmark`

Unmarks the assignment of student(s) identified by the index number(s) used in the displayed student list and the assignment name.

Format: `unmark [INDEX]... [INDEX_RANGE]...  c/CLASS a/ASSIGNMENT`

* Unmarks the assignment belonging to the specified class as not completed for the student(s) at the specified `INDEX`(es) or `INDEX_RANGE`(s).
* The index(es) refers to the index number shown in the displayed student list.
* The index(es) **must be a positive integer** 1, 2, 3, …​
* At least one index or index range must be provided.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
Like [`mark`](#marking-an-assignment-as-completed-mark), use [`filter`](#filtering-students-by-class-filter) first for efficient unmarking by class.
</div>

Examples:
* `list` followed by `unmark 1 3-5 c/Math-2000 a/MathHW1` unmarks the assignment `MathHW1` from class `Math-100` as not completed for the 1st, 3rd, 4th and 5th students in the student list.
* `find John` followed by `unmark 2 c/History a/ProjectDraft` unmarks the assignment `ProjectDraft` from class `History` as not completed for the 2nd student in the results of the `find` command.
* `filter c/Chemistry-1400` followed by `unmark 5-8 c/Chemistry-1400 a/Lab Report` unmarks the assignment for students 5-8 in the Chemistry-1400 class (recommended workflow).

### Adding class(es) to a student: `addclass`

Adds one or more classes to the specified student in TutorTrack.

Format: `addclass INDEX c/CLASS [c/CLASS]...`

* Adds class(es) to the student at the specified `INDEX`.
* The index refers to the index number shown in the displayed student list.
* The index **must be a positive integer** 1, 2, 3, …​
* At least one class must be provided.
* Class names must be alphanumeric and may contain spaces or hyphens.
* Class names must start with an alphanumeric character (letter or digit).
* Duplicate classes will not be added.

Examples:
* `list` followed by `addclass 1 c/Math-1000 c/Physics-2000` adds two classes to the 1st student in the address book.
* `find John` followed by `addclass 2 c/Chemistry-1400` adds a class to the 2nd student in the results of the `find` command.

### Deleting class(es) from a student: `deleteclass`

Deletes one or more classes from the specified student in TutorTrack.

Format: `deleteclass INDEX c/CLASS [c/CLASS]...`

* Deletes class(es) from the student at the specified `INDEX`.
* The index refers to the index number shown in the displayed student list.
* The index **must be a positive integer** 1, 2, 3, …​
* At least one class must be provided.
* Class names must be alphanumeric and may contain spaces or hyphens.
* Class names must start with an alphanumeric character (letter or digit).
* Only classes that exist for the student can be deleted.

Examples:
* `list` followed by `deleteclass 1 c/Math-1000 c/Physics-2000` deletes two classes from the 1st student in the address book.
* `find John` followed by `deleteclass 2 c/Chemistry-1400` deletes a class from the 2nd student in the results of the `find` command.

### Filtering students by class: `filter`

Finds and lists all students who are in the specified class name.

Format: `filter c/CLASS`

* Filters students by the specified class name.
* The search is case-insensitive. e.g., `math-1000` will match `Math-1000`
* Only one class can be specified at a time.
* Only students with an exact match to the class name will be shown.

Examples:
* `filter c/Math-1000` displays all students enrolled in the Math-1000 class.
* `filter c/Chemistry-1400` displays all students enrolled in the Chemistry-1400 class.


### Clearing all entries : `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

TutorTrack data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

TutorTrack data are saved automatically as a JSON file `[JAR file location]/data/tutortrack.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning"><span class="fas fa-exclamation-triangle" aria-hidden="true"></span> <strong>Caution:</strong>
If your changes to the data file makes its format invalid, TutorTrack will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause TutorTrack to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous TutorTrack home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE l/LEVEL [c/CLASS]…​` <br> e.g., `add n/John Doe p/98765432 l/2 c/Chemistry-1400`
**List** | `list`
**Edit** | `edit INDEX [n/NAME] [p/PHONE] [l/LEVEL]`<br> e.g., `edit 1 p/91234567`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find John`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Assign** | `assign INDEX c/CLASS a/ASSIGNMENT [a/ASSIGNMENT]...`<br> e.g., `assign 1 c/Math-2000 a/MathHW1 a/MathTopic2`
**Unassign** | `unassign INDEX c/CLASS a/ASSIGNMENT [a/ASSIGNMENT]...`<br> e.g., `unassign 1 c/Math-2000 a/MathHW1`
**Assign All** | `assignall c/CLASS a/ASSIGNMENT`<br> e.g., `assignall c/Math-2000 a/MathHW1`
**Unassign All** | `unassignall c/CLASS a/ASSIGNMENT`<br> e.g., `unassignall c/Math-2000 a/MathHW1`
**Mark** | `mark [INDEX]... [INDEX_RANGE]...  c/CLASS a/ASSIGNMENT`<br> e.g., `mark 1 3-5 c/Math-2000 a/MathHW1`
**Unmark** | `unmark [INDEX]... [INDEX_RANGE]...  c/CLASS a/ASSIGNMENT`<br> e.g., `unmark 1 3-5 c/Math-2000 a/MathHW1`
**Add Class** | `addclass INDEX c/CLASS [c/CLASS]...`<br> e.g., `addclass 1 c/Math-1000 c/Physics-2000`
**Delete Class** | `deleteclass INDEX c/CLASS [c/CLASS]...`<br> e.g., `deleteclass 1 c/Math-1000`
**Filter** | `filter c/CLASS`<br> e.g., `filter c/Math-1000`
**Clear** | `clear`
**Help** | `help`
**Exit** | `exit`
