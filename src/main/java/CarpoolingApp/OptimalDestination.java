package CarpoolingApp;

public class OptimalDestination<F, S, T, P> {
    private F summaryPrice;
    private S currentComplexRouteLength;
    private T offersIdList;
    private P complexRoute;

    public OptimalDestination(F price, S currentRoute, T offers, P route) {
        summaryPrice = price;
        currentComplexRouteLength = currentRoute;
        offersIdList = offers;
        complexRoute = route;

    }

    public F getSummaryPrice() {
        return summaryPrice;
    }

    public S getCurrentComplexRouteLength() {
        return currentComplexRouteLength;
    }

    public T getOffersIdList() {
        return offersIdList;
    }

    public P getComplexRoute() { return complexRoute; }

    public void setSummaryPrice(F price) {
        summaryPrice = price;
    }

    public void setCurrentComplexRouteLength(S currentRoute) {
        currentComplexRouteLength = currentRoute;
    }

    public void setOffersIdList(T offers) {
        offersIdList = offers;
    }

    public void setComplexRoute(P route) { complexRoute = route; }

    @Override
    public String toString() {
        return "Route length = " + currentComplexRouteLength + "; route points = " + complexRoute + "; route price = "
                + summaryPrice + "; offers for this route = " + offersIdList;
    }
}
