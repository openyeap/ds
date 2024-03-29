package cn.zhumingwu.dataswitch.admin.route;

import cn.zhumingwu.dataswitch.admin.route.strategy.*;
import cn.zhumingwu.dataswitch.core.util.I18nUtil;

public enum ExecutorRouteStrategyEnum {
    FIRST(I18nUtil.getInstance("").getString("jobconf_route_first"), new ExecutorRouteFirst()),
    LAST(I18nUtil.getInstance("").getString("jobconf_route_last"), new ExecutorRouteLast()),
    ROUND(I18nUtil.getInstance("").getString("jobconf_route_round"), new ExecutorRouteRound()),
    RANDOM(I18nUtil.getInstance("").getString("jobconf_route_random"), new ExecutorRouteRandom()),
    CONSISTENT_HASH(
            I18nUtil.getInstance("").getString("jobconf_route_consistenthash"), new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED(I18nUtil.getInstance("").getString("jobconf_route_lfu"), new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED(I18nUtil.getInstance("").getString("jobconf_route_lru"), new ExecutorRouteLRU()),
    FAILOVER(I18nUtil.getInstance("").getString("jobconf_route_failover"), new ExecutorRouteFailover()),
    BUSYOVER(I18nUtil.getInstance("").getString("jobconf_route_busyover"), new ExecutorRouteBusyover()),
    SHARDING_BROADCAST(I18nUtil.getInstance("").getString("jobconf_route_shard"), null);

    private String title;
    private ExecutorRouter router;

    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorRouteStrategyEnum item : ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

    public String getTitle() {
        return title;
    }

    public ExecutorRouter getRouter() {
        return router;
    }
}
