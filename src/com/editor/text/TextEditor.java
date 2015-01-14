package com.editor.text;

/**
 * 		@author eltntawy
 * 
 * 		Copyright (C) 2014-2015 Mohamed Refaat, Cairo - Egypt
 *
 *	 	This program is free software: you can redistribute it and/or modify
 * 		it under the terms of the GNU General Public License as published by
 *		the Free Software Foundation, either version 3 of the License, or
 *		(at your option) any later version.
 *
 *		This program is distributed in the hope that it will be useful,
 *		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *		GNU General Public License for more details.
 *
 *		You should have received a copy of the GNU General Public License
 *		along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * **/

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.SliderUI;
import javax.swing.JPopupMenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TextEditor extends JFrame implements ActionListener, KeyListener, ChangeListener {

    private JTextArea txtArea;
    private JScrollPane mainPanel;
    private JMenuBar menuBar;

    private JMenu menuFile;
    private JMenu menuEdit;
    private JMenu menuAbout;

    private JMenu popupMenuFile;
    private JMenu popupMenuEdit;
    private JMenu popupMenuAbout;

    private JMenuItem menuItemNew;
    private JMenuItem menuItemOpen;
    private JMenuItem menuItemSave;
    private JMenuItem menuItemExit;

    private JMenuItem menuItemFormat;
    private JMenuItem menuItemCopy;
    private JMenuItem menuItemPast;
    private JMenuItem menuItemCut;
    private JMenuItem menuItemSelectAll;

    private JMenuItem menuItemUndo;
    private JMenuItem menuItemRedo;
    private JMenuItem menuItemAbout;

    private JMenuItem popupMenuItemNew;
    private JMenuItem popupMenuItemOpen;
    private JMenuItem popupMenuItemSave;
    private JMenuItem popupMenuItemExit;

    private JMenuItem popupMenuItemFormat;
    private JMenuItem popupMenuItemCopy;
    private JMenuItem popupMenuItemPast;
    private JMenuItem popupMenuItemCut;
    private JMenuItem popupMenuItemSelectAll;

    private JMenuItem popupMenuItemUndo;
    private JMenuItem popupMenuItemRedo;
    private JMenuItem popupMenuItemAbout;

    private int index = 1;

    private JMenu menuLookAndFeel;

    Vector<String> historyVector = new Vector<String>();

    private JFileChooser fileChooser;
    private JPopupMenu popupMenu;
    private JButton btnNew;
    private JButton btnSave;
    private JButton btnCopy;
    private JButton btnPast;
    private JButton btnCut;
    private JButton btnOpen;
    private JButton btnUndo;
    private JButton btnRedo;
    private JComboBox<String> comboBoxFont;
    private JComboBox<String> comboBoxColor;

    private Font oldFont;
    private Font newFont;

    private Color oldColor = Color.black;
    private Color newColor = Color.black;

    private JSlider sizeJSlider;

    public TextEditor() {

	initSplashScreen();
	init();
	dynInit();

	setExtendedState(MAXIMIZED_BOTH);
    }

    static void renderSplashFrame(Graphics2D g, int frame) {
	final String[] comps = { "", ".", "..", "..." };
	g.setComposite(AlphaComposite.Clear);
	g.fillRect(0, 0, 400, 300);
	g.setPaintMode();
	g.setColor(Color.WHITE);
	g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
	g.drawString("Loading" + comps[(frame / 5) % 4] + "", 10, 280);
    }

    public void initSplashScreen() {

	SplashScreen splashScreen = SplashScreen.getSplashScreen();

	if (splashScreen == null) {
	    System.err.println("Error: TextEditor.initSplashScreen() cannot initialize SplashScreen");
	    return;
	}

	Graphics2D graphics = splashScreen.createGraphics();

	for (int i = 0; i < 25; i++) {

	    renderSplashFrame(graphics, i);
	    splashScreen.update();
	    try {
		Thread.sleep(90);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	splashScreen.close();
	toFront();

    }

    public void init() {

	// initializing component
	txtArea = new JTextArea();

	oldFont = txtArea.getFont();
	newFont = txtArea.getFont();

	popupMenu = new JPopupMenu();
	popupMenu.setPreferredSize(new Dimension(100, 100));
	addPopup(txtArea, popupMenu);
	mainPanel = new JScrollPane();
	menuBar = new JMenuBar();

	fileChooser = new JFileChooser();
	fileChooser.setFileFilter(new myFileFilter());

	menuLookAndFeel = new JMenu("Themes");

	menuFile = new JMenu("File");
	menuFile.setMnemonic('f');

	menuEdit = new JMenu("Edit");
	menuEdit.setMnemonic('e');

	menuAbout = new JMenu("About");
	menuAbout.setMnemonic('u');

	popupMenuFile = new JMenu("File");
	popupMenuEdit = new JMenu("Edit");
	popupMenuAbout = new JMenu("About");

	menuItemNew = new JMenuItem("New");
	menuItemNew.setMnemonic('n');
	menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));

	menuItemOpen = new JMenuItem("Open");
	menuItemOpen.setMnemonic('o');
	menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));

	menuItemSave = new JMenuItem("Save");
	menuItemSave.setMnemonic('s');
	menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));

	menuItemExit = new JMenuItem("Exit");
	menuItemExit.setMnemonic('x');
	menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));

	menuItemCopy = new JMenuItem("Copy");
	menuItemCopy.setMnemonic('C');
	menuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));

	menuItemFormat = new JMenuItem("Format");
	menuItemFormat.setMnemonic('f');
	menuItemFormat.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));

	menuItemPast = new JMenuItem("Past");
	menuItemPast.setMnemonic('p');
	menuItemPast.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));

	menuItemCut = new JMenuItem("Cut");
	menuItemCut.setMnemonic('d');
	menuItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));

	menuItemSelectAll = new JMenuItem("Select All");
	menuItemSelectAll.setMnemonic('a');
	menuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));

	menuItemAbout = new JMenuItem("About...");
	menuItemAbout.setMnemonic('u');
	menuItemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK));

	menuItemUndo = new JMenuItem("Undo");
	menuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));

	menuItemRedo = new JMenuItem("Redo");
	menuItemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK));

	// adding action listener for component
	menuItemNew.addActionListener(this);
	menuItemOpen.addActionListener(this);
	menuItemSave.addActionListener(this);
	menuItemExit.addActionListener(this);
	menuItemCopy.addActionListener(this);
	menuItemFormat.addActionListener(this);
	menuItemPast.addActionListener(this);
	menuItemCut.addActionListener(this);
	menuItemSelectAll.addActionListener(this);
	menuItemAbout.addActionListener(this);

	menuItemUndo.addActionListener(this);
	menuItemRedo.addActionListener(this);

	// ------------------------------------------------
	popupMenuItemNew = new JMenuItem("New");
	popupMenuItemOpen = new JMenuItem("Open");
	popupMenuItemSave = new JMenuItem("Save");
	popupMenuItemExit = new JMenuItem("Exit");
	popupMenuItemCopy = new JMenuItem("Copy");
	popupMenuItemFormat = new JMenuItem("Format");
	popupMenuItemPast = new JMenuItem("Past");
	popupMenuItemCut = new JMenuItem("Delete");
	popupMenuItemSelectAll = new JMenuItem("Select All");
	popupMenuItemAbout = new JMenuItem("About...");
	popupMenuItemUndo = new JMenuItem("Undo");
	popupMenuItemRedo = new JMenuItem("Redo");

	// adding action listener for component
	popupMenuItemNew.addActionListener(this);
	popupMenuItemOpen.addActionListener(this);
	popupMenuItemSave.addActionListener(this);
	popupMenuItemExit.addActionListener(this);
	popupMenuItemCopy.addActionListener(this);
	popupMenuItemFormat.addActionListener(this);
	popupMenuItemPast.addActionListener(this);
	popupMenuItemCut.addActionListener(this);
	popupMenuItemSelectAll.addActionListener(this);
	popupMenuItemAbout.addActionListener(this);

	popupMenuItemUndo.addActionListener(this);
	popupMenuItemRedo.addActionListener(this);
	// ----------------------------------------------------------------

	comboBoxFont = new JComboBox<String>();
	comboBoxColor = new JComboBox<String>();
	sizeJSlider = new JSlider(JSlider.HORIZONTAL);

	txtArea.addKeyListener(this);
	historyVector.add("");

    }

    public void dynInit() {

	// init frame
	this.setTitle("Text Editor version 1.0");
	this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	this.setSize(711, 400);
	this.setVisible(true);

	// adding component
	menuFile.add(menuItemNew);
	menuFile.add(menuItemOpen);
	menuFile.add(menuItemSave);
	menuFile.add(menuItemExit);

	menuEdit.add(menuItemFormat);
	menuEdit.add(menuItemCopy);
	menuEdit.add(menuItemPast);
	menuEdit.add(menuItemCut);
	menuEdit.add(menuItemSelectAll);
	menuEdit.add(menuItemUndo);
	menuEdit.add(menuItemRedo);

	menuAbout.add(menuItemAbout);

	// adding component popup
	popupMenuFile.add(popupMenuItemNew);
	popupMenuFile.add(popupMenuItemOpen);
	popupMenuFile.add(popupMenuItemSave);
	popupMenuFile.add(popupMenuItemExit);

	popupMenuEdit.add(popupMenuItemFormat);
	popupMenuEdit.add(popupMenuItemCopy);
	popupMenuEdit.add(popupMenuItemPast);
	popupMenuEdit.add(popupMenuItemCut);
	popupMenuEdit.add(popupMenuItemSelectAll);
	popupMenuEdit.add(popupMenuItemUndo);
	popupMenuEdit.add(popupMenuItemRedo);

	popupMenuAbout.add(popupMenuItemAbout);

	// popup menu
	popupMenu.add(popupMenuFile);
	popupMenu.add(popupMenuEdit);
	popupMenu.add(popupMenuAbout);

	menuBar.add(menuFile);
	menuBar.add(menuEdit);
	menuBar.add(menuLookAndFeel);
	menuBar.add(menuAbout);

	// get system available lookAndFeel
	LookAndFeelInfo[] lookAndFeelArray = UIManager.getInstalledLookAndFeels();

	for (LookAndFeelInfo l : lookAndFeelArray) {

	    JMenuItem item = new JMenuItem();
	    item.setText(l.getName());
	    item.setToolTipText(l.getClassName());

	    item.addActionListener(this);
	    menuLookAndFeel.add(item);

	}

	// add textArea to MainPanel
	mainPanel.setViewportView(txtArea);

	// adding component to frame
	this.setJMenuBar(menuBar);
	getContentPane().add(mainPanel);

	menuBar = new JMenuBar();
	menuBar.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menuBar.setPreferredSize(new Dimension(0, 60));
	menuBar.setMinimumSize(new Dimension(0, 45));
	getContentPane().add(menuBar, BorderLayout.NORTH);

	btnNew = new JButton("");
	btnNew.setIconTextGap(5);
	btnNew.setToolTipText("New File");
	btnNew.setPreferredSize(new Dimension(60, 40));
	btnNew.setMinimumSize(new Dimension(40, 40));
	btnNew.setIcon(new ImageIcon(TextEditor.class.getResource("/ico/File New.png")));
	menuBar.add(btnNew);

	btnSave = new JButton("");
	btnSave.setIconTextGap(5);
	btnSave.setToolTipText("Save File");
	btnSave.setPreferredSize(new Dimension(60, 40));
	btnSave.setMinimumSize(new Dimension(40, 40));
	btnSave.setIcon(new ImageIcon(TextEditor.class.getResource("/ico/Save.png")));
	menuBar.add(btnSave);

	btnOpen = new JButton("");
	btnOpen.setPreferredSize(new Dimension(60, 40));
	btnOpen.setIcon(new ImageIcon(TextEditor.class.getResource("/ico/Folder Open.png")));
	btnOpen.setToolTipText("Open File");
	menuBar.add(btnOpen);

	btnCut = new JButton("");
	btnCut.setIcon(new ImageIcon(TextEditor.class.getResource("/ico/cut.png")));
	btnCut.setToolTipText("Cut");
	btnCut.setPreferredSize(new Dimension(60, 40));
	menuBar.add(btnCut);

	btnCopy = new JButton("");
	btnCopy.setIcon(new ImageIcon(TextEditor.class.getResource("/ico/File Copy.png")));
	btnCopy.setToolTipText("Copy");
	btnCopy.setPreferredSize(new Dimension(60, 40));
	menuBar.add(btnCopy);

	btnPast = new JButton("");
	btnPast.setIcon(new ImageIcon(TextEditor.class.getResource("/ico/Paste.png")));
	btnPast.setToolTipText("Past");
	btnPast.setPreferredSize(new Dimension(60, 40));
	menuBar.add(btnPast);

	btnUndo = new JButton("");
	btnUndo.setIcon(new ImageIcon(TextEditor.class.getResource("/ico/Undo.png")));
	btnUndo.setToolTipText("Undo");
	btnUndo.setPreferredSize(new Dimension(60, 40));
	menuBar.add(btnUndo);

	btnRedo = new JButton("");
	btnRedo.setIcon(new ImageIcon(TextEditor.class.getResource("/ico/Redo.png")));
	btnRedo.setPreferredSize(new Dimension(60, 40));
	btnRedo.setToolTipText("Redo");
	menuBar.add(btnRedo);

	comboBoxFont = new JComboBox();
	menuBar.add(comboBoxFont);

	comboBoxColor = new JComboBox();
	menuBar.add(comboBoxColor);

	menuBar.add(sizeJSlider);

	// font
	DefaultComboBoxModel<String> fontListModel = new DefaultComboBoxModel<String>();
	GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	for (String fontName : genv.getAvailableFontFamilyNames()) {
	    fontListModel.addElement(fontName);
	}
	comboBoxFont.setModel(fontListModel);
	comboBoxFont.addActionListener(this);
	comboBoxFont.addPopupMenuListener(new PopupMenuListener() {

	    @Override
	    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		if (oldFont == newFont) {
		    txtArea.setFont(newFont);
		} else {
		    txtArea.setFont(oldFont);
		}
	    }

	    @Override
	    public void popupMenuCanceled(PopupMenuEvent e) {
		// TODO Auto-generated method stub

	    }
	});

	// color
	DefaultComboBoxModel<String> colorListModel = new DefaultComboBoxModel<String>();
	colorListModel.addElement("BLACK");
	colorListModel.addElement("RED");
	colorListModel.addElement("GREEN");
	colorListModel.addElement("BLUE");
	colorListModel.addElement("Color Picker");
	comboBoxColor.setModel(colorListModel);
	comboBoxColor.addActionListener(this);
	comboBoxColor.addPopupMenuListener(new PopupMenuListener() {

	    @Override
	    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub

		if (oldColor == newColor) {
		    txtArea.setForeground(newColor);
		} else {
		    txtArea.setForeground(oldColor);
		}
	    }

	    @Override
	    public void popupMenuCanceled(PopupMenuEvent e) {
		// TODO Auto-generated method stub

	    }
	});
	comboBoxFont.setRenderer(new ListCellRenderer<String>() {

	    @Override
	    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		JLabel l = new JLabel(value);
		l.setOpaque(true);

		if (isSelected) {
		    newFont = new Font(value, oldFont.getStyle(), oldFont.getSize());
		    txtArea.setFont(newFont);
		    l.setBackground(Color.gray);
		} else {
		    l.setBackground(Color.WHITE);
		}

		return l;
	    }

	});

	comboBoxColor.setRenderer(new ListCellRenderer<String>() {

	    @Override
	    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {

		JLabel l = new JLabel(value);
		l.setOpaque(true);
		Color selectedColor = null;

		if (isSelected) {
		    if ("RED" == value) {
			selectedColor = Color.RED;
		    } else if ("GREEN" == value) {
			selectedColor = Color.GREEN;
		    } else if ("BLUE" == value) {
			selectedColor = Color.BLUE;
		    } else if ("BLACK" == value) {
			selectedColor = Color.BLACK;
		    }
		    l.setBackground(selectedColor);
		    l.setForeground(Color.WHITE);

		    txtArea.setForeground(selectedColor);
		    newColor = selectedColor;

		} else {
		    l.setBackground(Color.WHITE);
		}
		return l;

	    }
	});

	// font size

	sizeJSlider.setMaximum(60);
	sizeJSlider.setMinimum(10);
	sizeJSlider.setMajorTickSpacing(10);
	sizeJSlider.setMinorTickSpacing(5);
	sizeJSlider.setPaintTicks(true);
	sizeJSlider.setPaintLabels(true);
	sizeJSlider.setPaintTrack(true);
	sizeJSlider.setValue(oldFont.getSize());

	sizeJSlider.addChangeListener(this);

	btnOpen.addActionListener(this);
	btnCopy.addActionListener(this);
	btnCut.addActionListener(this);
	btnNew.addActionListener(this);
	btnSave.addActionListener(this);
	btnPast.addActionListener(this);
	btnRedo.addActionListener(this);
	btnUndo.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub

	if (e.getSource() == comboBoxColor) {
	    String color = comboBoxColor.getSelectedItem().toString();
	    Color selectedColor = Color.black;
	    if ("RED" == color) {
		selectedColor = Color.RED;
	    } else if ("GREEN" == color) {
		selectedColor = Color.GREEN;
	    } else if ("BLUE" == color) {
		selectedColor = Color.BLUE;
	    } else if ("BLACK" == color) {
		selectedColor = Color.BLACK;
	    } else if ("Color Picker".equals(color)) {

		selectedColor = JColorChooser.showDialog(TextEditor.this, "Choose your Color", oldColor);
	    }
	    txtArea.setForeground(selectedColor);

	    oldColor = selectedColor;

	} else if (e.getSource() == comboBoxFont) {

	    oldFont = new Font(comboBoxFont.getSelectedItem().toString(), oldFont.getStyle(), sizeJSlider.getValue());

	    txtArea.setFont(oldFont);
	}

	if (e.getSource() == menuItemNew || e.getSource() == popupMenuItemNew || e.getSource() == btnNew) {
	    if (JOptionPane.showConfirmDialog(this, "Are you sure to open new Text Editor ?") == JOptionPane.OK_OPTION) {
		txtArea.setText("");
	    }

	} else if (e.getSource() == menuItemOpen || e.getSource() == popupMenuItemOpen || e.getSource() == btnOpen) {

	    final JDialog d = new JDialog(this, true);
	    d.setSize(300, 75);
	    d.getContentPane().setLayout(new FlowLayout());

	    final JComboBox<String> comboBox = new JComboBox<String>();
	    comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "FileInputStream", "FileReader", "DataInputStream" }));

	    JButton btnOk = new JButton("Ok");
	    btnOk.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO Auto-generated method stub
		    if ("FileInputStream".equals(comboBox.getSelectedItem())) {

			readUsingFileInputStream();

		    } else if ("FileReader".equals(comboBox.getSelectedItem())) {

			readUsingFileReader();

		    } else if ("DataInputStream".equals(comboBox.getSelectedItem())) {

			readUsingDataInputStream();

		    }
		    d.dispose();
		}
	    });
	    d.getContentPane().add(comboBox);
	    d.getContentPane().add(btnOk);
	    d.setVisible(true);

	} else if (e.getSource() == menuItemSave || e.getSource() == popupMenuItemSave || e.getSource() == btnSave) {

	    final JDialog d = new JDialog(this, true);
	    d.setSize(300, 100);
	    d.getContentPane().setLayout(new FlowLayout());
	    final JComboBox<String> comboBox = new JComboBox<String>();
	    comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "FileOutputStream", "FileWriter", "DataOutputStream" }));

	    JButton btnOk = new JButton("Ok");
	    btnOk.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO Auto-generated method stub
		    if ("FileOutputStream".equals(comboBox.getSelectedItem())) {
			writeUsingFileOutputStream();
		    } else if ("FileWriter".equals(comboBox.getSelectedItem())) {
			writeUsingFileWriter();
		    } else if ("DataOutputStream".equals(comboBox.getSelectedItem())) {
			writeUsingDataOutputStream();
		    }
		    d.dispose();
		}
	    });
	    d.getContentPane().add(comboBox);
	    d.getContentPane().add(btnOk);
	    d.setVisible(true);

	} else if (e.getSource() == menuItemExit || e.getSource() == popupMenuItemExit) {

	    if (JOptionPane.showConfirmDialog(this, "Are you sure to exit ?") == JOptionPane.OK_OPTION)
		dispose();

	} else if (e.getSource() == menuItemCopy || e.getSource() == popupMenuItemCopy || e.getSource() == btnCopy) {

	    txtArea.copy();

	} else if (e.getSource() == menuItemPast || e.getSource() == popupMenuItemPast || e.getSource() == btnPast) {

	    txtArea.paste();

	} else if (e.getSource() == menuItemCut || e.getSource() == popupMenuItemCut || e.getSource() == btnCut) {

	    txtArea.copy();
	    int start = txtArea.getSelectionStart();
	    int end = txtArea.getSelectionEnd();
	    String temp = txtArea.getText();

	    String strStart = "";
	    String strEnd = "";

	    strStart = temp.substring(0, start);
	    strEnd = temp.substring(end);

	    temp = strStart + strEnd;

	    txtArea.setText(temp);

	} else if (e.getSource() == menuItemSelectAll || e.getSource() == popupMenuItemSelectAll) {

	    txtArea.selectAll();

	} else if (e.getSource() == menuItemAbout || e.getSource() == popupMenuItemAbout) {

	    new AboutDialog(this);

	} else if (e.getSource() == menuItemUndo || e.getSource() == popupMenuItemUndo || e.getSource() == btnUndo) {

	    if (0 <= index && index <= historyVector.size()) {
		index = index == 0 ? 0 : index - 1;
		txtArea.setText(historyVector.get(index));
	    }
	    System.out.println(index + " - " + historyVector.get(index));

	} else if (e.getSource() == menuItemRedo || e.getSource() == popupMenuItemRedo || e.getSource() == btnRedo) {

	    if (0 <= index && index <= historyVector.size()) {
		index = index + 1 == historyVector.size() ? index : index + 1;

		txtArea.setText(historyVector.get(index));
	    }

	    System.out.println(index + " - " + historyVector.get(index));
	} else if (e.getSource() == menuItemFormat || e.getSource() == popupMenuItemFormat) {

	    FontDialog fontDialog = new FontDialog(this, txtArea.getFont(), txtArea.getForeground());
	    fontDialog.setVisible(true);

	} else if (e.getSource() instanceof JMenuItem) {

	    JMenuItem item = (JMenuItem) e.getSource();

	    try {
		if (item != null && item.getToolTipText() != null) {
		    UIManager.setLookAndFeel(item.getToolTipText());
		    SwingUtilities.updateComponentTreeUI(this);
		}
	    } catch (Exception e1) {

		e1.printStackTrace();
	    }
	}

    }

    private void readUsingFileInputStream() {

	if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(TextEditor.this)) {

	    File file = fileChooser.getSelectedFile();

	    try {

		FileInputStream fos = new FileInputStream(file);
		StringBuffer strBuffer = new StringBuffer();
		txtArea.setText("");
		int b;
		while ((b = fos.read()) != -1) {
		    strBuffer.append(Character.toString((char) b));
		}
		fos.close();
		txtArea.setText(strBuffer.toString());
	    } catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    }
	}

    }

    private void readUsingFileReader() {

	if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(TextEditor.this)) {

	    File file = fileChooser.getSelectedFile();

	    try {

		FileReader fis = new FileReader(file);
		StringBuffer strBuffer = new StringBuffer();
		txtArea.setText("");
		int b;
		while ((b = fis.read()) != -1) {
		    strBuffer.append(Character.toString((char) b));
		}
		fis.close();
		txtArea.setText(strBuffer.toString());
	    } catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    }
	}

    }

    private void readUsingDataInputStream() {

	if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(TextEditor.this)) {

	    File file = fileChooser.getSelectedFile();

	    try {

		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		StringBuffer strBuffer = new StringBuffer();
		txtArea.setText("");

		strBuffer.append(dis.readUTF());

		fis.close();
		dis.close();
		txtArea.setText(strBuffer.toString());
	    } catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    }
	}

    }

    private void writeUsingFileOutputStream() {
	if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(TextEditor.this)) {
	    File file = fileChooser.getSelectedFile();

	    try {
		FileOutputStream fos = new FileOutputStream(file);

		fos.write(txtArea.getText().getBytes());

		fos.flush();
		fos.close();
	    } catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    }
	}

    }

    private void writeUsingFileWriter() {

	if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(TextEditor.this)) {
	    File file = fileChooser.getSelectedFile();

	    try {
		FileWriter fos = new FileWriter(file);

		fos.write(txtArea.getText());

		fos.flush();
		fos.close();
	    } catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    }
	}

    }

    private void writeUsingDataOutputStream() {

	if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(TextEditor.this)) {
	    File file = fileChooser.getSelectedFile();

	    try {
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);

		dos.writeUTF(txtArea.getText());

		dos.flush();
		dos.close();
		fos.close();
	    } catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		JOptionPane.showMessageDialog(TextEditor.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		e1.printStackTrace();
	    }
	}

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
	if ((!e.isControlDown()) || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V)) {
	    updateHistoryVectory();
	}
    }

    private void updateHistoryVectory() {
	historyVector.add(txtArea.getText());
	index++;

	/*
	 * if(!historyVector.isEmpty()) for(int i = index+1 ; i <
	 * historyVector.size() ; i++) { historyVector.remove(i); }
	 */
    }

    class FontDialog extends JDialog implements ActionListener, ChangeListener, ListSelectionListener {

	private JList<String> jlistFont;
	private JComboBox<String> comboBoxColor;
	private JSlider sizeJSlider;
	private JPanel toolPanel;
	private JPanel samplePanel;
	private JLabel sampleLabel;
	private JPanel buttonPanel;
	private JButton applyButton;
	private JButton cancelButton;

	private Font font;
	private Color color;

	public FontDialog(JFrame f, Font font, Color color) {

	    super(f, true);
	    setTitle("Font Chooser");
	    setSize(500, 300);
	    setLayout(new BorderLayout());

	    this.font = font;
	    this.color = color;

	    init();
	    dynInit();

	    comboBoxFont.setAutoscrolls(true);

	    sampleLabel.setForeground(color);
	    sampleLabel.setFont(font);
	}

	public void init() {
	    jlistFont = new JList<String>();
	    comboBoxColor = new JComboBox<String>();
	    sizeJSlider = new JSlider(JSlider.VERTICAL);
	    toolPanel = new JPanel();

	    applyButton = new JButton("Apply");
	    cancelButton = new JButton("cancel");

	    samplePanel = new JPanel();
	    sampleLabel = new JLabel("Sample");
	    buttonPanel = new JPanel();

	    // font = sampleLabel.getFont();
	    add(toolPanel, BorderLayout.CENTER);
	    add(samplePanel, BorderLayout.SOUTH);

	}

	public void dynInit() {

	    // font
	    DefaultListModel<String> fontListModel = new DefaultListModel<String>();
	    GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    for (String fontName : genv.getAvailableFontFamilyNames()) {
		fontListModel.addElement(fontName);
	    }
	    jlistFont.setModel(fontListModel);
	    jlistFont.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    jlistFont.addListSelectionListener(this);

	    jlistFont.ensureIndexIsVisible(fontListModel.indexOf(font.getName()));
	    jlistFont.setSelectedIndex(fontListModel.indexOf(font.getName()));

	    // color
	    DefaultComboBoxModel<String> colorListModel = new DefaultComboBoxModel<String>();
	    colorListModel.addElement("BLACK");
	    colorListModel.addElement("RED");
	    colorListModel.addElement("GREEN");
	    colorListModel.addElement("BLUE");
	    comboBoxColor.setModel(colorListModel);
	    comboBoxColor.addActionListener(this);
	    comboBoxColor.setRenderer(new ListCellRenderer<String>() {

		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {

		    JLabel l = new JLabel(value);
		    l.setOpaque(true);
		    Color selectedColor = null;

		    if (isSelected) {
			if ("RED" == value) {
			    selectedColor = Color.RED;
			} else if ("GREEN" == value) {
			    selectedColor = Color.GREEN;
			} else if ("BLUE" == value) {
			    selectedColor = Color.BLUE;
			} else if ("BLACK" == value) {
			    selectedColor = Color.BLACK;
			}
			l.setBackground(selectedColor);
			l.setForeground(Color.WHITE);

		    } else {
			l.setBackground(Color.WHITE);
		    }
		    return l;

		}
	    });

	    String selectedColor = "";
	    if (Color.RED == color) {
		selectedColor = "RED";
	    } else if (Color.GREEN == color) {
		selectedColor = "GREEN";
	    } else if (Color.BLUE == color) {
		selectedColor = "BLUE";
	    } else if (Color.BLACK == color) {
		selectedColor = "BLACK";
	    }

	    comboBoxColor.setSelectedItem(selectedColor);
	    // font size

	    sizeJSlider.setMaximum(60);
	    sizeJSlider.setMinimum(10);
	    sizeJSlider.setMajorTickSpacing(10);
	    sizeJSlider.setMinorTickSpacing(5);
	    sizeJSlider.setPaintTicks(true);
	    sizeJSlider.setPaintLabels(true);
	    sizeJSlider.setPaintTrack(true);
	    sizeJSlider.setValue(font.getSize());

	    sizeJSlider.addChangeListener(this);

	    // button
	    applyButton.addActionListener(this);
	    cancelButton.addActionListener(this);

	    // adding to panel
	    samplePanel.setLayout(new BorderLayout());

	    buttonPanel.add(applyButton);
	    buttonPanel.add(cancelButton);

	    samplePanel.add(sampleLabel, BorderLayout.WEST);
	    samplePanel.add(buttonPanel, BorderLayout.EAST);

	    toolPanel.setLayout(new FlowLayout());
	    JScrollPane fontScrollPane = new JScrollPane();
	    fontScrollPane.setSize(50, 100);
	    fontScrollPane.setViewportView(comboBoxFont);
	    toolPanel.add(fontScrollPane);
	    toolPanel.add(comboBoxColor);
	    toolPanel.add(sizeJSlider);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    // TODO Auto-generated method stub

	    if (e.getSource() == applyButton) {

		txtArea.setFont(font);
		txtArea.setForeground(color);
		dispose();

	    } else if (e.getSource() == cancelButton) {
		dispose();
	    } else if (e.getSource() == comboBoxColor) {
		String selectedColor = comboBoxColor.getSelectedItem().toString();
		if ("RED".equals(selectedColor)) {
		    sampleLabel.setForeground(Color.RED);
		    color = Color.RED;
		} else if ("GREEN".equals(selectedColor)) {
		    sampleLabel.setForeground(Color.GREEN);
		    color = Color.GREEN;
		} else if ("BLUE".equals(selectedColor)) {
		    sampleLabel.setForeground(Color.BLUE);
		    color = Color.BLUE;
		} else if ("BLACK".equals(selectedColor)) {
		    sampleLabel.setForeground(Color.BLACK);
		    color = Color.BLACK;
		}

	    }

	}

	@Override
	public void stateChanged(ChangeEvent e) {
	    // TODO Auto-generated method stub
	    int fontSize = 0;

	    fontSize = ((JSlider) e.getSource()).getValue();

	    font = new Font(font.getName(), font.getStyle(), fontSize);
	    sampleLabel.setFont(font);

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
	    // TODO Auto-generated method stub
	    String fontName = ((JList<String>) e.getSource()).getSelectedValue().toString();

	    font = new Font(fontName, font.getStyle(), font.getSize());
	    sampleLabel.setFont(font);

	}

	public void setFont(Font font) {
	    this.font = font;
	}

	public Font getFont() {
	    return font;
	}

	public void setColor(Color color) {
	    sampleLabel.setForeground(color);
	}

	public Color getColor() {
	    return sampleLabel.getForeground();
	}

    }

    class AboutDialog extends JDialog {

	private JLabel dialogText;
	private JLabel dialogImg;

	public AboutDialog(TextEditor t) {
	    // TODO Auto-generated constructor stub
	    super(t);

	    dialogImg = new JLabel(new ImageIcon("Info.png"));
	    dialogText = new JLabel("<html><b>Text Editor version 0.1</b><br><br>Open Source Software<br>" + "Developed by : Mohamed Refaat<br />" + "Email        : Eltntawy@gmail.com</html>");
	    setTitle("About");

	    setLayout(new FlowLayout());

	    add(dialogImg);
	    add(dialogText);

	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	    setLocation(100, 100);
	    setSize(new Dimension(500, 125));
	    setResizable(false);
	    setVisible(true);
	}

    }

    class myFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
	    // TODO Auto-generated method stub

	    if (f.getName().endsWith(".txt") || f.getName().endsWith(".java") || f.isDirectory()) {
		return true;
	    }
	    return false;
	}

	@Override
	public String getDescription() {
	    // TODO Auto-generated method stub
	    return "Text files (*.txt, *.java)";
	}

    }

    private static void addPopup(Component component, final JPopupMenu popup) {
	component.addMouseListener(new MouseAdapter() {
	    public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
		    showMenu(e);
		}
	    }

	    public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
		    showMenu(e);
		}
	    }

	    private void showMenu(MouseEvent e) {
		popup.show(e.getComponent(), e.getX(), e.getY());
	    }
	});
    }

    public static void main(String[] args) {

	final TextEditor textEditor = new TextEditor();

	try {
	    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		// TODO Auto-generated method stub
		SwingUtilities.updateComponentTreeUI(textEditor);
	    }
	});

    }

    @Override
    public void stateChanged(ChangeEvent e) {
	// TODO Auto-generated method stub
	int fontSize = 0;

	fontSize = ((JSlider) e.getSource()).getValue();
	oldFont = new Font(newFont.getName(), newFont.getStyle(), fontSize);
	newFont = new Font(newFont.getName(), newFont.getStyle(), fontSize);
	txtArea.setFont(newFont);
    }
}
