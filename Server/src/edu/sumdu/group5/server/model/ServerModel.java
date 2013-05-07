package edu.sumdu.group5.server.model;

import java.util.List;

/**
 * The Interface ServerModel.
 */
public interface ServerModel {
    
    /**
     * Returns the list of students for specified group.
     * 
     * @param group
     *            the group
     * @return the students
     * @throws ServerException
     *             if can't get list of students for specified group
     */
    public List<Student> getStudents(Group group) throws ServerException;
    
    /**
     * Gets the list of groups.
     * 
     * @return the groups
     * @throws ServerException
     *             if can't get list of group
     */
    public List<Group> getGroups() throws ServerException;
    
    /**
     * Removes the group.
     * 
     * @param groupNumber
     *            the group name
     * @throws ServerException
     *             if can't remove the group or group with specified number does
     *             not exist
     */
    public void removeGroup(String source) throws ServerException;
    
    /**
     * Adds the group, check group number for uniqueness.
     * 
     * @param group
     *            the group
     * @throws ServerException
     *             if group number is not unique
     */
    public void addGroup(Group source) throws ServerException;
    
    /**
     * Adds the student, check ID for uniqueness.
     * 
     * @param student
     *            the student
     * @throws ServerException
     *             if id is not unique or if group with specified number does
     *             not exist
     */
    public void addStudent(Student source) throws ServerException;
    
    /**
     * Removes the student.
     * 
     * @param id
     *            the id
     * @throws ServerException
     *             if a student with specified id does not exist
     */
    public void removeStudent(Integer source) throws ServerException;
    
    /**
     * Update group, change faculty.
     * 
     * @param group
     *            the group
     * @throws ServerException
     *             if can not remove or add group.
     */
    public void updateGroup(Group source) throws ServerException;
    
    /**
     * Updates student.
     * 
     * @param student
     *            the student you want to update.
     * @throws ServerException
     *             if can not remove or add student.
     */
    public void updateStudent(Student source) throws ServerException;
    
    /**
     * Updates group, change group number.
     * 
     * @param group
     *            the group
     * @param newNumber
     *            number you want to set
     * @throws ServerException
     *             if can not remove or add group.
     */
    public void updateGroup(String oldNumber, String newNumber) throws ServerException;
    
    /**
     * Sets the xml path.
     *
     * @param xmlpath the new xml path
     */

    /**
     * Check new data for changes and changes DOM tree if necessary, deletes old
     * groups. Than saves DOM tree to xml file
     * 
     * @throws ServerException
     *             can't change DOM tree
     */
    public void upLoadData()throws ServerException;

    /**
     * Gets the group by number.
     * 
     * @param number
     *            the number
     * @return the group by number
     * @throws ServerException
     *             if can not find a group with specified number.
     */
    public Group getGroup(String group) throws ServerException;

    /**
     * Sets the xml path.
     * 
     * @param xmlPath
     *            the new xml path
     */
    public void setXmlPath(String xmlpath);

    /**
     * Sets the dtd path.
     * 
     * @param dtdPath
     *            the new dtd path
     */
    public void setDtdPath(String dtdpath);

    /**
     * Reads document using xml path.
     * 
     * @throws ServerException
     *             if can't not read or parse xml file
     */
    public void readDocument() throws ServerException;
}
