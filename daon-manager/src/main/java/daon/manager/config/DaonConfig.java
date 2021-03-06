package daon.manager.config;

import daon.core.Daon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by mac on 2017. 4. 18..
 */
@Configuration
@EnableScheduling
@ComponentScan(value = "daon.manager.*")
public class DaonConfig {

    @Bean
    public Daon daon() throws IOException {
        return new Daon();
    }


    @Bean(destroyMethod="shutdownNow")
    public ExecutorService executorService(){

        return new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());
    }

}
