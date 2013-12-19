package okushama.arcade.system;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

public class OSLogger {

private Logger log;
	
	public OSLogger(String name){
		log = Logger.getLogger(name);
		log.setParent(FMLLog.getLogger());
	}
	
	public void log(String out, boolean warning){
		if(warning){
			log.log(Level.WARNING, out);
		}
	}
	
	public void log(String out){
		log.log(Level.FINE, out);
	}
}
