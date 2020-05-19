package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ClientView<S> extends SantoriniRunnable<S> {

    protected ClientModel<S> clientModel;
    private boolean isFree;
    public final Object lockFree;
    private static final Logger LOGGER = Logger.getLogger(ClientView.class.getName());

    public ClientView(ClientModel<S> clientModel) {
        super();
        this.clientModel = clientModel;

        lockFree = new Object();
        setFree(true);
    }

    public ClientView() {
        this(null);
    }

    public void setClientModel(ClientModel<S> clientModel) {
        this.clientModel = clientModel;
    }

    public ClientModel<S> getClientModel() {
        return clientModel;
    }

    public boolean isFree() {
        boolean ret;

        synchronized (lockFree) {
            ret = isFree;
        }

        return ret;
    }

    public void setFree(boolean free) {
        synchronized (lockFree) {
            isFree = free;
        }
    }

    protected void endGame() {
        setActive(false);
        clientModel.setActive(false);
        clientModel.getClientConnection().closeConnection();
    }

    protected void setInitialRequest() {
        setFree(false);

        synchronized (clientModel.lock) {
            setDemand(new Demand<>(DemandType.CONNECT, (S) (new ReducedMessage(clientModel.getPlayer().getNickname()))));
            setChanged(true);
        }

        setFree(true);

        synchronized (lockDemand) {
            lockDemand.notifyAll();
        }
    }

    protected void createDemand(Demand demand) {
        setDemand(demand);
        setChanged(true);

        synchronized (lockDemand) {
            lockDemand.notifyAll();
        }
    }

    protected Thread asyncReadFromModel() {
        Thread t = new Thread(
                () -> {
                    try {
                        Answer<S> temp;
                        while (isActive()) {
                            synchronized (clientModel.lockAnswer) {
                                while (!clientModel.isChanged()) clientModel.lockAnswer.wait();
                                clientModel.setChanged(false);
                                temp = clientModel.getAnswer();
                            }

                            LOGGER.info("Receiving...");
                            synchronized (lockAnswer) {
                                setAnswer(temp);
                                LOGGER.info("Received!");
                                update();
                            }
                        }
                    } catch (Exception e){
                        if (!(e instanceof InterruptedException))
                            LOGGER.log(Level.SEVERE, "Got an exception", e);
                    }
                }
        );

        t.start();
        return t;
    }

    protected void runThreads(String name, String ip, int port) {
        ClientConnectionSocket clientConnectionSocket = null;
        ClientModel clientModel;

        try {
            clientConnectionSocket = new ClientConnectionSocket(ip, port);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException");
        }

        clientModel = new ClientModel(name, clientConnectionSocket);
        setClientModel(clientModel);
        clientConnectionSocket.setClientView(this);

        setInitialRequest();

        new Thread(
                clientConnectionSocket
        ).start();

        new Thread(
                clientModel
        ).start();
    }

    protected void becomeFree() {
        setFree(true);
        synchronized (lockFree) {
            lockFree.notifyAll();
        }
    }

    protected abstract void update();
}
