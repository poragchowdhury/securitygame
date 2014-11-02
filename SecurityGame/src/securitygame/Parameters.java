package securitygame;

public class Parameters {
    public static final int NUMBER_OF_NODES = 10;
    public static final int NUMBER_OF_PUBLIC_NODES = 2;
    public static final int NUMBER_OF_ROUTER_NODES = 2;
    public static final int NUMBER_OF_PRIVATE_NODES = NUMBER_OF_NODES - NUMBER_OF_PUBLIC_NODES - NUMBER_OF_ROUTER_NODES;
    public static final int MAX_NEIGHBORS = 3;
    public static final int MIN_NEIGHBORS = 2;
    public static final int MAX_POINT_VALUE = 20;
    public static final int MAX_ROUTER_EDGES = 5;
}
