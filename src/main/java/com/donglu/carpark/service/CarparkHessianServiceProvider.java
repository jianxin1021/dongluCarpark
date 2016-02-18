package com.donglu.carpark.service;

import com.caucho.hessian.client.HessianProxyFactory;
import com.donglu.carpark.ui.CarparkClientConfig;
import com.donglu.carpark.ui.ClientConfigUI;
import com.donglu.carpark.util.CarparkFileUtils;
import com.google.inject.*;
import com.google.inject.persist.PersistService;

import java.net.MalformedURLException;

/**
 *客户端的service服务配置
 */
public class CarparkHessianServiceProvider extends AbstractCarparkDatabaseServiceProvider{

    private PersistService persistService;
   
    @Override
	protected void initService() {
        try{
        	Injector injector = Guice.createInjector(new Model());
        	 setCarparkService(injector.getInstance(CarparkService.class));
             setCarparkUserService(injector.getInstance(CarparkUserService.class));
             setSystemUserService(injector.getInstance(SystemUserServiceI.class));
             setCarparkInOutService(injector.getInstance(CarparkInOutServiceI.class));
             setSystemOperaLogService(injector.getInstance(SystemOperaLogServiceI.class));
             setStoreService(injector.getInstance(StoreServiceI.class));
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

    @Override
	protected void stopServices() {
        persistService.stop();
    }

    public class Model extends AbstractModule {

        @Override
        protected void configure() {
            CarparkClientConfig cf=(CarparkClientConfig) CarparkFileUtils.readObject(ClientConfigUI.CARPARK_CLIENT_CONFIG);
            if (cf==null) {
				return;
			}
            String url = "http://"+CarparkClientConfig.getInstance().getDbServerIp()+":8899/";
//            LOGGER.info("客户端远程数据底层地址:{}",url);

            HessianProxyFactory factory = new HessianProxyFactory();
            factory.setOverloadEnabled(true);
            
            try {
            	this.bind(CarparkUserService.class).toInstance((CarparkUserService) factory.create(CarparkUserService.class, url+"user/"));
            	this.bind(SystemUserServiceI.class).toInstance((SystemUserServiceI) factory.create(SystemUserServiceI.class, url+"user/"));
				this.bind(CarparkService.class).toInstance((CarparkService) factory.create(CarparkService.class, url+"carpark/"));
				this.bind(SystemOperaLogServiceI.class).toInstance((SystemOperaLogServiceI) factory.create(SystemOperaLogServiceI.class, url+"user/"));
				this.bind(CarparkInOutServiceI.class).toInstance((CarparkInOutServiceI) factory.create(CarparkInOutServiceI.class, url+"inout/"));
				this.bind(StoreServiceI.class).toInstance((StoreServiceI) factory.create(StoreServiceI.class, url+"storeservice/"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
        }
    }


}
