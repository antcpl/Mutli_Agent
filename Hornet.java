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

	//private static final long serialVersionUID = -6999130646300839798L;
	static int border = 20;

	public Hornet() {
		super(true);
	}
	
	@Override
	protected void buzz() {
//	Message m = nextMessage();
//	if (m != null) {
//	    sendReply(m, new ObjectMessage<>(myInformation));
//	}
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
		int bee_position = 0;
		IntegerMessage m = (IntegerMessage) nextMessage();
		if (m != null) {
			bee_position = m.getContent();
			if(bee_position<0.5) {
				sendMessage(m.getSender(),new Message());
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			int acc = beeWorld.getHornetAcceleration().getValue();
			dX += randomFromRange(acc);
			dY += randomFromRange(acc);
		}
	}

}
