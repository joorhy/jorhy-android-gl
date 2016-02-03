package com.xltech.client.data;

public class DataElement {
    private String contentText;  
    private int level;  
    private String id;
    private String parentId;
    private boolean hasChildren;  
    private boolean isExpanded;
    private int channels;
    private boolean isOnline;
      
    public static final String NO_PARENT = "";
    public static final int TOP_LEVEL = 0;  
      
    public DataElement(int level, String contentText, String id, int channels, boolean isOnline,
                       String parentId) {
        super();  
        this.contentText = contentText;  
        this.level = level;  
        this.id = id;  
        this.parentId = parentId;
        this.hasChildren = false;
        this.isExpanded = false;
        this.channels = channels;
        this.isOnline = isOnline;
    }

    public DataElement(int level, String contentText, String id,
                       String parentId, boolean isExpanded) {
        super();
        this.contentText = contentText;
        this.level = level;
        this.id = id;
        this.parentId = parentId;
        this.hasChildren = true;
        this.isExpanded = isExpanded;
        this.channels = 0;
        this.isOnline = true;
    }
  
    public boolean isExpanded() {  
        return isExpanded;  
    }  
  
    public void setExpanded(boolean isExpanded) {  
        this.isExpanded = isExpanded;  
    }

    public int getChannels () {
        return channels;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getContentText() {  
        return contentText;  
    }  
  
    public void setContentText(String contentText) {  
        this.contentText = contentText;  
    }  
  
    public int getLevel() {  
        return level;  
    }  
  
    public void setLevel(int level) {  
        this.level = level;  
    }  
  
    public String getId() {
        return id;  
    }  
  
    public void setId(String id) {
        this.id = id;  
    }  
  
    public String getParentId() {
        return parentId;
    }  
  
    public void setParendId(String parendId) {
        this.parentId = parendId;
    }

    public boolean hasChildren() {
        return hasChildren;  
    }  
  
    public void setHasChildren(boolean hasChildren) {  
        this.hasChildren = hasChildren;  
    }  
}  