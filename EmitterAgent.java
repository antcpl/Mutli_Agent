package communication.ex04;

import static communication.ex04.Society.COMMUNITY;

import madkit.kernel.AbstractAgent;
import madkit.kernel.AbstractAgent.ReturnCode;
import static communication.ex04.Society.GROUP;
import static communication.ex04.Society.ROLEEMITTER;
import static communication.ex04.Society.ROLECOUNTER;

import java.util.logging.Level;

import java.util.Random;

import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;


public class EmitterAgent extends Agent {
	Random random = new Random();

	
	@Override
	protected void activate() {

		getLogger().setLevel(Level.FINEST);
		
		//to let the group being created
		pause(500);

		//request the role in the society created by the Controller
		requestRole(COMMUNITY, GROUP, ROLEEMITTER);

		pause(500);
	}

	@Override
	protected void live() {
		int nb_messages = random.nextInt(10);

		AgentAddress other = null;
		while (other == null) {
			other = getAgentWithRole(COMMUNITY, GROUP, ROLECOUNTER);
			pause(50);
		} // loop until we found a random counter agent

		for (int i = 0; i < nb_messages; i++) {
			//we need to test if the address we get is still valid 
			//sometimes, the counter address we have is not valid as the counter has disapeared
			//!!!!! add the test here with AbstractAgent.ReturnCode
			sendMessage(other, new Message());
			pause(random.nextInt(10) * 1000);
		}

	}
}
