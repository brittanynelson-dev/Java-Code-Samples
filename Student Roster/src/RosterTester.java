/**
 * Created by Brittany Ward on 10/22/2016.
 */
public class RosterTester
{
    public static void main(String[] args)
    {
        String [] students = {"1,John,Smith,John1989@gmail.com,20,88,79,59",
                "2,Suzan,Erickson,Erickson_1990@gmailcom,19,91,72,85",
                "3,Jack,Napoli,The_lawyer99yahoo.com,19,85,84,87",
                "4,Erin,Black,Erin.black@comcast.net,22,91,98,82",
                "5,Brittany,Ward,bward39@wgu.edu,28,80,96,91"};

        Roster task = new Roster(students);

        task.print_all();

        task.print_invalid_emails();

        for (int i = 0; i < task.getSize(); i++)
        {
            String studentID = task.getStudentID(i);
            task.print_average_grade(studentID);
        }

        task.remove("3");
        task.remove("3");
    }
}
