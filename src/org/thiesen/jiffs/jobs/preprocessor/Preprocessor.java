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

package org.thiesen.jiffs.jobs.preprocessor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.thiesen.jiffs.data.db.dao.StoryDAO;
import org.thiesen.jiffs.data.db.dbo.StoryDBO;
import org.thiesen.jiffs.jobs.Job;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class Preprocessor implements Job {

	private final StoryDAO _storyDAO;

	private final static Set<Object> GERMAN_STOP_WORDS = readStopWords( "de" );
	private final static Set<Object> ENGLISH_STOP_WORDS = readStopWords( "en" );

	static {
		GERMAN_STOP_WORDS.addAll( GermanAnalyzer.getDefaultStopSet() );
	}


	public static void main(final String...args ) {
		final Preprocessor job = new Preprocessor( new StoryDAO() );

		job.execute();
	}

	public Preprocessor( final StoryDAO storyDAO ) {
		_storyDAO = storyDAO;
	}

	private final static ExecutorService EXECUTOR = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );

	@Override
	public void execute() {
		final Iterable<StoryDBO> unprocessed = _storyDAO.findWithoutPreprocesing();
		
		System.out.println("Preprocessing " + Iterables.size( unprocessed ) + " stories");

		for ( final StoryDBO story : unprocessed ) {
			EXECUTOR.submit( new Runnable() {

				@Override
				public void run() {
					preproecessText( story );
				}
			});

		}

		EXECUTOR.shutdown();

		try {
			EXECUTOR.awaitTermination( 1 , TimeUnit.DAYS );
		} catch (InterruptedException e) {
			Thread.interrupted();

		}

	}

	private void preproecessText(StoryDBO story) {
		switch ( story.getLanguage() ) {
		case GERMAN: preprocess( story,  new GermanAnalyzer( Version.LUCENE_30, GERMAN_STOP_WORDS ) ); return;
		default: preprocess( story, new StandardAnalyzer( Version.LUCENE_30, ENGLISH_STOP_WORDS ) ); return;
		}
	}

	private void preprocess(StoryDBO story, Analyzer analyzer) {
		final String cleanedText = new HtmlStripper().stripHtml( story.getFullText() );

		try {
			final TokenStream tokenStream = analyzer.reusableTokenStream( "dummy", new StringReader( cleanedText ) );
			final TermAttribute termAtt = tokenStream.addAttribute( TermAttribute.class );

			final Collection<String> tokens = Sets.newHashSet();
			while ( tokenStream.incrementToken() ) {
				final String token = termAtt.term();

				if ( StringUtils.isNotBlank( token ) ) {
					tokens.add( token );
				}
			}

			final String tokenString = Joiner.on(',').join( tokens ) ;
			
			story.setPreprocessedText( tokenString );

			_storyDAO.update( story );
		} catch ( IOException e ) {
			throw new RuntimeException( "IOException on in memory operation ", e );
		}
	}

	private static Set<Object> readStopWords(String string) {
		try {
			final List<String> lines = Files.readLines( new File( "stopwords-" + string + ".txt" ), Charsets.UTF_8 );

			return Sets.<Object>newHashSet( lines );
		} catch ( IOException e ) {
			throw new RuntimeException( e ); 
		}
	}


}
