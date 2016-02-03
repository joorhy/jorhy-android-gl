package com.xltech.client.ui;

import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xltech.client.data.DataElement;

import java.util.ArrayList;

public class TreeViewAdapter extends BaseAdapter {  
    private ArrayList<DataElement> elementsData;
    private ArrayList<DataElement> elements;
    private LayoutInflater inflater;  
    private int indentionBase;  
      
    public TreeViewAdapter(ArrayList<DataElement> elements, ArrayList<DataElement> elementsData, LayoutInflater inflater) {
        this.elements = elements;  
        this.elementsData = elementsData;  
        this.inflater = inflater;  
        indentionBase = 40;
    }  
      
    public ArrayList<DataElement> getElements() {
        return elements;  
    }  
      
    public ArrayList<DataElement> getElementsData() {
        return elementsData;  
    }  
      
    @Override  
    public int getCount() {  
        return elements.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return elements.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder;
        if (convertView == null) {  
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.treeview_item, null);
            holder.disclosureImg = (ImageView) convertView.findViewById(R.id.disclosureImg);
            holder.contentText = (TextView) convertView.findViewById(R.id.contentText);  
            convertView.setTag(holder);  
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }
        final DataElement element = elements.get(position);
        int level = element.getLevel();
        holder.disclosureImg.setPadding(
                indentionBase * (level),
                holder.disclosureImg.getPaddingTop(),
                holder.disclosureImg.getPaddingRight(),
                holder.disclosureImg.getPaddingBottom());

        holder.contentText.setText(element.getContentText());
        if (element.hasChildren() && !element.isExpanded()) {
            holder.disclosureImg.setImageResource(R.drawable.close);
            holder.disclosureImg.setVisibility(View.VISIBLE);
        } else if (element.hasChildren() && element.isExpanded()) {
            holder.disclosureImg.setImageResource(R.drawable.open);
            holder.disclosureImg.setVisibility(View.VISIBLE);
        } else if (!element.hasChildren()) {
            holder.disclosureImg.setImageDrawable(null);
            holder.disclosureImg.setVisibility(View.VISIBLE);
        }
        return convertView;
    }  
      
    static class ViewHolder{
        ImageView disclosureImg;
        TextView contentText;
    }
}