package com.luna.app.rs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.luna.core.webservice.SteamController;
import com.luna.misc.exception.RequestException;

@RestController
public class SteamResource 
    extends 
        DefaultResource
{
    private final SteamController controller = SteamController.getInstance();
    private final Gson gson = new Gson();

    public SteamResource(){}

    @GetMapping( value="games" )
    public ResponseEntity<String> getUser( @RequestParam( "appids" ) String appids ) 
    {
        try 
        {
            Object game = controller.getGameByAppId( appids );
            if ( game == null )
            {
                return notFound( "game not found for the given appids" );
            }

            return ok( gson.toJson( game ) );
        }
        catch( RequestException e )
        {
            return dynamicResponse( e.getMessage(), e.getCode() );
        }
        catch ( Exception e ) 
        {   
            e.printStackTrace();
            return internalServerError( e.getMessage() );
        }
    }    
}
