package com.xltech.client.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;  
import android.widget.AdapterView.OnItemClickListener;

import com.xltech.client.config.ConfigTempData;
import com.xltech.client.config.Configer;
import com.xltech.client.data.DataElement;
import com.xltech.client.data.DataSelectedVehicle;
import com.xltech.client.service.ManActivitys;

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
          
        if (!element.isHasChildren()) {
            if (Configer.UseTemp()) {
                DataSelectedVehicle.getInstance().setSelectedVehicle(
                        ConfigTempData.getInstance().getVehicleId(),
                        ConfigTempData.getInstance().getTotalChannels());
            } else {

            }

            Activity currentActivity = ManActivitys.getInstance().currentActivity();
            if (currentActivity.getClass() == ActivityImage.class) {
                ((ActivityImage)currentActivity).HidePopupWindow();
                Intent intent = new Intent(currentActivity, ActivityPlayer.class);
                currentActivity.startActivity(intent);
            } else if (currentActivity.getClass() == ActivityPlayer.class) {
                ((ActivityPlayer)currentActivity).HidePopupWindow();
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
        } else if (element.isOnline()){
            element.setExpanded(true);  
            int i = 1;
            for (DataElement e : elementsData) {
                if (e.getParendId().equals(element.getId())) {
                    e.setExpanded(false);  
                    elements.add(position + i, e);  
                    i ++;  
                }  
            }  
            treeViewAdapter.notifyDataSetChanged();  
        }  
    }  
  
}  