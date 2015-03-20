package solution.development;

import solution.Models.CoordinateData;

import java.awt.geom.GeneralPath;

@Deprecated
public class PathData {
    private GeneralPath generalPath;
    private CoordinateData source;
    private CoordinateData target;

    public PathData(GeneralPath generalPath, CoordinateData source, CoordinateData target){
        this.generalPath = generalPath;
        this.source = source;
        this.target = target;
    }

    public GeneralPath getGeneralPath() {
        return generalPath;
    }

    public void setGeneralPath(GeneralPath generalPath) {
        this.generalPath = generalPath;
    }

    public CoordinateData getSource() {
        return source;
    }

    public void setSource(CoordinateData source) {
        this.source = source;
    }

    public CoordinateData getTarget() {
        return target;
    }

    public void setTarget(CoordinateData target) {
        this.target = target;
    }
}
