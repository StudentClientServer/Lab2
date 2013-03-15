package model;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;


public class Student implements Serializable {
    
    private static final long serialVersionUID = 4378002506140942424L;
    
    private int id;
    private String fio;
    private String groupNumber;
    private String enrolled; //зачислен
    private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy");    
    
    public Student (Node node) throws ServerException {
        try{
            setId(Integer.parseInt(node.getAttributes().getNamedItem("ID").getNodeValue()));
            setFio(node.getAttributes().getNamedItem("fio").getNodeValue());
            setGroupNumber(node.getAttributes().getNamedItem("groupnumber").getNodeValue());            
            setEnrolled(format.parse(node.getAttributes().getNamedItem("enrolled").getNodeValue()));            
        } catch(NumberFormatException e){
            throw new ServerException("Can not create a student! Something wrong with id!",e);
        } catch (DOMException e) {
            throw new ServerException("Can not create a student! DOMException - something wrong with XML-file!",e);
        } catch (ParseException e) {
            throw new ServerException("Can not create a student! Something wrong with date of enroll at the University, Date fromat must be dd.MM.yyyy!",e);
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result
                + ((enrolled == null) ? 0 : enrolled.hashCode());
        result = prime * result + ((fio == null) ? 0 : fio.hashCode());
        result = prime * result
                + ((groupNumber == null) ? 0 : groupNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Student other = (Student) obj;
        if (id != other.id)
            return false;
        if (enrolled == null) {
            if (other.enrolled != null)
                return false;
        } else if (!enrolled.equals(other.enrolled))
            return false;
        if (fio == null) {
            if (other.fio != null)
                return false;
        } else if (!fio.equals(other.fio))
            return false;
        if (groupNumber == null) {
            if (other.groupNumber != null)
                return false;
        } else if (!groupNumber.equals(other.groupNumber))
            return false;
        return true;
    }

    public Student(int iD, String fio, String groupNumber, Date enrolled) {
        setId(iD);
        setFio(fio);
        setGroupNumber(groupNumber);
        setEnrolled(enrolled);
    }
    
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFio() {
        return fio;
    }
    public void setFio(String fio) {
        this.fio = fio;
    }
    public String getGroupNumber() {
        return groupNumber;
    }
    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }
    public String getEnrolled() {
        return enrolled;
    }
    public void setEnrolled(Date enrolled) {
        this.enrolled = format.format(enrolled);        
    }
    
    
    
}
