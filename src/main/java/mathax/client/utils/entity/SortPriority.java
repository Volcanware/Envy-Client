package mathax.client.utils.entity;

public enum SortPriority {
    Lowest_Distance,
    Highest_Distance,
    Lowest_Health,
    Highest_Health,
    Closest_Angle;

    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }
}
