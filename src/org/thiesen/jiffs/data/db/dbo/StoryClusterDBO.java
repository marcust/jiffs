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

package org.thiesen.jiffs.data.db.dbo;

import java.util.List;

import org.joda.time.DateTime;
import org.thiesen.jiffs.data.types.DBO;

import com.mongodb.BasicDBObject;

public class StoryClusterDBO extends BasicDBObject implements DBO {

	private static final long serialVersionUID = -9124531240267960158L;

	public final static String CREATED_AT_PROPERTY = "createdAt";
	public final static String STORY_URIS_PROPERTY = "storyUris";
	
	
	public DateTime getCreatedAt() {
		return new DateTime( getLong( CREATED_AT_PROPERTY ) );
	}
	
	public void setCreatedAt( final DateTime publicationDate ) {
		put( CREATED_AT_PROPERTY , Long.valueOf( publicationDate.getMillis() ) );
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getStoryUris() {
		return (List<String>) get( STORY_URIS_PROPERTY );
	}
	
	public void setStoryUris( final List<String> storyUris ) {
		put( STORY_URIS_PROPERTY, storyUris );
	}
	
}
