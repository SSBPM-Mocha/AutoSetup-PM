package setup;

/**
 * Data for a profile
 */
public class Profile {
	public String name;
	public String input;
	public boolean tapJump;

	public Profile(String setName, String setInput, boolean setTapJump) {
		name = setName;
		input = setInput;
		tapJump = setTapJump;
	}
}
