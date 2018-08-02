package app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker.StateValue;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import locale.Context;
import locale.ContextChangeListener;
import utils.FileTableModel;

public class App {

	private static final String UNDO = "undo.png";
	private static final String RIGHT_ARROW = "right.png";
	private static final String LEFT_ARROW = "left.png";

	private static JFrame frame;
	private static JTable tableLeft;
	private static JTable tableRight;
	private static FileTableModel fileTableModelLeft;
	private static FileTableModel fileTableModelRight;
	private static JComboBox<File> rightComboBox;
	private static JComboBox<File> leftComboBox;
	private static JButton leftRootButton;
	private static JButton leftButtonCreateFile;
	private static JButton leftButtonCreateFolder;
	private static JButton leftButtonDelete;
	private static JButton rightRootButton;
	private static JButton rightButtonCreateFile;
	private static JButton rightButtonCreateFolder;
	private static JButton rightButtonDelete;
	private static JButton copyFromLeftToRight;
	private static JButton copyFromRightToLeft;
	private static JButton cancelButton;
	private static JToolBar progressToolBar;
	private static JTextField leftField;
	private static JTextField rightField;
	private static JMenu menuClose;
	private static JMenu menuLanguage;
	private static JMenu menuAbout;
	private static JMenuItem menuItemClose;
	private static JMenuItem menuItemEnglish;
	private static JMenuItem menuItemPolish;
	private static JMenuItem menuItemAbout;

	private static MySwingWorker worker;
	private static DeleteSwingWorker deleteSwingWorker;

	private static JProgressBar progressBar;

	private static final Locale locale = Locale.ENGLISH;
	private static final Locale localePL = new Locale("PL");

	private static Desktop desktop;

	private static Context context;

	public static void main(String[] args) {

		initGUI();
		context = new Context("MyResources");
		context.setLocale(localePL);
		ResourceBundle resourceBundle = context.getBundle();
		desktop = Desktop.getDesktop();
		initNames(resourceBundle, context);
		initActions();
	}

	/**
	 *
	 */
	private static void initGUI() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		// INIT LEFT SIDE
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		JPanel leftPanelMenu = new JPanel();
		leftPanelMenu.setLayout(new BoxLayout(leftPanelMenu, BoxLayout.X_AXIS));
		// button
		leftRootButton = new JButton();
		leftRootButton.setBorder(BorderFactory.createEmptyBorder());
		ImageIcon leftIcon = new ImageIcon(UNDO);
		Image leftImg = leftIcon.getImage();
		Image newLeftImg = leftImg.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);
		leftIcon = new ImageIcon(newLeftImg);
		leftRootButton.setIcon(leftIcon);
		leftPanelMenu.add(leftRootButton);
		// combobox
		leftComboBox = new JComboBox<File>(File.listRoots());
		// rightComboBox.setMaximumSize(new Dimension(1000, 20));
		leftPanelMenu.add(leftComboBox);
		// toolbar
		JToolBar toolBarLeft = new JToolBar();
		leftButtonCreateFile = new JButton();
		leftButtonCreateFolder = new JButton();
		leftButtonDelete = new JButton();
		leftField = new JTextField();

		// INIT TABLE LEFT
		tableLeft = new JTable();
		tableLeft.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableLeft.setAutoCreateRowSorter(true);
		tableLeft.setShowVerticalLines(true);
		fileTableModelLeft = new FileTableModel(leftField);
		tableLeft.setModel(fileTableModelLeft);
		
		/**
		 * SORTING
		 */
