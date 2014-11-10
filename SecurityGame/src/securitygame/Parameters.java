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
    
    public static final int DEFENDER_RATE = 10;
    public static final int DEFENDER_BUDGET = DEFENDER_RATE * NUMBER_OF_NODES;
    public static final int STRENGTHEN_RATE = 2;
    public static final int INVALID_RATE = 10;
    public static final int FIREWALL_RATE = 10;
    public static final int HONEYPOT_RATE = 50;
    
    public static final int ATTACKER_RATE = 10;
    public static final int ATTACKER_BUDGET = ATTACKER_RATE * NUMBER_OF_NODES;
    public static final int ATTACK_ROLL = 20;
    public static final int ATTACK_RATE = 5;
    public static final int SUPERATTACK_ROLL = 30;
    public static final int SUPERATTACK_RATE = 7;
    public static final int PROBE_SECURITY_RATE = 2;
    public static final int PROBE_POINT_RATE = 2;
    public static final int PROBE_CONNECTIONS_RATE = 1;
    public static final int PROBE_HONEY_RATE = 1;
}
