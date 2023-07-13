/**
 * Created by Brittany Ward on 10/22/2016.
 */
public class Student
{
    private String studentID = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private int age = 0;
    private Integer[] grades;

    /**
     * Constructor for the Student object.
     * @param aStudentID the student's ID number
     * @param aFirstName the student's first name
     * @param aLastName the student's last name
     * @param aEmail the student's email
     * @param aAge the student's age
     * @param grade1 the first of 3 test scores
     * @param grade2 the second of 3 test scores
     * @param grade3 the third of 3 test scores
     */
    public Student(String aStudentID, String aFirstName, String aLastName, String aEmail,
                   int aAge, int grade1, int grade2, int grade3)
    {
        studentID = aStudentID;
        firstName = aFirstName;
        lastName = aLastName;
        email = aEmail;
        age = aAge;
        grades = new Integer[3];
        grades[0] = grade1;
        grades[1] = grade2;
        grades[2] = grade3;
    }

    /**
     * Method to find the student's ID number
     * @return the student's ID number
     */
    public String getStudentID()
    {
        return studentID;
    }

    /**
     * Method to change the student's current ID number
     * @param newID the new ID number that will be replacing the current number
     */
    public void changeStudentID(String newID)
    {
        studentID = newID;
    }

    /**
     * Method to find the student's first name
     * @return the student's first name
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * Method to change the student's first name
     * @param newFirstName the new first name to store for the student
     */
    public void changeFirstName(String newFirstName)
    {
        firstName = newFirstName;
    }

    /**
     * Method to find the student's last name
     * @return the student's last name
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * MEthod to change the student's last name
     * @param newLastName the new last name to store for the student
     */
    public void changeLastName(String newLastName)
    {
        lastName = newLastName;
    }

    /**
     * Method to find the student's email address
     * @return the student's email address
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Method to change the student's email address
     * @param newEmail the new email address to store for the student
     */
    public void changeEmail(String newEmail)
    {
        email = newEmail;
    }

    /**
     * Method to find the student's age
     * @return the student's age
     */
    public int getAge()
    {
        return age;
    }

    /**
     * Method to change the student's age
     * @param newAge the new age to set for the student
     */
    public void changeAge(int newAge)
    {
        age = newAge;
    }

    /**
     * Method to find an individual grade belonging to a student
     * @param index the index number of the grade to return, ranges from 0-2
     * @return the integer grade stored in the selected array index location
     */
    public int getGrade(int index)
    {
        return grades[index];
    }

    /**
     * Method to find all grades in a student's array
     * @return a String of all three of the student's grades
     */
    public String getAllGrades()
    {
        String allGrades = "";

        for (int i = 0; i < grades.length; i++)
        {
            if (i < grades.length - 1)
            {
                allGrades = allGrades + grades[i] + ", ";
            }
            else
            {
                allGrades = allGrades + grades[i];
            }
        }
        return allGrades;
    }

    /**
     * Method to replace a specific grade with a new value
     * @param selection the index number of the grade to change
     * @param newGrade the new grade to replace the existing grade with
     */
    public void replaceGrade(int selection, int newGrade)
    {
        grades[selection] = newGrade;
    }

    /**
     * Method to print all current student data separated by tabs
     */
    public void print()
    {
        System.out.print(getStudentID() + "\t");
        System.out.print("First Name: " + getFirstName() + "\t");
        System.out.print("Last Name: " + getLastName() + "\t");
        System.out.print("Email: " + getEmail() + "\t");
        System.out.print("Age: " + getAge() + "\t");
        System.out.println("Grades: {" + getAllGrades() + "}");
    }


}