//		TableRowSorter<TableModel> sorter = new TableRowSorter<>(fileTableModelLeft);
//		sorter.setComparator(2, new Comparator<String>(){
//
//			@Override
//			public int compare(String o1, String o2)
//			{
//				System.out.println("Nooo");
//				if(o1.equals("Folder") && !o2.equals("Folder"))
//				{
//					return -1;
//				}
//				else
//				{
//					if(!o1.equals("Folder") && o2.equals("Folder"))
//					{
//						return 1;
//					}
//					else
//					{
//						if(o1.equals("Folder") && o2.equals("Folder"))
//						{
//							return 0;
//						}
//						else
//						{
//							String[] stringsO1 = o1.split(" ");
//							long o1Long = Long.parseLong(stringsO1[0]);
//							long multiplierO1=1;
//							switch(stringsO1[1])
//							{
//							case "B":
//								multiplierO1 = 1;
//								break;
//							case "kB":
//								multiplierO1 = 1000;
//								break;
//							case "MB":
//								multiplierO1 = 1000000;
//								break;
//							}
//							
//							String[] stringsO2 = o2.split(" ");
//							long o2Long = Long.parseLong(stringsO2[0]);
//							long multiplierO2=1;
//							switch(stringsO2[1])
//							{
//							case "B":
//								multiplierO2 = 1;
//								break;
//							case "kB":
//								multiplierO2 = 1000;
//								break;
//							case "MB":
//								multiplierO2 = 1000000;
//								break;
//							}
//							return (int)(o1Long * multiplierO1 - o2Long * multiplierO2);
//						}
//					}
//				}
////				return -1;
//			}
//			
//		});
//		tableLeft.setRowSorter(sorter);
		
		// mozna dac przycisk ze szczegolami
		toolBarLeft.add(leftField);
		toolBarLeft.add(leftButtonCreateFile);
		toolBarLeft.add(leftButtonCreateFolder);
		toolBarLeft.add(leftButtonDelete);
		toolBarLeft.add(leftField);
		leftPanel.add(leftPanelMenu, BorderLayout.NORTH);
		leftPanel.add(new JScrollPane(tableLeft), BorderLayout.CENTER);
		leftPanel.add(toolBarLeft, BorderLayout.SOUTH);

		File[] fileListRoots = File.listRoots();
		if (fileListRoots.length > 0) {
			fileTableModelLeft.setParentFile(fileListRoots[0]);
		}

		// INIT RIGHT SIDE
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		JPanel rightPanelMenu = new JPanel();
		rightPanelMenu.setLayout(new BoxLayout(rightPanelMenu, BoxLayout.X_AXIS));
		// button
		rightRootButton = new JButton();
		rightRootButton.setBorder(BorderFactory.createEmptyBorder());
		ImageIcon icon = new ImageIcon(UNDO);
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		rightRootButton.setIcon(icon);
		rightPanelMenu.add(rightRootButton);
		// combobox
		rightComboBox = new JComboBox<File>(File.listRoots());
		// rightComboBox.setMaximumSize(new Dimension(1000, 20));
		rightPanelMenu.add(rightComboBox);
		// toolbar
		JToolBar toolBarRight = new JToolBar();
		rightButtonCreateFile = new JButton();
		rightButtonCreateFolder = new JButton();
		rightButtonDelete = new JButton();
		rightField = new JTextField();

		// INIT TABLE RIGHT
		tableRight = new JTable();
		tableRight.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRight.setAutoCreateRowSorter(true);
		tableRight.setShowVerticalLines(true);
		fileTableModelRight = new FileTableModel(rightField);
		tableRight.setModel(fileTableModelRight);

		// mozna dac przycisk ze szczegolami

		toolBarRight.add(rightButtonCreateFile);
		toolBarRight.add(rightButtonCreateFolder);
		toolBarRight.add(rightButtonDelete);
		toolBarRight.add(rightField);
		rightPanel.add(rightPanelMenu, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(tableRight), BorderLayout.CENTER);
		rightPanel.add(toolBarRight, BorderLayout.SOUTH);

		File[] fileListRootsRight = File.listRoots();
		if (fileListRootsRight.length > 0) {
			fileTableModelRight.setParentFile(fileListRootsRight[0]);
		}

		// CENTER PANEL
		JToolBar centerPanel = new JToolBar(JToolBar.VERTICAL);
		centerPanel.setFloatable(false);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		copyFromLeftToRight = new JButton();
		// moveFromLeftToRight = new JButton();
		icon = new ImageIcon(RIGHT_ARROW);
		img = icon.getImage();
		newimg = img.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		copyFromLeftToRight.setIcon(icon);
		// moveFromLeftToRight.setIcon(icon);

		copyFromRightToLeft = new JButton();
		// moveFromRightToLeft = new JButton();
		icon = new ImageIcon(LEFT_ARROW);
		img = icon.getImage();
		newimg = img.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		copyFromRightToLeft.setIcon(icon);
		// moveFromRightToLeft.setIcon(icon);

		copyFromLeftToRight.setAlignmentY(Component.CENTER_ALIGNMENT);
		copyFromLeftToRight.setAlignmentX(Component.CENTER_ALIGNMENT);

		copyFromRightToLeft.setAlignmentY(Component.CENTER_ALIGNMENT);
		copyFromRightToLeft.setAlignmentX(Component.CENTER_ALIGNMENT);

		centerPanel.add(copyFromLeftToRight);
		centerPanel.add(copyFromRightToLeft);
		centerPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// centerPanel.add(moveFromLeftToRight);
		// centerPanel.add(moveFromRightToLeft);

		final GridBagLayout gridbag = new GridBagLayout();
		final GridBagConstraints cc = new GridBagConstraints();

		final JPanel gridPanel = new JPanel();
		gridPanel.setLayout(gridbag);

		cc.weightx = 10;
		cc.weighty = 1;
		cc.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(leftPanel, cc);
		gridPanel.add(leftPanel);

		cc.weightx = 1;
		gridbag.setConstraints(centerPanel, cc);
		gridPanel.add(centerPanel);

		cc.weightx = 10;
		gridbag.setConstraints(rightPanel, cc);
		gridPanel.add(rightPanel);

		JMenuBar menuBar = new JMenuBar();
		menuClose = new JMenu();
		menuLanguage = new JMenu();
		menuAbout = new JMenu();
		menuItemClose = new JMenuItem();
		menuItemEnglish = new JMenuItem();
		menuItemPolish = new JMenuItem();
		menuItemAbout = new JMenuItem();

		menuClose.add(menuItemClose);
		menuLanguage.add(menuItemEnglish);
		menuLanguage.add(menuItemPolish);
		menuAbout.add(menuItemAbout);
		menuBar.add(menuClose);
		menuBar.add(menuLanguage);
		menuBar.add(menuAbout);

		progressBar = new JProgressBar();
		progressBar.setString("%");
		progressBar.setStringPainted(true);
		// progressBar.setVisible(false);

		cancelButton = new JButton();

		progressToolBar = new JToolBar();
		progressToolBar.setFloatable(false);
		progressToolBar.setLayout(new BoxLayout(progressToolBar, BoxLayout.X_AXIS));
		progressToolBar.setVisible(false);

		progressToolBar.add(cancelButton);
		progressToolBar.add(progressBar);

		frame.add(gridPanel, BorderLayout.CENTER);
		frame.add(progressToolBar, BorderLayout.SOUTH);
		// frame.add(progressBar, BorderLayout.SOUTH);
		frame.add(menuBar, BorderLayout.NORTH);

		frame.pack();
		frame.setMinimumSize(new Dimension(frame.getWidth(), 200));
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

	}

	/**
	 *
	 * @param resourceBundle
	 */

	private static void initNames(ResourceBundle resourceBundle, Context argContext) {
		leftButtonDelete.setText(resourceBundle.getString("delete"));
		rightButtonDelete.setText(resourceBundle.getString("delete"));
		leftButtonCreateFile.setText(resourceBundle.getString("createfile"));
		rightButtonCreateFile.setText(resourceBundle.getString("createfile"));
		leftButtonCreateFolder.setText(resourceBundle.getString("createfolder"));
		rightButtonCreateFolder.setText(resourceBundle.getString("createfolder"));
		copyFromLeftToRight.setText(resourceBundle.getString("copy"));
		copyFromRightToLeft.setText(resourceBundle.getString("copy"));
		frame.setTitle(resourceBundle.getString("titleapp"));
		menuClose.setText(resourceBundle.getString("menufile"));
		menuLanguage.setText(resourceBundle.getString("menulanguage"));
		menuAbout.setText(resourceBundle.getString("menuabout"));
		menuItemClose.setText(resourceBundle.getString("itemclose"));
		menuItemEnglish.setText(resourceBundle.getString("itemenglish"));
		menuItemPolish.setText(resourceBundle.getString("itempolish"));
		menuItemAbout.setText(resourceBundle.getString("itemabout"));
		cancelButton.setText(resourceBundle.getString("cancel"));
		fileTableModelLeft.setColumns(new String[] { resourceBundle.getString("colicon"),
				resourceBundle.getString("colfile"), resourceBundle.getString("colsize"),
				resourceBundle.getString("colcreated"), resourceBundle.getString("colmodified") });
		fileTableModelRight.setColumns(new String[] { resourceBundle.getString("colicon"),
				resourceBundle.getString("colfile"), resourceBundle.getString("colsize"),
				resourceBundle.getString("colcreated"), resourceBundle.getString("colmodified") });
		fileTableModelLeft.changeLocale(argContext.getLocale());
		fileTableModelRight.changeLocale(argContext.getLocale());
	}

	/**
	 *
	 */
	private static void initActions() {
		context.addContextChangeListener(new ContextChangeListener() {

			@Override
			public void contextChanged() {
				initNames(context.getBundle(), context);
			}

		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (worker != null && worker.getState() == StateValue.STARTED) {
					worker.cancel(true);
				}
				if (deleteSwingWorker != null && deleteSwingWorker.getState() == StateValue.STARTED) {
					deleteSwingWorker.cancel(true);
				}
			}

		});

		
		rightComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					File file = (File) e.getItem();
					fileTableModelRight.setParentFile(file);
				}
			}
		});

		leftComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					File file = (File) e.getItem();
					fileTableModelLeft.setParentFile(file);
				}
			}
		});

		tableLeft.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				int row = table.rowAtPoint(point);
				if (mouseEvent.getClickCount() == 2) {
					File file = fileTableModelLeft.getFile(row);
					if (file.isDirectory()) {
						fileTableModelLeft.setParentFile(file);
					}
					if (file.isFile()) {
						try {
							desktop.open(file);
						} catch (IOException er) {
							JOptionPane.showMessageDialog(frame, context.getBundle().getString("errorfile"),
									context.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}

			}
		});

		tableRight.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				int row = table.rowAtPoint(point);
				if (mouseEvent.getClickCount() == 2) {
					File file = fileTableModelRight.getFile(row);
					if (file.isDirectory()) {
						fileTableModelRight.setParentFile(file);
					}
					if (file.isFile()) {
						try {
							desktop.open(file);
						} catch (IOException er) {
							JOptionPane.showMessageDialog(frame, context.getBundle().getString("errorfile"),
									context.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		leftRootButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = fileTableModelLeft.getParentFile();
				if (file != null) {
					fileTableModelLeft.setParentFile(file.getParentFile());
					int row = fileTableModelLeft.getRowOfFile(file);
					if (row >= 0) {
						tableLeft.setRowSelectionInterval(row, row);
					}
				}
			}

		});

		rightRootButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = fileTableModelRight.getParentFile();
				if (file != null) {
					fileTableModelRight.setParentFile(file.getParentFile());
					int row = fileTableModelRight.getRowOfFile(file);
					if (row >= 0) {
						tableRight.setRowSelectionInterval(row, row);
					}
				}

			}

		});

		leftButtonCreateFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File parent = fileTableModelLeft.getParentFile();
				String fileName = JOptionPane.showInputDialog(new JTextField(),
						context.getBundle().getString("windowcreatefiletitle"),
						context.getBundle().getString("windowcreatefilemsg"), JOptionPane.INFORMATION_MESSAGE);
				File createdFile = new File(parent.getAbsolutePath() + "\\" + fileName);
				try {
					createdFile.createNewFile();
				} catch (IOException er) {
					er.printStackTrace();
				}

				fileTableModelLeft.setParentFile(parent);

			}

		});

		rightButtonCreateFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				File parent = fileTableModelRight.getParentFile();
				String fileName = JOptionPane.showInputDialog(new JTextField(),
						context.getBundle().getString("windowcreatefiletitle"),
						context.getBundle().getString("windowcreatefilemsg"), JOptionPane.INFORMATION_MESSAGE);
				File createdFile = new File(parent.getAbsolutePath() + "\\" + fileName);
				try {
					createdFile.createNewFile();
				} catch (IOException er) {
					er.printStackTrace();
				}

				fileTableModelRight.setParentFile(parent);

			}

		});

		leftButtonCreateFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File parent = fileTableModelLeft.getParentFile();
				String fileName = JOptionPane.showInputDialog(new JTextField(),
						context.getBundle().getString("windowcreatefiletitle"),
						context.getBundle().getString("windowcreatefilemsg"), JOptionPane.INFORMATION_MESSAGE);
				File createdFile = new File(parent.getAbsolutePath() + "\\" + fileName);
				try {
					createdFile.mkdirs();
				} catch (SecurityException er) {
					er.printStackTrace();
				}

				fileTableModelLeft.setParentFile(parent);
			}
		});

		rightButtonCreateFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File parent = fileTableModelRight.getParentFile();
				String fileName = JOptionPane.showInputDialog(new JTextField(),
						context.getBundle().getString("windowcreatefiletitle"),
						context.getBundle().getString("windowcreatefilemsg"), JOptionPane.INFORMATION_MESSAGE);
				File createdFile = new File(parent.getAbsolutePath() + "\\" + fileName);
				try {
					createdFile.mkdirs();
				} catch (SecurityException er) {
					er.printStackTrace();
				}

				fileTableModelRight.setParentFile(parent);
			}
		});

		copyFromLeftToRight.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tableLeft.getSelectedRow();
				if (selectedRow >= 0) {

					File selectedFile = fileTableModelLeft.getFile(selectedRow);
					File directory = fileTableModelRight.getParentFile();

					File testDuplicate = new File(directory.getAbsolutePath() + "\\" + selectedFile.getName());
					if (testDuplicate.exists()) {

						int result = JOptionPane.showConfirmDialog(frame, context.getBundle().getString("confirmcopy"),
								context.getBundle().getString("copy"), JOptionPane.WARNING_MESSAGE);
						if (result == JOptionPane.OK_OPTION) {
							// MySwingWorker worker = new MySwingWorker(selectedFile, directory,
							// progressBar);

							if (worker == null || (worker != null && worker.getState() != StateValue.STARTED)) {
								worker = new MySwingWorker(selectedFile, directory, progressBar, progressToolBar);

								worker.addPropertyChangeListener(new PropertyChangeListener() {

									@Override
									public void propertyChange(PropertyChangeEvent evt) {
										fileTableModelRight.setParentFile(fileTableModelRight.getParentFile());
									}

								});
								worker.execute();
							}
						}
					} else {
						// MySwingWorker worker = new MySwingWorker(selectedFile, directory,
						// progressBar);
						if (worker == null || (worker != null && worker.getState() != StateValue.STARTED)) {

							worker = new MySwingWorker(selectedFile, directory, progressBar, progressToolBar);

							worker.addPropertyChangeListener(new PropertyChangeListener() {

								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									fileTableModelRight.setParentFile(fileTableModelRight.getParentFile());
								}

							});
							worker.execute();
						}
					}

				}
			}
		});

		copyFromRightToLeft.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tableRight.getSelectedRow();
				if (selectedRow >= 0) {
					File selectedFile = fileTableModelRight.getFile(selectedRow);
					File directory = fileTableModelLeft.getParentFile();
					File testDuplicate = new File(directory.getAbsolutePath() + "\\" + selectedFile.getName());
					if (testDuplicate.exists()) {

						int result = JOptionPane.showConfirmDialog(frame, context.getBundle().getString("confirmcopy"),
								context.getBundle().getString("copy"), JOptionPane.WARNING_MESSAGE);
						if (result == JOptionPane.OK_OPTION) {
							// MySwingWorker worker = new MySwingWorker(selectedFile, directory,
							// progressBar, progressToolBar);

							if (worker == null || (worker != null && worker.getState() != StateValue.STARTED)) {

								worker = new MySwingWorker(selectedFile, directory, progressBar, progressToolBar);
								worker.addPropertyChangeListener(new PropertyChangeListener() {

									@Override
									public void propertyChange(PropertyChangeEvent evt) {
										fileTableModelLeft.setParentFile(fileTableModelLeft.getParentFile());
									}

								});
								worker.execute();
							}
						}
					} else {

						if (worker == null || (worker != null && worker.getState() != StateValue.STARTED)) {
							worker = new MySwingWorker(selectedFile, directory, progressBar, progressToolBar);
							// MySwingWorker worker = new MySwingWorker(selectedFile, directory,
							// progressBar,progressToolBar);

							worker.addPropertyChangeListener(new PropertyChangeListener() {

								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									fileTableModelLeft.setParentFile(fileTableModelLeft.getParentFile());
								}

							});
							worker.execute();
						}
					}
				}
			}
		});

		leftField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField thisTextField = (JTextField) e.getSource();
				File file = new File(thisTextField.getText());
				if (file.exists()) {
					if (file.isDirectory()) {
						fileTableModelLeft.setParentFile(file);
					}
					if (file.isFile()) {
						try {
							desktop.open(file);
						} catch (IOException er) {
							JOptionPane.showMessageDialog(frame, context.getBundle().getString("errorfile"),
									context.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					thisTextField.setText(fileTableModelLeft.getParentFile().getAbsolutePath());
				}
			}

		});

		rightField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField thisTextField = (JTextField) e.getSource();
				File file = new File(thisTextField.getText());
				if (file.exists()) {
					if (file.isDirectory()) {
						fileTableModelRight.setParentFile(file);
					}
					if (file.isFile()) {
						try {
							desktop.open(file);
						} catch (IOException er) {
							JOptionPane.showMessageDialog(frame, context.getBundle().getString("errorfile"),
									context.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					thisTextField.setText(fileTableModelRight.getParentFile().getAbsolutePath());
				}
			}

		});

		leftButtonDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int selectedRow = tableLeft.getSelectedRow();
				if (selectedRow >= 0) {
					File file = fileTableModelLeft.getFile(selectedRow);
					if (file == null) {
						JOptionPane.showMessageDialog(frame, context.getBundle().getString("errornotfoundfile"),
								context.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					int result = JOptionPane.showConfirmDialog(frame, context.getBundle().getString("confirmdelete"),
							context.getBundle().getString("deletefile"), JOptionPane.WARNING_MESSAGE);

					if (result == JOptionPane.OK_OPTION) {
						fileTableModelLeft.removeFile(file);
						fileTableModelRight.removeFile(file);

						if (deleteSwingWorker == null
								|| (deleteSwingWorker != null && deleteSwingWorker.getState() != StateValue.STARTED)) {
							deleteSwingWorker = new DeleteSwingWorker(file, progressBar, progressToolBar);

							deleteSwingWorker.execute();
							deleteSwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									DeleteSwingWorker dsw = (DeleteSwingWorker) evt.getSource();
									if (dsw.getState() == StateValue.DONE) {
										fileTableModelLeft.setParentFile(fileTableModelLeft.getParentFile());
										fileTableModelRight.setParentFile(fileTableModelRight.getParentFile());
									}

								}

							});
						}
					}
				}
			}
		});

		rightButtonDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tableRight.getSelectedRow();
				if (selectedRow >= 0) {
					File file = fileTableModelRight.getFile(selectedRow);
					if (file == null) {
						JOptionPane.showMessageDialog(frame, context.getBundle().getString("errornotfoundfile"),
								context.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					int result = JOptionPane.showConfirmDialog(frame, context.getBundle().getString("confirmdelete"),
							context.getBundle().getString("deletefile"), JOptionPane.WARNING_MESSAGE);

					if (result == JOptionPane.OK_OPTION) {
						fileTableModelRight.removeFile(file);
						fileTableModelLeft.removeFile(file);

						if (deleteSwingWorker == null
								|| (deleteSwingWorker != null && deleteSwingWorker.getState() != StateValue.STARTED)) {
							// DeleteSwingWorker deleteSwingWorker = new DeleteSwingWorker(file,
							// progressBar, progressToolBar);

							deleteSwingWorker = new DeleteSwingWorker(file, progressBar, progressToolBar);
							deleteSwingWorker.execute();
							deleteSwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									DeleteSwingWorker dsw = (DeleteSwingWorker) evt.getSource();
									if (dsw.getState() == StateValue.DONE) {
										fileTableModelRight.setParentFile(fileTableModelRight.getParentFile());
										fileTableModelLeft.setParentFile(fileTableModelLeft.getParentFile());
									}

								}

							});
						}
					}
				}
			}
		});

		menuItemClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		menuItemEnglish.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				context.setLocale(locale);
			}
		});

		menuItemPolish.addActionListener((x) -> {
			context.setLocale(localePL);
		});

		menuItemAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, context.getBundle().getString("informationabout"),
						context.getBundle().getString("itemabout"), JOptionPane.INFORMATION_MESSAGE);
			}

		});

	}

}
