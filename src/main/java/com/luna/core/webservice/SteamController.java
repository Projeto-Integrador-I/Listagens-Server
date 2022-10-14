package com.luna.core.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.luna.misc.util.RequestProvider;
import com.luna.core.data.Game;
import com.luna.misc.util.ApiBuilder;


public class SteamController 
{
    private final String steamUrl = "https://store.steampowered.com/api/";
    private final String steamUrlApi = "https://api.steampowered.com";
    private final String APIKEY = "E9A9A76AA134F583B3BC4DBD977504DD";
    private ArrayList<Game> games = new ArrayList<>();

    private static SteamController defaultInstance;
    private Map<String, Object> fullGameList = new HashMap<>();
    private HashMap<String, String> queryParams = new HashMap<String, String>();

    private boolean hasResults = false;

    private SteamController() {}

    public static SteamController getInstance() 
    {
        if ( defaultInstance == null ) 
        {
            defaultInstance = new SteamController();
        }

        return defaultInstance;
    }

    public List<Game> getGamesByName( String name ) throws Exception
    {
        RequestProvider requestProvider = new RequestProvider( "http://api.steampowered.com/ISteamApps/GetAppList/" );
        
        queryParams.clear();
        queryParams.put( "key", "STEAMKEY" );
        queryParams.put( "format", "json" );
        
        if ( !hasResults )
        {
            fullGameList = requestProvider.setPath( "v0002" )
                                  .setQueryParam( queryParams )
                                  .addHeader( HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE )
                                  .get();

            hasResults = true;
        }
        
        Map<String, Object> app = (Map<String, Object>)fullGameList.get( "applist" );
        ArrayList<Object> appList = (ArrayList<Object>)app.get( "apps" );
        
        List<Object> result = appList.stream().filter( g -> g.toString().toLowerCase().contains( name.toLowerCase() ) ).collect( Collectors.toList() );

        ArrayList<Game> game = new ArrayList<>();

        for ( Object gameFiltered : result )
        {
            String[] data = gameFiltered.toString().replaceAll("=|appid|name|\\{|\\}","").split( ",");

            game.add( getGameByAppId( data[0].toString().split("\\.")[0] ) );
        }

        return game;                                              
    }

    public Game getGameByAppId( String appId ) throws Exception 
    {
        queryParams.clear();
        queryParams.put( "appids", appId );
        queryParams.put( "l", "portuguese" );

        RequestProvider requestProvider = new RequestProvider( steamUrl );

        Map<String, Object> json = requestProvider.setPath( "appdetails" )
                                                  .setQueryParam( queryParams )
                                                  .addHeader( HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE )
                                                  .get();

        return ApiBuilder.buildGame( json, appId );
    }

    public List<Game> getMostPlayedGames() throws Exception
    {
        queryParams.clear();
        queryParams.put("", APIKEY );
        RequestProvider requestProvider = new RequestProvider( steamUrlApi );


        Map<String, Object> json = requestProvider.setPath( "ISteamChartsService/GetMostPlayedGames/v1/?" + APIKEY )
                                                  .setQueryParam(queryParams)
                                                  .addHeader( HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE )
                                                  .get();

        Map<String, Object> response = (Map<String, Object>)json.get( "response" );
        ArrayList<Map<String,Object>> ranks = (ArrayList<Map<String,Object>>)response.get( "ranks" );

        for( Map<String,Object> game : ranks )
        {
            if ( Math.round(  Double.parseDouble( game.get( "rank" ).toString() ) ) != 25 )
            {
                games.add( getGameByAppId( game.get("appid").toString().replace( ".0", "") ) );
            }

            else
            {
                break;
            }
        }

        return games;
    }
}
