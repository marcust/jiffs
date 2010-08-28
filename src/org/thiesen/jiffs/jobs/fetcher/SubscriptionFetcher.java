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

package org.thiesen.jiffs.jobs.fetcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.thiesen.jiffs.data.db.dao.StoryDAO;
import org.thiesen.jiffs.data.db.dao.SubscriptionDAO;
import org.thiesen.jiffs.data.db.dbo.StoryDBO;
import org.thiesen.jiffs.data.db.dbo.SubscriptionDBO;
import org.thiesen.jiffs.data.enums.StoryState;
import org.thiesen.jiffs.data.enums.SubscriptionState;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;

public class SubscriptionFetcher implements Runnable {

	private final static FeedFetcherCache FEED_FETCHER_CACHE = HashMapFeedInfoCache.getInstance();

	private static final Predicate<SyndEnclosure> IMAGE_MIME_TYPE_PREDICATE = new Predicate<SyndEnclosure>() {

		@Override
		public boolean apply(SyndEnclosure enclosure) {
			final String type = enclosure.getType();
			return StringUtils.isNotBlank(type) && type.startsWith("image");
		}
	};

	private static final long MIN_CHECK_TIME_MILLIS = TimeUnit.MINUTES.convert( 15, TimeUnit.MILLISECONDS );
	private static final long MAX_CHECK_TIME_MILLIS = TimeUnit.DAYS.convert( 1, TimeUnit.MILLISECONDS );
	private static final long CHECK_TIME_STEPPING_MILLIS = TimeUnit.MINUTES.convert( 5, TimeUnit.MILLISECONDS );

	private StoryDAO _storyDAO;
	private SubscriptionDAO _subscriptionDAO;
	private SubscriptionDBO _subscription;

	public SubscriptionFetcher(StoryDAO storyDAO, SubscriptionDAO subscriptionDAO, SubscriptionDBO subscription) {
		_storyDAO = storyDAO;
		_subscriptionDAO = subscriptionDAO;
		_subscription = subscription;
	}

	@Override
	public void run() {
		final FeedFetcher feedFetcher = new HttpURLFeedFetcher( FEED_FETCHER_CACHE );

		try {
			final URI sourceUri = _subscription.getXmlUrl();
			final SyndFeed feed = feedFetcher.retrieveFeed( sourceUri.toURL() );

			@SuppressWarnings("unchecked")
			final List<SyndEntry> entries = feed.getEntries();

			int newStoryCount = 0;
			for ( final SyndEntry entry : entries ) {
				if ( insertNewUniqueStory( sourceUri, entry ) ) {
					newStoryCount++;
				}
			}

			_subscription.setNextCheck( computeNextCheck( newStoryCount, _subscription.getLastCheck() ) );
			_subscription.setLastCheck( new DateTime() );

			_subscriptionDAO.update( _subscription );

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FetcherException e) {
			if ( e.getResponseCode() == 404 ) {
				_subscription.setState( SubscriptionState.PERMANENT_FAIL );
			}
			e.printStackTrace();
		}

		_subscriptionDAO.update( _subscription );


	}



	private boolean insertNewUniqueStory(final URI sourceUri, final SyndEntry entry) {
		final URI parsedUri = findValidEntryUri(entry);

		if ( parsedUri == null ) {
			return false;
		}

		final StoryDBO byUri = _storyDAO.findByUri( parsedUri );

		if ( byUri != null ) {
			return false;
		}

		final StoryDBO newDBO = new StoryDBO();

		newDBO.setSourceUri( sourceUri );
		newDBO.setStoryUri( parsedUri );
		newDBO.setPublicationDate( new DateTime( entry.getPublishedDate() ) );
		newDBO.setState( StoryState.NEW );
		newDBO.setTitle( entry.getTitle() );
		if ( entry.getDescription() != null ) {
			newDBO.setText( entry.getDescription().getValue() );
		} else {
			newDBO.setText( "" );
		}
		try {
			newDBO.setLinkUri( URI.create( StringUtils.trim( entry.getLink() ) ) );
		} catch ( IllegalArgumentException e ) {
			System.err.println("Invalid Link in entry " + entry.getLink() );
			return false;
		}

		@SuppressWarnings("unchecked")
		final List<SyndEnclosure> enclosures = entry.getEnclosures();

		try {
			final SyndEnclosure imageEnclosure = Iterables.find( enclosures, IMAGE_MIME_TYPE_PREDICATE );

			newDBO.setImageUrl( URI.create( imageEnclosure.getUrl() ) );	
		} catch ( NoSuchElementException e ) {
			// no image.
		}

		_storyDAO.insert( newDBO );

		return true;
	}

	private URI findValidEntryUri(final SyndEntry entry) {
		final String uri = StringUtils.trim( entry.getUri() );
		URI parsedUri = null;
		try {
			try {
				parsedUri = URI.create( uri );
			} catch ( IllegalArgumentException e ) {
				try {
					parsedUri = URI.create( StringUtils.trim( entry.getLink() ) );

				} catch ( IllegalArgumentException e2 ) {
					return null;
				}
			}
		} catch ( NullPointerException e ) {
			// there are NPEs in the URI.create function :/
			return null;
		}
		return parsedUri;
	}

	private DateTime computeNextCheck(int newStoryCount, DateTime lastCheckTime ) {
		if ( lastCheckTime == null ) {
			return new DateTime().plusMillis( (int)MIN_CHECK_TIME_MILLIS );
		}

		final Duration duration = new Duration( lastCheckTime, new DateTime() );

		if ( newStoryCount == 0 ) {
			return increase( duration );
		} 
		if ( newStoryCount < 5 ) {
			return new DateTime().plus( duration );
		}

		return decrease( duration );
	}

	private DateTime decrease(Duration duration) {
		if ( duration.getMillis() <= MIN_CHECK_TIME_MILLIS ) {
			return new DateTime().plus( MIN_CHECK_TIME_MILLIS );
		}

		return new DateTime().plus( duration.minus( CHECK_TIME_STEPPING_MILLIS ) );
	}

	private DateTime increase(Duration duration) {
		if ( duration.getMillis() >= MAX_CHECK_TIME_MILLIS ) {
			return new DateTime().plus( MAX_CHECK_TIME_MILLIS );
		}

		return new DateTime().plus( duration.plus( CHECK_TIME_STEPPING_MILLIS ) );
	}

}
