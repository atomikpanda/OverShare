// Bailey Seymour
// DVP6 - 1903
// RecyclerEmptyStateListener.java

package com.baileyseymour.overshare.interfaces;

public interface RecyclerEmptyStateListener {
    // Runs when the empty view should be changed
    void onUpdateEmptyState(boolean hasData);
}
