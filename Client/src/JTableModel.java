package view;

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
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


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
	
	/** The student. */
	private Student student;
	
	/** The logger. */
	private Logger logger;
	
	/** The client. */
	private Client client;

	/**
	 * Instantiates a new j table model.
	 *
	 * @param groups the groups
	 * @param client the client
	 * @throws ClientException the client exception
	 */
	public JTableModel(List<Group> groups, Client client) throws ClientException {

		final Logger logger = Logger.getLogger(JTableModel.class.getName());
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
												try {
													addGoup();
												} catch (ServerException e1) {
													logger.log(Level.WARNING,"Got exception.",e);
													JOptionPane.showMessageDialog(frame, "Something wrong with something :(" );
													fireTableDataChanged();
													try {
														tb.setModel(getObjectModel(isGroup,null));
													} catch (IOException e2) {
														throw new ClientException(e2.toString());
													} catch (SAXException e2) {
														throw new ClientException(e2.toString());
													} catch (ParserConfigurationException e2) {
														throw new ClientException(e2.toString());
													}
													
												}catch (IOException e1) {
													logger.log(Level.WARNING,"Got exception.",e);
													JOptionPane.showMessageDialog(frame, "Something wrong with something :(" );
													fireTableDataChanged();
													try {
														tb.setModel(getObjectModel(isGroup,null));
													} catch (IOException e2) {
														throw new ClientException(e2.toString());
													} catch (SAXException e2) {
														throw new ClientException(e2.toString());
													} catch (ParserConfigurationException e2) {
														throw new ClientException(e2.toString());
													}
													
												}catch (SAXException e1) {
													logger.log(Level.WARNING,"Got exception.",e);
													JOptionPane.showMessageDialog(frame, "Something wrong with something :(" );
													fireTableDataChanged();
													try {
														tb.setModel(getObjectModel(isGroup,null));
													} catch (IOException e2) {
														throw new ClientException(e2.toString());
													} catch (SAXException e2) {
														throw new ClientException(e2.toString());
													} catch (ParserConfigurationException e2) {
														throw new ClientException(e2.toString());
													}
													
												}catch (ParserConfigurationException e1) {
													logger.log(Level.WARNING,"Got exception.",e);
													JOptionPane.showMessageDialog(frame, "Something wrong with something :(" );
													fireTableDataChanged();
													try {
														tb.setModel(getObjectModel(isGroup,null));
													} catch (IOException e2) {
														throw new ClientException(e2.toString());
													} catch (SAXException e2) {
														throw new ClientException(e2.toString());
													} catch (ParserConfigurationException e2) {
														throw new ClientException(e2.toString());
													}
													
												}
												
												fireTableDataChanged();
											} catch (ClientException e2) {
												logger.log(Level.INFO, "Запись лога с уровнем INFO (информационная)",e2);
											}
										}else{
											try {
												addStudent();
												fireTableDataChanged();
												try {
													tb.setModel(getObjectModel(false,elemGr));
												} catch (IOException e2) {
													throw new ClientException(e2);
												}catch (SAXException e2) {
													throw new ClientException(e2);
												}catch (ParserConfigurationException e2) {
													throw new ClientException(e2);
												}
											} catch (ClientException e1) {
												JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												logger.log(Level.INFO, "Something wrong with student :(",e1);
												try {
													tb.setModel(getObjectModel(false,elemGr));
												} catch (IOException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (SAXException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ParserConfigurationException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ClientException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}
											}catch (ServerException e1) {
												JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												logger.log(Level.INFO, "Something wrong with student :(",e1);
												try {
													tb.setModel(getObjectModel(false,elemGr));
												} catch (IOException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (SAXException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ParserConfigurationException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ClientException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}
											}catch (IOException e1) {
												JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												logger.log(Level.INFO, "Something wrong with student :(",e1);
												try {
													tb.setModel(getObjectModel(false,elemGr));
												} catch (IOException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (SAXException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ParserConfigurationException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ClientException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}
											}catch (SAXException e1) {
												JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												logger.log(Level.INFO, "Something wrong with student :(",e1);
												try {
													tb.setModel(getObjectModel(false,elemGr));
												} catch (IOException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (SAXException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ParserConfigurationException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ClientException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}
											}catch (ParserConfigurationException e1) {
												JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												logger.log(Level.INFO, "Something wrong with student :(",e1);
												try {
													tb.setModel(getObjectModel(false,elemGr));
												} catch (IOException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (SAXException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ParserConfigurationException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
												}catch (ClientException e2) {
													JOptionPane.showMessageDialog(frame, "Something wrong with student :(" );
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
													} catch (IOException e) {
														JOptionPane.showMessageDialog(frame, "Something wrong with input :(" );
														logger.log(Level.SEVERE,"!",e);
													} catch (SAXException e) {
														JOptionPane.showMessageDialog(frame, "Something wrong with SAX :(" );
														logger.log(Level.SEVERE,"!",e);
													} catch (ParserConfigurationException e) {
														JOptionPane.showMessageDialog(frame, "Something wrong with Parser :(" );
														logger.log(Level.SEVERE,"!",e);
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
													} catch (ServerException e) {
														JOptionPane.showMessageDialog(frame, "Something wrong :(" );
													}catch (IOException e) {
														JOptionPane.showMessageDialog(frame, "Something wrong :(" );
													}catch (SAXException e) {
														JOptionPane.showMessageDialog(frame, "Something wrong :(" );
													}catch (ParserConfigurationException e) {
														JOptionPane.showMessageDialog(frame, "Something wrong :(" );
													}catch (ClientException e) {
														JOptionPane.showMessageDialog(frame, "Something wrong :(" );
													}
													model1.removeRow(tb.getSelectedRow());
												}else{
													try {
														deleteStudent();
													} catch (IOException e) {
														JOptionPane.showMessageDialog(frame, "ConLost :(" );
													}catch (ServerException e) {
														JOptionPane.showMessageDialog(frame, "ConLost :(" );
													}catch (SAXException e) {
														JOptionPane.showMessageDialog(frame, "ConLost :(" );
													}catch (ParserConfigurationException e) {
														JOptionPane.showMessageDialog(frame, "ConLost :(" );
													}catch (ClientException e) {
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
											System.out.println(model1.getValueAt(0, 0));
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

											System.out.println(tb.getSelectedRow());
											try {
												deleteGoup();
												model1.removeRow(tb.getSelectedRow());
											} catch (ServerException e) {
												logger.log(Level.SEVERE,"Couldn't delete group",e);
												JOptionPane.showMessageDialog(frame, "Couldn't delete group :(" );
											}catch (IOException e) {
												logger.log(Level.SEVERE,"Couldn't delete group",e);
												JOptionPane.showMessageDialog(frame, "Couldn't delete group :(" );
											}catch (SAXException e) {
												logger.log(Level.SEVERE,"Couldn't delete group",e);
												JOptionPane.showMessageDialog(frame, "Couldn't delete group :(" );
											}catch (ParserConfigurationException e) {
												logger.log(Level.SEVERE,"Couldn't delete group",e);
												JOptionPane.showMessageDialog(frame, "Couldn't delete group :(" );
											}catch (ClientException e) {
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
											} catch (IOException e) {
												logger.log(Level.SEVERE,"!",e);
											} catch (SAXException e) {
												logger.log(Level.SEVERE,"!",e);
											} catch (ParserConfigurationException e) {
												logger.log(Level.SEVERE,"!",e);
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
				addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						close();
					}
				});
				
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
	 * Close.
	 */
	private void close() {
		try {
			// conn.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e.getMessage());
		}
	}


	
	
	
	/**
	 * Gets the object model.
	 *
	 * @param isGroup the is group
	 * @param el the el
	 * @return the object model
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the sAX exception
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws ClientException 
	 */
	public DefaultTableModel getObjectModel(Boolean isGroup, Object el) throws IOException, SAXException, ParserConfigurationException, ClientException {
		elemGr = el;
		String groupNumber = null;
		String fak = null;
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
						fak = groups.get(i).getFakulty();
					}
				}
				
				ArrayList<Student> st = new ArrayList<Student>();
				for (int i = 0; i < client.getShow(fak, groupNumber).size(); i++) {
					if (groupNumber.equals(client.getShow(fak, groupNumber).get(i).getEnrolled())) {
						
						rowCount++;
						st.add(client.getShow(fak, groupNumber).get(i));
					}
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
	 * @throws SAXException the sAX exception
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public void addGoup() throws ClientException, ServerException, IOException, SAXException, ParserConfigurationException{
		if (groups.size()<tb.getRowCount()){
			String numGr = tb.getModel().getValueAt(tb.getRowCount()-1, 1).toString();
			String fakulty = tb.getModel().getValueAt(tb.getRowCount()-1, 0).toString();
			for (Group g : groups) {
				if (g.getNumber().equals(numGr))
					throw new ClientException(null, "A group with number " + g.getNumber()
							+ "is alredy exist");
			}
			client.addGroup(fakulty, numGr);
			Group newGroup = new Group(fakulty, numGr);
			groups.add(newGroup);
		}
	}
	
	/**
	 * Delete goup.
	 *
	 * @throws ServerException the server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the sAX exception
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws ClientException 
	 */
	public void deleteGoup() throws ServerException, IOException, SAXException,ParserConfigurationException, ClientException {
		String f = tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString();
		String g = tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString();
		client.removeGroup(f, g);
		groups.remove(tb.getSelectedRow());
	}
	
	
	/**
	 * Adds the student.
	 *
	 * @return the student
	 * @throws ClientException the client exception
	 * @throws ServerException the server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the sAX exception
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public Student addStudent() throws ClientException, ServerException, IOException, SAXException, ParserConfigurationException{
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
		
				client.addStudent(grnumber, fName, lName, enr, Integer.valueOf(id));
			}
		}catch(Exception e){
			throw new ClientException(e);
		}
		
		return student;
	}
	
	/**
	 * Delete student.
	 *
	 * @throws ServerException the server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the sAX exception
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws ClientException 
	 */
	public void deleteStudent() throws ServerException, IOException, SAXException, ParserConfigurationException, ClientException{
		String groupNumber = null;
		String fak = null;
		Student stud = null;
		for (int i = 0; i < groups.size(); i++) {
			if (elemGr.equals(groups.get(i).getFakulty()) || elemGr.equals(groups.get(i).getNumber())){
				groupNumber = groups.get(i).getNumber();
				fak = groups.get(i).getFakulty();
			}
		}
		for (int i = 0; i < client.getShow(fak, groupNumber).size(); i++) {
			
			if (groupNumber.equals(client.getShow(fak, groupNumber).get(i).getEnrolled())) {
				if(tb.getModel().getValueAt(tb.getSelectedRow(), 0).toString().equals(client.getShow(fak, groupNumber).get(i).getFirstName()) && 
					tb.getModel().getValueAt(tb.getSelectedRow(), 1).toString().equals(client.getShow(fak, groupNumber).get(i).getLastName())){

					stud = client.getShow(fak, groupNumber).get(i);
				}
			}
		}
		client.removeStudent(groupNumber, stud.getId());	
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
	@Override
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
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