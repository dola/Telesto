package ch.ethz.syslab.telesto.client;

import java.util.List;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.model.Client;
import ch.ethz.syslab.telesto.model.ClientMode;
import ch.ethz.syslab.telesto.model.Message;
import ch.ethz.syslab.telesto.model.Queue;
import ch.ethz.syslab.telesto.model.ReadMode;

public class TelestoClient implements ITelestoClient {

    public TelestoClient() {
    }

    @Override
    public Client connect(ClientMode mode) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Client connect(int id) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Queue createQueue(String name) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteQueue(int id) throws ProcessingException {
        // TODO Auto-generated method stub

    }

    @Override
    public Queue getQueueByName(String name) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Queue getQueueById(int id) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Queue> getQueues() throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Queue> getActiveQueues() throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Message> readMessages(int queueId) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putMessage(Message message) throws ProcessingException {
        // TODO Auto-generated method stub

    }

    @Override
    public void putMessage(Message message, int[] queueId) throws ProcessingException {
        // TODO Auto-generated method stub

    }

    @Override
    public Message sendRequestResponseMessage(Message message) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Message retrieveMessage(int queueId) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Message retrieveMessage(int queueId, ReadMode mode) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Message retrieveMessage(int queueId, int sender) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Message retrieveMessage(int queueId, int sender, ReadMode mode) throws ProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

}
