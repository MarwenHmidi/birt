/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.model.api.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class to handle URI.
 */
public class URIUtil
{

	/**
	 * File schema.
	 */

	public static final String FILE_SCHEMA = "file"; //$NON-NLS-1$

	/**
	 * URL with JAR protocol.
	 */

	public static final String JAR_SCHEMA = "jar"; //$NON-NLS-1$

	/**
	 * URL with JAR protocol.
	 */

	public static final String HTTP_SCHEMA = "http"; //$NON-NLS-1$

	/**
	 * URL with JAR protocol.
	 */

	public static final String FTP_SCHEMA = "ftp"; //$NON-NLS-1$

	/**
	 * URL with bundle resource protocol.
	 */

	public static final String BUNDLE_RESOURCE_SCHEMA = "bundle"; //$NON-NLS-1$

	/**
	 * File with jar extention name.
	 */

	public static final String JAR_EXTENTION = ".jar"; //$NON-NLS-1$

	/**
	 * The defautl separator for url schema.
	 */

	private static final String URL_SEPARATOR = "/"; //$NON-NLS-1$

	/**
	 * Checks <code>uri</code> is file path. If <code>uri</code> is an
	 * absolute uri and refers to a file, removes "file://" and returns the file
	 * path. If <code>uri</code> is relative uri and refers to a file, returns
	 * the <code>uri</code>. For other cases, returns null.
	 * <p>
	 * For examples, following uri are supported:
	 * <ul>
	 * <li>file://C:/disk/test/data.file
	 * <li>/C:/disk/test/data.file
	 * <li>/usr/local/disk/test/data.file
	 * <li>C:\\disk\\test/data.file
	 * <li>C:/disk/test/data.file
	 * <li>./test/data.file
	 * </ul>
	 * 
	 * @param uri
	 *            the input uri
	 * @return the file path if <code>uri</code> refers to a file. Otherwise
	 *         null.
	 */

	public static String getLocalPath( String uri )
	{
		if ( uri == null )
			return null;

		URI objURI = null;

		try
		{
			objURI = new URI( uri );
		}
		catch ( URISyntaxException e )
		{
			return getLocalFileOfFailedURI( uri );
		}

		if ( objURI.getScheme( ) == null )
		{
			if ( isFileProtocol( uri ) )
				return uri;
		}
		else if ( objURI.getScheme( ).equalsIgnoreCase( FILE_SCHEMA ) )
		{
			return objURI.getSchemeSpecificPart( );
		}
		else
		{
			// this is for files on the windows platforms.

			if ( objURI.getScheme( ).length( ) == 1
					|| objURI.getScheme( ).equalsIgnoreCase( JAR_SCHEMA ) )
			{
				return uri;
			}

		}

		return null;
	}

	/**
	 * Checks whether <code>filePath</code> is a valid file on the disk.
	 * <code>filePath</code> can follow these scheme.
	 * <ul>
	 * <li>./../hello/
	 * <li>C:\\hello\..\
	 * <li>/C:/../hello/.
	 * </ul>
	 * 
	 * @param filePath
	 *            the input filePath
	 * @return true if filePath exists on the disk. Otherwise false.
	 */

	private static boolean isFileProtocol( String filePath )
	{
		try
		{
			URL fileUrl = new URL( filePath );
			if ( FILE_SCHEMA.equalsIgnoreCase( fileUrl.getProtocol( ) ) )
				return true;

			return false;
		}
		catch ( MalformedURLException e )
		{
			// ignore the error since this string is not in URL format
		}
		File file = new File( filePath );
		if ( file.toURI( ).getScheme( ).equalsIgnoreCase( FILE_SCHEMA ) )
		{
			return true;
		}
		return false;
	}

