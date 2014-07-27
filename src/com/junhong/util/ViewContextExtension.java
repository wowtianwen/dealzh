package com.junhong.util;

import java.util.Set;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

public class ViewContextExtension implements Extension {

	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager manager) {

		System.out.println("beforeBean Discovery");
	}

	// TODO need to clean up
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
		try {
			Context viewScopeContext = manager.getContext(ViewScoped.class);
			Set<Bean<?>> beans = manager.getBeans("messageBackingBean");
			if (viewScopeContext != null) {
				for (Bean bean : beans) {
					System.out.println("after bean Discovery" + bean.getName());
					if (viewScopeContext != null) {
						// viewScopeContext.get(bean);
						// bean.destroy(viewScopeContext.get(bean));
					}
				}

			}
			// viewScopeContext.get(Reflections.<Contextual> cast(MessageBackingBean.class));
		} catch (ContextNotActiveException e) {
			event.addContext(new ViewContext());
		}
	}

	public <X> void processBean(@Observes ProcessBean<X> event, BeanManager manager) {
		// System.out.println("process bean" + event.getBean().getName());
	}

	public void beforeShutdown(@Observes BeforeShutdown event, BeanManager manager) {
		System.out.println("weld server is going to shut down {}");
	}

	public void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager manager) {

		System.out.println("weld container life cycle {afterDeploymentValidation}");
	}

}
