package org.bcjj.huntfiles.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcjj.huntfiles.FileInfo;
import org.bcjj.huntfiles.Hit;
import org.bcjj.huntfiles.HuntFiles;
import org.bcjj.huntfiles.HuntFilesListener;
import org.bcjj.huntfiles.SearchOptions;


import jdk.jfr.events.FileWriteEvent;
import net.iharder.dnd.FileDrop;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;

public class HuntFilesMainWindow implements HuntFilesListener {

	int MAX_COMBO_PREFERENCES=15;
	
	private enum FieldType {
		ComboDirectory,ComboText,ComboFileName,ComboAfter,ComboFindText; //no cambiar estos nombres
	}
	
	private JFrame frmHuntfiles;
	private JTextField textBefore;
	private JTextField textGreaterThan;
	private JTextField textLessThan;
	private JTextField txtDuplicateEnding;
	private JComboBox<String> comboDirectory;
	private JComboBox<String> comboText;
	private JComboBox<String> comboFileName;
	private JCheckBox checkBoxAfter;
	private JCheckBox checkboxBefore;
	private JCheckBox chckbxZipjar;
	private JCheckBox chckbxRar;
	private JCheckBox chckbxz;
	private JLabel labelLessThanKb;
	private JLabel labelGreaterThan;
	private JButton buttonStart;
	private JButton buttonStop;
	private JButton buttonToZip;
	private JButton buttonOpenInExplorer;
	private JButton btnDuplicate;
	private JLabel labelDirectory;
	private JLabel labelText;
	private JLabel labelFileName;
	private DefaultComboBoxModel<String> textModel;
	private DefaultComboBoxModel<String> fileNameModel;
	private DefaultComboBoxModel<String> afterModel;
	private DefaultComboBoxModel<String> findTextModel;
	private DefaultComboBoxModel<String> directoryModel;
	private static final String NL="\r\n";
	
	private JComboBox<String> comboAfter;
	private JComboBox<String> comboFindTextPreview;
	private JLabel labelTextInFile;
	private JLabel labelStatus;
	private JPanel panelPreview;
	private JButton botonCompare;
	private JButton botonUp;
	private JButton botonDown;
	private JButton botonOpcionCompare;
	private JButton buttonOpen;
	private JScrollPane scrollPaneFileList;
	private JScrollPane scrollPaneErrores;
	private JTable tableFiles;
	private FilesTableModel filesTableModel;
	int filaAnterior=-1;
	private JTextPane textPaneError;
	private JButton buttonCopy;
	private SimpleImagePanel panelImg;
	private JScrollPane scrollPane;
	private JTextArea textAreaFileText;
	private JButton botonExclusions;
	private JCheckBox chechBoxSubdir;
	
	private boolean inProgress=false;
	private JCheckBox chckbxCopyHits;
	private JScrollPane scrollPane_1;
	private JTextArea textAreaHits;

	HuntFiles huntFiles=null;
	private JButton buttonCopyTxt;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HuntFilesMainWindow window = new HuntFilesMainWindow();
					window.frmHuntfiles.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HuntFilesMainWindow() {
		initialize();
	}