	/**
	 * Checks whether <code>filePath</code> is a file protocol if it is not a
	 * invalid URI.
	 * <p>
	 * A invalid URI contains excluded US-ASCII characters:
	 * <ul>
	 * <li>contro = <US-ASCII coded characters 00-1F and 7F hexadecimal>
	 * <li>space = <US-ASCII coded character 20 hexadecimal>
	 * <li>delims="<" | ">" | "#" | "%" | <">
	 * <li>unwise="{" | "}" | "|" | "\" | "^" | "[" | "]" | "`"
	 * </ul>
	 * Details are described at the hyperlink:
	 * http://www.ietf.org/rfc/rfc2396.txt.
	 * 
	 * @param uri
	 *            the input uri
	 * @return the file path if <code>uri</code> refers to a file. Otherwise
	 *         null.
	 */

	private static String getLocalFileOfFailedURI( String uri )
	{
		URL objURI = null;
		try
		{
			objURI = new URL( uri );

			if ( objURI.getProtocol( ).equalsIgnoreCase( FILE_SCHEMA ) )
			{
				return objURI.getAuthority( ) == null
						? objURI.getPath( )
						: objURI.getAuthority( ) + objURI.getPath( );
			}
			else if ( objURI.getProtocol( ).equalsIgnoreCase( JAR_SCHEMA ) )
				return uri;
			else
				return null;
		}
		catch ( MalformedURLException e )
		{
			File file = new File( uri );

			if ( uri.indexOf( JAR_EXTENTION ) > -1 )
				return JAR_SCHEMA
						+ ":" + FILE_SCHEMA + ":" + file.getAbsolutePath( ); //$NON-NLS-1$ //$NON-NLS-2$

			if ( uri.startsWith( FILE_SCHEMA ) )
				return file.toURI( ).getSchemeSpecificPart( );

			return uri;
		}
	}

	/**
	 * Converts a filename to a valid URL string. The filename can include
	 * directory information, either relative or absolute directory.
	 * 
	 * @param filePath
	 *            the file name
	 * @return a valid URL String
	 */

	public static String convertFileNameToURLString( String filePath )
	{
		StringBuffer buffer = new StringBuffer( );
		String path = filePath;

		// convert non-URL style file separators

		if ( File.separatorChar != '/' )
			path = path.replace( File.separatorChar, '/' );

		// copy, converting URL special characters as we go

		for ( int i = 0; i < path.length( ); i++ )
		{
			char c = path.charAt( i );
			if ( c < 0x1F || c == 0x7f )
				buffer.append( "%" + Character.toString( c ) ); //$NON-NLS-1$
			else if ( c == '#' )
				buffer.append( "%23" ); //$NON-NLS-1$
			else if ( c == '%' )
				buffer.append( "%25" ); //$NON-NLS-1$
			else if ( c == '<' )
				buffer.append( "%3C" ); //$NON-NLS-1$
			else if ( c == '>' )
				buffer.append( "%3E" ); //$NON-NLS-1$
			else if ( c == '"' )
				buffer.append( "%22" ); //$NON-NLS-1$			
			else
				buffer.append( c );
		}

		// return URL

		return buffer.toString( );
	}

	/**
	 * Converts a filename to a valid URL. The filename can include directory
	 * information, either relative or absolute directory. And the file should
	 * be on the local disk.
	 * 
	 * @param filePath
	 *            the file name
	 * @return a valid URL
	 */

	public static URL getDirectory( String filePath )
	{
		if ( filePath == null )
			return null;

		URL url = null;

		try
		{
			url = new URL( convertFileNameToURLString( filePath ) );
		}
		catch ( MalformedURLException e )
		{
			if ( filePath.indexOf( JAR_EXTENTION ) > -1 )
				// try jar format
				url = getJarDirectory( filePath );
			else
				// follows the file protocol
				url = getFileDirectory( filePath );

			return url;
		}

		if ( FILE_SCHEMA.equalsIgnoreCase( url.getProtocol( ) ) )
			return getFileDirectory( url.getPath( ) );
		else if ( JAR_SCHEMA.equalsIgnoreCase( url.getProtocol( ) )
				&& !url.getPath( ).toLowerCase( ).startsWith( HTTP_SCHEMA ) )
			return getJarDirectory( url.getPath( ) );

		// rather then the file protocol

		return getNetDirectory( url );
	}

