package setup;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.ini4j.Ini;

/**
 * This was supposed to be a tiny script (driver only). It got a bit bigger.
 * It's my excuse for the disorganization
 * 
 * @param args
 * @throws AWTException
 * @throws InterruptedException
 */
public class AutoSetupM {

	private static Robot r;
	private static Scanner console = new Scanner(System.in);
	private static Map<String, Integer> keyMap = new HashMap<String, Integer>();
	private static String destination;
	private static String [] IR_Values = {"1", "1.5", "2", "2.5", "3", "4"};
	private static int lettersTyped = 0;
	public static long minLatency = 25;
	private static boolean logging, fromMainMenu,
			promptLatencyAtStartup,	netplayLoop,
			adjustIR;
	public static boolean waitForButtonConfig, isMuted;
	private static int indexOfDolphinIR;
	public static long loadingScreenLatency,
			controllerConfigScreenLatency,
			mainMenuLatency;
	private static long globalLatency;
	private static long keyEventDuration = 35;

	/**
	 * Generic driver logic is here. Workflow is visible here
	 * 
	 * @param args
	 * @throws AWTException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws AWTException,
			InterruptedException, IOException {

		// Allows execution from the Main Menu
		if (args.length > 0) {
			if (args[0].equals("mainmenu")) {
				fromMainMenu = true;
			}
		}
		initialize();
		do {
			promptLatency();
			adjustIR(0); // 0 decreases IR to minimum, 1 returns it back to original setting from .ini
			long startTime = System.currentTimeMillis();
			navigateToMainMenu();
			disableDeflicker();
			setupSound();
				//TODO: Write readme and touch up .ini. edit icon to have transparent background in photoshop
				//TODO: Experiment with the new multiple profile, maybe add just a slightly longer wait time on it
			ProfileAutoSetup.createProfiles();
			goToDestination();
			adjustIR(1); //return resolution to original

			System.out.println("[INFO]: Setup completed in "
					+ (System.currentTimeMillis() - startTime) / 1000
					+ " seconds. \n\nDyno says, \"Good luck and have fun!\"");
			if (!netplayLoop) {
				System.out.println("\nThis window will close automatically in 5 seconds");
				Thread.sleep(5000);
			}
		} while (netplayLoop);
	}

	// Navigates from the very first screen to main menu, no save file
	public static void navigateToMainMenu() throws InterruptedException {

		for (int i = 0; i < 1; i++) {
			System.out.println("\n... Don't click on the Project M window!!!\n");
			Thread.sleep(1000);
		}
		if (!fromMainMenu) {
			//splash screen
			type("START", 4200 + loadingScreenLatency);//4200
			//Don't create save
			type("B", 300 );
			//continue without saving
			type("A", 999 + loadingScreenLatency);//1000
			//welcome to netplay build
			type("A", 340);
			//join the ladder
			type("A", 340);
			//thanks for playing
			type("A", 2000 + loadingScreenLatency);
			//video
			type("A", 350);
			//press start to continue
			type("A", 2450 + loadingScreenLatency);
			System.out.println("[INFO]: Navigated to main menu");
		} else {
			// Goes to splash then Main Menu. If focus is not on "FIGHT"
			type("B", 1000 + loadingScreenLatency);
			type("A", 3000 + loadingScreenLatency);
		}
	}
	public static void waveShine()throws InterruptedException {
		int testNumber = 61;
		r.keyPress(keyMap.get("RIGHT").intValue());
		Thread.sleep(32);
		r.keyPress(89);
		Thread.sleep(64);
		r.keyRelease(89);
		Thread.sleep(2336);
		r.keyRelease(keyMap.get("RIGHT").intValue());
		Thread.sleep(32);

		for (;;) {
			r.keyPress(keyMap.get("LEFT").intValue());
			Thread.sleep(16);
			r.keyRelease(keyMap.get("LEFT").intValue());
			System.out.println("Test Number: " + testNumber);
			for (int i = 0; i < 22; i++) {

				r.keyPress(keyMap.get("DOWN").intValue());
				r.keyPress(keyMap.get("B").intValue());
				Thread.sleep(16);

				r.keyRelease(keyMap.get("B").intValue());

				Thread.sleep(96);//seems about 64 but added sleep above so subtract that?//wait before jump



				r.keyPress(89);//Y to jump

				r.keyPress(keyMap.get("LEFT").intValue());

				Thread.sleep(16);
				r.keyRelease(89);

				Thread.sleep(64);

				r.keyPress(50);//2 to airdodge
				Thread.sleep(32);//was 20
				r.keyRelease(50);

				r.keyRelease(keyMap.get("LEFT").intValue());
				Thread.sleep(240);//196  or whatever is good i think test more
			}
			r.keyRelease(keyMap.get("DOWN").intValue());
			Thread.sleep(96); //wait to be able to run again

			r.keyPress(keyMap.get("RIGHT").intValue());
			Thread.sleep(3584);
			r.keyRelease(keyMap.get("RIGHT").intValue());
			testNumber++;
			if (testNumber == 64) {
				testNumber = 61;
			}
		}
	}

	public static void disableDeflicker() throws InterruptedException {

		type(new String[]{"RIGHT", "DOWN", "A", "RIGHT", "A", "A", "B"}, mainMenuLatency);
		System.out.println("[INFO]: Deflicker disabled.");
	}

	// Turn off sound
	private static void setupSound() throws InterruptedException {
		if (isMuted) {
			type(new String[]{"DOWN", "LEFT", "A"}, mainMenuLatency);

			if (logging) {
				System.out.println("[INPUT]: " + "LEFT");
			}

			// Press and release the key
			r.keyPress(keyMap.get("LEFT").intValue());
			Thread.sleep(900);
			r.keyRelease(keyMap.get("LEFT").intValue());

			Thread.sleep(globalLatency);
			Thread.sleep(100);

			type("B");

			System.out.println("[INFO]: Sound is muted");
		}
	}

	private static void goToDestination() throws InterruptedException {
		if (destination.equals("FIGHT")) {
			type(new String[] {"B", "B", "UP", "LEFT", "A", "A"}, mainMenuLatency);
		} else if (destination.equals("NAMEENTRY")) {
			//notify the other player that it's their turn by c-sticking
			type(new String[]{"cUp", "cLeft", "cDown", "cRight"});
		}
	}

	//prompt for globalLatency at startup, probably  for when changing opponents on netplay often
	private static void promptLatency() throws InterruptedException {
		if (promptLatencyAtStartup) {

			boolean exitLoop = false;

			System.out.println("\nIf you would like to keep a " + globalLatency + " millisecond globalLatency, just press enter.");
			System.out.print("Otherwise, enter an integer globalLatency value from 0-500 (in ms) and press enter.");
			if (netplayLoop) {
				System.out.print("\nQ to quit.");
			}
			System.out.print("\n\nLatency: ");

			while (!exitLoop) {
				//delete all the random characters from the console window if looped
				for (int i = 0; i <= lettersTyped; i++) {
					r.keyPress(8);
					r.keyRelease(8);
				}
				lettersTyped = 0;

				String s = console.nextLine();
				if (s.equals("")) {
					exitLoop = true;
				} else if (s.equalsIgnoreCase("Q") && netplayLoop) {
					System.exit(0);
				} else if (s.equalsIgnoreCase("Fax")) {
					waveShine();//only reason for interruptedexception
				} else {
					try {
						globalLatency = Long.parseLong(s);
						if (globalLatency <= 500 && globalLatency >= 0) {
							exitLoop = true;
						} else {
							System.out.println("\n*** Please enter a number between 0 and 500, " +
									"or change the value in the .ini if you want a globalLatency." +
									"greater than 500ms. ***\n");
							System.out.print("Latency: ");
						}
					} catch (NumberFormatException n) {
						System.out.println("*** Please enter a proper number for globalLatency. ***\n");
						System.out.print("Latency: ");
					}
				}
			}
			System.out.println("\nLatency is set to " + globalLatency + " milliseconds.");
		}
	}

	private static void adjustIR (int irSwitch) throws InterruptedException {
		if (adjustIR) {
			if (irSwitch == 0) { //decrease IR to minimum
				for (int i = indexOfDolphinIR; i > 0; i--) {
					type("irDown", 30 + globalLatency);
				}
				System.out.println("[INFO]: Internal resolution decreased to minimum.");
			} else if (irSwitch == 1) { //return resolution to original setting, defined in .ini
				for (int i = 0; i < indexOfDolphinIR; i++) {
					type("irUp", 30 + globalLatency);
				}
				System.out.println("[INFO]: Internal resolution returned to original setting.");
			} else {
				throw new IllegalArgumentException("You must use a value of 0 or 1 " +
						"for the setIR parameter");
			}
		}
	}

	/**
	 * Helper method for inputting keys. Utilizes the map, adds delays for
	 * slower connections, and enables logging.
	 * 
	 * @param input
	 * @throws InterruptedException
	 */
	public static void type(String input, long wait)
			throws InterruptedException {
		if (logging) {
			System.out.println("[INPUT]: " + input);
		}

		// Press and release the key
		r.keyPress(keyMap.get(input).intValue());
		Thread.sleep(keyEventDuration);
		r.keyRelease(keyMap.get(input).intValue());

		lettersTyped++;
		Thread.sleep(globalLatency);
		Thread.sleep(wait);
	}

