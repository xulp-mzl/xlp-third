package org.xlp.video.mp3;

import java.io.File;

import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.xlp.assertion.AssertUtils;
import org.xlp.assertion.IllegalObjectException;

/**
 * 获取MP3时长
 * 
 * @author xlp
 * <p> 2020-06-20 </p>
 */
public class MP3Duration {
	/**
	 * 存储MP3文件对象
	 */
	private MP3File mp3File;
	
	public MP3Duration(){
	}
	
	/**
	 * 构造函数
	 * 
	 * @param filePath
	 * @throws NullPointerException 假如参数为空，则抛出该异常
	 * @throws IllegalObjectException 假如给定的文件是目录或不存在，则抛出该异常
	 * @throws RuntimeException 假如解析MP3文件失败，则抛出该异常
	 */
	public MP3Duration(String filePath){
		AssertUtils.isNotNull(filePath, "filePath param is null!");
		fromFile(new File(filePath)); 
	}
	
	/**
	 * 构造函数
	 * 
	 * @param mp3File
	 * @throws IllegalObjectException 假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException 假如参数为null，则抛出该异常
	 * @throws RuntimeException 假如解析MP3文件失败，则抛出该异常
	 */
	public MP3Duration(File mp3File){
		fromFile(mp3File);
	}
	
	/**
	 * 加载MP3文件
	 * 
	 * @param mp3File
	 * @throws IllegalObjectException 假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException 假如参数为null，则抛出该异常
	 * @throws RuntimeException 假如解析MP3文件失败，则抛出该异常
	 */
	public void fromFile(File mp3File){
		AssertUtils.assertFile(mp3File);
		try {
			this.mp3File = new MP3File(mp3File);
		} catch (RuntimeException e) {
			throw e;
		}catch (Exception e) {
			throw new RuntimeException("读取MP3文件失败！", e);
		}
	}
	
	/**
	 * 获取MP3文件时长(单位为：秒)
	 * @return
	 */
	public int getDuration(){
		MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
		return (int) audioHeader.getPreciseTrackLength();
	}
	
	/**
	 * 获取MP3文件时长
	 * @return 格式化后的数据 例如：03:56
	 */
	public String getDurationFormat(){
		MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
		return audioHeader.getTrackLengthAsString();
	}
}
