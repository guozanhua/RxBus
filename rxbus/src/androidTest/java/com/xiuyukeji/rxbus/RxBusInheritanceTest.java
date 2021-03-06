package com.xiuyukeji.rxbus;

import junit.framework.TestCase;

import org.junit.Test;

public class RxBusInheritanceTest extends TestCase {

    protected RxBus eventBus;

    protected int countMyEventExtended;
    protected int countMyEvent;
    protected int countObjectEvent;
    private int countMyEventInterface;
    private int countMyEventInterfaceExtended;

    protected void setUp() throws Exception {
        super.setUp();
        eventBus =RxBus.get();
    }

    @Test
    public void testEventClassHierarchy() {
        eventBus.register(this);

        eventBus.post("Hello");
        assertEquals(1, countObjectEvent);

        eventBus.post(new MyEvent());
        assertEquals(2, countObjectEvent);
        assertEquals(1, countMyEvent);

        eventBus.post(new MyEventExtended());
        assertEquals(3, countObjectEvent);
        assertEquals(2, countMyEvent);
        assertEquals(1, countMyEventExtended);
    }

    @Test
    public void testEventClassHierarchySticky() {
        eventBus.postSticky("Hello");
        eventBus.postSticky(new MyEvent());
        eventBus.postSticky(new MyEventExtended());
        eventBus.register(new StickySubscriber());
        assertEquals(1, countMyEventExtended);
        assertEquals(1, countMyEvent);
        assertEquals(0, countObjectEvent);
    }

    @Test
    public void testEventInterfaceHierarchy() {
        eventBus.register(this);

        eventBus.post(new MyEvent());
        assertEquals(1, countMyEventInterface);

        eventBus.post(new MyEventExtended());
        assertEquals(2, countMyEventInterface);
        assertEquals(1, countMyEventInterfaceExtended);
    }

    @Test
    public void testEventSuperInterfaceHierarchy() {
        eventBus.register(this);

        eventBus.post(new MyEventInterfaceExtended() {
        });
        assertEquals(1, countMyEventInterface);
        assertEquals(1, countMyEventInterfaceExtended);
    }

    @Test
    public void testSubscriberClassHierarchy() {
        SubscriberExtended subscriber = new SubscriberExtended();
        eventBus.register(subscriber);

        eventBus.post("Hello");
        assertEquals(1, subscriber.countObjectEvent);

        eventBus.post(new MyEvent());
        assertEquals(2, subscriber.countObjectEvent);
        assertEquals(0, subscriber.countMyEvent);
        assertEquals(2, subscriber.countMyEventOverwritten);

        eventBus.post(new MyEventExtended());
        assertEquals(3, subscriber.countObjectEvent);
        assertEquals(0, subscriber.countMyEvent);
        assertEquals(1, subscriber.countMyEventExtended);
        assertEquals(4, subscriber.countMyEventOverwritten);
    }

    @Test
    public void testSubscriberClassHierarchyWithoutNewSubscriberMethod() {
        SubscriberExtendedWithoutNewSubscriberMethod subscriber = new SubscriberExtendedWithoutNewSubscriberMethod();
        eventBus.register(subscriber);

        eventBus.post("Hello");
        assertEquals(1, subscriber.countObjectEvent);

        eventBus.post(new MyEvent());
        assertEquals(2, subscriber.countObjectEvent);
        assertEquals(1, subscriber.countMyEvent);

        eventBus.post(new MyEventExtended());
        assertEquals(3, subscriber.countObjectEvent);
        assertEquals(2, subscriber.countMyEvent);
        assertEquals(1, subscriber.countMyEventExtended);
    }

    @Subscribe
    public void onEvent(Object event) {
        countObjectEvent++;
    }

    @Subscribe
    public void onEvent(MyEvent event) {
        countMyEvent++;
    }

    @Subscribe
    public void onEvent(MyEventExtended event) {
        countMyEventExtended++;
    }

    @Subscribe
    public void onEvent(MyEventInterface event) {
        countMyEventInterface++;
    }

    @Subscribe
    public void onEvent(MyEventInterfaceExtended event) {
        countMyEventInterfaceExtended++;
    }

    public static interface MyEventInterface {
    }

    public static class MyEvent implements MyEventInterface {
    }

    public static interface MyEventInterfaceExtended extends MyEventInterface {
    }

    public static class MyEventExtended extends MyEvent implements MyEventInterfaceExtended {
    }

    public static class SubscriberExtended extends RxBusInheritanceTest {
        private int countMyEventOverwritten;

        @Subscribe
        public void onEvent(MyEvent event) {
            countMyEventOverwritten++;
        }
    }

    static class SubscriberExtendedWithoutNewSubscriberMethod extends RxBusInheritanceTest {
    }

    public class StickySubscriber {
        @Subscribe(sticky = true)
        public void onEvent(Object event) {
            countObjectEvent++;
        }

        @Subscribe(sticky = true)
        public void onEvent(MyEvent event) {
            countMyEvent++;
        }

        @Subscribe(sticky = true)
        public void onEvent(MyEventExtended event) {
            countMyEventExtended++;
        }

        @Subscribe(sticky = true)
        public void onEvent(MyEventInterface event) {
            countMyEventInterface++;
        }

        @Subscribe(sticky = true)
        public void onEvent(MyEventInterfaceExtended event) {
            countMyEventInterfaceExtended++;
        }
    }

}
