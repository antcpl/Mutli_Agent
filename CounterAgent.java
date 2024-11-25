package communication.ex04;

import static communication.ex04.Society.COMMUNITY;
import static communication.ex04.Society.GROUP;
import static communication.ex04.Society.ROLECOUNTER;
import static communication.ex04.Society.ROLECONTROLLER;

import java.util.Random;
import java.util.logging.Level;

import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import madkit.message.IntegerMessage;

public class CounterAgent extends Agent {
	public int msg_rcvd = 0;
	Random random = new Random();

	public CounterAgent() {
		this.msg_rcvd = 0;
	}

	// attention !!!!! il faut faire deux constructeurs
	public CounterAgent(int msg_rcvd) {
		this.msg_rcvd = msg_rcvd;
	}

	@Override
	protected void activate() {

		getLogger().setLevel(Level.FINEST);

		pause(500);
		// pour éviter que l'agent demande le role avant que la société soit créée

		requestRole(COMMUNITY, GROUP, ROLECOUNTER);

		pause(500);
	}

	@Override
	protected void live() {

		long start_time = System.currentTimeMillis();
		long stop_time = random.nextInt(1000, 10000);

		System.out.println(start_time + stop_time);
		System.out.println(start_time + System.currentTimeMillis());
		System.out.println(System.currentTimeMillis()-start_time);
		System.out.println(stop_time);

		// count the messages received during a random amount of time
		while (start_time + (System.currentTimeMillis() - start_time) < start_time + stop_time) {
			Message m = nextMessage();
			if (m != null) {
				msg_rcvd++;
				getLogger().info("Counter Agent received message " + msg_rcvd);
			}
			pause(1000);
		}

		// send the message received number to a controller
		AgentAddress controller = null;
		while (controller == null) {
			controller = getAgentWithRole(COMMUNITY, GROUP, ROLECONTROLLER);
			pause(1000);
		} // loop until we found a controller agent
		sendMessage(controller, new IntegerMessage(msg_rcvd));
		getLogger().info("Counter Agent transform into emitter agent");
		// attention, il faut utiliser le nom de la classe name
		launchAgent(EmitterAgent.class.getName(), true);

	}

}