	/**
	 * Returns the directory of a file path that is a url with network
	 * protocols. Note that <code>filePath</code> should include the file name
	 * and file extension.
	 * 
	 * @param filePath
	 *            the file url
	 * @return a url for the directory of the file
	 */

	private static URL getNetDirectory( URL filePath )
	{
		assert filePath != null;

		String path = filePath.getFile( );

		// remove the file name

		int index = path.lastIndexOf( '/' );
		if ( index != -1 && index != path.length( ) - 1 )
			path = path.substring( 0, index + 1 );

		try
		{
			return new URL( filePath.getProtocol( ), filePath.getHost( ),
					filePath.getPort( ), path );
		}
		catch ( MalformedURLException e )
		{
		}

		assert false;
		return null;
	}

	/**
	 * Returns the directory of a file path that is a filepath or a url with the
	 * file protocol.
	 * 
	 * @param filePath
	 *            the file url
	 * @return a url for the directory of the file
	 */

	private static URL getFileDirectory( String filePath )
	{
		File file = new File( filePath );

		// get the absolute file in case of filePath is just
		// "newReport.rptdesign".

		file = file.getAbsoluteFile( );

		// get the parent file when the absolute file is ready.

		file = file.getParentFile( );

		if ( file == null )
			return null;

		try
		{
			return file.getCanonicalFile( ).toURL( );
		}
		catch ( MalformedURLException e )
		{
			assert false;
		}
		catch ( IOException e )
		{
		}

		return null;
	}

	/**
	 * Returns the directory of a file path that is a filepath or a url with the
	 * file protocol.
	 * 
	 * @param filePath
	 *            the file url
	 * @return a url for the directory of the file
	 */

	private static URL getJarDirectory( String filePath )
	{
		if ( filePath.startsWith( FILE_SCHEMA ) )
			filePath = filePath.substring( 5 );

		URL url = getFileDirectory( filePath );
		if ( url != null )
			try
			{
				return new URL( JAR_SCHEMA + ":" + FILE_SCHEMA + ":" //$NON-NLS-1$ //$NON-NLS-2$
						+ url.getPath( ) + '/' );
			}
			catch ( MalformedURLException e )
			{
				assert false;
			}

		return null;
	}

	/**
	 * Return the relative path for the given <code>resource</code> according
	 * to <code>base</code>. Only handle file system and valid url syntax.
	 * <p>
	 * The <code>base</code> value should be directory ONLY and does NOT
	 * contain file name and the format can be:
	 * <ul>
	 * <li>./../hello/
	 * <li>C:\\hello\..\
	 * <li>/C:/../hello/
	 * </ul>
	 * The spearator in the return path is platform-indepedent "/". Please note
	 * that the <code>/</code> in the end of directory will be striped in the
	 * return value.
	 * 
	 * @param base
	 *            the base directory
	 * @param resource
	 *            the full path
	 * @return the relative path.
	 */

	public static String getRelativePath( String base, String resource )
	{
		if ( base == null || resource == null )
			return resource;

		if ( isFileProtocol( resource ) && isFileProtocol( base ) )
			return createRelativePathFromFilePath( base, resource );

		return createRelativePathFromString( base, resource, URL_SEPARATOR );
	}

	/**
	 * Return the relative path for the given file path <code>resource</code>
	 * according to <code>base</code>. Only handle file system.
	 * 
	 * @param base
	 *            the base directory
	 * @param resource
	 *            the full path
	 * @return the relative path.
	 */

	private static String createRelativePathFromFilePath( String base,
			String resource )
	{
		String baseDir = getLocalPath( base );
		String resourceDir = getLocalPath( resource );

		if ( baseDir == null || resourceDir == null )
			return resource;

		File baseFile = new File( baseDir );
		File resourceFile = new File( resourceDir );

		// get platform-depedent file path by using Java File class

		baseDir = baseFile.getAbsolutePath( );
		resourceDir = resourceFile.getAbsolutePath( );

		return createRelativePathFromString( baseDir, resourceDir,
				File.separator );
	}

