package model;

import java.util.List;

/**
 * The Interface ServerModel.
 */
public interface ServerModel {
    
    /**
     * Gets the students.
     *
     * @param group the group
     * @return the students
     * @throws ServerException the server exception
     */
    public List<Student> getStudents(Group group) throws ServerException;
    
    /**
     * Gets the groups.
     *
     * @return the groups
     * @throws ServerException the server exception
     */
    public List<Group> getGroups() throws ServerException;
    
    /**
     * Removes the group.
     *
     * @param source the source
     * @throws ServerException the server exception
     */
    public void removeGroup(String source) throws ServerException;
    
    /**
     * Adds the group.
     *
     * @param source the source
     * @throws ServerException the server exception
     */
    public void addGroup(Group source) throws ServerException;
    
    /**
     * Adds the student.
     *
     * @param source the source
     * @throws ServerException the server exception
     */
    public void addStudent(Student source) throws ServerException;
    
    /**
     * Removes the student.
     *
     * @param source the source
     * @throws ServerException the server exception
     */
    public void removeStudent(Integer source) throws ServerException;
    
    /**
     * Update group.
     *
     * @param source the source
     * @throws ServerException the server exception
     */
    public void updateGroup(Group source) throws ServerException;
    
    /**
     * Update student.
     *
     * @param source the source
     * @throws ServerException the server exception
     */
    public void updateStudent(Student source) throws ServerException;
    
    /**
     * Update group.
     *
     * @param group the group
     * @param string the string
     * @throws ServerException the server exception
     */
    public void updateGroup(Group group, String string) throws ServerException;
    
    /**
     * Sets the xml path.
     *
     * @param xmlpath the new xml path
     */
    public void setXmlPath(String xmlpath);
    
    /**
     * Sets the dtd path.
     *
     * @param dtdpath the new dtd path
     */
    public void setDtdPath(String dtdpath);
    
    /**
     * Read document.
     *
     * @throws ServerException the server exception
     */
    public void readDocument() throws ServerException;
    
    /**
     * Gets the group.
     *
     * @param number the number
     * @return the group
     * @throws ServerException the server exception
     */
    public Group getGroup(String number) throws ServerException;
}
