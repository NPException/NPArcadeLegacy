package okushama.glnes;

public interface IKeyListener {

	public void onKeyDown(int key);
	public void onKeyUp(int key);
	public String getLabel();
	public boolean isListening();
}
