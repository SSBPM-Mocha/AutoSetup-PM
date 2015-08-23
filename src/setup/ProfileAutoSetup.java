package setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ini4j.Ini;

/**
 * Allows dynamic creation of profile names
 */
public class ProfileAutoSetup {
	private static Map<String, String> nameMap = new HashMap<String, String>();
	private static List<Profile> profileList = new ArrayList<Profile>();

	public static void createProfiles() throws InterruptedException {
		// Go to name entry menu
		if (AutoSetupM.isMuted) {
			AutoSetupM.type(new String[]{"UP", "LEFT"});
			AutoSetupM.type("A", 200);
		} else { //not muted so cursor is on deflicker still
			AutoSetupM.type(new String[]{"LEFT", "LEFT"});
			AutoSetupM.type("A", 200);
		}

		// Start adding profiles and configuring controllers
		for (Profile p : profileList) {

			ProfileAutoSetup.inputName(p.name);
			ControllerAutoSetup.setupController(p.tapJump, p.input);
			System.out.println(" [INFO]: Profile " + p.name + " complete.");
			if (profileList.size() > 1) {
				Thread.sleep(500);//fixes multiple profile input so it doesn't mess up
			}
		}
		System.out.println("[INFO]: " + profileList.size()
				+ " profiles created");
	}

	public static void createProfileList(Ini ini) {
		System.out.println("\nInitializing [profile] settings...");
		// Register arbitrary number of profiles (48)
		for (int i = 0; i < 48; i++) {
			boolean isDupe = false;
			String name = ini.get("profile", "name" + i);
			String input = ini.get("profile", "input" + i);
			String tapJump = ini.get("profile", "tapJump" + i);
			boolean setTapJump;

			if (name != null) {
				if (name.length() < 6) {
					if (tapJump == null) {
						setTapJump = true;
					} else {
						setTapJump = Boolean.valueOf(tapJump);
					}

					// Duplicate check
					for (Profile p : profileList) {
						if (p.name.equals(name)) {
							isDupe = true;
						}
					}

					if (!isDupe) {
						System.out.print(" [" + name + "]:  \ttapjump = ");
						if (setTapJump) {
							System.out.print("on ");
						} else {
							System.out.print("off");
						}
						System.out.print("      config = ");
						if (input != null) {
							System.out.println(input);
						} else {
							System.out.println("default");
						}
						profileList.add(new Profile(name, input, setTapJump));
					} else {
						System.out.println("[ERROR] Duplicate name: " + name);
					}
				} else {
					System.out.println("[ERROR] Name too long: " + name);
				}
			}
		}
		System.out.println();
	}

	/**
	 * Create key/value pairs of a character to number of key presses NOTE: Only
	 * the first page of tiles is supported at the moment
	 */
	public static void initializeTileData() {
		nameMap.put(" ", "00,01");
		nameMap.put("@", "00,02");
		nameMap.put("(", "00,03");
		nameMap.put(")", "00,04");
		nameMap.put("^", "00,05");
		nameMap.put(":", "00,06");
		nameMap.put(";", "00,07");
		nameMap.put("A", "01,01");
		nameMap.put("B", "01,02");
		nameMap.put("C", "01,03");
		nameMap.put("a", "01,04");
		nameMap.put("b", "01,05");
		nameMap.put("c", "01,06");
		nameMap.put("D", "02,01");
		nameMap.put("E", "02,02");
		nameMap.put("F", "02,03");
		nameMap.put("d", "02,04");
		nameMap.put("e", "02,05");
		nameMap.put("f", "02,06");
		nameMap.put("G", "03,01");
		nameMap.put("H", "03,02");
		nameMap.put("I", "03,03");
		nameMap.put("g", "03,04");
		nameMap.put("h", "03,05");
		nameMap.put("i", "03,06");
		nameMap.put("J", "04,01");
		nameMap.put("K", "04,02");
		nameMap.put("L", "04,03");
		nameMap.put("j", "04,04");
		nameMap.put("k", "04,05");
		nameMap.put("l", "04,06");
		nameMap.put("M", "05,01");
		nameMap.put("N", "05,02");
		nameMap.put("O", "05,03");
		nameMap.put("m", "05,04");
		nameMap.put("n", "05,05");
		nameMap.put("o", "05,06");
		nameMap.put("P", "06,01");
		nameMap.put("Q", "06,02");
		nameMap.put("R", "06,03");
		nameMap.put("S", "06,04");
		nameMap.put("p", "06,05");
		nameMap.put("q", "06,06");
		nameMap.put("r", "06,07");
		nameMap.put("s", "06,08");
		nameMap.put("T", "07,01");
		nameMap.put("U", "07,02");
		nameMap.put("V", "07,03");
		nameMap.put("t", "07,04");
		nameMap.put("u", "07,05");
		nameMap.put("v", "07,06");
		nameMap.put("W", "08,01");
		nameMap.put("X", "08,02");
		nameMap.put("Y", "08,03");
		nameMap.put("Z", "08,04");
		nameMap.put("w", "08,05");
		nameMap.put("x", "08,06");
		nameMap.put("y", "08,07");
		nameMap.put("z", "08,08");
		nameMap.put("!", "09,01");
		nameMap.put("?", "09,02");
		nameMap.put("&", "09,03");
		nameMap.put("%", "09,04");
		nameMap.put("·", "10,01");
		nameMap.put(",", "10,02");
		nameMap.put(".", "10,03");
		nameMap.put("/", "10,04");
		nameMap.put("~", "10,05");
	}

