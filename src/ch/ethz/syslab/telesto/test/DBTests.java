package ch.ethz.syslab.telesto.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.postgresql.ds.PGPoolingDataSource;

import ch.ethz.syslab.telesto.model.Queue;
import ch.ethz.syslab.telesto.server.config.CONFIG;
import ch.ethz.syslab.telesto.server.controller.PacketProcessingException;
import ch.ethz.syslab.telesto.server.db.Database;
import ch.ethz.syslab.telesto.server.db.procedure.ClientProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.MessageProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.QueueProcedure;
import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;

public class DBTests {

    private static int RUNS = 10000;

    private Database db;

    @Before
    public void initialize() {
        db = new Database();
        db.initialize();
    }

    // change to use setup method for db connection pool

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
        s.setString(2, "dola");
        s.setInt(3, 1);
        s.execute();

        Integer client_id = s.getInt(1);
        s.close();
        System.out.println(client_id);
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
    public void testSimpleProcedure() throws PacketProcessingException {
        int id = db.callSimpleProcedure(ClientProcedure.REQUEST_ID, "dola", 1);
        System.out.println(id);
        assertNotEquals(0, id);
    }

    @Test
    public void testSelectingProcedure1() throws PacketProcessingException {
        List<DatabaseResultEntry> result = db.callSelectingProcedure(ClientProcedure.IDENTIFY, 54);
        assertEquals(result.size(), 1);
    }

    @Test
    public void testMessageInsert() throws PacketProcessingException {
        // queue_id, sender_id, receiver_id, context, priority, message
        db.callProcedure(MessageProcedure.PUT_MESSAGE, 1, 1, null, null, 10, "hallo");
    }

    @Test
    public void testMultiMessageInsert() throws PacketProcessingException {
        // queue_id, sender_id, receiver_id, context, priority, message
        try {
            // todo: nicht auf eigener Connection...
            Array queueIds = db.getConnection().createArrayOf("int4", new Integer[] { 1, 2, 3 });
            db.callProcedure(MessageProcedure.PUT_MESSAGES, queueIds, 1, null, null, 10, "ich bin in Queue 1, 2 und 3");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testSelectingProcedure2() throws PacketProcessingException {
        String queueName = "first Queue";

        List<Queue> result = db.callQueueProcedure(QueueProcedure.CREATE_QUEUE, queueName);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).name, queueName);
    }
}
