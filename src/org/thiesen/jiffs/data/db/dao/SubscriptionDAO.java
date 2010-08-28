/*
 * (c) Copyright 2010 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of jiffs.
 *
 *  jiffs is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  jiffs is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jiffs.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.thiesen.jiffs.data.db.dao;

import org.joda.time.DateTime;
import org.thiesen.jiffs.data.db.dao.common.AbstractDAO;
import org.thiesen.jiffs.data.db.dbo.SubscriptionDBO;
import org.thiesen.jiffs.data.enums.SubscriptionState;
import org.thiesen.jiffs.data.types.DAO;

import com.mongodb.BasicDBObject;

public class SubscriptionDAO extends AbstractDAO<SubscriptionDBO> implements DAO {

	public SubscriptionDAO() {
		super( SubscriptionDBO.class );
	}

	public Iterable<SubscriptionDBO> findNextCheckBefore(DateTime time) {
		final BasicDBObject query = new BasicDBObject();

		query.put( SubscriptionDBO.NEXT_CHECK, new BasicDBObject("$lt", Long.valueOf( time.getMillis() ) ) );
		query.put( SubscriptionDBO.STATE_PROPERTY, SubscriptionState.SUBSCRIBED.toString() );

		
		return find( query );
	}





}
