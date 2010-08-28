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

package org.thiesen.jiffs.jobs.fullpage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.thiesen.jiffs.data.db.dao.StoryDAO;
import org.thiesen.jiffs.data.db.dbo.StoryDBO;
import org.thiesen.jiffs.jobs.Job;

public class FullPageLoader implements Job {

	public static void main(final String...args ) {
		final FullPageLoader job = new FullPageLoader( new StoryDAO() );

		job.execute();
	}

	private final StoryDAO _storyDAO;

	public FullPageLoader( final StoryDAO storyDAO ) {
		_storyDAO = storyDAO;
	}

	private final static ExecutorService EXECUTOR = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );

	@Override
	public void execute() {
		final Iterable<StoryDBO> stories = _storyDAO.findWithoutFullPage();
		
		for ( final StoryDBO story : stories ) {
			EXECUTOR.execute( new PageLoader( _storyDAO, story ) );
		}

		EXECUTOR.shutdown();

		try {
			EXECUTOR.awaitTermination( 1 , TimeUnit.DAYS );
		} catch (InterruptedException e) {
			Thread.interrupted();

		}

	}

}
