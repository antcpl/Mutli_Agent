package communication.ex04;

import static communication.ex04.Society.COMMUNITY;
import static communication.ex04.Society.GROUP;
import static communication.ex04.Society.ROLECONTROLLER;

import java.util.Random;
import java.util.logging.Level;

import madkit.kernel.Agent;
import madkit.kernel.Madkit;
import madkit.message.IntegerMessage;

public class ControllerAgent extends Agent {
	Random random = new Random();

	@Override
	protected void activate() {

		getLogger().setLevel(Level.FINEST);

		createGroup(COMMUNITY, GROUP);
		requestRole(COMMUNITY, GROUP, ROLECONTROLLER);

		pause(500);
	}

	@Override
	protected void live() {
		int msg_rcvd = 0;
		while (true) {
			IntegerMessage m = (IntegerMessage) waitNextMessage();
			if (m != null) {
				msg_rcvd = m.getContent();
				getLogger().info("Controller Agent received a message " + m.getContent());
				CounterAgent new_agent = new CounterAgent(msg_rcvd);
				launchAgent(new_agent, true);
				getLogger().info("Controller launched a new agent");
				msg_rcvd = 0;
			}
			

		}

	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new Madkit("--launchAgents", CounterAgent.class.getName() + ",true;",
				EmitterAgent.class.getName() + ",true;", ControllerAgent.class.getName() + ",true;"// This one so

		);
	}

}
