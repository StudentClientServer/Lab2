package edu.sumdu.group5.view;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;

//import javax.swing.GroupLayout.Group;
//import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JPopupMenu;

import edu.sumdu.group5.model.*;
import edu.sumdu.group5.exception.*;

/**
 * The Class JView.
 */
public class JView extends DefaultTableModel {


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The listeners. */
    private Collection<TableModelListener> listeners = new ArrayList<TableModelListener>();
    
    /** The command. */
    public String command;

    /** The groups. */
    private static List<Group> groups;
    
    /** The data. */
    private Object[][] data;
    
    /** The column names. */
    private Object[] columnNames;
    

    /** The is group. */
    private boolean isGroup=true;
    
    /** The model1. */
    private DefaultTableModel model1;
    
    /** The frame. */
    private JFrame frame;
    
    /** The tb. */
    private JTable tb;
    
    /** The bottom panel. */
    private JPanel bottomPanel;
    
    /** The timer. */
    private Timer timer;
    
    /** The click. */
    private int click;
    
    /** The jb student add. */
    private JButton jbStudentAdd;
    
    /** The jb student del. */
    private JButton jbStudentDel;
    
    /** The jb group add. */
    private JButton jbGroupAdd;
    
    /** The jb group del. */
    private JButton jbGroupDel;
    
    /** The jb back. */
    private JButton jbBack;
    
    /** The elem gr. */
    private Object elemGr;
    
    private String currentfakulty;
    
    private String groupNumber;
    
    /** The student. */
    private Student student;

    private String cellText = "";
    
    /** The client. */
    private static Client client;

    Logger logger = Logger.getLogger(JView.class.getName());
    
    /**
    * main method, which creates logger, and starts program
    */
    public static void main (String[] args) throws ClientException{
        JView run = new JView();
        run.createClient();
        run.groupsUpdate();
        run.createJView(groups, client);
    }
    
    /**
     * gets data from server while first run
     */
    public void groupsUpdate(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{    
            groups = client.getUpdate();
        }catch (ClientException e){
            logger.error("Can't update data form server",e);
            JOptionPane.showMessageDialog(frame, "Can't update data from server!" );
        }catch (ServerException e){
            logger.error("Can't update data form server",e);
            JOptionPane.showMessageDialog(frame, "Can't update data from server!" );
        }
    }
    
    /**
     * creates new Client
     */
    public void createClient(){
        if (logger.isDebugEnabled()){
            logger.debug("Called client creation");
        }
        client = new Client();        
    }
    
