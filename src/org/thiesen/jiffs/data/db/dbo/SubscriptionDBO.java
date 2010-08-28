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
import org.thiesen.jiffs.data.enums.SubscriptionState;
import org.thiesen.jiffs.data.types.DBO;

import com.mongodb.BasicDBObject;

public class SubscriptionDBO extends BasicDBObject implements DBO {

	private static final long serialVersionUID = -9157387866082380378L;

	private final static String TITLE_PROPERTY = "title";
	private final static String HTML_URL_PROPERTY = "htmlUrl";
	private final static String XML_URL_PROPERTY = "xmlUrl";
	public final static String STATE_PROPERTY = "state";
	public final static String NEXT_CHECK = "nextCheck";
	public final static String LAST_CHECK = "lastCheck";
	
	
	public SubscriptionState getState() {
		return SubscriptionState .valueOf( getString( STATE_PROPERTY ) );
	}
	
	public void setState( final SubscriptionState  state ) {
		put( STATE_PROPERTY, state.toString() );
	}
	
	public String getTitle() {
		return getString( TITLE_PROPERTY );
	}
	
	public void setTitle( final String title ) {
		put( TITLE_PROPERTY, title );
	}
	
	public URI getHtmlUrl() {
		return URI.create( getString( HTML_URL_PROPERTY ) );
	}
	
	public void setHtmlUrl( final URI uri ) {
		put( HTML_URL_PROPERTY , uri.toString() );
	}
	
	public URI getXmlUrl() {
		return URI.create( getString( XML_URL_PROPERTY ) );
	}
	
	public void setXmlUrl( final URI uri ) {
		put( XML_URL_PROPERTY , uri.toString() );
	}
	
	public DateTime getNextCheck() {
		return new DateTime( getLong( NEXT_CHECK ) );
	}
	
	public void setNextCheck( final DateTime nextCheck ) {
		put( NEXT_CHECK , Long.valueOf( nextCheck.getMillis() ) );
	}
	
	public @CheckForNull DateTime getLastCheck() {
		if ( containsField( LAST_CHECK ) ) {
			return new DateTime( getLong( LAST_CHECK ) );
		} 
		return null;
	}
	
	public void setLastCheck( final DateTime nextCheck ) {
		put( LAST_CHECK , Long.valueOf( nextCheck.getMillis() ) );
	}
}
