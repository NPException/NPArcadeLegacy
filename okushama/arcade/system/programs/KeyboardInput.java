package okushama.arcade.system.programs;

import java.util.Enumeration;
import java.util.Vector;

import org.lwjgl.input.Keyboard;

public class KeyboardInput {

	public static char getChar(int key) {
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		char out = ' ';
		switch (key) {
			//numbers
			case Keyboard.KEY_1:
				out = '1';
				if(shift){
					out = '!';
				}
				break;
			case Keyboard.KEY_2:
				out = '2';
				if(shift){
					out = '"';
				}
				break;
			case Keyboard.KEY_3:
				out = '3';
				if(shift){
					out = '£';
				}
				break;
			case Keyboard.KEY_4:
				out = '4';
				if(shift){
					out = '$';
				}
				break;
			case Keyboard.KEY_5:
				out = '5';
				if(shift){
					out = '%';
				}
				break;
			case Keyboard.KEY_6:
				out = '6';
				if(shift){
					out = '^';
				}
				break;
			case Keyboard.KEY_7:
				out = '7';
				if(shift){
					out = '&';
				}
				break;
			case Keyboard.KEY_8:
				out = '8';
				if(shift){
					out = '*';
				}
				break;
			case Keyboard.KEY_9:
				out = '9';
				if(shift){
					out = '(';
				}
				break;
			case Keyboard.KEY_0:
				out = '0';
				if(shift){
					out = ')';
				}
				break;

				// letters
			case Keyboard.KEY_Q:
				out = 'q';
				break;
			case Keyboard.KEY_W:
				out = 'w';
				break;
			case Keyboard.KEY_E:
				out = 'e';
				break;
			case Keyboard.KEY_R:
				out = 'r';
				break;
			case Keyboard.KEY_T:
				out = 't';
				break;
			case Keyboard.KEY_Y:
				out = 'y';
				break;
			case Keyboard.KEY_U:
				out = 'u';
				break;
			case Keyboard.KEY_I:
				out = 'i';
				break;
			case Keyboard.KEY_O:
				out = 'o';
				break;
			case Keyboard.KEY_P:
				out = 'p';
				break;
			case Keyboard.KEY_A:
				out = 'a';
				break;
			case Keyboard.KEY_S:
				out = 's';
				break;
			case Keyboard.KEY_D:
				out = 'd';
				break;
			case Keyboard.KEY_F:
				out = 'f';
				break;
			case Keyboard.KEY_G:
				out = 'g';
				break;
			case Keyboard.KEY_H:
				out = 'h';
				break;
			case Keyboard.KEY_J:
				out = 'j';
				break;
			case Keyboard.KEY_K:
				out = 'k';
				break;
			case Keyboard.KEY_L:
				out = 'l';
				break;
			case Keyboard.KEY_Z:
				out = 'z';
				break;
			case Keyboard.KEY_X:
				out = 'x';
				break;
			case Keyboard.KEY_C:
				out = 'c';
				break;
			case Keyboard.KEY_V:
				out = 'v';
				break;
			case Keyboard.KEY_B:
				out = 'b';
				break;
			case Keyboard.KEY_N:
				out = 'n';
				break;
			case Keyboard.KEY_M:
				out = 'm';
				break;

				//special
			case Keyboard.KEY_SPACE:
				out = ' ';
				break;
			case Keyboard.KEY_PERIOD:
				out = '.';
				if(shift){
					out = '>';
				}
				break;
			case Keyboard.KEY_COMMA:
				out = ',';
				if(shift){
					out = '<';
				}
				break;
			case Keyboard.KEY_SEMICOLON:
				out = ';';
				if(shift){
					out = ':';
				}
				break;
			case Keyboard.KEY_EQUALS:
				out = '=';
				if(shift){
					out = '+';
				}
				break;
			case Keyboard.KEY_BACKSLASH:
				out = '\\';
				if(shift){
					out = '|';
				}
				break;
			case Keyboard.KEY_SLASH:
				out = '/';
				if(shift){
					out = '?';
				}
				break;
			case Keyboard.KEY_APOSTROPHE:
				out = '\'';
				if(shift){
					out = '@';
				}
				break;
			case Keyboard.KEY_GRAVE:
				out = '#';
				if(shift){
					out = '~';
				}
				break;
			case Keyboard.KEY_LBRACKET:
				out = '[';
				if(shift){
					out = '{';
				}
				break;
			case Keyboard.KEY_RBRACKET:
				out = ']';
				if(shift){
					out = '}';
				}
				break;
			case Keyboard.KEY_MINUS:
				out = '-';
				if(shift){
					out = '_';
				}
				break;
			default:
				return '~';
		}
		if (shift && Character.getType(out) == Character.LOWERCASE_LETTER) {
			out = Character.toUpperCase(out);
		}
		return out;
	}

	public static String [] wrapText (String text, int len) {
		if (text == null) {
			return new String [] {};
		}

		if (len <= 0) {
			return new String [] {text};
		}

		if (text.length() <= len) {
			return new String [] {text};
		}

		char [] chars = text.toCharArray();
		Vector<String> lines = new Vector<String>();
		StringBuffer line = new StringBuffer();
		StringBuffer word = new StringBuffer();

		for (char c : chars) {
			word.append(c);

			if (c == ' ') {
				if ((line.length() + word.length()) > len) {
					lines.add(line.toString());
					line.delete(0, line.length());
				}

				line.append(word);
				word.delete(0, word.length());
			}
		}

		if (word.length() > 0) {
			if ((line.length() + word.length()) > len) {
				lines.add(line.toString());
				line.delete(0, line.length());
			}
			line.append(word);
		}

		if (line.length() > 0) {
			lines.add(line.toString());
		}

		String [] ret = new String[lines.size()];
		int c = 0;
		for (Enumeration<String> e = lines.elements(); e.hasMoreElements(); c++) {
			ret[c] = e.nextElement();
		}

		return ret;
	}
}
