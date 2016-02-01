package com.xltech.client.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by JooLiu on 2016/1/13.
 */
public class DataCategory {
    private static DataCategory instance = null;

    public ArrayList<DataElement> m_elements = null;
    public ArrayList<DataElement> m_elementsData = null;

    private ByteBuffer bodyBuffer = null;

    public static DataCategory getInstance() {
        if (instance == null) {
            instance = new DataCategory();
        }

        return instance;
    }

    public DataCategory() {
        bodyBuffer = ByteBuffer.allocate(1024 * 100);
        m_elements = new ArrayList<DataElement>();
        m_elementsData = new ArrayList<DataElement>();
    }

    public void cleanElement() {
        m_elements.clear();
        m_elementsData.clear();
    }

    public ArrayList<DataElement> getElements() {
        return m_elements;
    }

    public ArrayList<DataElement> getElementsData() {
        return m_elementsData;
    }

    public void cleanBody() {
        bodyBuffer.clear();
    }

    public void setBody(byte[] body) {
        bodyBuffer.put(body);
    }

    public void parse() {
        try {
            String strBody = new String(bodyBuffer.array(), "GBK");
            JSONTokener jsonParser = new JSONTokener(strBody);
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
            String strIdRoot = jsonObject.getString("id");
            String strNameRoot = jsonObject.getString("name");
            DataElement elementRoot = new DataElement(strNameRoot, DataElement.TOP_LEVEL, strIdRoot,
                    DataElement.NO_PARENT, true, false);
            m_elements.add(elementRoot);

            JSONArray itemsArray = jsonObject.getJSONArray("items");
            for (int i=0; i<itemsArray.length(); i++) {
                JSONObject item = (JSONObject) itemsArray.opt(i);
                String strIdLevel_1 = item.getString("id");
                String strNameLevel_1 = item.getString("name");
                DataElement elementLevel_1 = new DataElement(strNameLevel_1,
                        DataElement.TOP_LEVEL + 1, strIdLevel_1, strIdRoot, true, false);
                m_elementsData.add(elementLevel_1);

                JSONArray itemsCamera = jsonObject.getJSONArray("items");
                for (int j=0; j<itemsCamera.length(); j++) {
                    JSONObject itemCamera = (JSONObject) itemsArray.opt(j);
                    String strIdLevel_2 = itemCamera.getString("id");
                    String strNameLevel_2 = itemCamera.getString("name");
                    DataElement elementLevel_2 = new DataElement(strNameLevel_2,
                            DataElement.TOP_LEVEL + 2, strIdLevel_2, strIdLevel_1, false, false);
                    m_elementsData.add(elementLevel_2);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
