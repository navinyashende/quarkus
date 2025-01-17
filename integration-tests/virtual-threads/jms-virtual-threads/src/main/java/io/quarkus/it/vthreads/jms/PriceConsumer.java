package io.quarkus.it.vthreads.jms;

import static io.quarkus.it.vthreads.jms.AssertHelper.assertThatItRunsOnVirtualThread;

import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class PriceConsumer {

    @RestClient
    PriceAlertService alertService;

    @Incoming("prices")
    @RunOnVirtualThread
    public CompletionStage<Void> consume(Message<Double> msg) {
        assertThatItRunsOnVirtualThread();
        double price = msg.getPayload();
        alertService.alertMessage(price);
        return msg.ack();
    }

    @Incoming("prices")
    @RunOnVirtualThread
    public void consume(double price) {
        assertThatItRunsOnVirtualThread();
        alertService.alert(price);
    }

    Random r = new Random();
    AtomicInteger i = new AtomicInteger();

    @Outgoing("prices-out")
    @RunOnVirtualThread
    public Message<Double> randomPriceGenerator() {
        assertThatItRunsOnVirtualThread();
        return Message.of(r.nextDouble() * 10 * i.incrementAndGet());
    }

}
