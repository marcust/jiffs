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

package org.thiesen.jiffs.jobs.language;

import org.apache.commons.lang3.StringUtils;
import org.thiesen.jiffs.data.db.dao.StoryDAO;
import org.thiesen.jiffs.data.db.dbo.StoryDBO;
import org.thiesen.jiffs.jobs.Job;

import com.google.api.GoogleAPI;
import com.google.api.detect.Detect;
import com.google.api.detect.DetectResult;
import com.google.api.translate.Language;

public class LanguageIdentifyJob implements Job {

	private final StoryDAO _storyDAO;

	public static void main(final String...args ) {
		final LanguageIdentifyJob job = new LanguageIdentifyJob( new StoryDAO() );

		job.execute();
	}

	public LanguageIdentifyJob( final StoryDAO storyDAO ) {
		_storyDAO = storyDAO;
	}


	@Override
	public void execute() {
		final Iterable<StoryDBO> stories = _storyDAO.findWithoutLanguage();
		GoogleAPI.setHttpReferrer("http://www.thiesen.org/");
		
		for ( final StoryDBO  story : stories ) {
			try {

				DetectResult result = Detect.execute( StringUtils.abbreviate( story.getFullText(), 1000 ) );
				
				final Language language = result.getLanguage();
				
				if ( language != null ) {
					story.setLanguage( language );
				}

				_storyDAO.update( story );
				
				Thread.sleep( 100 ); // Google hat ein rate limit, daher muss man etwas langsamer sein


			} catch ( Exception e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	
	}
}