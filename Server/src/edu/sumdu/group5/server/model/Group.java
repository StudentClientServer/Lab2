package edu.sumdu.group5.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Group.
 */
public class Group implements Cloneable {

    /** The faculty. */
    private String fakulty;

    /** The number of group, unique. */
    private String number;

    private ArrayList<Student> students = new ArrayList<Student>();

    @Override
    public String toString() {
        StringBuilder groupString = new StringBuilder ();
        groupString.append("Group [fakulty=");
        groupString.append(fakulty);
        groupString.append(", number=");
        groupString.append(number);
        groupString.append("]");        
        return groupString.toString();
    }

    /**
     * Instantiates a new group.
     * 
     * @param fakulty
     *            the faculty
     * @param number
     *            the number
     */
    public Group(String fakulty, String number) {
        setFakulty(fakulty);
        setNumber(number);
    }

    public Group() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Adds the student.
     * 
     * @param student
     *            the student
     */
    public void addStudent(Student student) {
        students.add(student);
    }

    /**
     * Removes the student by id.
     * 
     * @param id
     *            the id
     * @throws ServerException
     *             the server exception
     */
    public void removeStudent(int id) throws ServerException {
        Student student = getStudentById(id);
        if (student == null)
            throw new ServerException("There is no student with id = " + id);
        students.remove(student);
    }

    /**
     * Gets the faculty.
     * 
     * @return the faculty
     */
    public String getFakulty() {
        return fakulty;
    }

    /**
     * Sets the fakulty.
     * 
     * @param fakulty
     *            the new faculty
     */
    public void setFakulty(String fakulty) {
        this.fakulty = fakulty;
    }

    /**
     * Gets the number.
     * 
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number.
     * 
     * @param number
     *            the new number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fakulty == null) ? 0 : fakulty.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        if (fakulty == null) {
            if (other.fakulty != null)
                return false;
        } else if (!fakulty.equals(other.fakulty))
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        return true;
    }

    /**
     * Checks if the group contains a student with specified id.
     * 
     * @param id
     *            the id
     * @return true, if successful
     * @throws ServerException
     *             the server exception
     */
    public boolean containsStudent(int id) throws ServerException {
        for (Student student : students) {
            if (student.getId() == id)
                return true;
        }
        return false;
    }

    /**
     * Gets the List of students.
     * 
     * @return the students
     * @throws ServerException
     *             the server exception
     */
    public List<Student> getStudents() throws ServerException {
        return students;
    }

    private Student getStudentById(int id) {
        for (Student st : students) {
            if (st.getId() == id)
                return st;
        }
        return null;
    }

    @Override
    public Object clone() {
        try {
            Group newGroup = (Group) super.clone();
            newGroup.setStudents(new ArrayList<Student>());
            for (Student st : students) {
                newGroup.addStudent((Student) st.clone());
            }
            return newGroup;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }
}