	/**
	 * Return the relative path for the given string <code>resource</code>
	 * according to <code>base</code>. This method purely works on character
	 * level.
	 * 
	 * @param baseDir
	 *            the base directory
	 * @param resourceDir
	 *            the full path
	 * @return the relative path.
	 */

	private static String createRelativePathFromString( String baseDir,
			String resourceDir, String separator )
	{
		String newBaseDir = baseDir;

		if ( newBaseDir.endsWith( "/" ) || newBaseDir.endsWith( separator ) ) //$NON-NLS-1$
			newBaseDir = newBaseDir.substring( 0, newBaseDir.length( ) - 1 );

		// do the string match to get the location of same prefix

		int matchedPos = 0;
		for ( matchedPos = 0; matchedPos < newBaseDir.length( )
				&& matchedPos < resourceDir.length( ); matchedPos++ )
		{
			if ( newBaseDir.charAt( matchedPos ) != resourceDir
					.charAt( matchedPos ) )
				break;
		}

		// adjust the same prefix by the path separator

		// for the special case like:
		// baseDir: c:/hello/test
		// resourceDir: c:/hello/test/library.xml
		// then the matched position is the length of baseDir insteadof
		// the slash before "test".

		if ( isLastDirectoryMatched( newBaseDir, resourceDir, matchedPos )
				|| isLastDirectoryMatched( resourceDir, newBaseDir, matchedPos ) )
			;
		else
		{
			int oldMatchedPos = matchedPos;
			matchedPos = newBaseDir.lastIndexOf( separator, oldMatchedPos );
		}

		// saves the matched position

		int samePrefixPos = matchedPos;

		int upDirs = 0;

		// calcualtes out the number of up directory should have.

		while ( matchedPos < newBaseDir.length( ) && matchedPos >= 0 )
		{
			matchedPos = newBaseDir.indexOf( separator, matchedPos + 1 );
			upDirs++;
		}

		// appends up directories information.

		StringBuffer sb = new StringBuffer( );
		for ( int i = 0; i < upDirs; i++ )
		{
			sb.append( "../" ); //$NON-NLS-1$
		}

		// appends the relative path.

		if ( samePrefixPos < resourceDir.length( ) )
		{
			String remainPath = resourceDir.substring( samePrefixPos + 1 );
			remainPath = remainPath.replace( '\\', '/' );
			sb.append( remainPath );
		}

		// remove the tail file.separatorChar

		int len = sb.length( );
		if ( len > 0 )
		{
			char lastChar = sb.charAt( len - 1 );
			if ( lastChar == '/' )
				sb.deleteCharAt( len - 1 );
		}

		return sb.toString( );
	}

	/**
	 * Gets the absolute path for the given <code>base</code> and
	 * <code>relativePath</code>.
	 * <p>
	 * The <code>base</code> value should be directory ONLY and does NOT
	 * contain file name and the format can be:
	 * <ul>
	 * <li>./../hello/
	 * <li>C:\\hello\..\
	 * <li>/C:/../hello/
	 * </ul>
	 * The spearator in the return path is platform-depedent.
	 * 
	 * @param base
	 *            the base directory
	 * @param relativePath
	 *            the relative path
	 * @return the absolute path
	 */

	public static String resolveAbsolutePath( String base, String relativePath )
	{
		if ( base == null || relativePath == null )
			return relativePath;

		if ( isFileProtocol( base ) && isFileProtocol( relativePath ) )
			return resolveAbsolutePathFromFilePath( base, relativePath );

		return resolveAbsolutePathFromString( base, relativePath );
	}

	/**
	 * Gets the absolute path for the given <code>base</code> and
	 * <code>relativePath</code>.
	 * <p>
	 * The <code>base</code> value should be directory ONLY and does NOT
	 * contain file name and the format can be:
	 * <ul>
	 * <li>./../hello/
	 * <li>C:\\hello\..\
	 * <li>/C:/../hello/
	 * </ul>
	 * The spearator in the return path is platform-depedent.
	 * 
	 * @param base
	 *            the base directory
	 * @param relativePath
	 *            the relative path
	 * @return the absolute path
	 */

