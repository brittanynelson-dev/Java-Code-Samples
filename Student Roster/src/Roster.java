import java.util.ArrayList;


/**
 * Created by Brittany Ward on 10/22/2016.
 */
public class Roster
{
    public static ArrayList<Student> studentRoster;

    public Roster ()
    {
         studentRoster = new ArrayList<Student>();
    }

    public Roster (String[] students)
    {
        studentRoster = new ArrayList<Student>();

        for (int i = 0; i < students.length; i++)
        {
            String str = students[i];
            String[] updatedInput = str.split(",");

            String id = updatedInput[0];
            String fName = updatedInput[1];
            String lName = updatedInput[2];
            String email = updatedInput[3];
            int age = Integer.parseInt(updatedInput[4]);
            int grade1 = Integer.parseInt(updatedInput[5]);
            int grade2 = Integer.parseInt(updatedInput[6]);
            int grade3 = Integer.parseInt(updatedInput[7]);

            studentRoster.add(new Student(id, fName, lName, email, age, grade1, grade2, grade3));
        }
    }

    /**
     * Add a student object to the current ArrayList
     * @param studentID the new student's ID number
     * @param firstName the student's first name
     * @param lastName the student's last name
     * @param emailAddress the student's email address
     * @param age the student's age
     * @param grade1 the student's first of 3 grades
     * @param grade2 the student's second of 3 grades
     * @param grade3 the students third of 3 grades
     */
    public static void add (String studentID, String firstName, String lastName,
                            String emailAddress, int age, int grade1, int grade2, int grade3)
    {
        studentRoster.add(new Student(studentID, firstName, lastName, emailAddress, age, grade1, grade2, grade3));
    }

    /**
     * Method for finding a student's index number in the ArrayList when given their Student ID Number
     * @param id the student ID number to locate the index for
     * @return return the index number if found, otherwise return -1
     */
    public static int findIndexFromID (String id)
    {
        boolean found = false;
        int index = 0;
        for (int i = 0; i < studentRoster.size() && !found;)
        {
            String testCase = studentRoster.get(i).getStudentID();
            if (testCase.equals(id))
            {
                index = i;
                found = true;
            }
            else
            {
                i++;
            }
        }

        if (found)
        {
            return index;
        }
        else
        {
            System.out.println("The student ID you entered does not exist in this roster.");
            return -1;
        }

    }

    /**
     * Get the student's student ID number when provided with the index number
     * @param index the student's index number in the roster
     * @return the student's ID number
     */
    public static String getStudentID (int index)
    {
        return studentRoster.get(index).getStudentID();
    }

    /**
     * Remove a student from the roster using their Student ID number
     * @param studentID the student ID number to remove from the roster
     */
    public static void remove(String studentID)
    {
        boolean removed = false;
        Student removedStudent;
        int index = findIndexFromID(studentID);

        if (index != -1)
        {
            removedStudent = studentRoster.remove(index);
            removed = true;
            System.out.println("Student " + removedStudent.getFirstName() + " " +
                    removedStudent.getLastName() + " was removed successfully.");
        }

        else
        {
            System.out.println("The student could not be removed. Please try again.");
        }
    }

    /**
     * Print a student's information using their index number
     * @param index the index number of the student to print
     */
    public void print(int index)
    {
        studentRoster.get(index).print();
    }

    /**
     * Print a student's information using their Student ID number
     * @param id the ID number of the student to print
     */
    public void print(String id)
    {
        int index = findIndexFromID(id);
        if (index != -1)
        {
            studentRoster.get(index).print();
        }
    }

    /**
     * Replace a student's existing grade with a new one. Requires input of their Student ID
     * number, the index number of the grade to change, and the new grade.
     * @param id the student ID number of the student to modify
     * @param gradeIndex the index number of the grade to change
     * @param newGrade the new grade that  should be stored for the student
     */
    public void replaceGrade(String id, int gradeIndex, int newGrade)
    {
        int index = findIndexFromID(id);
        if (index != -1)
        {
            studentRoster.get(index).replaceGrade(gradeIndex, newGrade);
        }
    }

    /**
     * Loops through all students in the roster and calls the print() method for them
     */
    public static void print_all()
    {
        for (int i = 0; i < studentRoster.size(); i++)
        {
            studentRoster.get(i).print();
        }
    }

    /**
     * Calculates and prints the average grade of a student
     * @param studentID the Student ID number of the student to calculate and print
     */
    public static void print_average_grade(String studentID)
    {
        int index = findIndexFromID(studentID);
        if (index != -1)
        {
            int grade1 = studentRoster.get(index).getGrade(0);
            int grade2 = studentRoster.get(index).getGrade(1);
            int grade3 = studentRoster.get(index).getGrade(2);
            int average = Math.round((grade1 + grade2 + grade3) / 3);
            System.out.println(studentRoster.get(index).getFirstName() + " "
                + studentRoster.get(index).getLastName() + "'s average grade is " + average);
        }
    }

    /**
     * Searches for emails that do not meet a valid pattern and prints them
     */
    public static void print_invalid_emails()
    {
        System.out.println("Invalid emails found:");
        for (int i = 0; i < studentRoster.size(); i++)
        {
            final String PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,4})";
            String email = studentRoster.get(i).getEmail();
            Boolean valid = email.matches(PATTERN);
            if (!valid)
            {
                System.out.println(email);
            }
        }
    }

    /**
     * Gets the current size of the array list of students in the roster object
     */
    public int getSize()
    {
        return studentRoster.size();
    }
}
