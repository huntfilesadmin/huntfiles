
import org.bcjj.huntfiles.gui.HuntFilesMainWindow;

public class HuntFiles {

	public static void main(String [] s) throws Exception {
		boolean gui=false;
		if (s.length==0) {
			gui=true;
		} else {
			for (String x:s) {
				if (x.equalsIgnoreCase("-gui")) {
					gui=true;
				}
			}
		}
		if (gui) {
			HuntFilesMainWindow.main(s);
		} else {
			org.bcjj.huntfiles.HuntFiles.main(s);
		}
	}
	
}