    /**
     * Instantiates a new j table model.
     *
     * @param groups the groups
     * @param client the client
     * @throws ClientException the client exception
     */
    public void createJView(List<Group> groups, Client client) throws ClientException {
        if (logger.isDebugEnabled()){
            logger.debug("Called method creating window");
        }
        this.groups = groups;
        this.client = client;
        String iP = "";
        try {
            iP = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.error("Error finding IP",e);
        }
        try{
            frame = new JFrame("Table frame with IP "+iP) {
            
            private static final long serialVersionUID = 1L;

            {
                add(new JScrollPane(new JTable(getObjectModel(isGroup,null)) {

                    private static final long serialVersionUID = 1L;

                    {
                        setFont(new Font("Arial", Font.PLAIN, 18));
                        setGridColor(Color.DARK_GRAY);
                        setAutoCreateRowSorter(true);
                        setShowHorizontalLines(false);
                        getSelectionModel().addListSelectionListener(this);
                        getColumnModel().addColumnModelListener(this);
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        tb = this;
                       // tb.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
                        tb.addKeyListener(new KeyListener() {
                                
                            @Override
                            public void keyTyped(KeyEvent e) {
                            	if(e.getKeyCode()!=KeyEvent.VK_TAB){
                            	    cellText = cellText.replaceAll("\\s","") + String.valueOf(e.getKeyChar());
                            	}else{
                            		cellText="";
                            	}
                            }
                                
                            @Override
                            public void keyReleased(KeyEvent e) {
                            }
                                
                            @Override
                            public void keyPressed(KeyEvent e) {
                            	if(e.getKeyCode()==KeyEvent.VK_TAB){
                            		cellText="";
                            	}
                                if(e.getKeyCode()==10){
                                    if (tb.getModel().getColumnName(0).equals("fakulty")) {
                                            
                                        try {
                                        	setValueInModel(cellText, tb.getRowCount()-1, tb.getSelectedColumn());
                                        	if(tb.getModel().getValueAt(tb.getRowCount()-1, 0).equals("") || tb.getModel().getValueAt(tb.getRowCount()-1, 1).equals("")){
                                        	    JOptionPane.showMessageDialog(frame, "First fill in all fields!" );
                                            }else{
                                                addGoup();
                                            }
                                            fireTableDataChanged();
                                        } catch (ClientException e2) {
                                            logger.error("Something wrong with data while adding group",e2);
                                            JOptionPane.showMessageDialog(frame, "Something wrong with client while adding group" );
                                        } catch (ServerException e2) {
                                            logger.error("Something wrong with data while adding group",e2);
                                            JOptionPane.showMessageDialog(frame, "Something wrong with server while adding group" );
                                        }       
                                    }else{
                                        if (checkForEqualityStudents()){
                                            try {
                                                updateStudents();
                                            } catch (ClientException e1) {
                                                JOptionPane.showMessageDialog(frame, "Something wrong with student, please check data" );
                                                logger.error("There is no that student anymore :(",e1);
                                                try {
                                                    tb.setModel(getObjectModel(false,null));
                                                } catch (ClientException e2) {
                                                    JOptionPane.showMessageDialog(frame, "Something wrong with data while refreshing!" );
                                                    logger.error("Something wrong with data while refreshing!",e2);
                                                }
                                            }
                                        }else{
                                            try {
                                                addStudent();
                                                fireTableDataChanged();
                                                try {
                                                    tb.setModel(getObjectModel(false,elemGr));
                                                } catch (ClientException e2) {
                                                    JOptionPane.showMessageDialog(frame, "Something wrong with data while refreshing!" );
                                                    logger.error("Something wrong with data while refreshing!",e2);
                                                }
                                            } catch (ClientException e1) {
                                                JOptionPane.showMessageDialog(frame, "Something wrong with data while adding!" );
                                                logger.error("Something wrong with data while adding!",e1);
                                                try {
                                                    tb.setModel(getObjectModel(false,elemGr));
                                                } catch (ClientException e2) {
                                                    JOptionPane.showMessageDialog(frame, "Something wrong with data while refreshing!" );
                                                    logger.error("Something wrong with data while refreshing!",e2);
                                                }
                                            }
                                        }
                                    };
                                }
                                    
                            }
                        });
                         
                        if (tb.getModel().getColumnName(0).equals("fakulty")) {
                            addMouseListener(new MouseAdapter() {

                                public void mouseClicked(MouseEvent evt) {
                                    if (tb.getModel().getColumnName(0).equals("fakulty")) {
                                        
                                        if (evt.getButton()==MouseEvent.BUTTON1){
                                           
                                            timer = new Timer(300, new ActionListener() {
                                                public void actionPerformed(ActionEvent evt) {
                                                    timer.stop();
                                                }
                                            });
                                                
                                            timer.setRepeats(false);
                                            timer.start();
                                            click++;
                                            if (click == 2 && timer.isRunning()) {
                                                int indexRow = tb.rowAtPoint(evt.getPoint());
                                                elemGr = tb.getModel().getValueAt(indexRow, 1);
                                                    
                                                isGroup = false;
                                                try {
                                                    tb.setModel(getObjectModel(isGroup,elemGr));
                                                } catch (ClientException e) {
                                                    JOptionPane.showMessageDialog(frame, "Something wrong with Client :(" );
                                                    logger.error("Something wrong with Client!",e);
                                                }
                                                jbGroupAdd.setVisible(false);
                                                jbGroupDel.setVisible(false);
                                                jbStudentAdd.setVisible(true);
                                                jbStudentDel.setVisible(true);
                                                jbBack.setVisible(true);
                                                jbBack.setLayout(new FlowLayout(FlowLayout.LEFT));
                                                click = 0;
                                                timer.start();
                                            } else {
                                                click = 1;
                                                timer.start();
                                            }
                                        }
                                    }
                                }
                                
                            });
                            tb = this;
                        }   
                    }
                }), CENTER);
            
                add(new JPanel() {
                    
                    private static final long serialVersionUID = 1L;
                    
                    {                           
                    add(new JButton("Add student") {
                                
                        private static final long serialVersionUID = 1L;
                                
                        {
                            jbStudentAdd = this;
                            setLayout(new FlowLayout(FlowLayout.RIGHT));
                            addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent event) {
                                    Object[] data = { "", "", "", "" ,"" };
                                    if(tb.getRowCount()==0){
                                    	model1.addRow(data);
                                    }else{;
                                    	if(tb.getModel().getValueAt(tb.getRowCount()-1, 1).equals("")){
                                    	    JOptionPane.showMessageDialog(frame, "First fill in all fields!" );
                                         
                                        }else{
                                    	    model1.addRow(data);
                                        }
                                    }
                                }
                            });
                            setVisible(false);
                        }
                    });
                    
                    add(new JButton("Delete student") {
                                
                        private static final long serialVersionUID = 1L;

                        {
                            jbStudentDel = this;
                            setLayout(new FlowLayout(FlowLayout.RIGHT));
                            addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent event) {
                                    int c=0;
                                    int selIndex =0;
                                    int[] selectedRows = tb.getSelectedRows();
                                    for (int i = 0; i < selectedRows.length; i++) {
                                        if (c<1){
                                            selIndex = selectedRows[i];
                                            c++;
                                        }
                                        try {
                                            deleteStudent();
                                        } catch (ClientException e) {
                                            JOptionPane.showMessageDialog(frame, e.getMessage() );
                                            logger.error("Couldn't delete student!!",e);
                                        }
                                            model1.removeRow(tb.getSelectedRow());
                                    }
                                }
                            });
                            setVisible(false);
                        }
                    });     
                    
                    add(new JButton("Add group") {
                                
                        private static final long serialVersionUID = 1L;

                        {
                            jbGroupAdd = this;
                            setLayout(new FlowLayout(FlowLayout.LEFT));
                            addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent event) {
                                	if(tb.getModel().getValueAt(tb.getRowCount()-1, 0).equals("") || tb.getModel().getValueAt(tb.getRowCount()-1, 1).equals("")){
                                	    JOptionPane.showMessageDialog(frame, "First fill in all fields!" );
                                    }else{
                                	    Object[] data = { "", "" };
                                        model1.addRow(data);
                                    }

                                }
                            });
                            setVisible(true);
                        }
                    });
                    
                    add(new JButton("Delete group") {
                                
                        private static final long serialVersionUID = 1L;

                        {
                            jbGroupDel = this;
                            setLayout(new FlowLayout(FlowLayout.LEFT));
                            addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                try{
                                    deleteGoup();
                                    model1.removeRow(tb.getSelectedRow());
                                }catch (ClientException e) {
                                    logger.error("Couldn't delete group",e);
                                    JOptionPane.showMessageDialog(frame, "Couldn't delete group :(" );
                                }
                            }
                            });
                            setVisible(true);
                        }
                    });
                            
                    add(new JButton("Back") {
                                
                        private static final long serialVersionUID = 1L;

                        {
                            jbBack = this;
                            setLayout(new FlowLayout(FlowLayout.LEFT));
                            addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent event) {
                                    try{
                                        tb.setModel(getObjectModel(true,null));
                                    }catch (ClientException e) {
                                        logger.error("Something wrong, cant set new model",e);
                                        JOptionPane.showMessageDialog(frame, "Something wrong, cant set new model!" );
                                    }
                                    jbGroupAdd.setVisible(true);
                                    jbGroupDel.setVisible(true);
                                    jbStudentAdd.setVisible(false);
                                    jbStudentDel.setVisible(false);
                                    jbBack.setVisible(false);          
                                }
                            });
                            setVisible(false);
                        }
                    });
                            
                    setVisible(true);
                    }
                }, NORTH);      
                
                add(new JPanel() {
                        
                    private static final long serialVersionUID = 1L;

                    {
                        bottomPanel = this;
                        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

                        JLabel selLabel = new JLabel("Selected:");
                        bottomPanel.add(selLabel);

                        final JLabel currentSelectionLabel = new JLabel("");
                        currentSelectionLabel.setAutoscrolls(true);
                        bottomPanel.add(currentSelectionLabel);
                        ListSelectionModel selModel = tb.getSelectionModel();

                        selModel.addListSelectionListener(new ListSelectionListener() {

                        public void valueChanged(ListSelectionEvent e) {
                            String result = "";
                            int[] selectedRows = tb.getSelectedRows();

                            for (int i = 0; i < selectedRows.length; i++) {
                                int selIndex = selectedRows[i];
                                TableModel model = tb.getModel();

                                Object value = model.getValueAt(selIndex, 0);
                                Object value2 = null;
                                if (tb.getModel().getColumnName(0).equals("fakulty")){
                                    value2 = "";
                                }else {
                                    value2 = model.getValueAt(selIndex, 1);
                                }
                                result = result + value + " " + value2;
                                if (i != selectedRows.length - 1) {
                                    result += ", ";
                                }
                            }
                            currentSelectionLabel.setText(result);
                        }
                        });
                    }
                }, SOUTH);
                
                setSize(640, 480);
                addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                    	close();
                    }
                });
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
            }
        };
    } catch (Exception e) {
        throw new ClientException("Couldn't create frame because of error",e);
    }
}
    
    /**
     * Sets value in cpecified cell of table
     */
    public void setValueInModel(String str, int row, int col){
    	tb.getModel().setValueAt(str, row, col);
    	cellText="";
    }
    
    /**
    * Close.
    */
    public void close() {
        try {
            client.close();
        } catch (ClientException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
    }
    
    /**
     * Gets the object model.
     *
     * @param isGroup if true creates new DefaultTableModel with groups, if false - with students  
     * @param el selected object.In this case is element group
     * @return the object model
     * @throws ClientException 
     */
    public DefaultTableModel getObjectModel(Boolean isGroup, Object el) throws ClientException {
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get object model");
        }
        elemGr = el;
        groupsUpdate();
        int rowCount =0;
        if (isGroup) {
            data = new Object[groups.size()][2];
            columnNames = new Object[2];
            columnNames[0] = "fakulty";
            columnNames[1] = "number";
            for (int i = 0; i < groups.size(); i++) {
                data[i][0] = groups.get(i).getFakulty();
                data[i][1] = groups.get(i).getNumber();
            }
            model1 = new DefaultTableModel(data, columnNames);
            isGroup = false;
            return model1;
        } else {
            if (elemGr != null) {
                for (int i = 0; i < groups.size(); i++) {
                    if (elemGr.equals(groups.get(i).getFakulty()) || elemGr.equals(groups.get(i).getNumber())){
                    	groupNumber = groups.get(i).getNumber();
                        currentfakulty = groups.get(i).getFakulty();
                    }
                }
                
                ArrayList<Student> st = new ArrayList<Student>();
                try{
                    for (int i = 0; i < client.getShow(currentfakulty, groupNumber).size(); i++) {
                        if (groupNumber.equals(client.getShow(currentfakulty, groupNumber).get(i).getGroupNumber())) {
                        
                            rowCount++;
                            try{
                            	
                                st.add(client.getShow(currentfakulty, groupNumber).get(i));
                            }catch(ServerException e){
                            	logger.error("Error while refreshing table",e);
                            	throw new ClientException(e);
                            }
                        }
                    }
                }catch(ServerException e){
                	logger.error("Error while getting new data to set new table",e);
                	throw new ClientException(e);
                }
                data = new Object[rowCount][4];
                columnNames = new Object[4];
                
                columnNames[0] = "firstname";
                columnNames[1] = "lastname";
                columnNames[2] = "enrolled";
                columnNames[3] = "groupnumber";         
                for (int i = 0; i < rowCount; i++) {
                    if (groupNumber.equals(st.get(i).getGroupNumber())) {
                        data[i][0] = st.get(i).getFirstName();
                        data[i][1] = st.get(i).getLastName();
                        data[i][2] = st.get(i).getEnrolled();
                        data[i][3] = st.get(i).getGroupNumber();
                    }
                }
                model1 = new DefaultTableModel(data, columnNames);
                isGroup = false;
                return model1;
            }
            return model1;
        }

    }
    
    /**
     * Adds the goup.
     *
     * @throws ClientException the client exception
     * @throws ServerException the server exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void addGoup() throws ServerException,ClientException{
        if (logger.isDebugEnabled()){
            logger.debug("Called adding group");
        }
        if (groups.size()<tb.getRowCount()){
            String numGr = tb.getModel().getValueAt(tb.getRowCount()-1, 1).toString();
            String fakulty = tb.getModel().getValueAt(tb.getRowCount()-1, 0).toString();
            for (Group g : groups) {
                if (g.getNumber().equals(numGr))
                    throw new ClientException("A group with number " + g.getNumber()
                            + "is alredy exist");
            }
            try{
                client.addGroup(fakulty, numGr);
            }catch(ServerException e){
                logger.error("Error while adding new group",e);
                throw new ClientException (e);
            }
            Group newGroup = new Group(fakulty, numGr);
            groups.add(newGroup);
        }
    }
    
    /**
     * Delete goup.
     *
     * @throws ClientException 
     */
    public void deleteGoup() throws ClientException {
        if (logger.isDebugEnabled()){
            logger.debug("Called deleting group");
        }
        String f = tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString();
        String g = tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString();
        try {
            client.removeGroup(f, g);
        }catch(ServerException e){
            logger.error("Error while deleting new group",e);
            throw new ClientException (e);
        }
        groups.remove(tb.getSelectedRow());
    }
    
    /**
     * Method checks for equality amount of students in server side and client
     * returns true if equal and false if not
     * @throws ClientException 
     */
    public Boolean checkForEqualityStudents(){
        if (logger.isDebugEnabled()){
            logger.debug("Called checking for equality");
        }
        if (elemGr != null) {
            for (int i = 0; i < groups.size(); i++) {
                if (elemGr.equals(groups.get(i).getFakulty()) || elemGr.equals(groups.get(i).getNumber())){
                    groupNumber = groups.get(i).getNumber();
                    currentfakulty = groups.get(i).getFakulty();
                }
            }
        }
        try {
            if (tb.getModel().getRowCount()== client.getShow(currentfakulty, groupNumber).size()){
                return true;
            }else {
                return false;
            }
        }catch (ClientException e) {
            logger.error("Error while cheking for equality.Something wrong with client data",e);
            JOptionPane.showMessageDialog(frame, "Something wrong with client data while checking for equality" );
        }catch(ServerException e){
            logger.error("Error while cheking for equality.Somethinf wrong with client data",e);
            JOptionPane.showMessageDialog(frame, "Something wrong with client data while checking for equality" );
        }
        return null;
    }
    
    /**
     * Method updates student's data
     * 
     * @throws ClientException 
     */
    public void updateStudents() throws ClientException{
        if (logger.isDebugEnabled()){
            logger.debug("Called updating student");
        }
        String firstName = tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString();
        String lastName  = tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString();
        String enrolled  = tb.getModel().getValueAt(tb.getSelectedRow(), 2).toString();
        String group     = elemGr.toString();
        Integer studentID = null;
        try{
            for (int i = 0; i < client.getShow(currentfakulty, group).size(); i++) {  
                if(tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString().equals(client.getShow(currentfakulty, group).get(i).getFirstName()) 
                		|| tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString().equals(client.getShow(currentfakulty, group).get(i).getLastName())){
                    try{
                        studentID = client.getShow(currentfakulty, group).get(i).getId();
                    }catch(ServerException e){
                        logger.error("Error while getting data from server",e);
                        throw new ClientException (e);
                    }
                }    
            }
        }catch(ServerException e){
            logger.error("Error while updating student",e);
            throw new ClientException (e);
        }
        try{
            client.changeStudent(currentfakulty, group, firstName, lastName, enrolled, studentID);
        }catch(ServerException e){
            logger.error("Error while updating student",e);
            throw new ClientException (e);
        }
    }
    
    /**
     * Adds the student.
     *
     * @return the student
     * @throws ClientException the client exception
     */
    public Student addStudent() throws ClientException{
        if (logger.isDebugEnabled()){
            logger.debug("Called adding student");
        }
        String groupNumber = null;
        String fak = null;
        for (int i = 0; i < groups.size(); i++) {
            if (elemGr.equals(groups.get(i).getFakulty()) || elemGr.equals(groups.get(i).getNumber())){
                groupNumber = groups.get(i).getNumber();
                fak = groups.get(i).getFakulty();
            }
        }
        try{
            if (client.getShow(fak, groupNumber).size()<tb.getRowCount() || client.getShow(fak, groupNumber).size()==0){
                String fName = tb.getModel().getValueAt(tb.getRowCount()-1, 0).toString();
                String lName = tb.getModel().getValueAt(tb.getRowCount()-1, 1).toString();
                String grnumber = groupNumber;
                String enr = tb.getModel().getValueAt(tb.getRowCount()-1, 2).toString();
                String id = generateID();
        
                try{
                    client.addStudent(grnumber, fName, lName, enr, Integer.valueOf(id));
                }catch(ServerException e){
                    logger.error("Error while adding student",e);
                    throw new ClientException(e);
                }
            }
        }catch(ServerException e){
            logger.error("Error while adding student to database",e);
            throw new ClientException(e);
        }
        
        return student;
    }
    
    /**
     * Delete student.
     *
     * @throws ClientException 
     */
    public void deleteStudent() throws ClientException{
        if (logger.isDebugEnabled()){
            logger.debug("Called deleting student");
        }
        String groupNumber = null;
        String fak = null;
        Student stud = null;
        for (int i = 0; i < groups.size(); i++) {
            if (elemGr.equals(groups.get(i).getFakulty()) || elemGr.equals(groups.get(i).getNumber())){
                groupNumber = groups.get(i).getNumber();
                fak = groups.get(i).getFakulty();
            }
        }
        try{
            for (int i = 0; i < client.getShow(fak, groupNumber).size(); i++) {
            
                if (groupNumber.equals(client.getShow(fak, groupNumber).get(i).getGroupNumber())) {
                    if(tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString().equals(client.getShow(fak, groupNumber).get(i).getFirstName()) && 
                        tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString().equals(client.getShow(fak, groupNumber).get(i).getLastName())){

                        try{
                            stud = client.getShow(fak, groupNumber).get(i);
                        }catch(ServerException e){
                            logger.error("Error while getting data from server",e);
                            throw new ClientException(e);
                        }
                    }
                }
            }
        }catch(ServerException e){
            logger.error("Error while deleting student",e);
            throw new ClientException(e);
        }
        if(stud!=null){
        try{
            client.removeStudent(groupNumber, stud.getId());
        }catch(ServerException e){
            logger.error("Error while deleting student on the server side",e);
            throw new ClientException(e);
        }
        }else{
        	throw new ClientException("There is no this student in model");
        }
    }
    
    /**
     * Generate id.
     *
     * @return the string
     */
    private String generateID() {
        if (logger.isDebugEnabled()){
            logger.debug("Called generation of ID");
        }
        int ID = 0;
        Random rand = new Random();
            ID = rand.nextInt(2147483646);
        return String.valueOf(ID);
    }

    /*
    * Gets count of rows.
    */
    @Override
    public int getRowCount() {
        if(isGroup){
            return groups.size();
        }else{
            return 1;
        }
    }

    /*
    * Gets count of columns.
    */
    @Override
    public int getColumnCount() {
        if(isGroup){
            return 3;
        }else{
            return 5;
        }
    }

    /*
    * Gets value at specifided row and column
    */
    @Override
    public Object getValueAt(int row, int column) {
        return data[row][column];
    }

    /*
    * Gets column name
    */
    @Override
    public String getColumnName(int column) {
        return columnNames[column].toString();
    }

    /*
    * Return class name of specifided column 
    */
    @SuppressWarnings("unchecked")
    @Override
    public Class<JView> getColumnClass(int c) {
        return (Class<JView>) getValueAt(0, c).getClass();
    }

    /*
    * Return true if cell is editable and false otherwise
    */
    @Override
    public boolean isCellEditable(int row, int column) {
    	if (tb.getModel().getColumnName(0).equals("fakulty")) {
    		return true;
    	}else{
    		if (column == 3) {
                return false;
            } else {
                return true;
            }
    	}
    }

    /*
    * Adds listener to table
    */
    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /*
    * Removes listener from table
    */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
    
    /*
    * Sets value at at specifided cell
    */
    @Override
    public void setValueAt(Object value, int row, int col) {
            try{
                data[row][col] = value;
                
                fireTableCellUpdated(row, col);
                
                TableModelEvent e = new TableModelEvent(this, row); 
                for (TableModelListener l : listeners) 
                    l.tableChanged(e);
            }catch (Exception e) { 
                  JOptionPane.showMessageDialog(frame,e.getMessage()); 
            }
    }

}
