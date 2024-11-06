/*
 * Copyright 1997-2012 Fabien Michel, Olivier Gutknecht, Jacques Ferber
 * 
 * This file is part of MaDKit_Demos.
 * 
 * MaDKit_Demos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MaDKit_Demos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MaDKit_Demos. If not, see <http://www.gnu.org/licenses/>.
 */
package madkit.bees;

import static madkit.bees.BeeLauncher.COMMUNITY;
import static madkit.bees.BeeLauncher.QUEEN_ROLE;
import static madkit.bees.BeeLauncher.HORNET_ROLE;
import static madkit.bees.BeeLauncher.SIMU_GROUP;

import java.awt.Point;
import java.util.List;
import java.util.logging.Level;

import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import madkit.message.IntegerMessage;
import madkit.message.ObjectMessage;
import madkit.kernel.Madkit;
import madkit.kernel.Agent;

/**
 * @version 2.3
 * @author Fabien Michel, Olivier Gutknecht
 */
public class Bee extends AbstractBee {

	private static final long serialVersionUID = -2393301912353816186L;
	BeeInformation leaderInfo = null;
	AgentAddress leader = null;
	AgentAddress hornet = null;
	BeeInformation hornetInfo = null;

	@Override
	public void activate() {
		requestRole("buzz", SIMU_GROUP, "bee", null);
		requestRole("buzz", SIMU_GROUP, "follower", null);
	}

	public Bee(boolean testHornet) {
		super(testHornet);
	}
	
	/** The "do it" method called by the activator */
	@Override
	public void buzz() {
		updateLeader();
		super.buzz();
	}

	private void updateLeader() {
		ObjectMessage<BeeInformation> m = (ObjectMessage<BeeInformation>) nextMessage();
		// if a bee get no message, by default it has no leader
		if (m == null) {
			return;
		}

		// if a bee get a message and this is a message from its leader then the leader
		// is quiting
		// so we reset the leader infos
		
		//fonctionne pas que des bees ou des abstractagent
//		System.out.println(m.getSender().getRole());
//		System.out.println(m.getSender().getClass().getName());
//		System.out.println(m.getSender().getClass().getTypeName());
//		System.out.println(m.getSender().toString());
		//System.out.println(m.getContent().getHornetBool());

		if (m.getSender().equals(leader)) {// leader quitting
			leader = null;
			leaderInfo = null;
		}
		else if(m.getContent().getHornetBool()) {
			newHornetInfos(m);
		}
		// if it gets a message and this is not from its leader
		else {
			// if the bee has no leader already set
			if (leader == null)
				// if will follow a new leader
				followNewLeader(m);
			else {
				// if the bee already has a leader set
				List<AgentAddress> queens = getAgentsWithRole(COMMUNITY, SIMU_GROUP, QUEEN_ROLE);
				if (queens != null && generator.nextDouble() < (1.0 / queens.size())) {// change leader randomly
					followNewLeader(m);
				}
			}
		}
	}

	/**
	 * @param leaderMessage
	 */
	private void followNewLeader(ObjectMessage<BeeInformation> leaderMessage) {
		leader = leaderMessage.getSender();
		leaderInfo = leaderMessage.getContent();
		myInformation.setBeeColor(leaderInfo.getBeeColor());
	}

	private void newHornetInfos(ObjectMessage<BeeInformation> hornetMessage) {
		hornet = hornetMessage.getSender();
		hornetInfo = hornetMessage.getContent();
	}

	@Override
	protected void computeNewVelocities() {
		final Point location = myInformation.getCurrentPosition();
		// distances from bee to queen
		int dtx;
		int dty;

		if (hornetInfo != null) {
			final Point hornetLocation = hornetInfo.getCurrentPosition();
			dtx = hornetLocation.x - location.x;
			dty = hornetLocation.y - location.y;

			
//			System.out.println(beeWorld.getWidth()/10);
//			System.out.println(beeWorld.getHeight()/10);
//			
//			System.out.println(Math.abs(dtx));
//			System.out.println(Math.abs(dty));
//			
			
			if (Math.abs(dtx) < (beeWorld.getWidth()/10) && Math.abs(dty) < (beeWorld.getHeight()/10)) {
				dX = 0;
				dY = 0;
				//System.out.println("coucou");
				// send a message to the hornet
//				sendMessage(hornet, new IntegerMessage(dtx + dty));
//				ObjectMessage<BeeInformation> m = (ObjectMessage<BeeInformation>) nextMessage();
//				while(m==null) {
//					m = (ObjectMessage<BeeInformation>) nextMessage();
//				}
//				if(m.getContent().getHornetBool() && m) {
//										
//				}
//				else {
//					
//				}
//				if (m != null) {
//					if (m.getSender() == hornet) {
//						getLogger().info(() -> "The bee " + this + "has been killed");
//						killAgent(this);
//					}
//				}
			} else {

				if (leaderInfo != null) {
					final Point leaderLocation = leaderInfo.getCurrentPosition();
					dtx = leaderLocation.x - location.x;
					dty = leaderLocation.y - location.y;
				} else {
					dtx = generator.nextInt(5);
					dty = generator.nextInt(5);
					if (generator.nextBoolean()) {
						dtx = -dtx;
						dty = -dty;
					}
				}

				int acc = 0;
				if (beeWorld != null) {
					acc = beeWorld.getBeeAcceleration().getValue();
				}
				int dist = Math.abs(dtx) + Math.abs(dty);
				if (dist == 0)
					dist = 1; // avoid dividing by zero
				// the randomFromRange adds some extra jitter to prevent the bees from flying in
				// formation
				dX += ((dtx * acc) / dist) + randomFromRange(2);
				dY += ((dty * acc) / dist) + randomFromRange(2);
			}
		} else {

			if (leaderInfo != null) {
				final Point leaderLocation = leaderInfo.getCurrentPosition();
				dtx = leaderLocation.x - location.x;
				dty = leaderLocation.y - location.y;
			} else {
				dtx = generator.nextInt(5);
				dty = generator.nextInt(5);
				if (generator.nextBoolean()) {
					dtx = -dtx;
					dty = -dty;
				}
			}
			int acc = 0;
			if (beeWorld != null) {
				acc = beeWorld.getBeeAcceleration().getValue();
			}
			int dist = Math.abs(dtx) + Math.abs(dty);
			if (dist == 0)
				dist = 1; // avoid dividing by zero
			// the randomFromRange adds some extra jitter to prevent the bees from flying in
			// formation
			dX += ((dtx * acc) / dist) + randomFromRange(2);
			dY += ((dty * acc) / dist) + randomFromRange(2);
		}

	}

	@Override
	protected int getMaxVelocity() {
		if (beeWorld != null) {
			return beeWorld.getBeeVelocity().getValue();
		}
		return 0;
	}
}
