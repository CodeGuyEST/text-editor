import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

public class FileManager {
	
	private static FileManager fileManager;
	private String currentFilePath;
	private static boolean fileUpToDate = true;
	private Gui.DocumentListener documentListener;
	
	private FileManager(){}
	
	public void setDocumentListener(Gui.DocumentListener documentListener) {
		this.documentListener = documentListener;
	}
	
	public static synchronized FileManager getInstance(){
		if (fileManager == null)
			fileManager = new FileManager();
		return fileManager;
	}
	
	public void setCurrentFilePath(String path){
		currentFilePath = path;
	}
	
	public String getCurrentFilePath(){
		return currentFilePath;
	}
	
	public String readFile(String filePath) throws IOException,FileNotFoundException
	{   
		documentListener.setEnabled(false);//We open new file and therefore want to initially mark the file as up to date.
	    String content = null;
	    File file = new File(filePath); //for ex foo.txt
	    FileReader reader = null;
	    reader = new FileReader(file);
	    char[] chars = new char[(int) file.length()];
	    reader.read(chars);
	    content = new String(chars);
	    reader.close();
	    return content;
	}
	
	public boolean saveFile(String path,String text) {
		if(path == null) {
			System.out.println("PATH NULL");
			JFileChooser fileChooser = new JFileChooser();
			int retrieval = fileChooser.showSaveDialog(fileChooser);
			if(retrieval == JFileChooser.APPROVE_OPTION) {
				String result = fileChooser.getSelectedFile().getAbsolutePath();
				if(!result.endsWith(".txt"))result = result.concat(".txt");
				setCurrentFilePath(result);
				storeLastFilePath(currentFilePath);
				return saveFile(currentFilePath,text);
			}
			else return false;
		}
		if(!path.endsWith(".txt"))path = path.concat(".txt");
		try(BufferedReader reader = new BufferedReader(new StringReader(text));
		        PrintWriter writer = new PrintWriter(new FileWriter(new File(path)));) {
			reader.lines().forEach(line -> writer.println(line));
			reader.close();
	  } catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
	  } catch (FileNotFoundException e) {
		    e.printStackTrace();
	  } catch (IOException e) {
		    e.printStackTrace();
	    }
		return true;
	}
	
	public void storeLastFilePath(String filePath) {
		Preferences preferences = Preferences.userRoot().node(Utils.PREFERENCES_NODE_PATH);
		preferences.put(Utils.PREFERENCES_LAST_FILE_KEY, filePath);
	}
	
	public void setFileUpToDate(boolean upToDate){
		fileUpToDate = upToDate;
	}
	
	public boolean isFileUpToDate(){
		return fileUpToDate;
	}
}
