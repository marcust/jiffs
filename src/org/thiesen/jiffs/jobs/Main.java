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

package org.thiesen.jiffs.jobs;

import org.thiesen.jiffs.data.db.dao.StoryDAO;
import org.thiesen.jiffs.data.db.dao.SubscriptionDAO;
import org.thiesen.jiffs.jobs.fetcher.FetcherJob;
import org.thiesen.jiffs.jobs.language.LanguageIdentifyJob;

public class Main {

	public static void main( final String... args ) {
		final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
		final StoryDAO storyDAO = new StoryDAO();
		
		new FetcherJob( subscriptionDAO, storyDAO ).execute();
		new LanguageIdentifyJob( storyDAO ).execute();
		
	}
	
	
}
