package shared;

import java.util.Comparator;

/**
 * Compares polygon nodes
 * @author Barney
 */
class PolygonNodeComparator implements Comparator<PolygonNode> {
	@Override
	public int compare(PolygonNode p1, PolygonNode p2) {
		if (p1.getF() < p2.getF()) {
			return 1;
		} else if (p1.getF() > p2.getF()) {
			return -1;
		} else {
			return 0;
		}
	}
}
