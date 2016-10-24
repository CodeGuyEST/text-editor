import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Gui extends JFrame{
	private JTextPane textArea;
	private JScrollPane scrollPane;
	private TopBar topBar;
	private Gui.DocumentListener documentListener;
	
	private void updateTextAreaSize(int padding) {
		Rectangle bounds = getContentPane().getBounds();
		bounds.x += padding;
		bounds.y += padding;
		bounds.width -= 2 * padding;
		bounds.height -= 2 * padding;
		scrollPane.setBounds(bounds);
		revalidate();
	}
	
	public Gui.DocumentListener getDocumentListener() {
		return documentListener;
	}
	
	public JTextPane getTextPane(){
		return textArea;
	}
	
	public void setUp(){
		System.out.println(Color.decode("0xffffff"));
		System.out.println(Color.decode("#ffffff"));
		setPreferredSize(new Dimension(800,800));
		setLayout(null);
		setMinimumSize(new Dimension(500,500));
		pack();
		textArea = new JTextPane();
		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		propertiesManager.loadAttributeSet(textArea);
		documentListener = new Gui.DocumentListener();
		textArea.getDocument().addDocumentListener(documentListener);
		scrollPane = new JScrollPane(textArea);
		updateTextAreaSize(30);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		add(scrollPane);
		topBar = new TopBar();
		topBar.setGui(this);
		setJMenuBar(topBar);
		setVisible(true);
		getRootPane().addComponentListener(new RootPaneComponentListener());
		FileManager.getInstance().setDocumentListener(documentListener);
		addWindowListener(new Gui.WindowListener()); 
		loadLastFile();
	}
	
	private int getFileSaveCode(String message) {
		int saveChoice = JOptionPane.showOptionDialog(this,message,message,JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,null,new Object[]{"Save","Don't Save","Cancel"},null);
		return saveChoice;
	}
	
	private void loadLastFile() {
		Preferences prefs = Preferences.userRoot().node(Utils.PREFERENCES_NODE_PATH);
		if(!prefs.get(Utils.PREFERENCES_LAST_FILE_KEY, "").equals("")) {
			FileManager.getInstance().setCurrentFilePath(prefs.get(Utils.PREFERENCES_LAST_FILE_KEY, ""));
			try {
				System.out.println(prefs.get(Utils.PREFERENCES_LAST_FILE_KEY,"SITTAGI"));
				textArea.setText(FileManager.getInstance().readFile(prefs.get(Utils.PREFERENCES_LAST_FILE_KEY,"")));
				this.setTitle(FileManager.getInstance().getCurrentFilePath());
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this,
					    "Couldn't find last file.",
					    "File not found",
					    JOptionPane.ERROR_MESSAGE);
				FileManager.getInstance().setCurrentFilePath("");
				FileManager.getInstance().storeLastFilePath(FileManager.getInstance().getCurrentFilePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		documentListener.setEnabled(true);
	}
	
	public static class DocumentListener implements javax.swing.event.DocumentListener{
		private boolean enabled = false;
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			//if(enabled)FileManager.getInstance().setFileUpToDate(false);	
		}
		@Override
		public void insertUpdate(DocumentEvent arg0) {
			if(enabled){FileManager.getInstance().setFileUpToDate(false);System.out.println("Inserted");}	
		}
		@Override
		public void removeUpdate(DocumentEvent arg0) {
			if(enabled){FileManager.getInstance().setFileUpToDate(false);System.out.println("Removed");}
		}
		
	}
	
	private class WindowListener extends WindowAdapter{
		@Override
		public void windowClosing(WindowEvent windowEvent){
			FileManager fileManager = FileManager.getInstance();
			if(!fileManager.isFileUpToDate()) {
				switch(getFileSaveCode("Do You want to save Your changes?")) {
				case JOptionPane.YES_OPTION:
					if(fileManager.saveFile(fileManager.getCurrentFilePath(),textArea.getText())){
						fileManager.storeLastFilePath(fileManager.getCurrentFilePath());
					}
					PropertiesManager.getInstance().storeAttributes();
					windowEvent.getWindow().dispose();
					System.exit(0);
					break;
				case JOptionPane.NO_OPTION:
					if(fileManager.getCurrentFilePath() != null)
						fileManager.storeLastFilePath(fileManager.getCurrentFilePath());
					PropertiesManager.getInstance().storeAttributes();
					windowEvent.getWindow().dispose();
					System.exit(0);
					break;
				case JOptionPane.CANCEL_OPTION:
					break;
				}
			}
			else if(fileManager.getCurrentFilePath() != null){
				FileManager.getInstance().storeLastFilePath(FileManager.getInstance().getCurrentFilePath());
				PropertiesManager.getInstance().storeAttributes();
				System.exit(0);
			}
			else {
				PropertiesManager.getInstance().storeAttributes();
				System.exit(0);
			}
		}
	}
	
	private class RootPaneComponentListener implements java.awt.event.ComponentListener{

		@Override
		public void componentHidden(ComponentEvent arg0) {
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			updateTextAreaSize(30);
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
		}
		
	}				
}
		
	
	
