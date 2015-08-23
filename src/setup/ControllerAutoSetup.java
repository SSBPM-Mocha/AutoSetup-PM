package setup;

import java.util.HashMap;
import java.util.Map;

public class ControllerAutoSetup {
	private static Map<String, ControllerButton> buttonMap = new HashMap<String, ControllerButton>();

	public static void initializeControllerAutoSetup() {
		buttonMap.put("L", new ControllerButton(3, 4));
		buttonMap.put("R", new ControllerButton(3, 4));
		buttonMap.put("DU", new ControllerButton(5, 2));
		buttonMap.put("DS", new ControllerButton(6, 1));
		buttonMap.put("DD", new ControllerButton(7, 0));
		buttonMap.put("Z", new ControllerButton(3, 4));
		buttonMap.put("X", new ControllerButton(2, 5));
		buttonMap.put("Y", new ControllerButton(2, 5));
		buttonMap.put("A", new ControllerButton(0, 7));
		buttonMap.put("B", new ControllerButton(1, 6));
		buttonMap.put("C", new ControllerButton(5, 0));
	}

	/**
	 * Setup for controller from the input name screen
	 * 
	 * @throws InterruptedException
	 */
	public static void setupController(boolean tapJump, String input)
			throws InterruptedException {

		System.out.println("[INFO]: Setting up controller...");
		// If default settings, just return to input name screen
		if (tapJump && input == null) {
			AutoSetupM.type("B", 1300);
			return;
		} else {
			AutoSetupM.type("A", 600 + AutoSetupM.loadingScreenLatency);//1350
			if (AutoSetupM.waitForButtonConfig) {
				Thread.sleep(3000);
			}
			System.out.println("Now disable tap jump");
		}

		// Turn off Tap Jump
		if (!tapJump) {
			AutoSetupM.type("LEFT", AutoSetupM.controllerConfigScreenLatency);
			AutoSetupM.type("A", AutoSetupM.controllerConfigScreenLatency);
			if (input == null) {
				AutoSetupM.type("UP", 150);
				AutoSetupM.type("A", 200);

				// Close "Configuration Saved" pop-up
				AutoSetupM.type("A", 140);

				// Leaves controller select (returns to input name screen)
				AutoSetupM.type("B", 1950);
				return;
			} else {
				AutoSetupM.type("RIGHT", AutoSetupM.controllerConfigScreenLatency);
			}
		}

		String[] inputSettings = input.split(" ");

		// L Button
		buttonMapHelper("L", inputSettings);
		AutoSetupM.type("DOWN", AutoSetupM.controllerConfigScreenLatency);

		// D-Pad Up
		buttonMapHelper("DU", inputSettings);
		AutoSetupM.type("DOWN", AutoSetupM.controllerConfigScreenLatency);

		// D-Pad Side
		buttonMapHelper("DS", inputSettings);
		AutoSetupM.type("DOWN", AutoSetupM.controllerConfigScreenLatency);

		// D-Pad Down
		buttonMapHelper("DD", inputSettings);
		AutoSetupM.type("RIGHT", AutoSetupM.controllerConfigScreenLatency);

		// C-Stick
		buttonMapHelper("C", inputSettings);
		AutoSetupM.type("UP", AutoSetupM.controllerConfigScreenLatency);

		// B Button
		buttonMapHelper("B", inputSettings);
		AutoSetupM.type("UP", AutoSetupM.controllerConfigScreenLatency);

		// A Button
		buttonMapHelper("A", inputSettings);
		AutoSetupM.type("UP", AutoSetupM.controllerConfigScreenLatency);

		// X Button
		buttonMapHelper("X", inputSettings);
		AutoSetupM.type("UP", AutoSetupM.controllerConfigScreenLatency);

		// Y Button
		buttonMapHelper("Y", inputSettings);
		AutoSetupM.type("UP", AutoSetupM.controllerConfigScreenLatency);

		// Z Button
		buttonMapHelper("Z", inputSettings);
		AutoSetupM.type("UP", AutoSetupM.controllerConfigScreenLatency);

		// R Button
		buttonMapHelper("R", inputSettings);
		AutoSetupM.type("UP", AutoSetupM.controllerConfigScreenLatency);
		AutoSetupM.type("RIGHT", AutoSetupM.controllerConfigScreenLatency);
		AutoSetupM.type("A", 200 + AutoSetupM.controllerConfigScreenLatency);

		// Close "Configuration Saved" pop-up
		AutoSetupM.type("A", 140);

		// Leaves controller select (returns to input name screen)
		System.out.println("[INFO]: Controller configured.");
		AutoSetupM.type("B", 2920);
	}

	public static void buttonMapHelper(String key, String[] inputSettings)
			throws InterruptedException {
		for (String s : inputSettings) {
			if (s.contains(key)) {

				// Error handling for improper values (too much/little text)
				if (s.length() == key.length()
						|| (s.length() > key.length() + 2)) {
					System.out.println("[ERROR]: Improper value specified for "
							+ key + ". " + s);
					return;
				}

				int i = Integer
						.parseInt(Character.toString(s.charAt(s.length() - 1)));
				boolean success = false;

				// Handle negative and positive. Works with no '+' sign as well
				if (s.charAt(s.length() - 2) == '-') {
					if (i <= buttonMap.get(key).lowerLimit) {
						AutoSetupM.type("A", AutoSetupM.controllerConfigScreenLatency);
						for (int j = 0; j < i; j++) {
							AutoSetupM.type("LEFT", AutoSetupM.controllerConfigScreenLatency);
						}
						success = true;
					} else {
						System.out
								.println("[ERROR]: Value is out of range for "
										+ key + ". At -" + i);
					}
				} else {
					if (i <= buttonMap.get(key).upperLimit) {
						AutoSetupM.type("A", AutoSetupM.controllerConfigScreenLatency);
						for (int j = 0; j < i; j++) {
							AutoSetupM.type("RIGHT", AutoSetupM.controllerConfigScreenLatency);
						}
						success = true;
					} else {
						System.out
								.println("[ERROR]: Value is out of range for "
										+ key + ". At " + i);
					}
				}

				if (success) {
					AutoSetupM.type("A", AutoSetupM.controllerConfigScreenLatency);
				}
				break;
			}
		}
	}
}
