package org.cloudbus.cloudsim.ex.web.workload;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.ex.web.WebSession;
import org.cloudbus.cloudsim.ex.web.workload.freq.FrequencyFunction;
import org.cloudbus.cloudsim.ex.web.workload.sessions.ISessionGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.PoissonGenerator;

/**
 * Represents a timed factory for sessions of a given type. Defines the workload
 * consisting of sessions of a given type directed to a data center.
 * Mathematically can be represented as Po(f(t)).
 * 
 * @author nikolay.grozev
 * 
 */
public class StatWorkloadGenerator implements IWorkloadGenerator {

    private final FrequencyFunction freqFun;
    private final ISessionGenerator sessGen;
    private final Random rng;

    /**
     * Constructor.
     * 
     * @param seed
     *            - the seed for the used Poisson distribution or null if no
     *            seed is used. Must not be null.
     * @param freqFun
     *            - the frequency function for the Poisson distribution. Must
     *            not be null.
     * @param sessGen
     *            - generator for sessions.
     */
    public StatWorkloadGenerator(final byte[] seed, final FrequencyFunction freqFun, final ISessionGenerator sessGen) {
	super();
	this.freqFun = freqFun;
	this.sessGen = sessGen;

	rng = seed == null ? new MersenneTwisterRNG() : new MersenneTwisterRNG(seed);
    }

    /**
     * Constructor.
     * 
     * @param freqFun
     *            - the frequency function for the Poisson distribution.
     * @param sessGen
     *            - generator for sessions.
     */
    public StatWorkloadGenerator(final FrequencyFunction freqFun, final ISessionGenerator sessGen) {
	this(null, freqFun, sessGen);
    }

    /**
     * Generates sessions for the period [startTime, startTime + periodLen].
     * 
     * @param startTime
     *            - the start time of the generated sessions.
     * @param periodLen
     *            - the length of the period.
     * @return a map between session start times and sessions.
     */
    @Override
    public Map<Double, List<WebSession>> generateSessions(final double startTime, final double periodLen) {
	double unit = freqFun.getUnit();
	double freq = freqFun.getFrequency(startTime);

	Map<Double, List<WebSession>> timesToSessions = new LinkedHashMap<>();
	if (freq > 0) {
	    // The frequency within this period
	    double freqInLen = freq * (periodLen / unit);
	    NumberGenerator<Integer> poiss = new PoissonGenerator(freqInLen, rng);
	    int numberOfSessions = poiss.nextValue();

	    // Distribute uniformly the created sessions
	    double timeStep = periodLen / numberOfSessions;
	    for (int i = 0; i < numberOfSessions; i++) {
		double sessionTime = startTime + i * timeStep;
		if (!timesToSessions.containsKey(sessionTime)) {
		    timesToSessions.put(sessionTime, new ArrayList<WebSession>());
		}

		timesToSessions.get(sessionTime).add(sessGen.generateSessionAt(sessionTime));
	    }
	}

	return timesToSessions;
    }

}