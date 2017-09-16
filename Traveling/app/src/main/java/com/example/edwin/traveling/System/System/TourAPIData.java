package com.example.edwin.traveling.System.System;

/**
 * Created by Edwin on 2017-09-15.
 */

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sleep on 2017-08-19.
 */

public class TourAPIData {
    private final String OS = "AND";
    private final String APPNAME = "TRIPLAY";
    private Document doc;

    private TourAPIData(){
    }

    private static class LazyIdiom {
        public static TourAPIData INSTANCE = new TourAPIData();
    }

    public static TourAPIData getInstance() {
        return LazyIdiom.INSTANCE;
    }

    public ArrayList<TravelPlace> getAdjacencyPlace(float pos_x, float pos_y){
        final int MAX_NUM = 10;
        final String RADIUS = "1000";
        final String MAX_ROW = "100";
        ArrayList<TravelPlace> result = new ArrayList<TravelPlace>();

        Log.d("TRIPLAY", "mapX="+pos_x+" "+"mapY="+pos_y);
        doc = getXMLData("locationBasedList", 1, "arrange=E", "mapX="+String.valueOf(pos_x), "mapY="+String.valueOf(pos_y), "radius="+RADIUS, "numOfRows="+MAX_ROW);

        Elements eResult = doc.getElementsByTag("item");
        int count = 0, cursor = 0;
        int rowNum = eResult.size();
        Log.d("TRIPLAY", "Row="+rowNum);
        while(count < MAX_NUM && cursor < rowNum){
            int type = Integer.parseInt(eResult.get(cursor).getElementsByTag("contenttypeid").get(0).text());
            if(!checkOtherType(type)){
                cursor++;
                continue;
            }

            String id = eResult.get(cursor).getElementsByTag("contentid").get(0).text();
            String name = eResult.get(cursor).getElementsByTag("title").get(0).text();
            float x = Float.valueOf(eResult.get(cursor).getElementsByTag("mapx").get(0).text());
            float y = Float.valueOf(eResult.get(cursor).getElementsByTag("mapy").get(0).text());

            TravelPlace pl = new TravelPlace(id, type, name, x, y);
            result.add(pl);
            cursor++;
            count++;
        }

        return result;
    }

    public ArrayList<TravelPlace> getAdjacencyFestival(float pos_x, float pos_y){
        final int MAX_NUM = 5;
        final String RADIUS = "1000";
        final int MAX_ROW = 100;
        ArrayList<TravelPlace> result = new ArrayList<TravelPlace>();

        doc = getXMLData("locationBasedList", 1, "arrange=E", "mapX="+String.valueOf(pos_x), "mapY="+String.valueOf(pos_y), "radius="+RADIUS, "numOfRows="+String.valueOf(MAX_ROW));

        Elements eResult = doc.getElementsByTag("item");
        int count = 0, cursor = 0;
        int rowNum = eResult.size();
        Log.d("TRIPLAY", "ROWNUM="+rowNum);
        while(count < MAX_NUM && cursor < rowNum){
            int type = Integer.parseInt(eResult.get(cursor).getElementsByTag("contenttypeid").get(0).text());
            if(!checkFestival(type)){
                cursor++;
                continue;
            }

            String id = eResult.get(cursor).getElementsByTag("contentid").get(0).text();
            String name = eResult.get(cursor).getElementsByTag("title").get(0).text();
            float x = Float.valueOf(eResult.get(cursor).getElementsByTag("mapx").get(0).text());
            float y = Float.valueOf(eResult.get(cursor).getElementsByTag("mapy").get(0).text());

            TravelPlace pl = new TravelPlace(id, type, name, x, y);
            result.add(pl);
            cursor++;
            count++;
        }

        return result;
    }

    private boolean checkFestival(int type){
        if(type == TravelPlace.FESTIVAL){
            return true;
        }

        return false;
    }
    private boolean checkOtherType(int type){
        switch(type) {
            case TravelPlace.CULTURE:
            case TravelPlace.FOOD:
            case TravelPlace.REPORTS:
            case TravelPlace.TOUR:
                return true;
            default:
                return false;
        }
    }

    public String getOverview(String ID){
        String result="";

        doc = getXMLData("detailCommon", 1, "contentId="+ID, "overviewYN=Y");

        result += doc.getElementsByTag("overview").get(0).text();

        return result;
    }

    private synchronized Document getXMLData(String serviceName, int page, String... parameters){

        //Make real URL from parameters
        String connectionURL = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/" + serviceName + "?ServiceKey="+ getServiceKey() + "&pageNo=" + String.valueOf(page) +"&MobileOS=" + OS + "&MobileApp=" + APPNAME;

        for(String s : parameters) {
            connectionURL += "&" + s;
        }

        Document doc = new Document(""); //initialize

        try {
            //get XML file from URL
            doc = Jsoup.connect(connectionURL).parser(Parser.xmlParser()).get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    private String getServiceKey(){
        return "ftiu7oSNDPVL1tqSYAzl%2BR4UYUiCuLsBDB61NBMu6n%2FDCJYOgrmKCSs4cVSwiYrdc5QIs%2FLKA4iqXU3%2FUx1I5w%3D%3D";
    }
}