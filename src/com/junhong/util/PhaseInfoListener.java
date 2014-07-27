package com.junhong.util;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhaseInfoListener implements PhaseListener {

	private static final long	serialVersionUID	= -2073085542199587888L;
	private Logger				logger				= LoggerFactory.getLogger(PhaseInfoListener.class.toString());

	@Override
	public void afterPhase(PhaseEvent event) {
		// TODO Auto-generated method stub
		PhaseId phase = event.getPhaseId();
		logger.info("-------After---Phase---------{}", phase.toString());

	}

	@Override
	public void beforePhase(PhaseEvent event) {
		// TODO Auto-generated method stub
		PhaseId phase = event.getPhaseId();
		logger.info("-------Before---Phase---------{}", phase.toString());
	}

	@Override
	public PhaseId getPhaseId() {
		// TODO Auto-generated method stub
		return PhaseId.ANY_PHASE;
	}

}
