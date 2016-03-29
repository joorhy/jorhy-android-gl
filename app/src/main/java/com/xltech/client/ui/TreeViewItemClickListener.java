package com.xltech.client.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;  
import android.widget.AdapterView.OnItemClickListener;

import com.xltech.client.data.DataCategory;
import com.xltech.client.data.DataElement;
import com.xltech.client.data.DataSelectedVehicle;
import com.xltech.client.service.ManMessage;

public class TreeViewItemClickListener implements OnItemClickListener {
    private TreeViewAdapter treeViewAdapter;
      
    public TreeViewItemClickListener(TreeViewAdapter treeViewAdapter) {  
        this.treeViewAdapter = treeViewAdapter;  
    }  
      
    @Override  
    public void onItemClick(AdapterView<?> parent, View view, int position,  long id) {
        DataElement element = (DataElement) treeViewAdapter.getItem(position);
        ArrayList<DataElement> elements = treeViewAdapter.getElements();
        ArrayList<DataElement> elementsData = treeViewAdapter.getElementsData();

        if (!element.hasChildren()) {
            DataSelectedVehicle.getInstance().setSelectedVehicle(
                    element.getId(), element.getChannels());

            if (PopupCategory.parentName == ActivityImage.class.getName()) {
                ManMessage.DispatchOpenVideoMessage();
            } else if (PopupCategory.parentName == ActivityPlayer.class.getName()) {
                ManMessage.DispatchChangeVideoMessage();
            }

            return;
        }

        if (element.isExpanded()) {
            element.setExpanded(false);
            ArrayList<DataElement> elementsToDel = new ArrayList<DataElement>();
            for (int i = position + 1; i < elements.size(); i++) {
                if (element.getLevel() >= elements.get(i).getLevel())
                    break;
                elementsToDel.add(elements.get(i));
            }
            elements.removeAll(elementsToDel);
            treeViewAdapter.notifyDataSetChanged();
        } else {
            element.setExpanded(true);
            int i = 1;
            for (DataElement e : elementsData) {
                if (e.getParentId().equals(element.getId())) {
                    if(DataCategory.getInstance().isShowAll()){
                        e.setExpanded(false);
                        elements.add(position + i, e);
                        i++;
                    } else {
                        if (e.isOnline()) {
                            e.setExpanded(false);
                            elements.add(position + i, e);
                            i++;
                        }
                    }
                }
            }
            treeViewAdapter.notifyDataSetChanged();
        }
    }

    public void refreshItems() {
        ArrayList<DataElement> elements = treeViewAdapter.getElements();
        ArrayList<DataElement> elementsData = treeViewAdapter.getElementsData();

        ArrayList<DataElement> elementsToRefresh = new ArrayList<DataElement>();
        for (DataElement e : elements) {
            if (e.isExpanded() && e.getLevel() != (DataElement.TOP_LEVEL + 2))
            elementsToRefresh.add(e);
        }


        for (DataElement element : elementsToRefresh) {
            int i = 1;
            int position = elements.indexOf(element);
            for (DataElement e : elementsData) {
                if (e.getParentId().equals(element.getId())) {
                    if (DataCategory.getInstance().isShowAll()) {
                        if (elements.indexOf(e) == -1) {
                            elements.add(position + i, e);
                        }

                    } else {
                        if (e.isOnline()) {
                            if (elements.indexOf(e) == -1) {
                                elements.add(position + i, e);
                            }
                        } else {
                            if (elements.indexOf(e) != -1) {
                                elements.remove(e);
                                i--;
                            }
                        }
                    }
                    i++;
                }
            }
        }
        treeViewAdapter.notifyDataSetChanged();
    }
}  