	public HuntFilesMainWindow(String [] s) {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmHuntfiles = new JFrame();
		frmHuntfiles.setTitle("HuntFiles 1.0");
		frmHuntfiles.setBounds(100, 100, 637, 446);
		frmHuntfiles.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initFileDrop();
		
		JPanel panelCriteria = new JPanel();
		panelCriteria.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelCriteria.setPreferredSize(new Dimension(60, 60));
		frmHuntfiles.getContentPane().add(panelCriteria, BorderLayout.NORTH);
		panelCriteria.setSize(new Dimension(60, 60));
		panelCriteria.setLayout(new BorderLayout(0, 0));
		
		JPanel panelCriteriaLabel = new JPanel();
		panelCriteriaLabel.setPreferredSize(new Dimension(90, 20));
		panelCriteria.add(panelCriteriaLabel, BorderLayout.WEST);
		panelCriteriaLabel.setLayout(null);
		
		labelFileName = new JLabel("File name:");
		labelFileName.setFont(new Font("Tahoma", Font.PLAIN, 10));
		labelFileName.setToolTipText("nombre fichero");
		labelFileName.setBounds(5, 0, 64, 14);
		panelCriteriaLabel.add(labelFileName);
		
		labelText = new JLabel("Text:");
		labelText.setFont(new Font("Tahoma", Font.PLAIN, 10));
		labelText.setToolTipText("texto");
		labelText.setBounds(5, 22, 46, 14);
		panelCriteriaLabel.add(labelText);
		
		labelDirectory = new JLabel("Directory:");
		labelDirectory.setFont(new Font("Tahoma", Font.PLAIN, 10));
		labelDirectory.setToolTipText("directorio");
		labelDirectory.setBounds(5, 40, 50, 14);
		panelCriteriaLabel.add(labelDirectory);
		
		botonExclusions = new JButton("!");
		botonExclusions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirPanelExclusiones();
			}
		});
		botonExclusions.setMargin(new Insets(2, 2, 2, 2));
		botonExclusions.setToolTipText("exclusiones");
		botonExclusions.setBounds(70, 40, 15, 15);
		panelCriteriaLabel.add(botonExclusions);
		
		chechBoxSubdir = new JCheckBox("");
		chechBoxSubdir.setSelected(true);
		chechBoxSubdir.setToolTipText("subdir");
		chechBoxSubdir.setBounds(50, 40, 20, 15);
		panelCriteriaLabel.add(chechBoxSubdir);
		
		JPanel panelCriteriaParameters = new JPanel();
		panelCriteria.add(panelCriteriaParameters, BorderLayout.CENTER);
		panelCriteriaParameters.setLayout(new BorderLayout(0, 0));
		
		comboDirectory = new JComboBox<String>();
		directoryModel = createDirectoryModel();
		comboDirectory.setModel(directoryModel);
		comboDirectory.setEditable(true);
		comboDirectory.setPreferredSize(new Dimension(29, 20));
		comboDirectory.setMinimumSize(new Dimension(29, 20));
		panelCriteriaParameters.add(comboDirectory, BorderLayout.SOUTH);
		
		comboText = new JComboBox<String>();
		textModel = createTextModel();
		comboText.setModel(textModel);
		comboText.setEditable(true);
		panelCriteriaParameters.add(comboText, BorderLayout.CENTER);
		
		comboFileName = new JComboBox<String>();
		fileNameModel = createFileNameModel();
		comboFileName.setModel(fileNameModel);
		comboFileName.setEditable(true);
		comboFileName.setMinimumSize(new Dimension(29, 20));
		comboFileName.setPreferredSize(new Dimension(29, 20));
		panelCriteriaParameters.add(comboFileName, BorderLayout.NORTH);
		
		JPanel panelCriteriaParams2 = new JPanel();
		panelCriteriaParams2.setPreferredSize(new Dimension(300, 10));
		panelCriteria.add(panelCriteriaParams2, BorderLayout.EAST);
		panelCriteriaParams2.setLayout(null);
		
		checkBoxAfter = new JCheckBox("after");
		checkBoxAfter.setFont(new Font("Tahoma", Font.PLAIN, 9));
		checkBoxAfter.setToolTipText("modificado antes de");
		checkBoxAfter.setBounds(0, 0, 49, 14);
		panelCriteriaParams2.add(checkBoxAfter);
		
		checkboxBefore = new JCheckBox("before");
		checkboxBefore.setFont(new Font("Tahoma", Font.PLAIN, 9));
		checkboxBefore.setToolTipText("modificado despues de");
		checkboxBefore.setBounds(0, 17, 57, 23);
		panelCriteriaParams2.add(checkboxBefore);
		
		textBefore = new JTextField();
		textBefore.setFont(new Font("Tahoma", Font.PLAIN, 10));
		textBefore.setToolTipText("formato:  yyyy/MM/dd HH:mm:ss");
		textBefore.setBounds(55, 18, 119, 20);
		panelCriteriaParams2.add(textBefore);
		textBefore.setColumns(10);
		
		chckbxZipjar = new JCheckBox("zip,jar");
		chckbxZipjar.setFont(new Font("Tahoma", Font.PLAIN, 9));
		chckbxZipjar.setBounds(0, 37, 52, 23);
		panelCriteriaParams2.add(chckbxZipjar);
		
		chckbxRar = new JCheckBox("rar");
		chckbxRar.setFont(new Font("Tahoma", Font.PLAIN, 9));
		chckbxRar.setBounds(52, 37, 39, 23);
		panelCriteriaParams2.add(chckbxRar);
		
		chckbxz = new JCheckBox("7z");
		chckbxz.setFont(new Font("Tahoma", Font.PLAIN, 9));
		chckbxz.setBounds(89, 37, 37, 23);
		panelCriteriaParams2.add(chckbxz);
		
		labelLessThanKb = new JLabel("< (k)");
		labelLessThanKb.setFont(new Font("Tahoma", Font.PLAIN, 9));
		labelLessThanKb.setToolTipText("tama\u00F1o menor de ([B Bytes,K kiloBytes,M megaBytes ,G gigaBytes] defecto K)");
		labelLessThanKb.setBounds(200, 0, 30, 14);
		panelCriteriaParams2.add(labelLessThanKb);
		
		labelGreaterThan = new JLabel("> (k)");
		labelGreaterThan.setFont(new Font("Tahoma", Font.PLAIN, 9));
		labelGreaterThan.setToolTipText("tama\u00F1o mayor de  ([B Bytes,K kiloBytes,M megaBytes ,G gigaBytes] defecto K)");
		labelGreaterThan.setBounds(200, 21, 30, 14);
		panelCriteriaParams2.add(labelGreaterThan);
		
		textGreaterThan = new JTextField();
		textGreaterThan.setToolTipText("size greatter than ([B Bytes,K kiloBytes,M megaBytes ,G gigaBytes] default K)");
		textGreaterThan.setBounds(235, 18, 65, 18);
		panelCriteriaParams2.add(textGreaterThan);
		textGreaterThan.setColumns(10);
		
		textLessThan = new JTextField();
		textLessThan.setToolTipText("size less than ([B Bytes,K kiloBytes,M megaBytes ,G gigaBytes] default K)");
		textLessThan.setBounds(235, 0, 65, 18);
		panelCriteriaParams2.add(textLessThan);
		textLessThan.setColumns(10);
		
		buttonStart = new JButton("START");
		buttonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				starSearch();
			}
		});
		buttonStart.setToolTipText("INICIAR BUSQUEDA");
		buttonStart.setBounds(132, 39, 100, 19);
		panelCriteriaParams2.add(buttonStart);
		
		buttonStop = new JButton("Stop");
		buttonStop.setBounds(235, 39, 65, 18);
		panelCriteriaParams2.add(buttonStop);
		buttonStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopSearch();
			}
		});
		
		comboAfter = new JComboBox<String>();
		afterModel = createAfterModel();
		comboAfter.setModel(afterModel);
		comboAfter.setFont(new Font("Tahoma", Font.PLAIN, 10));
		comboAfter.setEditable(true);
		comboAfter.setToolTipText("format:  yyyy/MM/dd HH:mm:ss #coment");
		comboAfter.setBounds(55, 0, 135, 20);
		panelCriteriaParams2.add(comboAfter);

		
		JPanel panelResultContainer = new JPanel();
		frmHuntfiles.getContentPane().add(panelResultContainer, BorderLayout.CENTER);
		panelResultContainer.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPanelResultContainer = new JSplitPane();
		splitPanelResultContainer.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelResultContainer.add(splitPanelResultContainer);
		splitPanelResultContainer.setDividerLocation(170);
		
		JPanel panelResults = new JPanel();
		panelResults.setPreferredSize(new Dimension(100, 120));
		splitPanelResultContainer.setLeftComponent(panelResults);
		panelResults.setLayout(new BorderLayout(0, 0));
		
		JPanel panelFileList = new JPanel();
		panelFileList.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelResults.add(panelFileList, BorderLayout.CENTER);
		panelFileList.setLayout(new BorderLayout(0, 0));
		

		
		scrollPaneFileList = new JScrollPane();
		panelFileList.add(scrollPaneFileList);
		
		tableFiles = new JTable();
		scrollPaneFileList.setViewportView(tableFiles);
		filesTableModel = new FilesTableModel(tableFiles);
		tableFiles.setModel(filesTableModel);
		tableFiles.setAutoCreateRowSorter(true);
		tableFiles.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					//System.out.println("jtable.selecciontionListener "+e);
					registroTablaFileSeleccionado(e);
				}
			}
			
		});
		
		
		JPanel panelOptions = new JPanel();
		panelOptions.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelOptions.setSize(new Dimension(50, 0));
		panelOptions.setPreferredSize(new Dimension(85, 10));
		panelResults.add(panelOptions, BorderLayout.EAST);
		panelOptions.setLayout(null);
		
		buttonToZip = new JButton("to Zip");
		buttonToZip.setMargin(new Insets(2, 2, 2, 2));
		buttonToZip.setToolTipText("crear zip con los resultados seleccionados");
		buttonToZip.setBounds(5, 5, 75, 15);
		panelOptions.add(buttonToZip);
		buttonToZip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectToZip();
			}
		});
		
		buttonOpenInExplorer = new JButton("explorer");
		buttonOpenInExplorer.setMargin(new Insets(2, 2, 2, 2));
		buttonOpenInExplorer.setToolTipText("abrir en explorer");
		buttonOpenInExplorer.setBounds(5, 25, 75, 15);
		panelOptions.add(buttonOpenInExplorer);
		buttonOpenInExplorer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openInExplorer();
			}
		});
		
		btnDuplicate = new JButton("duplicate");
		btnDuplicate.setMargin(new Insets(2, 2, 2, 2));
		btnDuplicate.setToolTipText("crea duplicado");
		btnDuplicate.setBounds(5, 45, 75, 15);
		panelOptions.add(btnDuplicate);
		btnDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				duplicar();
			}
		});
		
		txtDuplicateEnding = new JTextField();
		txtDuplicateEnding.setToolTipText("terminacion de duplicado");
		txtDuplicateEnding.setFont(new Font("Tahoma", Font.PLAIN, 9));
		txtDuplicateEnding.setText("-ORIG");
		txtDuplicateEnding.setBounds(5, 60, 75, 15);
		panelOptions.add(txtDuplicateEnding);
		txtDuplicateEnding.setColumns(10);
		
		botonCompare = new JButton("compare");
		botonCompare.setToolTipText("elige 2 y comparalos");
		botonCompare.setMargin(new Insets(2, 2, 2, 2));
		botonCompare.setFont(new Font("Tahoma", Font.PLAIN, 11));
		botonCompare.setBounds(5, 80, 57, 15);
		panelOptions.add(botonCompare);
		botonCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comparar();
			}
		});
		
		botonOpcionCompare = new JButton("?");
		botonOpcionCompare.setToolTipText("settings / ajustes de comparacion");
		botonOpcionCompare.setFont(new Font("Tahoma", Font.PLAIN, 9));
		botonOpcionCompare.setMargin(new Insets(1, 1, 2, 1));
		botonOpcionCompare.setBounds(65, 80, 15, 15);
		panelOptions.add(botonOpcionCompare);
		botonOpcionCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compareOptions();
			}
		});
		
		buttonOpen = new JButton("open");
		buttonOpen.setMargin(new Insets(2, 2, 2, 2));
		buttonOpen.setToolTipText("abrir");
		buttonOpen.setBounds(5, 100, 75, 15);
		panelOptions.add(buttonOpen);
		
		buttonCopyTxt = new JButton("copy txt");
		buttonCopyTxt.setMargin(new Insets(2, 2, 2, 2));
		buttonCopyTxt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyFilesTxt();
			}
		});
		buttonCopyTxt.setBounds(5, 120, 57, 15);
		panelOptions.add(buttonCopyTxt);
		
		chckbxCopyHits = new JCheckBox("");
		chckbxCopyHits.setToolTipText("with hits");
		chckbxCopyHits.setBounds(60, 120, 20, 15);
		panelOptions.add(chckbxCopyHits);
		buttonOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		
		buttonCopy = new JButton("copy");
		buttonCopy.setMargin(new Insets(2, 2, 2, 2));
		buttonCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyFiles();
			}
		});
		buttonCopy.setBounds(5, 140, 75, 15);
		panelOptions.add(buttonCopy);		
		
		//JPanel panelFilePreview0 = new JPanel();
		//panelFilePreview0.setPreferredSize(new Dimension(100, 100));
		//panelFilePreview0.setLayout(new BorderLayout(0, 0));
		//splitPanelResultContainer.setRightComponent(panelFilePreview0);
		
		JSplitPane splitPanelResultContainer2 = new JSplitPane();
		splitPanelResultContainer2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPanelResultContainer2.setDividerLocation(50);		
		splitPanelResultContainer.setRightComponent(splitPanelResultContainer2);
		
		JPanel panelErrores = new JPanel();
		panelErrores.setBorder(new LineBorder(new Color(0, 0, 0)));
		//panelFilePreview.add(panelPreview, BorderLayout.CENTER);
		splitPanelResultContainer2.setLeftComponent(panelErrores);
		panelErrores.setLayout(new BorderLayout(0, 0));
		
		scrollPaneErrores = new JScrollPane();
		panelErrores.add(scrollPaneErrores, BorderLayout.CENTER);
		
		textPaneError = new JTextPane();
		textPaneError.setBackground(Color.LIGHT_GRAY);
		textPaneError.setFont(new Font("Tahoma", Font.PLAIN, 9));
		scrollPaneErrores.setViewportView(textPaneError);
		
		JSplitPane splitPanelResultContainer3 = new JSplitPane();
		splitPanelResultContainer3.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPanelResultContainer3.setDividerLocation(50);		
		splitPanelResultContainer2.setRightComponent(splitPanelResultContainer3);
		
		//panelImg = new JPanel();
		panelImg=new SimpleImagePanel();
		panelImg.setPreferredSize(new Dimension(100, 10));
		splitPanelResultContainer3.setLeftComponent(panelImg);
		
		JSplitPane splitPanelResultContainer4 = new JSplitPane();
		splitPanelResultContainer4.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPanelResultContainer4.setDividerLocation(50);		
		splitPanelResultContainer3.setRightComponent(splitPanelResultContainer4);
		
		JPanel panelFilePreviewHits = new JPanel();
		panelFilePreviewHits.setPreferredSize(new Dimension(100, 100));
		panelFilePreviewHits.setLayout(new BorderLayout(0, 0));
		splitPanelResultContainer4.setLeftComponent(panelFilePreviewHits);	
		
		scrollPane_1 = new JScrollPane();
		panelFilePreviewHits.add(scrollPane_1, BorderLayout.CENTER);
		
		textAreaHits = new JTextArea();
		scrollPane_1.setViewportView(textAreaHits);
		
		JPanel panelFilePreview = new JPanel();
		panelFilePreview.setPreferredSize(new Dimension(100, 100));
		panelFilePreview.setLayout(new BorderLayout(0, 0));
		splitPanelResultContainer4.setRightComponent(panelFilePreview);
		
		panelPreview = new JPanel();
		panelPreview.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelPreview.setSize(new Dimension(100, 0));
		panelPreview.setPreferredSize(new Dimension(100, 10));
		panelPreview.setMinimumSize(new Dimension(200, 10));
		panelFilePreview.add(panelPreview, BorderLayout.CENTER);
		panelPreview.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panelPreview.add(scrollPane, BorderLayout.CENTER);
		
		textAreaFileText = new JTextArea();
		scrollPane.setViewportView(textAreaFileText);
		
		
		JPanel panelPreviewOptions = new JPanel();
		panelPreviewOptions.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelPreviewOptions.setSize(new Dimension(100, 0));
		panelPreviewOptions.setPreferredSize(new Dimension(100, 10));
		panelPreviewOptions.setMinimumSize(new Dimension(200, 10));
		panelFilePreview.add(panelPreviewOptions, BorderLayout.EAST);
		panelPreviewOptions.setLayout(null);
		
		comboFindTextPreview = new JComboBox<String>();
		comboFindTextPreview.setEditable(true);
		findTextModel = createAfterModel();
		comboFindTextPreview.setModel(findTextModel);
		comboFindTextPreview.setFont(new Font("Tahoma", Font.PLAIN, 9));
		comboFindTextPreview.setBounds(0, 15, 100, 20);
		panelPreviewOptions.add(comboFindTextPreview);
		
		labelTextInFile = new JLabel("find:");
		labelTextInFile.setToolTipText("buscar texto en fichero");
		labelTextInFile.setBounds(3, 1, 46, 14);
		panelPreviewOptions.add(labelTextInFile);
		
		botonUp = new JButton("up");
		botonUp.setFont(new Font("Tahoma", Font.PLAIN, 9));
		botonUp.setMargin(new Insets(2, 2, 2, 2));
		botonUp.setBounds(10, 36, 23, 23);
		panelPreviewOptions.add(botonUp);
		botonUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findUp();
			}
		});
		
		botonDown = new JButton("down");
		botonDown.setMargin(new Insets(2, 2, 2, 2));
		botonDown.setFont(new Font("Tahoma", Font.PLAIN, 9));
		botonDown.setBounds(43, 36, 47, 23);
		panelPreviewOptions.add(botonDown);
		botonDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findDown();
			}
		});
		
		JPanel panelStatus = new JPanel();
		panelStatus.setPreferredSize(new Dimension(20, 20));
		frmHuntfiles.getContentPane().add(panelStatus, BorderLayout.SOUTH);
		panelStatus.setLayout(new BorderLayout(0, 0));
		
		labelStatus = new JLabel("HuntFiles");
		labelStatus.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelStatus.add(labelStatus, BorderLayout.CENTER);
		
		setEnabled(true);
	}

	protected void abrirPanelExclusiones() {
		askForString("TO DO. abrir panel de exclusiones de directorios", "");
	}

	protected void copyFilesTxt() {
		boolean withHits=chckbxCopyHits.isSelected();
		List<FileInfo> files=getSelectedFiles();
		StringBuilder sb=new StringBuilder();
		for (FileInfo fileInfo:files) {
			sb.append(fileInfo.toString(withHits,"  > "));
		}
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(sb.toString());
		clipboard.setContents(stringSelection, null);
		
	}

	protected void copyFiles() {
		List<FileInfo> files=getSelectedFiles();
		
        List<File> listOfFiles = new ArrayList();
        Set<String> fichs=new HashSet<String>();
        for (FileInfo fileInfo:files) {
        	if (!fichs.contains(fileInfo.getFile().getPath())) {
        		fichs.add(fileInfo.getFile().getPath());
        		listOfFiles.add(fileInfo.getFile());
        	}
        }

        FileTransferable ft = new FileTransferable(listOfFiles);

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, new ClipboardOwner() {
			@Override
			public void lostOwnership(Clipboard clipboard, Transferable contents) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	
	public static String askForString(String message,String value) {
		String reply = JOptionPane.showInputDialog(message,value);
		return reply;
	}	
	
	public static boolean askConfirm(String message) {
		int dialogResult = JOptionPane.showConfirmDialog (null, message,"HuntFiles",JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
		  return true;
		}
		return false;
	}
	
	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message,"HuntFiles",JOptionPane.OK_OPTION);
	}
	
	protected void findDown() {
		//comboFindTextPreview
		askForString("to do", "");
		String textValue=getComboValue(comboFindTextPreview, textModel,FieldType.ComboFindText);
		findTextInJText(textAreaFileText, textValue);
	}

	protected void findUp() {
		askForString("to do", "");
	}

	protected void openFile() {
		List<FileInfo> selected=getSelectedFiles();
		if (selected.size()>1 && !askConfirm("open "+selected.size()+" files")) {
			return;
		}
		for (FileInfo fileInfo:selected) {
			try {
				Desktop.getDesktop().open(fileInfo.getFile());
			} catch (IOException e) {
				appendErrMsg("error opening:"+fileInfo.getFile()+" :: "+e);
			}
		}
	}
	


	protected void appendErrMsg(String errMsg) {
		StringBuilder err=new StringBuilder();
		err.append(textPaneError.getText());
		err.append(errMsg).append(NL);
		textPaneError.setText(err.toString());
	}
	
	protected void compareOptions() {
		askForString("to do", "");
	}

	protected void comparar() {
		 getSelectedFiles() ;
		askForString("to do", "");
	}

	protected void duplicar() {
		List<FileInfo> files=getSelectedFiles();
		String txtDup=txtDuplicateEnding.getText();
		txtDup=StringUtils.replace(txtDup, "\\", "-");
		txtDup=StringUtils.replace(txtDup, "/", "-");
		txtDup=StringUtils.replace(txtDup, "|", "-");
		txtDup=StringUtils.replace(txtDup, "*", "-");
		txtDup=StringUtils.replace(txtDup, ":", "-");
		txtDup=StringUtils.replace(txtDup, "\"", "-");
		txtDup=StringUtils.replace(txtDup, "'", "-");
		txtDup=StringUtils.replace(txtDup, "<", "-");
		txtDup=StringUtils.replace(txtDup, ">", "-");
		txtDup=StringUtils.replace(txtDup, "?", "-");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date now=new Date();
		if (files.size()==1) {
			File f=files.get(0).getFile();
			File dest=new File(f.getPath()+"-"+txtDup+"-"+sdf.format(now));
			try {
				FileUtils.copyFile(f, dest);
			} catch (Exception e) {
				showMessage("ERROR "+e);
			}
		} else {
			showMessage("only 1 file please");
		}
	}

	protected void openInExplorer() {
		List<FileInfo> files=getSelectedFiles();
		
		if (files.size()==1) {
			File f=files.get(0).getFile();
			try {
				Desktop.getDesktop().open(f.getParentFile());
			} catch (Exception e) {
				showMessage("ERROR "+e);
			}
		} else {
			showMessage("only 1 file please");
		}
	}

	protected void selectToZip() {
			List<FileInfo> files=getSelectedFiles();
			
	        List<File> listOfFiles = new ArrayList();
	        Set<String> fichs=new HashSet<String>();
	        for (FileInfo fileInfo:files) {
	        	if (!fichs.contains(fileInfo.getFile().getPath())) {
	        		fichs.add(fileInfo.getFile().getPath());
	        		listOfFiles.add(fileInfo.getFile());
	        	}
	        }
	        
	        if (listOfFiles.size()==0) {
				showMessage("select 1 or more files please");
			}
	        
	        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
			Date now=new Date();
			
	        String zip=askForString("zip file name", "/temp/export-"+sdf.format(now)+".zip");
	        File zipFile=new File(zip);
	        try {
	        	zipFile.getParentFile().mkdirs();
	        } catch (Exception r) {
	        	//ignore
	        }
	        try {
	            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFile));
	            for (File file:listOfFiles) {
	            	File parent=file;
	            	while (parent.getParentFile()!=null) {
	            		parent=parent.getParentFile();
	            	}
	            	Path sourceDir = Paths.get(parent.getPath());
	            	Path sourceFile=Paths.get(file.getPath());
	                Path targetFile = sourceDir.relativize(sourceFile);
	                ZipEntry zipEntry=new ZipEntry(targetFile.toString());
	                zipEntry.setTime(file.lastModified());
	                outputStream.putNextEntry(zipEntry);
	                byte[] bytes = Files.readAllBytes(sourceFile);
	                outputStream.write(bytes, 0, bytes.length);
	                outputStream.closeEntry();
	            };
	            outputStream.close();
	        } catch (Exception e) {
	        	showMessage("ERROR making zip "+zip+" :: "+e);
	        }
	        
	}

	protected void stopSearch() {
		if (huntFiles!=null) {
			huntFiles.endSearch();
		}
	}

	
	protected List<FileInfo> getSelectedFiles() {
		List<FileInfo> selected=new ArrayList<FileInfo>();
		//askForString("sin hacer", "");
		int[] selectedIdx=tableFiles.getSelectedRows();
		FilesTableModel ftm=filesTableModel;
		for (int i=0;i<selectedIdx.length;i++) {
			int idx=selectedIdx[i];
			idx=tableFiles.convertRowIndexToModel(idx);
			FileInfo fileInfo=filesTableModel.getFileInfo(idx);
			//System.out.println(" i:"+i+"  "+fileInfo.getFile());
			selected.add(fileInfo);
		}
		return selected;
	}

	protected void registroTablaFileSeleccionado(ListSelectionEvent evento) {
		//System.out.println("click "+System.currentTimeMillis());
		//panel.setEnabled(true);
		int row=tableFiles.getSelectedRow();
		
		FileInfo fileInfo=null;
		if (row>-1) {
			row=tableFiles.convertRowIndexToModel(row);
			fileInfo=filesTableModel.getFileInfo(row);	
			System.out.println(" SEL "+fileInfo);
			preview(fileInfo);
		} else {
			System.out.println(" NO SEL ");
			preview(null);
		}
	}

	
	public void preview(FileInfo fileInfo) {
		if (fileInfo==null) {
			textAreaFileText.setText("");
			textAreaHits.setText("");
			try {
				panelImg.setImageInputStream(null);
			} catch (Exception e) {
				
			}
		} else {
			StringBuilder hits=new StringBuilder();
			if (fileInfo.getHits()!=null) {
				for (Hit hit:fileInfo.getHits()) {
					hits.append(hit).append(NL);
				}
			}
			textAreaHits.setText(hits.toString());
			
			String text="";
			InputStream is1=null;
			try {
				is1=fileInfo.getInputStream();
				text=getStringFromInputStream(is1);
				textAreaFileText.setText(text);
				//comboFindTextPreview
				String textValue=getComboValue(comboText, textModel,FieldType.ComboText);
				findTextInJText(textAreaFileText, textValue);
				
			} catch (Exception r) {
				try {
					if (is1!=null) {
						is1.close();
					}
				} catch (Exception r2) {
					textAreaFileText.setText("ERROR opening "+fileInfo+" :: "+r);
				}
			}
			
			InputStream is2=null;
			try {
				is2=fileInfo.getInputStream();
				panelImg.setImageInputStream(is2);
			} catch (Exception e) {
				System.out.println("Error panelImg "+e);
				try {
					if (is2!=null) {
						is2.close();
					}
				} catch (Exception r) {
					//ignore
				}
			}
		}
	}
	
	
	public void findTextInJText(JTextComponent jText,String lookFor) {
		//askForString("to do", "");
	}

	public String getStringFromInputStream(InputStream inputStream) throws IOException {
		//https://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
		//most efficient way 8: (Ways to convert an InputStream to a String)
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[8092];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		// StandardCharsets.UTF_8.name() > JDK 7
		return result.toString(); //"UTF-8");
	}
	
	
	private String getComboValue(JComboBox<String> combo,DefaultComboBoxModel<String> modelo, FieldType fieldType) {
		if (combo.getSelectedItem()==null) {
			return null;
		}
		String value= combo.getSelectedItem().toString();
		if (combo.getSelectedIndex()==-1) {
			modelo.insertElementAt(value,0);
		} else {
			modelo.removeElementAt(combo.getSelectedIndex());
			modelo.insertElementAt(value,0);
			modelo.setSelectedItem(value);
		}
		while (modelo.getSize()>MAX_COMBO_PREFERENCES) {
			modelo.removeElementAt(modelo.getSize()-1);
		}
		savePreference(modelo, fieldType);
		return value;
	}

	private void loadPreference(DefaultComboBoxModel<String> modelo, FieldType fieldType) {
		try (BufferedReader br = new BufferedReader(new FileReader(getPreferenceFile(fieldType)))) {
			String lin=null;
			while ((lin=br.readLine())!=null) {
				if (!lin.trim().equals("")) {
					modelo.addElement(lin);
				}
			}
		} catch (IOException e) {
			System.out.println("Error al leer las preferencias de "+fieldType.name());
		}
	}
	
	
	private void savePreference(DefaultComboBoxModel<String> modelo, FieldType fieldType) {
		try (FileWriter fw = new FileWriter(getPreferenceFile(fieldType))) {
			for (int i=0;i<modelo.getSize();i++) {
				String x=modelo.getElementAt(i);
				fw.write(x+NL);
			}
		} catch (IOException e) {
			System.out.println("Error al guardar las preferencias de "+fieldType.name());
		}
	}
	
	private File getPreferenceFile(FieldType fieldType) {
		String userHomeDir = System.getProperty("user.home");
		String prefDir=userHomeDir+"/.huntFiles";
		File d=new File(prefDir);
		d.mkdirs();
		String prefFile=prefDir+"/"+fieldType.name();
		File f=new File(prefFile);
		return f;
	}

	protected void starSearch() {
		String textValue=getComboValue(comboText, textModel,FieldType.ComboText);
		String dirValue=getComboValue(comboDirectory, directoryModel,FieldType.ComboDirectory);
		String filenameValue=getComboValue(comboFileName, fileNameModel,FieldType.ComboFileName);
		String afterValue=getComboValue(comboAfter, afterModel,FieldType.ComboAfter);
		if (afterValue.indexOf("#")>-1) {
			afterValue=afterValue.substring(0, afterValue.indexOf("#")).trim();
		}
		String beforeValue=textBefore.getText();
		if (beforeValue.indexOf("#")>-1) {
			beforeValue=beforeValue.substring(0, beforeValue.indexOf("#")).trim();
		}
		//String findInTextValue=getComboValue(comboFindTextPreview, findTextModel,FieldType.ComboFindText);
		
		String greaterThan=textGreaterThan.getText();
		String lessThan=textLessThan.getText();
		boolean zipjar=chckbxZipjar.isSelected();
		boolean z7=chckbxz.isSelected();
		boolean rar=chckbxRar.isSelected();
		boolean recursive=chechBoxSubdir.isSelected();
		
		SearchOptions searchOptions=new SearchOptions(dirValue);
		searchOptions.setText(textValue);
		
		if (checkBoxAfter.isSelected()) {
			try {
				searchOptions.setAfter(afterValue);
			} catch (Exception e) {
				showMessage("not valid: After");
				return;
			}
		}
		if (checkboxBefore.isSelected()) {
			try {
				searchOptions.setBefore(beforeValue);
			} catch (Exception e) {
				showMessage("not valid: Before");
				return;
			}
		}
		searchOptions.setFilename(filenameValue);
		searchOptions.setGreater(greaterThan);
		searchOptions.setLessThan(lessThan);
		searchOptions.setRar(rar);
		searchOptions.setRecursive(recursive);
		searchOptions.setZ7(z7);
		searchOptions.setZipjar(zipjar);
		searchOptions.setIgnorePaths(null);
		
		filesTableModel.clearFiles();
		filesTableModel.fireTableDataChanged();
		
		/*
		File dir=new File(dirValue);
		File [] fich=dir.listFiles();
		for (File f:fich) {
			if (f.isFile()) {
				filesTableModel.addFileInfo(new FileInfo(f,null,null));
			}
		}
		filesTableModel.fireTableDataChanged();

		*/
		
		setEnabled(false);
		
		huntFiles=new HuntFiles(searchOptions, this);
		
        Thread worker = new Thread() {
            
            public void run() {

	                try {
	                	inProgress=true;
	                	huntFiles.search();
	                } catch (Throwable ex) {
	                	System.out.println("Error worker.run "+ex);
	                	ex.printStackTrace(System.out);
	                	showMessage("ERROR "+ex);
	                }
	                inProgress=false;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setEnabled(true);
                    }
                });
            }
        };
         
        worker.start(); // So we don't hold up the dispatch thread.
		
		
		
	}

	private void setEnabled(boolean enable) {
		buttonStart.setEnabled(enable);
		buttonStop.setEnabled(!enable);
	}

	private DefaultComboBoxModel<String> createTextModel() {
		textModel= new DefaultComboBoxModel<String>();
		loadPreference(textModel, FieldType.ComboText);
		return textModel;
	}

	private DefaultComboBoxModel<String> createDirectoryModel() {
		directoryModel= new DefaultComboBoxModel<String>();
		loadPreference(directoryModel, FieldType.ComboDirectory);
		return directoryModel;
	}
	
	private DefaultComboBoxModel<String> createFileNameModel() {
		fileNameModel = new DefaultComboBoxModel<String>();
		loadPreference(fileNameModel, FieldType.ComboFileName);
		return fileNameModel;
	}
	
	private DefaultComboBoxModel<String> createAfterModel() {
		afterModel = new DefaultComboBoxModel<String>();
		loadPreference(afterModel, FieldType.ComboAfter);
		return afterModel;
	}
	
	private DefaultComboBoxModel<String> createFindTextModel() {
		findTextModel = new DefaultComboBoxModel<String>();
		loadPreference(findTextModel, FieldType.ComboFindText);
		return findTextModel;
	}
	
	public void initFileDrop() {
		FileDrop fileDrop=new FileDrop( frmHuntfiles, new FileDrop.Listener()
			      {   public void filesDropped( java.io.File[] files )
			          {   
			              if (files.length==1) {
			            	  File f=files[0];
			            	  if (f.isDirectory()) {
			            		  String dir=f.getAbsolutePath();
			            		  comboDirectory.getEditor().setItem(dir);
			            	  } else {
			            		  showMessage("drop only 1 directory, not a file");
			            	  }
			              } else {
			            	  showMessage("drop only 1 directory");
			            	  
			              }
			          }   // end filesDropped
			      });		
	}

	@Override
	public void addFile(FileInfo fileInfo) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        		filesTableModel.addFileInfo(fileInfo);
        		filesTableModel.fireTableDataChanged();
            }
        });
	}

	@Override
	public void addError(String err) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	appendErrMsg(err);
            }
        });
	}

	@Override
	public void updateProgress(int foundFiles, int searchedFiles, int totalFiles) {
		if (inProgress) {
			
		}
	}

	@Override
	public void workingInArchive(String archive) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	if (inProgress) {
            		labelStatus.setText(archive);
            	}
            }
        });
	}
}
