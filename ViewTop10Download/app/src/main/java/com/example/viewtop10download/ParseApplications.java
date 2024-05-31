package com.example.viewtop10download;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    private static final String TAG = "ParseApplication";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData){
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean inEntry = false;
        boolean gotImage = false;
        String textValue = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xmlPullParser.getName();
                switch(eventType){
                    case XmlPullParser.START_TAG:
                        Log.d(TAG,"parse: Starting tag for " + tagName);
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        } else if (("image".equalsIgnoreCase(tagName)) && inEntry) {
                            String imageResolution = xmlPullParser.getAttributeValue(null,"height");
                            if (imageResolution != null){
                                gotImage = "53".equalsIgnoreCase(imageResolution);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT: // data is valuable so we store it in the textValue
                        textValue = xmlPullParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG,"parse:Ending tag for "+ tagName);
                        if(inEntry){
                            if("entry".equalsIgnoreCase(tagName)){
                                applications.add(currentRecord);
                                inEntry = false;
                            }else if("name".equalsIgnoreCase(tagName)){//to prevent null pointer exception of tagName, tagName could be null 
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                if (gotImage){
                                    currentRecord.setImageUrl(textValue);
                                }
                            }else if("summary".equalsIgnoreCase(tagName)){
                                currentRecord.setSummary(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            }
                        }
                        break;
                        
                    default:
                }
                eventType = xmlPullParser.next();
            }
            for(FeedEntry app:applications){
                Log.d(TAG,"*******************");
                Log.d(TAG,app.toString());
            }

        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
