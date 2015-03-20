package solution.helpers;

import solution.development.models.ViewRoute;

/**
 * Created by rory on 20/03/15.
 */
public class RouteHelper {
    
    public static boolean routeContains(ViewRoute route, int id1, int id2){
        return (route.id1 == id1 && route.id2 == id2) || (route.id2 == id1 && route.id1 == id2);
    }
}
