package org.cloudbus.cloudsim.incubator.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.incubator.web.WebSession;

/**
 * A factory for CloudSim entities' ids. CloudSim requires a lot of ids, that
 * are provided by the end user. This class is a utility for automatically
 * generating valid ids.
 * 
 * @author nikolay.grozev
 * 
 */
public final class Id {

    private static final Map<Class<?>, Integer> COUNTERS = new LinkedHashMap<>();
    private static int globalCounter = 0;

    static {
	COUNTERS.put(Cloudlet.class, 1);
	COUNTERS.put(Vm.class, 1);
	COUNTERS.put(Host.class, 1);
	COUNTERS.put(DatacenterBroker.class, 1);
	COUNTERS.put(WebSession.class, 1);
    }

    private Id() {
    }

    /**
     * Returns a valid id for the specified class.
     * @param clazz - the class of the object to get an id for. Must not be null.
     * @return a valid id for the specified class.
     */
    public static synchronized int pollId(Class<?> clazz) {
	Class<?> matchClass = null;
	if (COUNTERS.containsKey(clazz)) {
	    matchClass = clazz;
	} else {
	    for (Class<?> key : COUNTERS.keySet()) {
		if (key.isAssignableFrom(clazz)) {
		    matchClass = clazz;
		    break;
		}
	    }
	}

	int result = -1;
	if (matchClass == null) {
	    result = pollGlobalId();
	} else {
	    result = COUNTERS.get(matchClass);
	    COUNTERS.put(matchClass, result + 1);
	}
	return result;
    }

    private static synchronized int pollGlobalId() {
	return globalCounter++;
    }

}