package model;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;

import java.awt.Color;
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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import model.*;
import exceptions.*;
import client.*;

/**
 * The Class JTableModel.
 */
public class JTableModel extends DefaultTableModel {


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The listeners. */
    private Collection<TableModelListener> listeners = new ArrayList<TableModelListener>();
    
    /** The command. */
    public String command;

    /** The groups. */
    private List<Group> groups;
    
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
    
    /** The student. */
    private Student student;

    
    /** The client. */
    private Client client;

    Logger logger = Logger.getLogger(JTableModel.class.getName());
    
    private void createLoger(){
        
    	try {
        	
            HtmlFormatter htmlformatter = new HtmlFormatter();
            FileHandler htmlfile = new FileHandler("LogApp.html");
            
            htmlfile.setFormatter(htmlformatter);
            logger.addHandler(htmlfile);
            
            
        } catch (SecurityException e) {
            logger.log(Level.SEVERE,"Couldn't create log file because of security policy", e);
            JOptionPane.showMessageDialog(frame, "Couldn't create log file because of security policy!" );
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Couldn't create log file because of input.",e);
            JOptionPane.showMessageDialog(frame, "Couldn't create log file because of input!" );
        }
    }
    /**
     * Instantiates a new j table model.
     *
     * @param groups the groups
     * @param client the client
     * @throws ClientException the client exception
     */
    public JTableModel(List<Group> groups, Client client) throws ClientException {

        createLoger();
        this.groups = groups;
        this.client = client;
        String IP = "";
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.log(Level.WARNING,"Error finding IP",e);
        }
        try{
        frame = new JFrame("Table frame with IP "+IP) {
            
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
                         //setVisible(false);
                         tb = this;
                         
                         tb.addKeyListener(new KeyListener() {
                                
                                @Override
                                public void keyTyped(KeyEvent e) {
                                    if (e.getKeyCode()==KeyEvent.VK_ENTER){
                                    }
                                }
                                
                                @Override
                                public void keyReleased(KeyEvent e) {
                                }
                                
                                @Override
                                public void keyPressed(KeyEvent e) {
                                    if(e.getKeyCode()==10){
                                        if (tb.getModel().getColumnName(0).equals("fakulty")) {
                                            
                                                try {
                                                    addGoup();
                                                    fireTableDataChanged();
                                                } catch (ClientException e2) {
                                                    logger.log(Level.INFO, "Something wrong with data",e2);
													JOptionPane.showMessageDialog(frame, "Something wrong with client :(" );
                                                } catch (ServerException e2) {
                                                    logger.log(Level.INFO, "Something wrong with data",e2);
													JOptionPane.showMessageDialog(frame, "Something wrong with server :(" );
                                                }
                                            
                                        }else{
										System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                            if (checkForEqualityStudents()){
											System.out.println("!!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@!!!!!!!!!!!!!!!!!!!");
                                                try {
                                                    updadeStudents();
                                                } catch (ClientException e1) {
                                                    JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
                                                    logger.log(Level.INFO, "There is no that student anymore :(",e1);
                                                }
                                            }else{
                                                try {
                                                    addStudent();
                                                    fireTableDataChanged();
                                                    try {
                                                        tb.setModel(getObjectModel(false,elemGr));
                                                    } catch (ClientException e2) {
                                                        JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
                                                    }
                                                } catch (ClientException e1) {
                                                    JOptionPane.showMessageDialog(frame, "Something wrong with date :(" );
                                                    logger.log(Level.INFO, "Something wrong with date :(",e1);
                                                    try {
                                                        tb.setModel(getObjectModel(false,elemGr));
                                                    } catch (ClientException e2) {
                                                        JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
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
                                                timer = new Timer(50,new ActionListener() {
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
                                                        logger.log(Level.SEVERE,"!",e);
                                                    }
                                                    jbGroupAdd.setVisible(false);
                                                    jbGroupDel.setVisible(false);
                                                    jbStudentAdd.setVisible(true);
                                                    jbStudentDel.setVisible(true);
                                                    jbBack.setVisible(true);
                                                    jbBack.setLayout(new FlowLayout(FlowLayout.LEFT));
                                                    //timer.stop();
                                                    click = 0;
                                                } else {
                                                    click = 1;
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
                                            model1.addRow(data);
                                        }
                                    });
                                    setVisible(false);
                                }
                            });
                            add(new JButton("Delete student") {
                                /**
                                 * 
                                 */
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
                                                if (selIndex>model1.getRowCount()-1){
                                                    try {
                                                        deleteStudent();
                                                    } catch (ClientException e) {
                                                        JOptionPane.showMessageDialog(frame, "Something wrong :(" );
                                                    }
                                                    model1.removeRow(tb.getSelectedRow());
                                                }else{
                                                    try {
                                                        deleteStudent();
                                                    } catch (ClientException e) {
                                                        JOptionPane.showMessageDialog(frame, "ConLost :(" );
                                                    }
                                                    model1.removeRow(tb.getSelectedRow());
                                                }
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
                                            Object[] data = { "", "" };
                                            model1.addRow(data);

                                            // addGoup();
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

                                            //System.out.println(tb.getSelectedRow());
                                            try {
                                                deleteGoup();
                                                model1.removeRow(tb.getSelectedRow());
                                            } catch (ClientException e) {
                                                logger.log(Level.SEVERE,"Couldn't delete group",e);
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
                                            try {
                                                tb.setModel(getObjectModel(true,null));
                                            } catch (ClientException e) {
                                                logger.log(Level.SEVERE,"Somethinf wrong",e);
                                                JOptionPane.showMessageDialog(frame, "Somethinf wrong :(" );
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
                        /**
                         * 
                         */
                        private static final long serialVersionUID = 1L;

                        {
                            bottomPanel = this;
                            bottomPanel.setLayout(new FlowLayout(
                                    FlowLayout.LEFT));

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

                                        Object value = model.getValueAt(
                                                selIndex, 0);
                                        Object value2 = null;
                                        if (tb.getModel().getColumnName(0).equals("fakulty")){
                                            value2 = "";
                                        }else {
                                            value2 = model.getValueAt(
                                                    selIndex, 1);
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
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
            }
        };
    } catch (Exception e) {
        throw new ClientException(ErrorCode.ERROR,
                "",e);
    }
}
    
    /**
     * Gets the object model.
     *
     * @param isGroup the is group
     * @param el the el
     * @return the object model
     * @throws ClientException 
     */
    public DefaultTableModel getObjectModel(Boolean isGroup, Object el) throws ClientException {
        elemGr = el;
        String groupNumber = null;
        
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
						if (groupNumber.equals(client.getShow(currentfakulty, groupNumber).get(i).getEnrolled())) {
                        
							rowCount++;
							try{	
								st.add(client.getShow(currentfakulty, groupNumber).get(i));
							}catch(ServerException e){
								throw new ClientException(e);
							}
						}
					}
				}catch(ServerException e){
					throw new ClientException(e);
				}
                data = new Object[rowCount][4];
                columnNames = new Object[4];
                
                columnNames[0] = "firstname";
                columnNames[1] = "lastname";
                columnNames[2] = "enrolled";
                columnNames[3] = "groupnumber";         
                for (int i = 0; i < rowCount; i++) {
            
                    if (groupNumber.equals(st.get(i).getEnrolled())) {
                        data[i][0] = st.get(i).getFirstName();
                        data[i][1] = st.get(i).getLastName();
                        data[i][2] = st.get(i).getGroupNumber();
                        data[i][3] = st.get(i).getEnrolled();
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
        if (groups.size()<tb.getRowCount()){
            String numGr = tb.getModel().getValueAt(tb.getRowCount()-1, 1).toString();
            String fakulty = tb.getModel().getValueAt(tb.getRowCount()-1, 0).toString();
            for (Group g : groups) {
                if (g.getNumber().equals(numGr))
                    throw new ClientException(null, "A group with number " + g.getNumber()
                            + "is alredy exist");
            }
			try{	
				client.addGroup(fakulty, numGr);
			}catch(ServerException e){
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
        String f = tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString();
        String g = tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString();
        try {
			client.removeGroup(f, g);
		}catch(ServerException e){
			throw new ClientException (e);
		}
        groups.remove(tb.getSelectedRow());
    }
    
    public Boolean checkForEqualityStudents(){
        
		try {
		System.out.println(">>>> "+tb.getModel().getRowCount()+" <<<< >>>>"+client.getUpdate().size());
            if (tb.getModel().getRowCount()== client.getUpdate().size()){
                return true;
            }else {
                return false;
            }
        }catch (ClientException e) {
            logger.log(Level.SEVERE,"Somethinf wrong",e);
            JOptionPane.showMessageDialog(frame, "Somethinf wrong :(" );
        }catch(ServerException e){
			logger.log(Level.SEVERE,"Somethinf wrong",e);
            JOptionPane.showMessageDialog(frame, "Somethinf wrong :(" );
		}
        return null;
    }
    
    public void updadeStudents() throws ClientException{
        
        String firstName = tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString();
        String lastName  = tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString();
        String enrolled  = tb.getModel().getValueAt(tb.getSelectedRow(), 2).toString();
        String group     = tb.getModel().getValueAt(tb.getSelectedRow(), 3).toString();
        Integer studentID = null;
		try{
			for (int i = 0; i < client.getShow(currentfakulty, group).size(); i++) {   

				if (group.equals(client.getShow(currentfakulty, group).get(i).getEnrolled())) {
					if(tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString().equals(client.getShow(currentfakulty, group).get(i).getFirstName())){
						try{
							studentID = client.getShow(currentfakulty, group).get(i).getId();
						}catch(ServerException e){
							throw new ClientException (e);
						}
					}
				}
			}
		}catch(ServerException e){
				throw new ClientException (e);
		}
		try{
		System.out.println(currentfakulty+group+firstName+lastName+enrolled+studentID);
			client.changeStudent(currentfakulty, group, firstName, lastName, enrolled, studentID);
		}catch(ServerException e){
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
                String fName   = tb.getModel().getValueAt(tb.getRowCount()-1, 0).toString();
                String lName    = tb.getModel().getValueAt(tb.getRowCount()-1, 1).toString();
                String grnumber = groupNumber;
                String enr    = tb.getModel().getValueAt(tb.getRowCount()-1, 2).toString();
                String id = generateID();
        
				try{
					client.addStudent(grnumber, fName, lName, enr, Integer.valueOf(id));
				}catch(ServerException e){
					throw new ClientException(e);
				}
            }
        }catch(ServerException e){
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
            
				if (groupNumber.equals(client.getShow(fak, groupNumber).get(i).getEnrolled())) {
					if(tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString().equals(client.getShow(fak, groupNumber).get(i).getFirstName()) && 
						tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString().equals(client.getShow(fak, groupNumber).get(i).getLastName())){

						try{	
							stud = client.getShow(fak, groupNumber).get(i);
						}catch(ServerException e){
							throw new ClientException(e);
						}
					}
				}
			}
		}catch(ServerException e){
            throw new ClientException(e);
        }
		try{
			client.removeStudent(groupNumber, stud.getId());
		}catch(ServerException e){
            throw new ClientException(e);
        }
    }
    
    /**
     * Generate id.
     *
     * @return the string
     */
    private String generateID() {
        int ID = 0;
        Random rand = new Random();
            ID = rand.nextInt(2147483646);
        return String.valueOf(ID);
    }

    /* 
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
     */
    @Override
    public Object getValueAt(int row, int column) {
        return data[row][column];
    }

    /* 
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column].toString();
    }

    /* 
     */
    @SuppressWarnings("unchecked")
	@Override
    public Class<JTableModel> getColumnClass(int c) {
        return (Class<JTableModel>) getValueAt(0, c).getClass();
    }

    /* 
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0 || column == 1) {
            return false;
        } else {
            return true;
        }
    }

    /* 
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /* 
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
    
    /* 
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
                  JOptionPane.showMessageDialog(null,
              e.getMessage()); 
            }
    }

}
