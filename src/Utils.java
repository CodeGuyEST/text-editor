import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public final class Utils {
	private Utils(){}
	
	public static final String PREFERENCES_NODE_PATH = "/prefs";
	public static final String PREFERENCES_LAST_FILE_KEY = "last_file"; 
	
	//Keyboard shortcuts
	public static final KeyStroke SAVE_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
	public static final KeyStroke NEW_FILE_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
	public static final KeyStroke OPEN_FILE_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_O,KeyEvent.CTRL_DOWN_MASK);
	
	//User preferences
	public static final String PREFERENCES_BACKGROUND_COLOR_KEY = "background_color";
	public static final String PREFERENCES_FOREGROUND_COLOR_KEY = "foreground_color";
	public static final String PREFERENCES_TEXT_SIZE_KEY = "text_size";
}