	public static void type(String input) throws InterruptedException {
		type(input, minLatency);
	}

	public static void type(String[] inputs) throws InterruptedException {
		for (int i = 0; i < inputs.length; i++) {
			type(inputs[i]);
		}
	}

	public static void type(String[] inputs,long wait) throws InterruptedException {
		for (int i = 0; i < inputs.length; i++) {
			type(inputs[i], wait);
		}
	}

	/**
	 * Initializes Robot and reads settings.ini for settings, profiles, etc
	 *
	 * @throws AWTException
	 * @throws IOException
	 */
	public static void initialize() throws AWTException, IOException {

		r = new Robot();
		Ini settingsIni = new Ini(new File("config/settings.ini"));
		Ini profilesIni = new Ini(new File("config/profiles.ini"));
		Ini keyboardConfigIni = new Ini(new File("config/keyboardConfig.ini"));

		// Initialize general settings
		String internalResolutionInDolphin = String.valueOf(settingsIni.get("general", "internalResolutionInDolphin"));
		logging = Boolean.valueOf(settingsIni.get("general", "logging"));
		globalLatency = Long.parseLong(settingsIni.get("general", "globalLatency"));
		isMuted = Boolean.valueOf(settingsIni.get("general", "isMuted"));
		adjustIR = Boolean.valueOf(settingsIni.get("general", "adjustIR"));
		netplayLoop = Boolean.valueOf(settingsIni.get("general", "netplayLoop"));
		waitForButtonConfig = Boolean.valueOf(settingsIni.get("general", "waitForButtonConfig"));
		mainMenuLatency = (Long.parseLong(settingsIni.get("general", "mainMenuLatency")));
		loadingScreenLatency = (Long.parseLong(settingsIni.get("general", "loadingScreenLatency")));
		controllerConfigScreenLatency = (Long.parseLong(settingsIni.get("general", "controllerConfigScreenLatency")));
		destination = settingsIni.get("general", "destination").toUpperCase();
		//prevents continuous looping by creating a prompt
		if (netplayLoop) {
			promptLatencyAtStartup = true;
		} else {
			//prompt user to input values, rather than using .ini by default
			promptLatencyAtStartup = Boolean.valueOf(settingsIni.get("general", "promptLatencyAtStartup"));
		}
		//setup internal resolution adjustment
		indexOfDolphinIR = Arrays.binarySearch(IR_Values, internalResolutionInDolphin);

		System.out.println("Initializing [general] settings...");
		System.out.println("  [LOGGING]:                          " + logging);
		System.out.println("  [LOOP PROGRAM]:                     " + netplayLoop);
		System.out.println("  [GLOBAL LATENCY PROMPT AT START]:   " + promptLatencyAtStartup);
		System.out.println("  [GLOBAL LATENCY]:                   " + globalLatency + " ms");
		System.out.println("  [MAIN MENU LATENCY]:                " + mainMenuLatency + " ms");
		System.out.println("  [LOADING SCREEN LATENCY]:           " + loadingScreenLatency + " ms");
		System.out.println("  [CONTROLLER CONFIG LATENCY]:        " + controllerConfigScreenLatency + " ms");
		System.out.println("  [WAIT FOR BUTTON CONFIG]:           " + waitForButtonConfig);
		System.out.println("  [ISMUTED]:                          " + isMuted);
		System.out.println("  [DESTINATION]:                      " + destination);
		System.out.println("  [ADJUST IR]:                        " + adjustIR);
		System.out.println("  [INTERNAL RESOLUTION IN DOLPHIN]:   " + IR_Values[indexOfDolphinIR]);

		// Initialize buttons that AutoSetup will press
		keyMap.put("A", Integer.parseInt(keyboardConfigIni.get("buttons", "a_button")));
		keyMap.put("B", Integer.parseInt(keyboardConfigIni.get("buttons", "b_button")));
		keyMap.put("UP", Integer.parseInt(keyboardConfigIni.get("buttons", "up")));
		keyMap.put("DOWN", Integer.parseInt(keyboardConfigIni.get("buttons", "down")));
		keyMap.put("LEFT", Integer.parseInt(keyboardConfigIni.get("buttons", "left")));
		keyMap.put("RIGHT", Integer.parseInt(keyboardConfigIni.get("buttons", "right")));
		keyMap.put("START", Integer.parseInt(keyboardConfigIni.get("buttons", "start")));
		keyMap.put("cUp", Integer.parseInt(keyboardConfigIni.get("buttons", "cUp")));
		keyMap.put("cLeft", Integer.parseInt(keyboardConfigIni.get("buttons", "cLeft")));
		keyMap.put("cDown", Integer.parseInt(keyboardConfigIni.get("buttons", "cDown")));
		keyMap.put("cRight", Integer.parseInt(keyboardConfigIni.get("buttons", "cRight")));
		keyMap.put("irDown", Integer.parseInt(keyboardConfigIni.get("buttons", "irDown")));
		keyMap.put("irUp", Integer.parseInt(keyboardConfigIni.get("buttons", "irUp")));

		ProfileAutoSetup.initializeTileData();
		ProfileAutoSetup.createProfileList(profilesIni);
		ControllerAutoSetup.initializeControllerAutoSetup();
	}
}