	private static String resolveAbsolutePathFromFilePath( String base,
			String relativePath )
	{

		File file = new File( relativePath );
		if ( file.isAbsolute( ) )
			return relativePath;

		String baseDir = getLocalPath( base );
		String relativeDir = getLocalPath( relativePath );

		if ( baseDir == null || relativeDir == null )
			return relativePath;

		File baseFile = new File( baseDir );
		File resourceFile = new File( baseFile, relativeDir );

		return resourceFile.getPath( );
	}

	/**
	 * Gets the absolute path for the given <code>base</code> and
	 * <code>relativePath</code>.
	 * <p>
	 * The <code>base</code> value should be directory ONLY and does NOT
	 * contain file name and the format can be:
	 * <ul>
	 * <li>./../hello/
	 * <li>C:\\hello\..\
	 * <li>/C:/../hello/
	 * </ul>
	 * The spearator in the return path is platform-depedent.
	 * 
	 * @param base
	 *            the base directory
	 * @param relativePath
	 *            the relative path
	 * @return the absolute path
	 */

	private static String resolveAbsolutePathFromString( String base,
			String relativePath )
	{
		if ( base == null || relativePath == null )
			return relativePath;

		boolean appendDirectorySeparator = false;
		if ( base.length( ) > 0 && relativePath.length( ) > 0 )
		{
			char lastBaseChar = base.charAt( base.length( ) - 1 );
			char firstRelativeChar = relativePath.charAt( 0 );

			if ( lastBaseChar != '/' && lastBaseChar != File.separatorChar
					&& firstRelativeChar != '/'
					&& firstRelativeChar != File.separatorChar )
				appendDirectorySeparator = true;
		}

		if ( appendDirectorySeparator )
			return base + '/' + relativePath;

		return base + relativePath;
	}

	/**
	 * Tests whether the string before <code>matchedPos</code> is a directory.
	 * 
	 * @param baseDir
	 *            the base directory
	 * @param resourceDir
	 *            the resource directory
	 * @param matchedPos
	 *            the 0-based position
	 * @return <code>true</code> if the string before <code>matchedPos</code>
	 *         is a directory
	 */

	private static boolean isLastDirectoryMatched( String baseDir,
			String resourceDir, int matchedPos )
	{
		if ( matchedPos == baseDir.length( )
				&& ( ( matchedPos < resourceDir.length( )
						&& ( resourceDir.charAt( matchedPos ) == File.separatorChar || resourceDir
								.charAt( matchedPos ) == '/' ) || matchedPos == resourceDir
						.length( ) ) ) )
			return true;

		return false;
	}

	/**
	 * Tests whether the input string is a valid resource directory.
	 * 
	 * @param resourceDir
	 *            the resource directory
	 * @return <code>true</code> if the input string is a valid resource
	 *         directory, <code>false</code> otherwise.
	 * @throws MalformedURLException
	 */

	public static boolean isValidResourcePath( String resourceDir )
	{
		if ( resourceDir == null )
			return false;
		File f = new File( resourceDir );

		// TODO: support resource path with jar format.

		// if ( resourceDir.contains( JAR_EXTENTION ) )
		// {
		// if ( f.exists( ) )
		// // TODO: check case like a.jar.txt
		// return true;
		// else
		// {
		// URL url;
		// try
		// {
		// url = new URL( resourceDir );
		// JarURLConnection connection = (JarURLConnection) url
		// .openConnection( );
		// connection.connect( );
		// ZipEntry zip = connection.getJarEntry( );
		// if ( zip != null && zip.isDirectory( ) )
		// return true;
		// }
		// catch ( MalformedURLException e )
		// {
		// return false;
		// }
		// catch ( IOException e1 )
		// {
		// return false;
		// }
		// }
		// }

		if ( f.isAbsolute( ) && f.exists( ) && f.isDirectory( ) )
			return true;

		return false;
	}
}
