package com.xltech.client.data;

public class DataElement {
    private String contentText;  
    private int level;  
    private String id;
    private String parendId;
    private boolean hasChildren;  
    private boolean isExpanded;
    private boolean isSelected;
    private boolean isOnline;
      
    public static final String NO_PARENT = "";
    public static final int TOP_LEVEL = 0;  
      
    public DataElement(String contentText, int level, String id, String parendId,
            boolean hasChildren, boolean isExpanded) {  
        super();  
        this.contentText = contentText;  
        this.level = level;  
        this.id = id;  
        this.parendId = parendId;  
        this.hasChildren = hasChildren;  
        this.isExpanded = isExpanded;
        this.isSelected = false;
        this.isOnline = true;
    }  
  
    public boolean isExpanded() {  
        return isExpanded;  
    }  
  
    public void setExpanded(boolean isExpanded) {  
        this.isExpanded = isExpanded;  
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
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
  
    public String getParendId() {
        return parendId;  
    }  
  
    public void setParendId(String parendId) {
        this.parendId = parendId;  
    }

    public boolean isHasChildren() {  
        return hasChildren;  
    }  
  
    public void setHasChildren(boolean hasChildren) {  
        this.hasChildren = hasChildren;  
    }  
}  