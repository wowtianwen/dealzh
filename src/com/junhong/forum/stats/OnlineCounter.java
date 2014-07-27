/**
 * forum
 * zhanjung
 */
package com.junhong.forum.stats;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * @author zhanjung
 * 
 */
@Named
@ApplicationScoped
// servlet context is not thread safe.
public class OnlineCounter {
	/* ------------instance Variable-------------- */
	private AtomicInteger counter = new AtomicInteger(850);

	public AtomicInteger getCounter() {
		return counter;
	}

	public void setCounter(AtomicInteger counter) {
		this.counter = counter;
	}

	/* -------------business logic----------------- */
	public void raise() {
		counter.getAndIncrement();
	}

	public void reduce() {
		counter.decrementAndGet();
	}
	/* -------------getter/setter----------------- */
}
