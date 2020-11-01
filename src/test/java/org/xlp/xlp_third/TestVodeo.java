package org.xlp.xlp_third;

import java.io.File;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.mp4.Mp4FileReader;
import org.xlp.video.mp3.MP3Duration;

public class TestVodeo {
	public static void main(String[] args) throws Exception {
//		long start = System.currentTimeMillis();
//		MP3File mp3File = new MP3File("E:\\酷狗\\火箭少女101段奥娟 - 陪我长大.mp3");
//		MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
//		String strLen = audioHeader.getTrackLengthAsString();
//		System.out.println(strLen);
//		int intLen = audioHeader.getTrackLength();
//		System.out.println(intLen);
//		System.out.println(System.currentTimeMillis() - start);
//		System.out.println(new MP3Duration("E:\\酷狗\\火箭少女101段奥娟 - 陪我长大.mp3").getDuration());
//		System.out.println(new MP3Duration("E:\\酷狗\\火箭少女101段奥娟 - 陪我长大.mp3").getDurationFormat());
		Font font = WorkbookFactory.create(true).createFont();
		font.setFontHeight((short) 1);
	}
}
