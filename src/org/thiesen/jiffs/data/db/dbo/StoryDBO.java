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

import java.net.URI;

import javax.annotation.CheckForNull;

import org.joda.time.DateTime;
import org.thiesen.jiffs.data.enums.StoryState;
import org.thiesen.jiffs.data.types.DBO;

import com.google.api.translate.Language;
import com.mongodb.BasicDBObject;

public class StoryDBO extends BasicDBObject implements DBO {

	private static final long serialVersionUID = 3559937707272202397L;

	public final static String URI_PROPERTY = "storyUri";
	private final static String STATE_PROPERTY = "state";
	private final static String TITLE_PROPERTY = "title";
	private final static String TEXT_PROPERTY = "text";
	private final static String RELEVANCE_PROPERTY = "relevance";
	private final static String PUBLICATION_DATE_PROPERTY = "publicationDate";
	private final static String LINK_PROPERTY = "link";
	private final static String IMAGE_URL_PROPERTY = "imageUrl";
	public final static String LANGUAGE_PROPERTY = "language";
	
	public URI getStoryUri() {
		return URI.create( getString( URI_PROPERTY ) );
	}
	
	public void setStoryUri( final URI uri ) {
		put( URI_PROPERTY , uri.toString() );
	}
	
	public StoryState getState() {
		return StoryState.valueOf( getString( STATE_PROPERTY ) );
	}
	
	public void setState( final StoryState state ) {
		put( STATE_PROPERTY, state.toString() );
	}
	
	public String getTitle() {
		return getString( TITLE_PROPERTY );
	}
	
	public void setTitle( final String title ) {
		put( TITLE_PROPERTY, title );
	}
	
	public String getText() {
		return getString( TEXT_PROPERTY );
	}
	
	public void setText( final String text ) {
		put( TEXT_PROPERTY, text );
	}
	
	public URI getLink() {
		return URI.create( getString( LINK_PROPERTY ) );
	}
	
	public void setLinkUri( final URI link ) {
		put( LINK_PROPERTY , link.toString() );
	}
	
	@CheckForNull
	public URI getImageUrl() {
		if ( containsField( IMAGE_URL_PROPERTY ) ) {
			return URI.create( getString( IMAGE_URL_PROPERTY ) );
		}
		return null;
	}
	
	public void setImageUrl( final URI imageUrl ) {
		put( IMAGE_URL_PROPERTY , imageUrl.toString() );
	}
	
	@CheckForNull
	public Double getRelevance() {
		if ( containsField( RELEVANCE_PROPERTY ) ) {
			return Double.valueOf( getDouble( RELEVANCE_PROPERTY ) );
		} 
		
		return null;
	}
	
	public void setRelevance( final Double relevance ) {
		put( RELEVANCE_PROPERTY, relevance );
	}
	
	public DateTime getPublicationDate() {
		return new DateTime( getLong( PUBLICATION_DATE_PROPERTY ) );
	}
	
	public void setPublicationDate( final DateTime publicationDate ) {
		put( PUBLICATION_DATE_PROPERTY , Long.valueOf( publicationDate.getMillis() ) );
	}
	
	public @CheckForNull Language getLanguage() {
		if ( containsField( LANGUAGE_PROPERTY ) ) {
			return Language.fromString( getString( LANGUAGE_PROPERTY ) );
		}
		return null;
	}
	
	public void setLanguage( final Language language ) {
		put( LANGUAGE_PROPERTY, language.toString() );
	}

	public String getFullText() {
		return getTitle() + " " + getText(); 
	}
	
}
