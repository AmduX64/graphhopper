package com.graphhopper.routing.template;

import com.graphhopper.routing.profiles.BooleanEncodedValue;
import com.graphhopper.routing.profiles.EncodedValueLookup;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;

import java.util.List;

import static com.graphhopper.util.EdgeIterator.NO_EDGE;

/**
 * @author Peter Karich
 */
public abstract class AbstractRoutingTemplate {
    protected final Weighting weighting;
    protected final EdgeFilter edgeFilter;
    protected final BooleanEncodedValue accessEnc;
    protected final LocationIndex locationIndex;
    protected final EncodedValueLookup lookup;
    // result from lookup
    protected List<QueryResult> queryResults;

    protected AbstractRoutingTemplate(LocationIndex locationIndex, EncodedValueLookup lookup, final Weighting weighting) {
        this.locationIndex = locationIndex;
        this.lookup = lookup;
        this.weighting = weighting;
        this.accessEnc = weighting.getFlagEncoder().getAccessEnc();
        this.edgeFilter = new EdgeFilter() {
            @Override
            public boolean accept(EdgeIteratorState edgeState) {
                return edgeState.get(accessEnc) && !Double.isInfinite(weighting.calcWeight(edgeState, false, NO_EDGE))
                        || edgeState.getReverse(accessEnc) && !Double.isInfinite(weighting.calcWeight(edgeState, true, NO_EDGE));
            }
        };
    }

    protected PointList getWaypoints() {
        PointList pointList = new PointList(queryResults.size(), true);
        for (QueryResult qr : queryResults) {
            pointList.add(qr.getSnappedPoint());
        }
        return pointList;
    }
}