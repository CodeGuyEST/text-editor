import java.awt.Color;
import java.util.prefs.Preferences;

import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

public class PropertiesManager {
	
	private Color foreGroundColor, backGroundColor;
	private int fontSize;
	
	private static PropertiesManager propertiesManager;
	private PropertiesManager(){}
	
	public void setFontSize(int size){
		fontSize = size;
	}
	public void setTextColor(Color color){
		foreGroundColor = color;
	}
	public void setBackGroundColor(Color color){
		backGroundColor = color;
	}
	
	public void loadAttributeSet(JTextPane textPane){
		MutableAttributeSet set = textPane.getInputAttributes();
		Preferences preferences = Preferences.userRoot().node(Utils.PREFERENCES_NODE_PATH);
		String backGround = preferences.get(Utils.PREFERENCES_BACKGROUND_COLOR_KEY, "");
		System.out.println("BACKGROUND:" + backGround);
		backGroundColor = backGround.equals("") ? Color.WHITE : Color.decode(backGround);
		String foreGround = preferences.get(Utils.PREFERENCES_FOREGROUND_COLOR_KEY, "");
		foreGroundColor = foreGround.equals("") ? Color.BLACK : Color.decode(foreGround);
		System.out.println("FOREGROUND:" + foreGround);
		int textSize = preferences.getInt(Utils.PREFERENCES_TEXT_SIZE_KEY,0);
		fontSize = textSize == 0 ? 12 : textSize;
		
		textPane.setBackground(backGroundColor);
		StyleConstants.setForeground(set,foreGroundColor);
		StyleConstants.setFontSize(set,fontSize);
	}
	
	public void storeAttributes(){
		Preferences preferences = Preferences.userRoot().node(Utils.PREFERENCES_NODE_PATH);
		String hex = String.format("#%02x%02x%02x",backGroundColor.getRed(), 
				backGroundColor.getGreen(),backGroundColor.getBlue());
		preferences.put(Utils.PREFERENCES_BACKGROUND_COLOR_KEY,hex);
		String hex1 = String.format("#%02x%02x%02x",foreGroundColor.getRed(), 
				foreGroundColor.getGreen(),foreGroundColor.getBlue());
		preferences.put(Utils.PREFERENCES_FOREGROUND_COLOR_KEY,hex1);
		preferences.putInt(Utils.PREFERENCES_TEXT_SIZE_KEY,fontSize);
	}
	
	public static synchronized PropertiesManager getInstance(){
		if (propertiesManager == null)
			propertiesManager = new PropertiesManager();
		return propertiesManager;
	}
}
