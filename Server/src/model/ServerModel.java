package model;

import java.util.List;

public interface ServerModel {
    public List<Student> getStudents(Group group) throws ServerException;
    public List<Group> getGroups() throws ServerException;
    public void removeGroup(String source) throws ServerException;
    public void addGroup(Group source) throws ServerException;
    public void addStudent(Student source) throws ServerException;
    public void removeStudent(Integer source) throws ServerException;
    public void updateGroup(Group source) throws ServerException;
    public void updateStudent(Student source) throws ServerException;
    public void updateGroup(Group group, String string) throws ServerException;
    public void setXmlPath(String xmlpath);
    public void setDtdPath(String dtdpath);
    public void readDocument() throws ServerException;
    public Group getGroup(String number) throws ServerException;
}
