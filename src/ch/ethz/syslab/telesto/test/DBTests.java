package ch.ethz.syslab.telesto.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.ds.PGPoolingDataSource;

import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.ClientMode;
import ch.ethz.syslab.telesto.common.model.Queue;
import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.server.db.Database;
import ch.ethz.syslab.telesto.server.db.procedure.ClientProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.MessageProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.QueueProcedure;

public class DBTests {

    private static int RUNS = 5000;

    private static Database db;

    @BeforeClass
    public static void initialize() throws SQLException {
        db = new Database();
        db.initialize();

        String[] stms = new String[] {
                "ALTER SEQUENCE clients_client_id_seq RESTART",
                "ALTER SEQUENCE queue_queue_id_seq RESTART",
                "ALTER SEQUENCE messages_message_id_seq RESTART",
                "TRUNCATE clients, queues, messages"
        };

        PreparedStatement s = null;
        for (String stm : stms) {
            s = db.getConnection().prepareStatement(stm);
            s.execute();
        }
        s.getConnection().close();
    }

    @Test
    public void createConnectionPool() throws SQLException {
        PGPoolingDataSource connectionPool = new PGPoolingDataSource();
        connectionPool.setApplicationName(CONFIG.DB_SERVER_NAME);
        connectionPool.setServerName(CONFIG.DB_SERVER_NAME);
        connectionPool.setPortNumber(CONFIG.DB_PORT_NUMBER);
        connectionPool.setDatabaseName(CONFIG.DB_NAME);
        connectionPool.setUser(CONFIG.DB_USER);
        connectionPool.setPassword(CONFIG.DB_PASSWORD);
        connectionPool.setMaxConnections(CONFIG.DB_MAX_CONNECTIONS);

        Connection c = connectionPool.getConnection();

        // request id for user dola with mode 1
        CallableStatement s = c.prepareCall("{ ? = call request_id( ? , ? ) }");
        s.registerOutParameter(1, Types.INTEGER);
        s.setString(2, "blubbedi");
        s.setInt(3, 1);
        s.execute();

        Integer client_id = s.getInt(1);
        s.close();
        assertNotNull("returned client id is null", client_id);
    }

    @Test
    public void testPreparedStatementGeneration() {
        for (int i = 0; i < RUNS; i++) {
            String out = Database.buildCallStatement("method1", 5, true);
            assertEquals(out, "{ ? = call method1( ? , ? , ? , ? , ? ) }");
        }

        String out = Database.buildCallStatement("method2", 0, false);
        assertEquals(out, "{ call method2() }");

        out = Database.buildCallStatement("method3", 0, true);
        assertEquals(out, "{ ? = call method3() }");

        out = Database.buildCallStatement("method4", 2, false);
        assertEquals(out, "{ call method4( ? , ? ) }");
    }

    @Test
    public void testManySimpleProcedures() throws PacketProcessingException {
        for (int i = 2; i < RUNS; i++) {
            db.callSimpleProcedure(ClientProcedure.REQUEST_ID, "dola " + i, ClientMode.FULL.getByteValue());
        }
    }

    @Test
    public void testClientProcedure1() throws PacketProcessingException {
        int id = db.callSimpleProcedure(ClientProcedure.REQUEST_ID, "dola", ClientMode.FULL.getByteValue());

        List<Client> result = db.callClientProcedure(ClientProcedure.IDENTIFY, id);
        assertEquals(1, result.size());
        assertEquals("dola", result.get(0).name);
        assertEquals(ClientMode.FULL, result.get(0).mode);
    }

    @Test
    public void testQueueCreation() throws PacketProcessingException {
        String[] names = new String[] { "oneWayQueue", "requestResponsePairQueue", "serviceQueue" };

        // queue_id, queue_name
        for (String name : names) {
            List<Queue> queues = db.callQueueProcedure(QueueProcedure.CREATE_QUEUE, name);
            assertEquals(1, queues.size());
            assertEquals(name, queues.get(0).name);
        }
    }

    @Test
    public void testMessageInsert() throws PacketProcessingException {
        List<Queue> queues = db.callQueueProcedure(QueueProcedure.CREATE_QUEUE, "messageInsertTestQueue");

        int queueId = queues.get(0).id;

        // queue_id, sender_id, receiver_id, context, priority, message
        int insertedQueueId = db.callSimpleProcedure(MessageProcedure.PUT_MESSAGE, queueId, 1, null, null, 10, "hallo");
        assertEquals(queueId, insertedQueueId);
    }

    @Test
    public void testMultiMessageInsert() throws PacketProcessingException {
        Integer[] queuesToInsert = new Integer[3];

        for (int i = 0; i < 3; i++) {
            List<Queue> queues = db.callQueueProcedure(QueueProcedure.CREATE_QUEUE, "messageInsertTestQueue" + i);
            queuesToInsert[i] = queues.get(0).id;
        }

        // queue_id, sender_id, receiver_id, context, priority, message
        List<Integer> queues = db.callIntegerListProcedure(MessageProcedure.PUT_MESSAGES, queuesToInsert, 1, null, null, 10, "ich bin in Queue 1, 2 und 3");
        assertArrayEquals(queuesToInsert, queues.toArray());
    }

    @Test
    public void testSelectingProcedure2() throws PacketProcessingException {
        String queueName = "first Queue";

        List<Queue> result = db.callQueueProcedure(QueueProcedure.CREATE_QUEUE, queueName);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).name, queueName);
    }
}
