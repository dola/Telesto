package ch.ethz.syslab.telesto.client.test;

public enum ClientTest {
    ONE_WAY(OneWayClientTest.class),
    REQUEST_RESPONSE_PAIR_CLIENT(RequestResponsePairClientTest.class),
    REQUEST_RESPONSE_PAIR_SERVER(RequestResponsePairServerTest.class),
    REQUEST_SERVICE(RequestServiceClientTest.class),
    SERVE_SERVICE(ServerServiceClientTest.class);

    private Class<? extends IClientTest> testClass;

    private ClientTest(Class<? extends IClientTest> testClass) {
        this.testClass = testClass;
    }

    public Class<? extends IClientTest> getTestClass() {
        return testClass;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static ClientTest getByString(String id) {
        for (ClientTest t : values()) {
            if (t.toString().toLowerCase().equals(id.toLowerCase())) {
                return t;
            }
        }
        return null;
    }
}
