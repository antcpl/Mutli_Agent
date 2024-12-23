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

import static madkit.bees.BeeLauncher.BEE_ROLE;
import static madkit.bees.BeeLauncher.COMMUNITY;
import static madkit.bees.BeeLauncher.FOLLOWER_ROLE;
import static madkit.bees.BeeLauncher.SIMU_GROUP;
import static madkit.bees.BeeLauncher.HORNET_ROLE;
import static madkit.bees.BeeLauncher.QUEEN_ROLE;

import java.awt.Point;

import madkit.kernel.Message;
import madkit.message.IntegerMessage;
import madkit.message.ObjectMessage;

/**
 * The leader of a group.
 * 
 * @version 2.0.0.3
 * @author Fabien Michel, Olivier Gutknecht
 */
public class Hornet extends AbstractBee {

	// private static final long serialVersionUID = -6999130646300839798L;
	static int border = 20;
	public int beesInRange = 0;
	public int stopValue = 0;
	public long startTime = 0;


	public Hornet(boolean testHornet) {
		super(testHornet);
	}

	@Override
	protected void buzz() {
		super.buzz();


		if (beeWorld != null) {
			// check to see if the queen hits the edge
			final Point location = myInformation.getCurrentPosition();
			if (location.x < border || location.x > (beeWorld.getWidth() - border)) {
				dX = -dX;
				location.x += (dX);
			}
			if (location.y < border || location.y > (beeWorld.getHeight() - border)) {
				dY = -dY;
				location.y += (dY);
			}
		}
		IntegerMessage m = (IntegerMessage) nextMessage();
		if (m != null) {
			System.out.println("[H] beesInRange value =" + beesInRange);
			System.out.println("" + m.getContent());

			if (m.getContent().equals(1)) {
				//counter for number of bees in the range for potential kill
				beesInRange++;
				//handle the stop for the selected bee
				if(stopValue==0) {
					startTime=System.currentTimeMillis();
					System.out.println("[H] I have selected the bee which I want to kill");
					sendMessage(m.getSender(), new ObjectMessage<>(null));
					stopValue=1;
				}

			} else if (m.getContent().equals(0)) {
				// a bee is quitting the range
				if (beesInRange > 0) {
					beesInRange--;
//					System.out.println("Une abeille sort de ma range");
				} else {
					System.out.println("Erreur beesInRange negatif");
				}
			} else if (m.getContent().equals(3)) {
				//a bee has been killed by the simulation so we update the counter
				if (beesInRange > 0) {
					beesInRange--;
//					System.out.println("Mort d'une abeille elle sort de ma range");
				}
			}
//			else if(m.getContent().equals(4)) {
//				//The selected bee has been killed by the hornet
//				stopValue=0;
//				System.out.println("[H] I killed the bee, begin to move again");
//				//as we just kill the bee without make it send a message, we have to update the counter
//				beesInRange--;
//			}
			//if too many bees are in the range, the hornet die
			if (beesInRange > 15) {
				killAgent(this);
			}
		}
	}

	@Override
	protected void activate() {
		requestRole(COMMUNITY, SIMU_GROUP, HORNET_ROLE, null);
		// !!! we have to add this second role just for the viewer
		requestRole(COMMUNITY, SIMU_GROUP, BEE_ROLE, null);
		broadcastMessage(COMMUNITY, SIMU_GROUP, FOLLOWER_ROLE, new ObjectMessage<>(myInformation));
	}

	@Override
	protected void end() {
		broadcastMessage(COMMUNITY, SIMU_GROUP, FOLLOWER_ROLE, new ObjectMessage<>(myInformation));
	}

	@Override
	protected int getMaxVelocity() {
		if (beeWorld != null) {
			return beeWorld.getHornetVelocity().getValue();
		}
		return 0;
	}

	@Override
	protected void computeNewVelocities() {
		if (beeWorld != null) {
			if(stopValue==1 && (System.currentTimeMillis()<startTime+500)) {
				//if the hornet is killing the bee, it doesn't move
				System.out.println("[H] Killing the bee, stop moving");
				dX=0;
				dY=0;
			}
			else if(stopValue==1 && System.currentTimeMillis()>=startTime+500) {
				stopValue=0;
				beesInRange--;
				System.out.println("[H] I killed the bee, begin to move again");
			}
			else {
				//normal moving for the hornet
				int acc = beeWorld.getHornetAcceleration().getValue();
				dX += randomFromRange(acc);
				dY += randomFromRange(acc);
			}
		}
	}

}
