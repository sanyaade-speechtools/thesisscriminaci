package org.aitools.programd.server.core.lsa.spazioParolaParola;

public class SparseIndex implements Comparable {
	public int si, sj;

	public SparseIndex(int si1, int sj1) {
		si = si1;
		sj = sj1;
	}
	public int compareTo(Object sx) {
		SparseIndex s2 = (SparseIndex) sx;
		if (s2.si < si)
			return 1;
		if (s2.si > si)
			return -1;
		if (s2.sj < sj)
			return 1;
		if (s2.sj > sj)
			return -1;
		return 0;
	}
}
