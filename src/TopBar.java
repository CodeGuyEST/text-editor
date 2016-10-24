import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TopBar extends JMenuBar {
	private JMenu fileMenu, editMenu,textSize;
	private JMenuItem newFile,saveFile,saveAsFile,openFile;
	private JMenuItem textColor,backGroundColor;
	private JFileChooser fileChooser;
	private JTextPane mainTextArea;
	private Gui gui;
	
	public void setGui(Gui gui){
		this.gui = gui;
		this.mainTextArea = gui.getTextPane();
	}
	
	public TopBar(){
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		
		newFile = new JMenuItem("New");
		saveFile = new JMenuItem("Save");
		saveAsFile = new JMenuItem("Save As");
		openFile = new JMenuItem("Open");
		openFile.addActionListener(new OpenFileOnClickListener());
		saveFile.addActionListener(new SaveFileOnClickListener());
		saveAsFile.addActionListener(new SaveAsFileOnClickListener());
		newFile.addActionListener(new NewFileOnClickListener());
		
		newFile.setAccelerator(Utils.NEW_FILE_KEY_STROKE);
		openFile.setAccelerator(Utils.OPEN_FILE_KEY_STROKE);
		saveFile.setAccelerator(Utils.SAVE_KEY_STROKE);
		
		textColor = new JMenuItem("Color");
		textColor.addActionListener(new TextColorOnClickListener());
		textSize = new JMenu("Size");
		backGroundColor = new JMenuItem("Background");
		backGroundColor.addActionListener(new BackGroundColorOnClickListener());
		
		for(int i = 5; i <= 12; i++) {
			JMenuItem item = new JMenuItem(Integer.toString(i));
			item.addActionListener(new TextSizeOnClickListener());
			textSize.add(item);
		}
		
		for(int i = 20; i <= 120; i+=10) {
			JMenuItem item = new JMenuItem(Integer.toString(i));
			item.addActionListener(new TextSizeOnClickListener());
			textSize.add(item);
		}

		fileMenu.add(newFile);
		fileMenu.add(saveFile);
		fileMenu.add(saveAsFile);
		fileMenu.add(openFile);
		
		editMenu.add(backGroundColor);
		editMenu.add(textColor);
		editMenu.add(textSize);
		
		add(fileMenu);
		add(editMenu);
		
		fileChooser = new JFileChooser();
	}
	
	private class OpenFileOnClickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int retrieval = fileChooser.showOpenDialog(fileChooser);
			if(retrieval == JFileChooser.APPROVE_OPTION) {
				FileManager fileManager = FileManager.getInstance();
				fileManager.setCurrentFilePath(fileChooser.getSelectedFile().getAbsolutePath());
				String newText;
				try {
					newText = fileManager.readFile(fileChooser.getSelectedFile().getAbsolutePath());
					mainTextArea.setText(newText);
					gui.setTitle(fileManager.getCurrentFilePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				gui.getDocumentListener().setEnabled(true);
			}
		}
		
	}
	
	private class SaveFileOnClickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			FileManager fileManager = FileManager.getInstance();
			if(fileManager.getCurrentFilePath() != null) {
				fileManager.saveFile(fileManager.getCurrentFilePath(), mainTextArea.getText());
			}
			else {
				int retrieval = fileChooser.showSaveDialog(fileChooser);
				if(retrieval == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					if(!filePath.endsWith(".txt"))filePath = filePath.concat(".txt");
					fileManager.setCurrentFilePath(filePath);
					fileManager.saveFile(fileManager.getCurrentFilePath(), mainTextArea.getText());
				}
			}
			FileManager.getInstance().setFileUpToDate(true);
		}
		
	}
	
	private class SaveAsFileOnClickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int retrieval = fileChooser.showSaveDialog(fileChooser);
			if(retrieval == JFileChooser.APPROVE_OPTION) {
				FileManager fileManager = FileManager.getInstance();
				fileManager.setCurrentFilePath(fileChooser.getSelectedFile().getAbsolutePath());
				fileManager.saveFile(fileManager.getCurrentFilePath() + ".txt", mainTextArea.getText());
			}
			FileManager.getInstance().setFileUpToDate(true);
		}
		
	}
	
	private class NewFileOnClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			gui.getDocumentListener().setEnabled(false);
			mainTextArea.setText(null);
			FileManager.getInstance().setCurrentFilePath(null);
			Preferences.userRoot().node(Utils.PREFERENCES_NODE_PATH).put(Utils.PREFERENCES_LAST_FILE_KEY,"");
			gui.getDocumentListener().setEnabled(true);
			gui.setTitle(null);
		}
		
	}
	
	private class TextColorOnClickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Color color = JColorChooser.showDialog(null,"Title",Color.BLACK);
			JTextPane textPane = gui.getTextPane();
			String text = textPane.getText();
			StyledDocument doc = textPane.getStyledDocument();
			MutableAttributeSet set = textPane.getInputAttributes();
			StyleConstants.setForeground(set,color);
			PropertiesManager.getInstance().setTextColor(color);
			try {
				doc.remove(0,doc.getLength());
			    doc.insertString(doc.getLength(),text,set);
			}
	        catch (BadLocationException e){}
		}
		
	}
	
	private class TextSizeOnClickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
			int textSize = Integer.parseInt(((JMenuItem)(event.getSource())).getText());
			JTextPane textPane = gui.getTextPane();
			String text = textPane.getText();
			StyledDocument doc = textPane.getStyledDocument();
			MutableAttributeSet set = textPane.getInputAttributes();
			StyleConstants.setFontSize(set, textSize);
			PropertiesManager.getInstance().setFontSize(textSize);
			try {
				doc.remove(0,doc.getLength());
			    doc.insertString(doc.getLength(),text,set);
			}
	        catch (BadLocationException e){}
		}
		
	}
	
	private class BackGroundColorOnClickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
			Color color = JColorChooser.showDialog(null,"Title",Color.BLACK);
			JTextPane textPane = gui.getTextPane();
			textPane.setBackground(color);
			PropertiesManager.getInstance().setBackGroundColor(color);
		}
	}

}
