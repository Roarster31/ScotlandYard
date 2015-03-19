package solution.development.models;

import java.util.ArrayList;

/**
 * Created by rory on 18/03/15.
 */
public class DataSave {
    public ArrayList<DataPosition> positionList;
    public ArrayList<DataPath> pathList;
    public ArrayList<DataRoute> routeList;

    public DataSave(ArrayList<DataPosition> mPositionList, ArrayList<DataPath> mPathList, ArrayList<DataRoute> mRouteList) {
        positionList = mPositionList;
        pathList = mPathList;
        routeList = mRouteList;
    }
}
