package com.tazzie02.tazbot.helpers;

public interface ImageSearch {
	
	String getTitle(int index);
	String getImage(int index);
	String getDisplayUrl(int index);
	String getSourceUrl(int index);
	String getType(int index);
	int getHeight(int index);
	int getWidth(int index);
	long getFileSize(int index);
	
	String getThumbnailImage(int index);
	int getThumbnailHeight(int index);
	int getThumbnailWidth(int index);
	
	int getCount();
	
}
