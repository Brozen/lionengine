/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine;

import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.core.Media;

/**
 * Get quick information from an image without reading all data.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final ImageInfo info = ImageInfo.get(Core.MEDIA.create(&quot;dot.png&quot;));
 * Assert.assertEquals(64, info.getWidth());
 * Assert.assertEquals(32, info.getHeight());
 * Assert.assertEquals(&quot;png&quot;, info.getFormat());
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ImageInfo
{
    /** Unsupported format. */
    private static final String ERROR_FORMAT = "Unsupported image format";
    /** Read error. */
    private static final String ERROR_READ = "Can not read image information";
    /** Invalid Jpg. */
    private static final String ERROR_JPG = "Invalid JPG file !";
    /** Message skipped. */
    private static final String MESSAGE_SKIPPED = "Skipped ";
    /** Message bytes instead of. */
    private static final String MESSAGE_BYTES_INSTEAD_OF = " bytes instead of ";
    /** Bmp format. */
    private static final String FORMAT_BMP = "bmp";
    /** Png format. */
    private static final String FORMAT_PNG = "png";
    /** Gif format. */
    private static final String FORMAT_GIF = "gif";
    /** Jpg format. */
    private static final String FORMAT_JPG = "jpg";
    /** Tiff format. */
    private static final String FORMAT_TIFF = "tiff";

    /**
     * Get the image info of the specified image media.
     * 
     * @param media The media.
     * @return The image info instance.
     */
    public static ImageInfo get(Media media)
    {
        return new ImageInfo(media);
    }

    /**
     * Read integer in image data.
     * 
     * @param inputStream The stream.
     * @param bytesNumber The number of bytes to read.
     * @param bigEndian The big endian flag.
     * @return The integer read.
     * @throws IOException if error on reading.
     */
    private static int readInt(InputStream inputStream, int bytesNumber, boolean bigEndian) throws IOException
    {
        int ret = 0;
        int sv = bigEndian ? (bytesNumber - 1) * 8 : 0;
        final int cnt = bigEndian ? -8 : 8;
        for (int i = 0; i < bytesNumber; i++)
        {
            ret |= inputStream.read() << sv;
            sv += cnt;
        }
        return ret;
    }

    /**
     * Skipped message error.
     * 
     * @param skipped The skipped value.
     * @param instead The instead value.
     * @return The message error.
     */
    private static String skippedError(long skipped, int instead)
    {
        return ImageInfo.MESSAGE_SKIPPED + skipped + ImageInfo.MESSAGE_BYTES_INSTEAD_OF + instead;
    }

    /** Image width. */
    private int width;
    /** Image height. */
    private int height;
    /** Image format. */
    private String format;

    /**
     * Constructor.
     * 
     * @param media The image media path.
     */
    private ImageInfo(Media media)
    {
        Check.notNull(media);
        try (InputStream inputStream = media.getInputStream();)
        {
            final int byte1 = inputStream.read();
            final int byte2 = inputStream.read();
            final int byte3 = inputStream.read();

            checkFormat(inputStream, byte1, byte2, byte3);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ImageInfo.ERROR_READ);
        }
    }

    /**
     * Get image width.
     * 
     * @return The image width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get image height.
     * 
     * @return The image height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get image format.
     * 
     * @return The image format.
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * Check the image format and read it.
     * 
     * @param inputStream The input stream.
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @throws IOException If an error occurs.
     */
    private void checkFormat(InputStream inputStream, int byte1, int byte2, int byte3) throws IOException
    {
        if (!checkGif(inputStream, byte1, byte2, byte3))
        {
            if (!checkJpg(inputStream, byte1, byte2, byte3))
            {
                if (!checkPng(inputStream, byte1, byte2, byte3))
                {
                    if (!checkBmp(inputStream, byte1, byte2, byte3))
                    {
                        checkTiff(inputStream, byte1, byte2, byte3);
                    }
                }
            }
        }
    }

    /**
     * Check if can read as GIF.
     * 
     * @param inputStream The input stream.
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @return <code>true</code> if read, <code>false</code> else.
     * @throws IOException If an error occurs.
     */
    private boolean checkGif(InputStream inputStream, int byte1, int byte2, int byte3) throws IOException
    {
        final boolean gif = 'G' == byte1 && 'I' == byte2 && 'F' == byte3;
        if (gif)
        {
            readGif(inputStream);
        }
        return gif;
    }

    /**
     * Check if can read as JPG.
     * 
     * @param inputStream The input stream.
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @return <code>true</code> if read, <code>false</code> else.
     * @throws IOException If an error occurs.
     */
    private boolean checkJpg(InputStream inputStream, int byte1, int byte2, int byte3) throws IOException
    {
        final boolean jpg = 0xFF == byte1 && 0xD8 == byte2;
        if (jpg)
        {
            readJpg(inputStream, byte3);
        }
        return jpg;
    }

    /**
     * Check if can read as PNG.
     * 
     * @param inputStream The input stream.
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @return <code>true</code> if read, <code>false</code> else.
     * @throws IOException If an error occurs.
     */
    private boolean checkPng(InputStream inputStream, int byte1, int byte2, int byte3) throws IOException
    {
        final boolean png = 137 == byte1 && 80 == byte2 && 78 == byte3;
        if (png)
        {
            readPng(inputStream);
        }
        return png;
    }

    /**
     * Check if can read as BMP.
     * 
     * @param inputStream The input stream.
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @return <code>true</code> if read, <code>false</code> else.
     * @throws IOException If an error occurs.
     */
    private boolean checkBmp(InputStream inputStream, int byte1, int byte2, int byte3) throws IOException
    {
        final boolean bmp = 66 == byte1 && 77 == byte2;
        if (bmp)
        {
            readBmp(inputStream);
        }
        return bmp;
    }

    /**
     * Check if can read as TIFF.
     * 
     * @param inputStream The input stream.
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @throws IOException If an error occurs.
     */
    private void checkTiff(InputStream inputStream, int byte1, int byte2, int byte3) throws IOException
    {
        final int byte4 = inputStream.read();
        final boolean tiff1 = 'M' == byte1 && 'M' == byte2 && 0 == byte3 && 42 == byte4;
        final boolean tiff2 = 'I' == byte1 && 'I' == byte2 && 42 == byte3 && 0 == byte4;
        final boolean tiff = tiff1 || tiff2;

        if (tiff)
        {
            readTiff(inputStream, byte1);
        }
        else
        {
            throw new LionEngineException(ImageInfo.ERROR_FORMAT);
        }
    }

    /**
     * Read GIF header.
     * 
     * @param inputStream The input stream.
     * @throws IOException If an error occurs.
     */
    private void readGif(InputStream inputStream) throws IOException
    {
        final long skipped = inputStream.skip(3);
        if (skipped != 3)
        {
            throw new IOException(ImageInfo.skippedError(skipped, 3));
        }
        width = ImageInfo.readInt(inputStream, 2, false);
        height = ImageInfo.readInt(inputStream, 2, false);
        format = ImageInfo.FORMAT_GIF;
    }

    /**
     * Read JPG header.
     * 
     * @param inputStream The input stream.
     * @param byte3 The third byte.
     * @throws IOException If an error occurs.
     */
    private void readJpg(InputStream inputStream, int byte3) throws IOException
    {
        boolean success = false;
        int current = byte3;
        while (255 == current)
        {
            final int marker = inputStream.read();
            final int len = ImageInfo.readInt(inputStream, 2, true);
            if (192 == marker || 193 == marker || 194 == marker)
            {
                final long skipped = inputStream.skip(1);
                if (skipped != 1)
                {
                    throw new IOException(ImageInfo.skippedError(skipped, 1));
                }
                height = ImageInfo.readInt(inputStream, 2, true);
                width = ImageInfo.readInt(inputStream, 2, true);
                format = ImageInfo.FORMAT_JPG;
                success = true;
                break;
            }
            final long skipped = inputStream.skip(len - 2);
            if (skipped != len - 2)
            {
                throw new IOException(ImageInfo.skippedError(skipped, len - 2));
            }
            current = inputStream.read();
        }
        if (!success)
        {
            throw new IOException(ImageInfo.ERROR_JPG);
        }
    }

    /**
     * Read PNG header.
     * 
     * @param inputStream The input stream.
     * @throws IOException If an error occurs.
     */
    private void readPng(InputStream inputStream) throws IOException
    {
        final int toSkip = 15;
        long skipped = inputStream.skip(toSkip);
        if (skipped != toSkip)
        {
            throw new IOException(ImageInfo.skippedError(skipped, 15));
        }
        width = ImageInfo.readInt(inputStream, 2, true);
        skipped = inputStream.skip(2);
        if (skipped != 2)
        {
            throw new IOException(ImageInfo.skippedError(skipped, 2));
        }
        height = ImageInfo.readInt(inputStream, 2, true);
        format = ImageInfo.FORMAT_PNG;
    }

    /**
     * Read BMP header.
     * 
     * @param inputStream The input stream.
     * @throws IOException If an error occurs.
     */
    private void readBmp(InputStream inputStream) throws IOException
    {
        final int toSkip = 15;
        long skipped = inputStream.skip(toSkip);
        if (skipped != toSkip)
        {
            throw new IOException(ImageInfo.skippedError(skipped, 15));
        }
        width = ImageInfo.readInt(inputStream, 2, false);
        skipped = inputStream.skip(2);
        if (skipped != 2)
        {
            throw new IOException(ImageInfo.skippedError(skipped, 2));
        }
        height = ImageInfo.readInt(inputStream, 2, false);
        format = ImageInfo.FORMAT_BMP;
    }

    /**
     * Read TIFF header.
     * 
     * @param inputStream The input stream.
     * @param byte1 The firsts byte.
     * @throws IOException If an error occurs.
     */
    private void readTiff(InputStream inputStream, int byte1) throws IOException
    {
        final int toSkip = 8;
        final boolean bigEndian = 'M' == byte1;
        int w = -1, h = -1;
        final int ifd = ImageInfo.readInt(inputStream, 4, bigEndian);
        long skipped = inputStream.skip(ifd - toSkip);
        if (skipped != ifd - toSkip)
        {
            throw new IOException(ImageInfo.skippedError(skipped, ifd - toSkip));
        }
        final int entries = ImageInfo.readInt(inputStream, 2, bigEndian);

        for (int i = 1; i <= entries; i++)
        {
            final int tag = ImageInfo.readInt(inputStream, 2, bigEndian);
            final int fieldType = ImageInfo.readInt(inputStream, 2, bigEndian);
            ImageInfo.readInt(inputStream, 4, bigEndian);
            int valOffset;
            if (3 == fieldType || 8 == fieldType)
            {
                valOffset = ImageInfo.readInt(inputStream, 2, bigEndian);
                skipped = inputStream.skip(2);
                if (skipped != 2)
                {
                    throw new IOException(ImageInfo.skippedError(skipped, 2));
                }
            }
            else
            {
                valOffset = ImageInfo.readInt(inputStream, 4, bigEndian);
            }
            if (256 == tag)
            {
                w = valOffset;
            }
            else if (257 == tag)
            {
                h = valOffset;
            }
            if (-1 != w && -1 != h)
            {
                width = w;
                height = h;
                format = ImageInfo.FORMAT_TIFF;
                break;
            }
        }
    }
}
