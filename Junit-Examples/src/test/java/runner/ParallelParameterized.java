package runner;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelParameterized extends Parameterized {


    public ParallelParameterized(Class<?> klass) throws Throwable {
        super(klass);
        setScheduler(new ThreadedScheduler());
    }

    private static class ThreadedScheduler implements RunnerScheduler{
        private ExecutorService service;
        public ThreadedScheduler(){
            service = Executors.newFixedThreadPool(
                    Integer.valueOf(
                            System.getProperty("junit.parallel.threads", "4"))); // you can have your own property to confi this 

        }

        @Override
        public void schedule(Runnable childStatement) {
            service.submit(childStatement);
        }

        @Override
        public void finished() {
            service.shutdown();
            try {
                service.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error Stopping Threads",e);
            }
        }
    }
}