	/**
	 * Creates a profile with the given input
	 * 
	 * @param input
	 * @throws InterruptedException
	 */
	public static void inputName(String input) throws InterruptedException {
		System.out.println("[INFO]: Inputting Name...");
		AutoSetupM.type("A");
		int currentTile = 0;
		for (int i = 0; i < input.length(); i++) {
			currentTile = letterHelper(Character.toString(input.charAt(i)),
					currentTile);
		}

		// Done! Move to controller setup
		AutoSetupM.type("START");
		System.out.println("[INFO]: Name input complete.");
		AutoSetupM.type("A", 1300 + AutoSetupM.loadingScreenLatency);
	}

	/**
	 * Helper to translate the nameMap, bit ugly right now
	 * 
	 * @param letter
	 * @param currentTile
	 * @return
	 * @throws InterruptedException
	 */
	private static int letterHelper(String letter, int currentTile)
			throws InterruptedException {

		String info = nameMap.get(letter);

		// Adds a question mark if the letter isn't supported
		if (info == null) {
			info = nameMap.get("?");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(info, 0, 2);

		// Move to the correct tile
		int targetTile = Integer.valueOf(sb.toString()).intValue();
		tileMovementHelper(currentTile, targetTile);

		// Press the "A" button until we get the letter
		sb.setLength(0);
		sb.append(info, 3, 5);
		for (int i = 0; i < Integer.valueOf(sb.toString()).intValue(); i++) {
			AutoSetupM.type("A");
		}

		return targetTile;
	}

	/**
	 * Helper which memorizes last cursor position and moves efficiently
	 * 
	 * @param current
	 * @param target
	 * @throws InterruptedException
	 */
	private static void tileMovementHelper(int current, int target)
			throws InterruptedException {

		// Same as current tile, reset letter via down + up
		if (current == target) {
			AutoSetupM.type(new String[] { "DOWN", "UP" });
			return;
		}

		int difference = current - target;

		// Move up/down the appropriate number of times
		while (difference >= 3) {
			AutoSetupM.type("UP");
			difference -= 3;
		}
		while (difference <= -3) {
			AutoSetupM.type("DOWN");
			difference += 3;
		}

		// Distance was divisible by 3, return
		if (difference == 0) {
			return;
		}

		int remainder = current % 3;

		// This took a surprising amount of time to implement
		if (difference == 2) {
			if (remainder == 2) {
				AutoSetupM.type(new String[] { "LEFT", "LEFT" });
			} else {
				AutoSetupM.type(new String[] { "RIGHT", "UP" });
			}
		} else if (difference == 1) {
			if (remainder == 0) {
				AutoSetupM.type(new String[] { "RIGHT", "UP", "RIGHT" });
			} else {
				AutoSetupM.type("LEFT");
			}
		} else if (difference == -2) {
			if (remainder == 0) {
				AutoSetupM.type(new String[] { "RIGHT", "RIGHT" });
			} else {
				AutoSetupM.type(new String[] { "DOWN", "LEFT" });
			}
		} else {
			if (remainder == 2) {
				AutoSetupM.type(new String[] { "LEFT", "DOWN", "LEFT" });
			} else {
				AutoSetupM.type("RIGHT");
			}
		}
	}
}
