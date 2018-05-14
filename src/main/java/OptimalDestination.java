public class OptimalDestination<F, S, T> {
    private F summaryRoutesLength;
    private S currentComplexRouteLength;
    private T passengersIdList;

    public OptimalDestination(F allRoutes, S currentRoute, T passengers) {
        summaryRoutesLength = allRoutes;
        currentComplexRouteLength = currentRoute;
        passengersIdList = passengers;
    }

    public F getSummaryRoutesLength() {
        return summaryRoutesLength;
    }

    public S getCurrentComplexRouteLength() {
        return currentComplexRouteLength;
    }

    public T getPassengersIdList() {
        return passengersIdList;
    }

    public void setSummaryRoutesLength(F allRoutes) {
        summaryRoutesLength = allRoutes;
    }

    public void setCurrentComplexRouteLength(S currentRoute) {
        currentComplexRouteLength = currentRoute;
    }

    public void setPassengersIdList(T passengers) {
        passengersIdList = passengers;
    }

    @Override
    public String toString() {
        return "Summary routes length: " + summaryRoutesLength + "; current route length for you: "
                + currentComplexRouteLength + "; your passengers are: " + passengersIdList;
    }
}
